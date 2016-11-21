package com.redblaster.hsl.common;

import com.redblaster.hsl.main.R;

/**
 * Enum represents a vechicle types 
 * 
 * @author Ilja Hamalainen
 * 
 */
public enum Vehicle {
	BUS, 
	TRAIN, 
	METRO,
	TRAM,
	FARRY,
	ULINES;

	/**
	 * Returns value for Reittiopas API
	 * 
	 * @return Int
	 */
	public int toInt() {
		int nRet = -1;
		switch (this) {
			case BUS:
				nRet = Constants.INT_VEHICLE_TYPE_BUS;
				break;
			case TRAIN:
				nRet = Constants.INT_VEHICLE_TYPE_TRAIN;
				break;
			case METRO:
				nRet = Constants.INT_VEHICLE_TYPE_METRO;
				break;
			case TRAM:
				nRet = Constants.INT_VEHICLE_TYPE_TRAM;
				break;
			case FARRY:
				nRet = Constants.INT_VEHICLE_TYPE_FERRY;
				break;
			case ULINES:
				nRet = Constants.INT_VEHICLE_TYPE_U_LINES;
				break;
		}
		return nRet;
	}
	
	/**
	 * Gets ID of button on Main Page corresponding the given vehicle type
	 * 
	 * @return
	 */
	public int getButtionId() {
		int nButtonId = -1;
		switch (this) {
		case BUS:
			nButtonId = R.id.btnBus;
			break;
		case TRAIN:
			nButtonId = R.id.btnTrain;
			break;
		case METRO:
			nButtonId = R.id.btnMetro;
			break;
		case TRAM:
			nButtonId = R.id.btnTramm;
			break;
		case FARRY:
			nButtonId = R.id.btnFerry;
			break;
		case ULINES:
			nButtonId = R.id.btnUlines;
			break;
		}
		return nButtonId;
	}
	
	/**
	 * Returns the ICO drawable resourse ID for the given vehicle type
	 * @return
	 */
	public int getIco() {
		int nIcoResourse = -1;
		switch (this) {
		case BUS:
			nIcoResourse = R.drawable.ico_bus;
			break;
		case TRAIN:
			nIcoResourse = R.drawable.ico_train;
			break;
		case METRO:
			nIcoResourse = R.drawable.ico_metro;
			break;
		case TRAM:
			nIcoResourse = R.drawable.ico_tram;
			break;
		case FARRY:
			nIcoResourse = R.drawable.ico_farry;
			break;
		case ULINES:
			nIcoResourse = R.drawable.ico_u_linjat;
			break;
		default:
			nIcoResourse = R.drawable.icon_launcher;
			break;
		}
		return nIcoResourse;
	}
	
	/**
	 * Returns a Vehicle object by the it's ID
	 * 
	 * @param id
	 * @return
	 */
	public static Vehicle getById(int id) {
		Vehicle vehicle = null;
		switch (id) {
			case Constants.INT_VEHICLE_TYPE_BUS:
				vehicle = Vehicle.BUS;
				break;
			case Constants.INT_VEHICLE_TYPE_FERRY:
				vehicle = Vehicle.FARRY;
				break;
			case Constants.INT_VEHICLE_TYPE_METRO:
				vehicle = Vehicle.METRO;
				break;
			case Constants.INT_VEHICLE_TYPE_TRAIN:
				vehicle = Vehicle.TRAIN;
				break;
			case Constants.INT_VEHICLE_TYPE_TRAM:
				vehicle = Vehicle.TRAM;
				break;
			case Constants.INT_VEHICLE_TYPE_U_LINES:
				vehicle = Vehicle.ULINES;
				break;
			default:
				vehicle = Vehicle.BUS;
				break;
		}
		return vehicle;
	}
	
	/**
	 * Returns a string resource. Can be used to retrieve the string name
	 * @return
	 */
	public int getStringResource() {
		int nButtonId = -14;
		switch (this) {
		case BUS:
			nButtonId = R.string.vehicle_type_bus;
			break;
		case TRAIN:
			nButtonId = R.string.vehicle_type_train;
			break;
		case METRO:
			nButtonId = R.string.vehicle_type_metro;
			break;
		case TRAM:
			nButtonId = R.string.vehicle_type_tram;
			break;
		case FARRY:
			nButtonId = R.string.vehicle_type_farry;
			break;
		case ULINES:
			nButtonId = R.string.vehicle_type_u_lines;
			break;
		}
		return nButtonId;
	}
}