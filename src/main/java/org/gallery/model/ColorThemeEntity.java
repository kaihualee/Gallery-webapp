/**
 * 
 */
package org.gallery.model;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.bridj.Pointer;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.model.common.BaseEntityImpl;

/**
 * @author Dahaka
 * 
 */
public class ColorThemeEntity extends BaseEntityImpl {

	private final static ObjectMapper mapper = new ObjectMapper();

	public ColorThemeEntity() {

	}

	public ColorThemeEntity(final int colorNum, Pointer<Byte> r,
			Pointer<Byte> g, Pointer<Byte> b, Pointer<Double> percents)
			throws JsonGenerationException, JsonMappingException,
			SerialException, SQLException, IOException {

		int[] local_rgbs = new int[colorNum];
		double[] local_percents = new double[colorNum];
		for (int idx = 0; idx < colorNum; ++idx) {
			int rgb = ((r.get(idx) & 0xFF) << 16) | ((g.get(idx) & 0xFF) << 8)
					| ((b.get(idx) & 0xFF) << 0);
			local_rgbs[idx] = rgb;
			local_percents[idx] = percents.getDoubleAtIndex(idx);
		}
		this.colorNum = colorNum;
		this.rgbs = new SerialBlob(mapper.writeValueAsBytes(local_rgbs));
		this.percents = new SerialBlob(mapper.writeValueAsBytes(local_percents));
	}

	/**
	 * @return the colorNum
	 */
	public int getColorNum() {
		return colorNum;
	}

	/**
	 * @param colorNum
	 *            the colorNum to set
	 */
	public void setColorNum(int colorNum) {
		this.colorNum = colorNum;
	}

	/**
	 * @return the rgbs
	 */
	public Blob getRgbs() {
		return rgbs;
	}

	/**
	 * @return the percents
	 */
	public Blob getPercents() {
		return percents;
	}

	/**
	 * @param rgbs
	 *            the rgbs to set
	 */
	public void setRgbs(Blob rgbs) {
		this.rgbs = rgbs;
	}

	/**
	 * @param percents
	 *            the percents to set
	 */
	public void setPercents(Blob percents) {
		this.percents = percents;
	}

	@Override
	public String toString() {
		return super.toString() + " " + colorNum + " " + rgbs.toString() + " "
				+ percents.toString();
	}

	private int colorNum;
	private Blob rgbs;
	private Blob percents;
}
