package com.redblaster.hsl.main.bookmarks.migration;

import com.redblaster.hsl.dto.BaseDTO;

/**
 * 
 * Bookmark.
 * 
 * This DTO class is used only in migration purposes, because it contains not real ID,
 * but the string names. This is because ID's may be changed in different database versions,
 * but the names will remains.
 * 
 * @author Ilja Hamalainen
 *
 */
public class MigrationBookmark extends BaseDTO{
	private String stationStartName;
	private String stationName;
	private long transportNumberId;
	private String bookmarkNameHumanReadable;
	
	// used when we will needed to remove the line (in case when some transport will be cancelled)
	private long transportLineID;
	
	public MigrationBookmark(long bookmarkId, long lineId, String strTransportNumberName, long transportNumberID, String stationStart, String station, String nameHumanReadable) {
		super(bookmarkId, strTransportNumberName);
		this.setStationName(station);
		this.setStationStartName(stationStart);
		this.setTransportNumberId(transportNumberID);
		this.setBookmarkLineID(lineId);
		this.setBookmarkNameHumanReadable(nameHumanReadable);
	}

	/**
	 * @param stationStartName the stationStartName to set
	 */
	public void setStationStartName(String stationStartName) {
		this.stationStartName = stationStartName;
	}

	/**
	 * @return the stationStartName
	 */
	public String getStationStartName() {
		return stationStartName;
	}

	/**
	 * @param stationName the stationName to set
	 */
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	/**
	 * @return the stationName
	 */
	public String getStationName() {
		return stationName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MigrationBookmark [stationStartName=" + stationStartName
				+ ", stationName=" + stationName + "] + " + super.toString();
	}

	/**
	 * @param transportNumberId the transportNumberId to set
	 */
	public void setTransportNumberId(long transportNumberId) {
		this.transportNumberId = transportNumberId;
	}

	/**
	 * @return the transportNumberId
	 */
	public long getTransportNumberId() {
		return transportNumberId;
	}

	/**
	 * @param transportLineID the transportLineID to set
	 */
	public void setBookmarkLineID(long transportLineID) {
		this.transportLineID = transportLineID;
	}

	/**
	 * @return the transportLineID
	 */
	public long getBookmarkLineID() {
		return transportLineID;
	}

	/**
	 * @param bookmarkNameHumanReadable the bookmarkNameHumanReadable to set
	 */
	public void setBookmarkNameHumanReadable(String bookmarkNameHumanReadable) {
		this.bookmarkNameHumanReadable = bookmarkNameHumanReadable;
	}

	/**
	 * @return the bookmarkNameHumanReadable
	 */
	public String getBookmarkNameHumanReadable() {
		return bookmarkNameHumanReadable;
	}
	
	
}