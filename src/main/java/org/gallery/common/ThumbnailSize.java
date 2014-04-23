/**
 * 
 */
package org.gallery.common;

/**
 * @author Dahaka
 * 
 */
public enum ThumbnailSize {
	SMALL_SIZE(0, 150, "jpg", 1.0), MEDIUM_SIZE(1, 1024, "jpg", 1.0), BIG_SIZE(
			2, 2048, "jpg", 0.6);

	private ThumbnailSize(int id, int size, String formatName,
			double compressionQuality) {
		this.id = id;
		this.size = size;
		this.formatName = formatName;
		this.compressionQuality = compressionQuality;
	}

	public static ThumbnailSize valueOf(int id) {
		if (id == BIG_SIZE.id) {
			return SMALL_SIZE;
		} else if (id == MEDIUM_SIZE.id) {
			return MEDIUM_SIZE;
		} else {
			return SMALL_SIZE;
		}
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return the formatName
	 */
	public String getFormatName() {
		return formatName;
	}

	/**
	 * @return the compressionQuality
	 */
	public double getCompressionQuality() {
		return compressionQuality;
	}

	private int id;
	private int size;
	private String formatName;
	private double compressionQuality;

}
