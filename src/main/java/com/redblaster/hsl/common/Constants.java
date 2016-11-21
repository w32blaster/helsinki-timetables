package com.redblaster.hsl.common;

/**
 * Constants.
 * 
 * @author Ilja Hamalainen
 * 
 */
public interface Constants {

	boolean IS_LOG_ENABLED = false;

	int INT_CACHE_SIZE = 10;

	String STR_FOLDER_NAME = "hsl";
	String STR_ARCHIVE_NAME = "hsl.gz";
	String STR_VEHICLE_TYPE = "vehicle_type";
	String STR_TRANSPORT_NUMBER_ID = "transport_number_id";
	String STR_TRANSPORT_NUMBER_NAME = "transport_number_name";
	String STR_TRIP_ID = "trip_id";
	String STR_HEADER_NAME = "header_name";
	String STR_TRIP_NAME = "trip_name";
	String STR_TRIP_ONE_NAME = "trip_one_name";
	String STR_STATION_ID = "station_id";
	String STR_STATION_START_ID = "station_start_id";
	String STR_STATION_NAME = "station_name";
	String STR_STATION_TIME = "station_time";
	String STR_STATION_LETTER = "station_letter";
	String STR_BOOKMARK_ID = "bookmark_id";
	String STR_BOOKMARK_NAME = "bookmark_name";

	// values are from reitiopas API site
	int INT_VEHICLE_TYPE_BUS = 1;
	int INT_VEHICLE_TYPE_TRAIN = 2;
	int INT_VEHICLE_TYPE_METRO = 3;
	int INT_VEHICLE_TYPE_TRAM = 4;
	int INT_VEHICLE_TYPE_FERRY = 5;
	int INT_VEHICLE_TYPE_U_LINES = 6;

	String STR_EMPTY = "";
	String STR_SPACE = " ";
	String STR_ZERO = "0";

	int BREADCRUMBS_FIRST_ITEM = 1;
	int BREADCRUMBS_MIDDLE_ITEM = 2;
	int BREADCRUMBS_LAST_ITEM = 3;

	short DAYS_WEEKDAY = 0;
	short DAYS_SAT = 1;
	short DAYS_SUN = 2;

	// database
	String DATABASE_NAME = "helsinki_timetables";
	String KEY_ROWID = "_id";
	String KEY_NAME = "name";
	String KEY_CITY_ID = "city_id";
	String KEY_TRIP_ID = "trip_id";
	String KEY_STATION_ID = "station_id";
	String KEY_TIME = "time";
	String KEY_INDEX = "idx";
	String KEY_TRANSPORT_NUMBER_ID = "transport_number_id";
	String KEY_TRANSPORT_MODE_ID = "transport_mode_id";
	String KEY_VEHICLE_TYPE_ID = "vehicle_type_id";
	String KEY_STATION_START = "station_id_start";
	String KEY_STATION_END = "station_id_end";
	String KEY_DAY = "day";
	String KEY_COMPANY_ID = "company_id";
	String KEY_SERVICE_NAME = "service_name";
	String KEY_IMAGE = "image";
	String KEY_VALUE = "value";
	String KEY_BOOKMARK_ID = "bookmark_id";

	int RECORDS_PER_ROW = 4;
	int BOOKMARKS_PER_TRIP = 5;

	short DOWNLOAD_AUTOMATICALLY = 1;
	short DOWNLOAD_MANUALLY = 2;

	int MENU_ADD_BOOKMARK = 1;
	int MENU_DELETE_BOOKMARK = 2;
	int MENU_UPDATE = 3;

	String URL_SITE = "http://hsl.2rooms.net/downloads/";
	String URL_METADATA_FILE = "version.xml";
	String URL_ARCHIVE_URL = "ga/download.php?locale=%1$s&res=%2$dx%3$d";

	String STR_HANDLER_MESSAGE_COMMAND = "command";
	String STR_HANDLER_MESSAGE_VALUE = "value";
	String STR_HANDLER_MESSAGE_LABEL = "label";

	int INT_HM_REQUEST_METADATA = 1;
	int INT_HM_AVAILABLE_DISC_SPACE = 2;
	int INT_HM_DOWNLOAD_ARCHIVE = 3;
	int INT_HM_VERIFY_CHECKSUM = 4;
	int INT_HM_UNPACK_ARCHIVE = 5;
	int INT_HM_FINISH = 6;
	int INT_HM_OLD_DATABASE = 7;
	int INT_HM_CACHE_OLD_BOOKMARKS = 8;
	int INT_HM_CORRECT_BOOKMARK_IDS = 9;

	int INT_VALUE_SUCCESS = -3;
	int INT_VALUE_ERROR = -2;
	int INT_VALUE_IN_PPROGRESS = -1;

	int DB_ERROR_ALL_RIGHT = -1;
	int DB_ERROR_NO_SDCARD = 1;
	int DB_ERROR_DATABASE_IS_CORRUPTED = 2;
	int DB_ERROR_DATABASE_FILE_IS_ABSENT = 3;
	int DB_ERROR_OPEN_DATABASE = 4;
	int DB_ERROR_OTHER = 5;
	int DB_ERROR_DATABASE_IS_EMPTY = 6;
	
	byte BYTE_LOADER = 1;
	byte BYTE_ICON_DB = 2;
	byte BYTE_ERR = 3;
	byte BYTE_ICON_WARN = 4;
	
}