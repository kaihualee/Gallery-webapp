/**
 * 
 */
package org.gallery.web.vo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import org.bridj.Pointer;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.model.ColorThemeEntity;

/**
 * @author Dahaka
 * 
 */
public class ColorThemeVO {

	private final static ObjectMapper mapper = new ObjectMapper();

	public ColorThemeVO(final int colorNum, Pointer<Byte> r, Pointer<Byte> g,
			Pointer<Byte> b, Pointer<Double> percents) {
		this.colorNum = colorNum;
		this.rgbs = new int[colorNum];
		this.percents = new double[colorNum];
		for (int idx = 0; idx < colorNum; ++idx) {
			int rgb = ((r.get(idx) & 0xFF) << 16) | ((g.get(idx) & 0xFF) << 8)
					| ((b.get(idx) & 0xFF) << 0);
			this.rgbs[idx] = rgb;
			this.percents[idx] = percents.getDoubleAtIndex(idx);
		}
	}

	public ColorThemeVO(ColorThemeEntity entity) throws JsonParseException,
			JsonMappingException, IOException, SQLException {
		this.colorNum = entity.getColorNum();
		// System.out.println(entity.getRgbs().getBytes(0,
		// entity.getRgbs().length()));
		this.rgbs = mapper.readValue(entity.getRgbs().getBinaryStream(),
				int[].class);
		this.percents = mapper.readValue(
				entity.getPercents().getBinaryStream(), double[].class);
	}

	/**
	 * @return the colorNum
	 */
	public int getColorNum() {
		return colorNum;
	}

	/**
	 * @return the rgbs
	 */
	public int[] getRgbs() {
		return rgbs;
	}

	/**
	 * @return the percents
	 */
	public double[] getPercents() {
		return percents;
	}

	/**
	 * @param colorNum
	 *            the colorNum to set
	 */
	public void setColorNum(int colorNum) {
		this.colorNum = colorNum;
	}

	/**
	 * @param rgbs
	 *            the rgbs to set
	 */
	public void setRgbs(int[] rgbs) {
		this.rgbs = rgbs;
	}

	/**
	 * @param percents
	 *            the percents to set
	 */
	public void setPercents(double[] percents) {
		this.percents = percents;
	}

	@Override
	public String toString() {
		return super.toString() + " " + colorNum + " " + Arrays.toString(rgbs)
				+ " " + Arrays.toString(percents);
	}

	private int colorNum;
	private int[] rgbs;
	private double[] percents;
}
