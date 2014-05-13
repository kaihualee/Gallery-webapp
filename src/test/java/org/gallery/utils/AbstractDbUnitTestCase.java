/* 
 * ========================================================================
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.gallery.utils;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.filter.ITableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.hsqldb.HsqldbConnection;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractDbUnitTestCase {

	protected static Connection connection;
	protected static DatabaseConnection dbunitConnection;

	@AfterClass
	public static void closeDatabase() throws Exception {
		if (dbunitConnection != null) {
			dbunitConnection.close();
			dbunitConnection = null;
		}
	}

	public static IDataSet getDataSet(String name) throws Exception {
		InputStream inputStream = AbstractDbUnitTestCase.class.getClassLoader()
				.getResourceAsStream(name);
		assertNotNull("file " + name + " not found in classpath", inputStream);
		Reader reader = new InputStreamReader(inputStream);
		FlatXmlDataSet dataset = new FlatXmlDataSet(reader);
		return dataset;
	}

	public static void saveDataSet(String[] tablenames, String saveFilename)
			throws DataSetException, SQLException, FileNotFoundException,
			IOException {
		IDataSet actualDataSet = dbunitConnection.createDataSet(tablenames);
		ITableFilter filter = new DatabaseSequenceFilter(dbunitConnection);
		FilteredDataSet filteredDataSet = new FilteredDataSet(filter,
				actualDataSet);

		FlatXmlDataSet.write(filteredDataSet,
				new FileOutputStream(saveFilename));
	}

	public static void saveDataSet(String saveFilename)
			throws DataSetException, SQLException, FileNotFoundException,
			IOException {
		IDataSet actualDataSet = dbunitConnection.createDataSet();
		ITableFilter filter = new DatabaseSequenceFilter(dbunitConnection);
		FilteredDataSet filteredDataSet = new FilteredDataSet(filter,
				actualDataSet);

		FlatXmlDataSet.write(filteredDataSet,
				new FileOutputStream(saveFilename));
	}

}
