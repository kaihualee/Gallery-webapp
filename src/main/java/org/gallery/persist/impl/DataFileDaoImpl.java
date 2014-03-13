/**
 * 
 */
package org.gallery.persist.impl;

import org.gallery.model.DataFileEntity;
import org.gallery.persist.DataFileDao;
import org.gallery.persist.common.LogicDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * @author likaihua
 */
@Repository
public class DataFileDaoImpl extends LogicDaoImpl<DataFileEntity> implements
    DataFileDao {

    private final static Logger log = LoggerFactory
        .getLogger(DataFileDaoImpl.class);
}
