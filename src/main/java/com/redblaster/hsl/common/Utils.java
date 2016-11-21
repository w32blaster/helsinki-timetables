package com.redblaster.hsl.common;

import java.io.File;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

/**
 * Utils class
 * 
 * @author Ilja Hamalainen
 *
 */
public class Utils {

	/**
	 * Returns Vehicle name by its type
	 * @param nType
	 * @return
	 */
	public static String getVehicleNameByType(int nType) {
		String strRet = Constants.STR_EMPTY;
		for (Vehicle v : Vehicle.values()) {
			if (v.toInt() == nType) {
				strRet = v.toString();
			}
		}
		return strRet;
	}

	/**
	 * Gets the safe name of SD card
	 * @return
	 */
	public static File getSDcardSafeName() {
		return Environment.getExternalStorageDirectory();	
	}
	
	/**
	 * Checks, is the SD card is mounted or not.
	 * @return {boolean} result
	 */
	public static boolean isSDcardMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * Returns the path of folder on SD card, where must be placed the Database file
	 * @return
	 */
	public static String getHSLfolderName() {
		return getSDcardSafeName() + "/" + Constants.STR_FOLDER_NAME;
	}
	
	/**
	 * Combines formatted time like "23:35"
	 * 
	 * @param strVal
	 * @return string time
	 */
	public static String getFormattedTime(String strVal) {
		if (strVal.length() == 3) strVal = Constants.STR_ZERO + strVal;
		
		String strHrs = strVal.substring(0, 2);
		int h = Integer.parseInt(strHrs);
		if (h >= 24) h = h - 24;
		return h + ":" + strVal.substring(2); 
	}
	
	/**
	 * Gets raw time with a format HM. It means, that "14:35" eill be "1435"
	 * @return
	 */
	public static String getRawCurrentTime() {
		Time now = new Time();
		now.setToNow();
		return now.format("%H%M");
	}
	
	/**
	 * Simple Log
	 * 
	 * @param str
	 */
	public static void log(String str) {
		if (Constants.IS_LOG_ENABLED) {
			Log.d("My LOG",str);
		}
	}
	
	/**
	 * Sends a message with a bundle of vars to the Hadler
	 * 
	 * @param handler
	 * @param command
	 * @param value
	 */
	public synchronized static void sendMessage(Handler handler, int command, int value) {
		sendMessage(handler, command, value, Constants.STR_EMPTY);
	}
	
	/**
	 * Sends a message with a bundle of vars to the Hadler with custom result label
	 * 
	 * @param handler
	 * @param command
	 * @param value
	 * @param customLabel. You can define your own label instead of [OK] or [ERROR]
	 */
	public synchronized static void sendMessage(Handler handler, int command, int value, String customLabel) {
    	Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt(Constants.STR_HANDLER_MESSAGE_COMMAND, command);
        b.putInt(Constants.STR_HANDLER_MESSAGE_VALUE, value);
        b.putString(Constants.STR_HANDLER_MESSAGE_LABEL, customLabel);
        msg.setData(b);
    	handler.sendMessage(msg);
	}
	
	/**
	 * Returns right INT value, representing result
	 * 
	 * @param bResult
	 * @return INT_VALUE_SUCCESS or INT_VALUE_ERROR
	 */
	public synchronized static int getResultValue(boolean bResult) {
		return bResult ? Constants.INT_VALUE_SUCCESS : Constants.INT_VALUE_ERROR;
	}
	
	/**
	 * Gets a today day of week
	 * 
	 * @return
	 */
	public static short getCurrentDayType() {
		short res = Constants.DAYS_WEEKDAY;
		
		switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SUNDAY:
				res = Constants.DAYS_SUN;
				break;
	
			case Calendar.SATURDAY:
				res = Constants.DAYS_SAT;
				break;
				
			default:
				res = Constants.DAYS_WEEKDAY;
				break;
		}
		
		return res;
	}
}