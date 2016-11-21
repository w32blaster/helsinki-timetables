package com.redblaster.hsl.dto;

import com.redblaster.hsl.common.Utils;


/**
 * Inner class, representing the set of transport lines, becoming to one bookmark
 * @author Ilja Hamalainen
 *
 */
public class TransportLineDTO {
	private long transportNumberID;
	private long stationIDstart;
	private long stationID;
	private String transportLine;
	private String time;
	
	public TransportLineDTO(long tr, long statStart, long statID){
		this.transportNumberID = tr;
		this.stationIDstart = statStart;
		this.stationID = statID;
	}
	
	public TransportLineDTO(long tr, long statStart, long statID, String trLineName, String strTime){
		this(tr, statStart, statID);
		this.transportLine = trLineName;
		this.time = strTime;
	}

	/**
	 * Combines a well formatted time with a transport line name, like "21:32 (65A)"
	 * 
	 * @return string
	 */
	public String getFormattedTimeWithTransportNumber() {
		return Utils.getFormattedTime(this.time) + " (" + this.transportLine + ")";
	}
	
	/**
	 * @return the transportNumberID
	 */
	public long getTransportNumberID() {
		return transportNumberID;
	}

	/**
	 * @param transportNumberID the transportNumberID to set
	 */
	public void setTransportNumberID(long transportNumberID) {
		this.transportNumberID = transportNumberID;
	}

	/**
	 * @return the stationIDstart
	 */
	public long getStationIDstart() {
		return stationIDstart;
	}

	/**
	 * @param stationIDstart the stationIDstart to set
	 */
	public void setStationIDstart(long stationIDstart) {
		this.stationIDstart = stationIDstart;
	}

	/**
	 * @param stationID the stationID to set
	 */
	public void setStationID(long stationID) {
		this.stationID = stationID;
	}

	/**
	 * @return the stationID
	 */
	public long getStationID() {
		return stationID;
	}

	/**
	 * @param transportLine the transportLine to set
	 */
	public void setTransportLine(String transportLine) {
		this.transportLine = transportLine;
	}

	/**
	 * @return the transportLine
	 */
	public String getTransportLine() {
		return transportLine;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransportLineDTO [transportNumberID=" + transportNumberID
				+ ", stationIDstart=" + stationIDstart + ", stationID="
				+ stationID + ", transportLine=" + transportLine + ", time="
				+ time + "]";
	}
}