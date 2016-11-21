package com.redblaster.hsl.common;

import com.redblaster.hsl.main.R;

/**
 * Holds all error messages in one place
 * 
 * @author Ilja Hamalainen
 *
 */
public enum ErrorMessage {
	NO_SDCARD,
	DATABASE_IS_CORRUPTED,
	DATABASE_FILE_IS_ABSENT,
	CANT_OPEN_DATABASE,
	SQL_ERROR,
	OTHER_ERROR,
	DATABASE_EMPTY;
	
	public int toInt() {
		int ret = -1;
		switch(this) {
			case NO_SDCARD:
				ret = Constants.DB_ERROR_NO_SDCARD;
				break;
			case DATABASE_FILE_IS_ABSENT:
				ret = Constants.DB_ERROR_DATABASE_FILE_IS_ABSENT;
				break;
			case DATABASE_IS_CORRUPTED:
				ret = Constants.DB_ERROR_DATABASE_IS_CORRUPTED;
				break;
			case CANT_OPEN_DATABASE:
				ret = Constants.DB_ERROR_OPEN_DATABASE;
				break;
			case SQL_ERROR:
				ret = Constants.DB_ERROR_OTHER;
				break;
			case DATABASE_EMPTY:
				ret = Constants.DB_ERROR_DATABASE_IS_EMPTY;
				break;
		}
		return ret;
	}
	
	public static ErrorMessage getByID(final int id) {
		ErrorMessage errorMsg = null;
		
		switch(id) {
			case Constants.DB_ERROR_DATABASE_FILE_IS_ABSENT:
				errorMsg = ErrorMessage.DATABASE_FILE_IS_ABSENT;
				break;
				
			case Constants.DB_ERROR_DATABASE_IS_CORRUPTED:
				errorMsg = ErrorMessage.DATABASE_IS_CORRUPTED;
				break;
				
			case Constants.DB_ERROR_NO_SDCARD:
				errorMsg = ErrorMessage.NO_SDCARD;
				break;
				
			case Constants.DB_ERROR_OPEN_DATABASE:
				errorMsg = ErrorMessage.CANT_OPEN_DATABASE;
				break;
				
			case Constants.DB_ERROR_OTHER:
				errorMsg = ErrorMessage.OTHER_ERROR;
				break;
				
			case Constants.DB_ERROR_DATABASE_IS_EMPTY:
				errorMsg = ErrorMessage.DATABASE_EMPTY;
				break;
		}
		
		return errorMsg;
	}
	
	public int getMessageResource() {
		int ret = -1;
		switch(this) {
			case NO_SDCARD:
				ret = R.string.err_msg_no_sdcard;
				break;
			case DATABASE_FILE_IS_ABSENT:
				ret = R.string.err_msg_db_file_adsent;
				break;
			case DATABASE_IS_CORRUPTED:
				ret = R.string.err_msg_db_corrupted;
				break;
			case CANT_OPEN_DATABASE:
				ret = R.string.err_msg_cant_open_db;
				break;
			case SQL_ERROR:
				ret = R.string.err_msg_sql_error;
				break;
			case OTHER_ERROR:
				ret = R.string.error;
				break;
			case DATABASE_EMPTY:
				ret = R.string.err_msg_db_is_empty;
				break;
				
				
		}
		return ret;
	}
}