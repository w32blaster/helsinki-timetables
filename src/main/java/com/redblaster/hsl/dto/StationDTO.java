package com.redblaster.hsl.dto;

public class StationDTO extends BaseDTO {
	private long city;
	
	StationDTO(Long lId, String strName) {
		super(lId, strName);
	}

	public StationDTO(Long lId, String strName, Long cityId) {
		super(lId, strName);
		this.city = cityId;
	}
	
	/**
	 * @param city the city to set
	 */
	public void setCity(long city) {
		this.city = city;
	}

	/**
	 * @return the city
	 */
	public long getCity() {
		return city;
	}

}
