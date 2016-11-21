package com.redblaster.hsl.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.util.Log;

import com.redblaster.hsl.common.Constants;

public class BookmarkDTO extends BaseDTO {
	private int image = -1;
	private HashSet<TransportLineDTO> lines;
	private int errorCode = Constants.DB_ERROR_ALL_RIGHT;
	private String lastTimeUpdated;
	
	public BookmarkDTO(Long lId, String strName) {
		super(lId, strName);
		this.lines = new HashSet<TransportLineDTO>();
	}
	
	public BookmarkDTO(Long lId, String strName, int img) {
		this(lId, strName);
		this.image = img;
	}

	/**
	 * @return the image
	 */
	public int getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(int image) {
		this.image = image;
	}

	/**
	 * Adds 
	 * @param transportLine
	 * @param stationStart
	 */
	public void addLine(long transportLine, long stationStart, long station) {
		lines.add(new TransportLineDTO(transportLine, stationStart, station));
	}

	/**
	 * Adds 
	 * @param transportLine
	 * @param stationStart
	 */
	public void addLine(long transportLine, String strTrLineName, String time) {
		lines.add(new TransportLineDTO(transportLine, -1L, -1L, strTrLineName, time));
	}
	
	/**
	 * 
	 * @return
	 */
	public HashSet<TransportLineDTO> getLines() {
		return this.lines;
	}

	/**
	 * Correction for the time. We need to sort strings, so "804" and "0804" are different strings
	 * and we should make sure that all time values are correct with length = 4.
	 * 
	 * @param str - string to check
	 * @return formatted string
	 */
	private String getCorrectedTimeString(String str) {
		if (str.length() == 3) {
			return Constants.STR_ZERO + str;
		}
		else {
			return str;
		}
	}
	
	/**
	 * Returned the list of all lines. sorted by time.
	 * 
	 * @return List<TransportLineDTO>
	 */
	public List<TransportLineDTO> getLinesAsSortedList() {
		List<TransportLineDTO> list = new ArrayList<TransportLineDTO>(this.lines);
		
		if (!list.isEmpty()) {
			try {
				Collections.sort(list, new Comparator<TransportLineDTO>() {
					
					@Override
					public int compare(TransportLineDTO object1, TransportLineDTO object2) {
						final String str1 = getCorrectedTimeString(object1.getTime());
						final String str2 = getCorrectedTimeString(object2.getTime());
						
						if (str1 == null || str2 == null) {
							return 0;
						}
						else if (str1.compareTo(str2) > 1) {
							return 1;
						}
						else if(str1.compareTo(str2) < 1) {
							return -1;
						}
						else {
							return 0;
						}
					}
					
				});
			} catch (IllegalArgumentException e) {
				Log.e("Error", "Error while sorting: " + e);
			}
		}
		
		return list;
	}
	
	/**
	 * Returned the list of all lines. Sorted by time and limited by Constants.BOOKMARKS_PER_TRIP
	 * 
	 * @return
	 */
	public List<TransportLineDTO> getLinesAsLimitedSortedList() {
		List<TransportLineDTO> lst = this.getLinesAsSortedList();
		if (lst.size() >= Constants.BOOKMARKS_PER_TRIP) {
			lst = lst.subList(0, Constants.BOOKMARKS_PER_TRIP);
		}
		return lst;
	}
	
	/**
	 * Adds new record for this bookmark.
	 * 
	 * @param String lBookmarkID 
	 * @param String name
	 * @param int nImage
	 * @param String strTrLine
	 * @param String strTime
	 * @param long lTrLineID
	 */
	public void addNewRecod(long lBookmarkID, String name, int nImage, String strTrLine, String strTime, long lTrLineID) {
		//Utils.log("Added to DTO. bookmarkID: " + lBookmarkID + ", name: " + name + ", nImage: " + nImage + ", strTrLine: " + strTrLine + ", strTime: " + strTime + ", lTrLineID: " + lTrLineID);
		if (-1L == super.getId()) {
			super.setId(lBookmarkID);
			super.setName(name);
			this.setImage(nImage);
		}
		
		this.addLine(lTrLineID, strTrLine, strTime);
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @param lastTimeUpdated the lastTimeUpdated to set
	 */
	public void setLastTimeUpdated(String lastTimeUpdated) {
		this.lastTimeUpdated = lastTimeUpdated;
	}

	/**
	 * @return the lastTimeUpdated
	 */
	public String getLastTimeUpdated() {
		return lastTimeUpdated;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BookmarkDTO [image=" + image + ", lines=" + lines
				+ ", errorCode=" + errorCode + ", lastTimeUpdated="
				+ lastTimeUpdated + "]";
	}
	
}