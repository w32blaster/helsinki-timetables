package com.redblaster.hsl.common;

import java.io.File;
import java.util.HashSet;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.util.Log;

import com.redblaster.hsl.exceptions.DatabaseException;

/**
 * Class-adaptor for working with a external read-only database, which must be found on SD-card.
 * This database must contain all data about the Helsinki's routes.
 * 
 * @author Ilja Hamalainen
 *
 */
public class DBAdapterExternal 
{
	private static final String TABLE_CITY = "city";
    private static final String TABLE_STATION = "station";
    private static final String TABLE_POINT = "point";
    private static final String TABLE_TRIP = "trip";
    private static final String TABLE_TRANSPORT_NUMBER = "transport_number";
    private static final String TABLE_TRANSPORT_MODE = "transport_mode";
    //private static final String TABLE_VEHILE_TYPE = "vehicle_type";
    
    private final Context context;     
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    /**
     * Constructor
     * @param ctx
     */
    public DBAdapterExternal(Context ctx) 
    {
    	this.context = ctx;
        DBHelper = new DatabaseHelper();
    }
 
    public static class DatabaseHelper {
		private SQLiteDatabase db;
		
		/**
		 * Open connection
		 * @return
		 */
		public SQLiteDatabase open() throws DatabaseException{
			File dbFile = null;
			
			if (Utils.isSDcardMounted()) {
				dbFile = new File(Utils.getHSLfolderName(), Constants.DATABASE_NAME + ".sqlite");
			} else {
				Log.e("ERROR", "No sd card");
				throw new DatabaseException("No sd card", Constants.DB_ERROR_NO_SDCARD);
			}

			if (!dbFile.canRead()) {
                throw new DatabaseException("Can't read the database file (although it exists)", Constants.DB_ERROR_DATABASE_FILE_IS_ABSENT);
            }

			if (dbFile.exists()) {
				db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, db.OPEN_READONLY);
			} else {
				throw new DatabaseException("No db file. Path: " + dbFile.getAbsolutePath(), Constants.DB_ERROR_DATABASE_FILE_IS_ABSENT);
			}
			return db;
		}
		
		/**
		 * Close connection
		 */
		public void close() {
			db.close();
		}
    }
    
    /**
     * Opens the database
     * @return
     * @throws SQLException
     * @throws DatabaseException 
     */
    public DBAdapterExternal open() throws DatabaseException 
    {
        try {
			db = DBHelper.open();
		} catch (SQLException e) {
			throw new DatabaseException("Error in database opening (" + e.getMessage() + ")", e, Constants.DB_ERROR_OPEN_DATABASE);
		}
        return (null != db ? this : null);
    }

    /**
     * Closes the database connection  
     */
    public void close() 
    {
        DBHelper.close();
    }
    
	/**
	 * Function executes query, which must return only one string field.
	 * 
	 * @param query
	 * @return result {Name}
	 * @throws DatabaseException
	 */
	private String _executeQueryForSingleField(String query) throws DatabaseException {
		Cursor curs = _executeRawQuery(query);
    	String strName = Constants.STR_EMPTY;
    	if (null != curs) {
    		if (curs.moveToFirst()) {
    			strName = curs.getString(0);
    		}
    		curs.close();
    	}
    	return strName;
	}
	
    /**
     * Common part. Just executes a raw query
     * @param strQuery
     * @return
     */
    private Cursor _executeRawQuery(String strQuery) throws DatabaseException{
    	Cursor mCursor = null;
    	Utils.log("Query: " + strQuery);
    	try {
			mCursor = db.rawQuery(strQuery, null);
	        
			if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
			
		} catch (SQLiteDatabaseCorruptException e) {
			throw new DatabaseException("Database is corrupted", e, Constants.DB_ERROR_DATABASE_IS_CORRUPTED);
			
		} catch (Exception e) {	
			throw new DatabaseException("Error in db", e, Constants.DB_ERROR_OTHER);
		}
        return mCursor;
    }
    
    /**
     * Get all trips by the Vehicle type and by transport mode
     * 
     * @param vehicleTypeId
     * @return
     * @throws SQLException
     * @throws DatabaseException 
     */
    public Cursor getAllTripsByVehicleTypeAndTransportMode(long vehicleTypeId, long transportModeID) throws SQLException, DatabaseException 
    {
    	StringBuilder query = new StringBuilder("select tn." + Constants.KEY_ROWID + ", tn.name, tm.name, tn.service_name from ");
    	query.append(TABLE_TRANSPORT_NUMBER);
    	query.append(" as tn INNER JOIN ");
    	query.append(TABLE_TRANSPORT_MODE);
    	query.append(" as tm ON tm." + Constants.KEY_ROWID + " = tn.");
    	query.append(Constants.KEY_TRANSPORT_MODE_ID); 
    	query.append(" WHERE tm.");
    	query.append(Constants.KEY_VEHICLE_TYPE_ID);
    	query.append(" = ");
    	query.append(vehicleTypeId);
    	query.append(" AND tm.");
    	query.append(Constants.KEY_ROWID);
    	query.append(" = ");
    	query.append(transportModeID);
    	query.append(" ORDER BY tm." + Constants.KEY_ROWID + ", tn.name");

        return _executeRawQuery(query.toString());
    }
    
    /**
     * Gets the list of all used Transport modes 
     * 
     * @param rowId
     * @return Cursor
     * 
     * @throws SQLException
     * @throws DatabaseException
     */
    public Cursor getAllTransportModesByVehicleType(long rowId) throws SQLException, DatabaseException 
    {
    	StringBuilder query = new StringBuilder("SELECT tn." + Constants.KEY_ROWID + ", tm." + Constants.KEY_ROWID + ", tm.name, tn.service_name FROM ");
    	query.append(TABLE_TRANSPORT_MODE);
    	query.append(" as tm INNER JOIN ");
    	query.append(TABLE_TRANSPORT_NUMBER);
    	query.append(" as tn ON tm." + Constants.KEY_ROWID + " = tn.");
    	query.append(Constants.KEY_TRANSPORT_MODE_ID); 
    	query.append(" WHERE tm.");
    	query.append(Constants.KEY_VEHICLE_TYPE_ID);
    	query.append(" = ");
    	query.append(rowId);
    	query.append(" GROUP BY tm." + Constants.KEY_ROWID);
    	query.append(" ORDER BY tm." + Constants.KEY_ROWID + ", tn.name");

        return _executeRawQuery(query.toString());
    }

    /**
     * Returns a list of all first letters
     * @return
     * @throws SQLException
     * @throws DatabaseException 
     */
    public Cursor getAllLettersOfStations() throws SQLException, DatabaseException 
    {
    	final String queryLetter = "select substr(" + Constants.KEY_NAME + ", 1, 1) as fl from " + TABLE_STATION + " GROUP BY fl ORDER BY fl";
        return _executeRawQuery(queryLetter);
    }
    
    /**
     * Returns a list of all first letters
     * @return
     * @throws SQLException
     * @throws DatabaseException 
     */
    public Cursor getAllStationsWithOneLetter(final String strLetter) throws SQLException, DatabaseException 
    {
    	final String queryLetter = "select * from station where name LIKE '" + strLetter + "%' GROUP BY name ORDER BY name LIMIT 30";
    	return _executeRawQuery(queryLetter);
    }   
    
    /**
     * Get all directions (normally, two lines) for one transport line.
     * 
     * @param transportNumberId ID of given transport line
     * @return cursor
     * @throws SQLException
     * @throws DatabaseException 
     */
    public Cursor getAllDirectionsForGivenTransportLine(long transportNumberId) throws SQLException, DatabaseException 
    {
    	final String query = "select t." + Constants.KEY_ROWID + ", tn." + Constants.KEY_SERVICE_NAME + ", s1.name, s2.name, s1." + Constants.KEY_ROWID + " from " + TABLE_TRIP + " as t " +
				" INNER JOIN " + TABLE_STATION + " as s1 ON s1." + Constants.KEY_ROWID + " = t." + Constants.KEY_STATION_START +
				" INNER JOIN " + TABLE_STATION + " as s2 ON s2." + Constants.KEY_ROWID + " = t." + Constants.KEY_STATION_END +
				" INNER JOIN " + TABLE_TRANSPORT_NUMBER + " AS tn ON tn." + Constants.KEY_ROWID + " = t." + Constants.KEY_TRANSPORT_NUMBER_ID +
				" WHERE t." + Constants.KEY_TRANSPORT_NUMBER_ID + " = " + transportNumberId +
				" GROUP BY " + Constants.KEY_STATION_START + " LIMIT 2";	
        return _executeRawQuery(query);
    }
    
    /**
     * Get all stations for one transport line.
     * 
     * @param tripID ID of given trip
     * @return
     * @throws SQLException
     * @throws DatabaseException 
     */
    public Cursor getAllStationForGivenTransportLine(long tripID) throws SQLException, DatabaseException 
    {
    	final String query = "select s." + Constants.KEY_ROWID + ", s.name  from " + TABLE_POINT + " as tp " +
    			" INNER JOIN " + TABLE_STATION + " as s ON s." + Constants.KEY_ROWID + " = tp." + Constants.KEY_STATION_ID + " " +
    			" WHERE tp." + Constants.KEY_TRIP_ID + " = " + tripID +
    			" ORDER BY tp." + Constants.KEY_INDEX + ", tp." + Constants.KEY_TRIP_ID;
    	return _executeRawQuery(query);
    }
    
    /**
     * Get all times for one station by the transport number ID and station ID
     * 
     * @param tripID ID of given trip
     * @return
     * @throws SQLException
     * @throws DatabaseException 
     */
    public Cursor getAllTimesForGivenStation(long transportNumberID, long stationId, long stationStartID) throws SQLException, DatabaseException 
    {
    	String query = "select t." + Constants.KEY_ROWID + ", p." + Constants.KEY_TIME + ", t.is_workday, t.is_saturday, t.is_sunday from " + TABLE_POINT + " as p " +
    			" INNER JOIN " + TABLE_STATION + " as s ON s." + Constants.KEY_ROWID + " = p." + Constants.KEY_STATION_ID +
    			" INNER JOIN " + TABLE_TRIP + " as t ON t." + Constants.KEY_ROWID + " = p." + Constants.KEY_TRIP_ID +
    			" WHERE s." + Constants.KEY_ROWID + " = " + stationId + " and t." + Constants.KEY_TRANSPORT_NUMBER_ID + " = " + transportNumberID +
    			" AND t.station_id_start = " + stationStartID +
    			" ORDER BY p." + Constants.KEY_TIME;
    	return _executeRawQuery(query);
    }
    
    /**
     * Get all times for one station by the transport number ID and station ID
     * 
     * @param tripID ID of given trip
     * @return
     * @throws SQLException
     * @throws DatabaseException 
     */
    public Cursor getAllStationsInOneTrip(long tripId) throws SQLException, DatabaseException 
    {
    	String query = "select p." + Constants.KEY_TIME + ", s." + Constants.KEY_NAME +
    			" FROM " + TABLE_POINT + " as p " +
    			" INNER JOIN " + TABLE_STATION + " as s ON s." + Constants.KEY_ROWID + " = p." + Constants.KEY_STATION_ID +
    			" WHERE p." + Constants.KEY_TRIP_ID + " = " + tripId + " ORDER BY p." + Constants.KEY_INDEX;
    	return _executeRawQuery(query);
    }
    
    /**
     * Gives all station's ID having the same name, as given station has.
     * 
     * @param tripID ID of given trip
     * @return
     * @throws SQLException
     * @throws DatabaseException 
     */
    public Cursor getStationIDwithASingleName(long stationId) throws SQLException, DatabaseException 
    {
    	String query = "SELECT " + Constants.KEY_ROWID + ", " + Constants.KEY_NAME + " FROM " + TABLE_STATION + " WHERE " + Constants.KEY_NAME
    		+ " = (SELECT " + Constants.KEY_NAME + " FROM " + TABLE_STATION + " WHERE " + Constants.KEY_ROWID + " = " + stationId + ")";
    	return _executeRawQuery(query);
    }
    
    /**
     * Gets index of the given station during the trip.
     * 
     * @param transportNumberId
     * @param stationId
     * @param stationIdStart
     * @return index
     * @throws SQLException
     * @throws DatabaseException
     */
    public int getStationIndexByID(long transportNumberId, long stationId, long stationIdStart) throws SQLException, DatabaseException 
    {
    	String query = "select p." + Constants.KEY_INDEX + " from " + TABLE_TRIP + " as t " +
    			" INNER JOIN " + TABLE_POINT + " AS p ON t." + Constants.KEY_ROWID + "=p." + Constants.KEY_TRIP_ID + 
    			" INNER JOIN " + TABLE_STATION + " AS s ON s." + Constants.KEY_ROWID + "=p." + Constants.KEY_STATION_ID +
    			" INNER JOIN " + TABLE_STATION + " AS s1 ON s1." + Constants.KEY_ROWID + "=t." + Constants.KEY_STATION_START +
    			" WHERE t." + Constants.KEY_TRANSPORT_NUMBER_ID + " = " + transportNumberId + 
    			" AND s." + Constants.KEY_ROWID + "=" + stationId + " AND s1." + Constants.KEY_ROWID + "=" + stationIdStart + " LIMIT 1";
    	
    	Cursor curs = _executeRawQuery(query);
    	int nIdx = 0;
    	if (null != curs) {
    		if (curs.moveToFirst()) {
    			nIdx = curs.getInt(0);
    		}
    		curs.close();
    	}
    	return nIdx;
    }
    
    /**
     * Returns the city name, where is situated given station.
     * 
     * @param stationId
     * @return
     * @throws SQLException
     * @throws DatabaseException
     */
    public String getStationCityNameByID(long stationId) throws SQLException, DatabaseException 
    {
    	String query = "SELECT c." + Constants.KEY_NAME + " FROM " + TABLE_CITY
			    	+ " as c INNER JOIN " + TABLE_STATION + " AS s ON s." + Constants.KEY_CITY_ID + "=c." + Constants.KEY_ROWID
			    	+ " WHERE s." + Constants.KEY_ROWID + " = " + stationId + " LIMIT 1";
    	return _executeQueryForSingleField(query);
    }

    
    /**
     * Returns valid part of SQL, containing condition about the current day of week.
     * Don't forget, that the table "TRIP" must have an alias "t"
     * 
     * @param dayOfWeek
     * @return string Part of SQL
     */
    private String getValidConditionForDayOfWeek(short dayOfWeek) {
    	String ret = null;
    	// TODO: refactoring. Store columns in constatns
    	if (dayOfWeek == Constants.DAYS_SAT) {
    		ret = " AND t.is_saturday = 1 ";
    	}
    	else if (dayOfWeek == Constants.DAYS_SUN) {
    		ret = " AND t.is_sunday = 1 ";
    	}
    	else if (dayOfWeek == Constants.DAYS_WEEKDAY) {
    		ret = " AND t.is_workday = 1 ";
    	}
    	
    	return ret;
    }
    
    /**
     * 
     * 
     * @param sb String Buffer query appends to
     * @param name - bookmarks name. Just to adds to query to retrieve on the layout rendering
     * @param image - image name. The same as name
     * @param stationID 
     * @param transportNumberID
     * @param stationIDstart
     * @param current time. Time must be derived using the android.text.format.Time class, because "now" of SQlite show time without repairing of TimeZone. Additionally, Time() class is faster.
     * @throws DatabaseException 
     */
    public Cursor getDataForOneBookmark(String name, String image, long bookmarkID, long stationID, long transportNumberID, long stationIDstart, String strCurrentTime, short sDayOfWeek) throws DatabaseException {
    	StringBuilder sb = new StringBuilder();
    	sb.append("SELECT '").append(name).append("' as bname, '").append(image).append("', '").append(bookmarkID).append("', p.").append(Constants.KEY_TIME).append(", tn.").append(Constants.KEY_NAME).append(", tn.").append(Constants.KEY_ROWID);
    	sb.append(" FROM ").append(TABLE_POINT).append(" as p ");
    	sb.append(" INNER JOIN ").append(TABLE_TRIP).append(" as t ON t.").append(Constants.KEY_ROWID).append(" = p.").append(Constants.KEY_TRIP_ID);
    	sb.append(" INNER JOIN ").append(TABLE_TRANSPORT_NUMBER).append(" AS tn ON tn.").append(Constants.KEY_ROWID).append(" = t.").append(Constants.KEY_TRANSPORT_NUMBER_ID);
    	sb.append(" WHERE p.").append(Constants.KEY_STATION_ID).append(" = ");
    	sb.append(stationID);
    	sb.append(" AND t.").append(Constants.KEY_STATION_START).append(" = ");
    	sb.append(stationIDstart);
    	sb.append(" AND t.").append(Constants.KEY_TRANSPORT_NUMBER_ID).append(" = ");
    	sb.append(transportNumberID);
    	sb.append(" AND p.").append(Constants.KEY_TIME).append(" >= ").append(strCurrentTime);
    	sb.append(getValidConditionForDayOfWeek(sDayOfWeek)).append(" ORDER BY p.").append(Constants.KEY_TIME).append(" LIMIT 5");
    	
    	return _executeRawQuery(sb.toString());
    }
    
    /**
     * Gives all possible trips for a given station. Searching is grouped by name.
     * 
     * @param tripID ID of given trip
     * @return
     * @throws SQLException
     * @throws DatabaseException 
     */
    public Cursor getAllTripsForGivenStation(HashSet<Integer> setIds) throws SQLException, DatabaseException 
    {
    	StringBuilder sbIN = new StringBuilder();
    	for (int id : setIds) {
    		sbIN.append(id);
    		sbIN.append(",");
    	}

    	/*
    	 * For grouping purposes we count the field "fieldForGrouping". It is kind a "hash" for each trip.
    	 * The main criteria is to get unique "hash" value for each transport line going to the one direction.
    	 * 37 here is for decreasing collision possibility (37 is used by tradition in simple hash functions).
    	 * So, the "66A" from Rautatientori and "66A" towards Rautatientori have different hashes. 
    	 */
    	String query = "select tn." + Constants.KEY_ROWID + " as tnID " +
    			", tn." + Constants.KEY_NAME + " as name " +
    			", s1." + Constants.KEY_NAME + " as s1Name " +
    			", s2." + Constants.KEY_NAME + " as s2Name " +
    			", s1." + Constants.KEY_ROWID + " as s1ID " +
    			", t." + Constants.KEY_ROWID + " as _id" +
    			", p." + Constants.KEY_STATION_ID +
    			", tn." + Constants.KEY_SERVICE_NAME +
    			", tn." + Constants.KEY_ROWID + "+s1." + Constants.KEY_ROWID + "+(length(tn." + Constants.KEY_SERVICE_NAME + ")*37) AS fieldForGrouping" +
    			" FROM " + TABLE_POINT + " as p " +
    			" INNER JOIN " + TABLE_TRIP + " as t ON t." + Constants.KEY_ROWID + " = p." + Constants.KEY_TRIP_ID + 
    			" INNER JOIN " + TABLE_TRANSPORT_NUMBER + " as tn ON tn." + Constants.KEY_ROWID + " = t." + Constants.KEY_TRANSPORT_NUMBER_ID +
    			" INNER JOIN " + TABLE_STATION + " as s1 ON s1." + Constants.KEY_ROWID + " = t." + Constants.KEY_STATION_START +
    			" INNER JOIN " + TABLE_STATION + " as s2 ON s2." + Constants.KEY_ROWID + " = t." + Constants.KEY_STATION_END +
    			" WHERE p." + Constants.KEY_STATION_ID + " IN (" + sbIN.substring(0, sbIN.length() - 1) + ") " +
    			" GROUP BY fieldForGrouping " +
    			" ORDER BY tn." + Constants.KEY_NAME;
    	return _executeRawQuery(query);
    }
    
    /**
     * Read names of transport line, station start and station. It is used by bookmark migration.
     * 
     * 
     * @param transportNumberID - transport number ID
     * @param stationStartID - station start ID
     * @param stationID - station ID (where the given bookmark is used)
     * @return
     * @throws DatabaseException 
     */
    public Cursor retrieveBookmarkDataForMigration(long transportNumberID, long stationStartID, long stationID) throws DatabaseException {
    	String query = "select tn.name as tn_name, s.name as start_station_name, s2.name as station_name " +
    			"from trip as t " +
    			"INNER JOIN transport_number as tn ON tn._id = t.transport_number_id " +
    			"INNER JOIN station as s ON t.station_id_start = s._id " +
    			"INNER JOIN point AS p ON p.trip_id = t._id " +
    			"INNER JOIN station AS s2 ON s2._id = p.station_id " +
    			"WHERE tn._id = " + transportNumberID +
    			" AND s._id = " + stationStartID +
    			" AND s2._id = " + stationID +
    			" LIMIT 1";
    	
    	return _executeRawQuery(query);
    }
    
    /**
     * Retrieves IDs for the bookmark migration.
     * 
     * @param strTransporLineName
     * @param stationStartName
     * @param stationName
     * @return
     * @throws DatabaseException
     */
    public Cursor retreiveIDsForBookmarkMigrtion(String strTransporLineName, String stationStartName, String stationName) throws DatabaseException {
    	String query = "select tn._id as tn_id, s._id as start_station_id, s2._id as station_id from trip as t " +
    			"INNER JOIN transport_number as tn ON tn._id = t.transport_number_id " +
    			"INNER JOIN station as s ON t.station_id_start = s._id " +
    			"INNER JOIN point AS p ON p.trip_id = t._id " +
    			"INNER JOIN station AS s2 ON s2._id = p.station_id " +
    			"WHERE tn.name = '" + strTransporLineName + "' " +
    			"AND s.name = '" + stationStartName +"' " +
    			"AND s2.name = '" + stationName + "' " +
    			"GROUP BY tn._id";
    	
    	return _executeRawQuery(query);
    }
 }