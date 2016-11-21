package com.redblaster.hsl.common;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.redblaster.hsl.dto.BookmarkDTO;
import com.redblaster.hsl.dto.TransportLineDTO;

/**
 * Class-adaptor for working with an internal database, i.e which must be found on phone's memory. It stores
 * all settings. Main function of that - storing bookmarks.
 * 
 * @author Ilja Hamalainen
 *
 */
public class DBAdapter 
{
    private static final String DATABASE_NAME = "helsinki_timetables_settings";
    private static final String TABLE_BOOKMARKS = "bookmarks";
    private static final String TABLE_TRANSPORT_LINES = "transport_lines";
    private static final String TABLE_CACHE = "cache";
   
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_CREATE_BOOKMARKS =
        "create table " + TABLE_BOOKMARKS + " (" 
        + Constants.KEY_ROWID + " integer primary key autoincrement, "
        + Constants.KEY_NAME + " VARCHAR(50) not null, "
        + Constants.KEY_IMAGE + " integer not null)";
    
    private static final String DATABASE_CREATE_CACHE =
        "create table " + TABLE_CACHE + " (" 
        + Constants.KEY_ROWID + " integer primary key autoincrement, "
        + Constants.KEY_NAME + " VARCHAR(50) not null, "
        + Constants.KEY_VALUE + " integer not null)";

    private static final String DATABASE_CREATE_TRANSPORT_LINES =
        "create table " + TABLE_TRANSPORT_LINES + " ("
        + Constants.KEY_ROWID + " integer primary key autoincrement, "
        + Constants.KEY_BOOKMARK_ID + " integer not null, "
        + Constants.KEY_TRANSPORT_NUMBER_ID + " integer not null, "
        + Constants.KEY_STATION_START + " integer not null, " 
        + Constants.KEY_STATION_ID + " integer not null)";
    
    private final Context context;     
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    /**
     * Constructor
     * @param ctx
     */
    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

	private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Creates database first time
         */
        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(DATABASE_CREATE_BOOKMARKS);
            db.execSQL(DATABASE_CREATE_TRANSPORT_LINES);
            db.execSQL(DATABASE_CREATE_CACHE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
            
        	List<BookmarkDTO> lstBookmarks = this.dumpBookmarksData(db);
        	
        	db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSPORT_LINES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHE);
            
            onCreate(db);
            
            this.insertBookmarksData(db, lstBookmarks);
            lstBookmarks = null;
        }

        /**
         * Inserts all bookmarks to the database
         * 
         * @param db
         * @param lstBookmarks
         * @return
         */
        private boolean insertBookmarksData(SQLiteDatabase db, List<BookmarkDTO> lstBookmarks) {
        	
        	for(BookmarkDTO b : lstBookmarks) {
        		createBookmark(db, b);
        		Log.d("TRACE", "inserted: " + b.toString());
        	}
        	
			return false;
		}

		/**
         * Dump all bookmark and store them locally as <i>List&lt;Bookmark&gt;</i> collection
         * 
         * @param db
         * @return
         */
    	private List<BookmarkDTO> dumpBookmarksData(SQLiteDatabase db) {
    		
    		Log.d("TRACE", "Retreiving the old bookmarks for the migration");
    		
    		List<BookmarkDTO> lst = new ArrayList<BookmarkDTO>();
    		Cursor cursor = null;
    		String query = "select b." + Constants.KEY_ROWID + ", " +
    				"b." + Constants.KEY_NAME + ", " +
    				"b." + Constants.KEY_IMAGE + ", " +
    				"t." + Constants.KEY_TRANSPORT_NUMBER_ID + ", " +
    				"t." + Constants.KEY_STATION_START + ", " +
    				"t." + Constants.KEY_STATION_ID + " " +
    				"FROM " + TABLE_BOOKMARKS + " AS b " +
    				"INNER JOIN " + TABLE_TRANSPORT_LINES + " AS t ON t." + Constants.KEY_BOOKMARK_ID + " = b." + Constants.KEY_ROWID +
    				" ORDER BY b." + Constants.KEY_ROWID;
    		
    		try {
    			
    			cursor = db.rawQuery(query, null);
    			
    		} catch (Exception e) {	
    			Log.e("ERROR in db","Eror: " + e.getMessage());
    			if (!cursor.isClosed()) cursor.close();
    		} 
    		
    		if (null != cursor && cursor.moveToFirst()) {
    			BookmarkDTO bookmark = null;
    			long lastID = -1;
    			long id;
    			do {
    				id = cursor.getLong(0);
    				if (id != lastID) {
    					if (null != bookmark) lst.add(bookmark);
    					bookmark = new BookmarkDTO(id, cursor.getString(1), cursor.getInt(2)); 
    					lastID = id;
    				}
    				
    				bookmark.addLine(cursor.getLong(3), cursor.getLong(4), cursor.getLong(5));
    				
    			} while (cursor.moveToNext());
    			lst.add(bookmark);
    		}
    		
    		if (Constants.IS_LOG_ENABLED) {
    			// for logging purposes
	    		for(BookmarkDTO b : lst) {
	    			Log.d("Trace", "" + b.toString());
	    		}
    		}
    		
    		if (!cursor.isClosed()) cursor.close();
    		
    		return lst;
    	}
    	
    }
    

	
    /**
     * Opens the database
     * @return
     * @throws SQLException
     */
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    /**
     * Closes the database connection  
     */
    public void close() 
    {
        DBHelper.close();
    }
    
    /**
     * Common part. Executes the query
     * @param strQuery
     * @return
     */
    private Cursor executeRawQuery(String strQuery) {
    	Cursor mCursor = null;
    	Utils.log("Query: " + strQuery);
    	try {
			mCursor = db.rawQuery(strQuery, null);
		} catch (Exception e) {	
			Log.e("ERROR in db","Eror: " + e.getMessage());
		}
		
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Makes a query for the ID of current and following by current bookmarks
     * 
     * @param index
     * @return
     */
    public Cursor getIDofCurrnetAndNextFollowingBookmarks(int index) {
    	return executeRawQuery("select " + Constants.KEY_ROWID + " from bookmarks LIMIT " + index + ",2");
    }
    
    /**
     * Retrieve the list of bookmarks
     * @return
     */
    public Cursor getListOfAllBookmarks() {
    	return executeRawQuery("SELECT * FROM " + TABLE_BOOKMARKS );
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public int getCachedValue(String key) {
    	StringBuffer sb = new StringBuffer();
		sb.append("select ")
		.append(Constants.KEY_VALUE)
		.append(" FROM ")
		.append(TABLE_CACHE)
		.append(" WHERE ")
		.append(Constants.KEY_NAME)
		.append(" = '")
		.append(key)
		.append("' LIMIT 1");
	
		Cursor c = this.executeRawQuery(sb.toString());
		try {
			if (c.moveToFirst()) {
				return c.getInt(0);
			}
			else {
				return -1;
			}
		}
		finally {
			if (null != c) c.close();
		}
    }
    
    /**
     * Saves or updates value in the database
     * 
     * @param key
     * @param value
     * @return
     */
    public boolean saveCachedValue(String key, long value) {
    	if (this.getCachedValue(key) == -1) {
    		// insert new value
        	ContentValues initVal = new ContentValues();
        	initVal.put(Constants.KEY_NAME, key);
        	initVal.put(Constants.KEY_VALUE, value);
        	return (-1 != db.insert(TABLE_CACHE, null, initVal));
    	}
    	else {
    		// update existed value
    		final String strCond = Constants.KEY_NAME + " = '" + key + "' ";
    		ContentValues args = new ContentValues();
    		args.put(Constants.KEY_VALUE, value);
    		return (0 < db.update(TABLE_CACHE, args, strCond, null));
    	}
    }
    
    /**
     * Deletes all bookmarks
     * 
     * @return
     */
    public boolean deleteAllBookmarks() 
    {
    	int nCnt = 0;
    	nCnt += db.delete(TABLE_TRANSPORT_LINES, null, null);
    	nCnt += db.delete(TABLE_BOOKMARKS, null, null);
    	return nCnt > 0;
    }
    
    /**
     * Finds all bookmarks. If ID is not -1, then query returns all records for the given bookmark
     * 
     * @param ID of bookmarks. If -1, then function returns all records
     * @return Cursor
     */
    public Cursor getAllBookmarksData(long ID) {
    	StringBuffer sb = new StringBuffer();
    		sb.append("select b.");
    		sb.append(Constants.KEY_ROWID);
    		sb.append(" as bookmarkID, b.");
    		sb.append(Constants.KEY_NAME);
    		sb.append(", b.");
    		sb.append(Constants.KEY_IMAGE);
    		sb.append(", t.");
    		sb.append(Constants.KEY_STATION_ID);
    		sb.append(", t.");
    		sb.append(Constants.KEY_TRANSPORT_NUMBER_ID);
    		sb.append(", t.");
    		sb.append(Constants.KEY_STATION_START);
    		sb.append(", t.");
    		sb.append(Constants.KEY_ROWID);
    		sb.append(" AS transportLineID FROM ");
    		sb.append(TABLE_BOOKMARKS);
    		sb.append(" as b INNER JOIN ");
    		sb.append(TABLE_TRANSPORT_LINES);
    		sb.append(" as t ON t.");
    		sb.append(Constants.KEY_BOOKMARK_ID);
    		sb.append(" = b.");
    		sb.append(Constants.KEY_ROWID);
    		
    		if (ID > -1) {
    			sb.append(" WHERE bookmarkID=");
    			sb.append(ID);
    		}
    		
    		sb.append(Constants.STR_SPACE);
    		sb.append(" ORDER BY b.");
    		sb.append(Constants.KEY_ROWID);
    	
    	return this.executeRawQuery(sb.toString());
    }
    
    /**
     * Updates bookmark ID's
     * 
     * @param bookmarkLineID
     * @param transportNumberID
     * @param stationIDstart
     * @param stationID
     * @return
     */
	public boolean updateBookmarksIDs(long bookmarkLineID, long transportNumberID, long stationIDstart, long stationID) {
		
    	ContentValues initVal = new ContentValues();
    	initVal.put(Constants.KEY_STATION_ID, stationID);
    	initVal.put(Constants.KEY_TRANSPORT_NUMBER_ID, transportNumberID);
    	initVal.put(Constants.KEY_STATION_START, stationIDstart);
    	
    	return (0 < db.update(TABLE_TRANSPORT_LINES, initVal, Constants.KEY_ROWID + " = ?", new String [] { bookmarkLineID + "" } ));
	}
	
	/**
     * Creates bookmark record
     * 
     * @param BookmarkDTO bookmark object
     * @return created list's ID
     */
    public long createBookmark(BookmarkDTO bookmark) 
    {
		return createBookmark(db, bookmark);
    }
	
    public static long createBookmark(SQLiteDatabase db, BookmarkDTO bookmark) 
    {
    	ContentValues initialValues = new ContentValues();
        initialValues.put(Constants.KEY_NAME, bookmark.getName());
        initialValues.put(Constants.KEY_IMAGE, bookmark.getImage());
        long newBookmarkID = db.insert(TABLE_BOOKMARKS, null, initialValues);
        
        if (-1 != newBookmarkID) {
        	for (TransportLineDTO trip : bookmark.getLines()) {
            	ContentValues initVal = new ContentValues();
            	initVal.put(Constants.KEY_BOOKMARK_ID, newBookmarkID);
            	initVal.put(Constants.KEY_TRANSPORT_NUMBER_ID, trip.getTransportNumberID());
            	initVal.put(Constants.KEY_STATION_ID, trip.getStationID());
            	initVal.put(Constants.KEY_STATION_START, trip.getStationIDstart());
            	if (-1 == db.insert(TABLE_TRANSPORT_LINES, null, initVal)) {
            		newBookmarkID = -1;
            		break;
            	}
        	}
        }
        return newBookmarkID;
    }

    /**
     * Set new custom name and icon for the given bookmark
     * 
     * @param bookmarkId
     * @param icon
     * @param name
     */
	public int updateBookmarksProperties(long bookmarkId, int icon, String name) {
		String strCond = Constants.KEY_ROWID + " = " + bookmarkId + " ";
		ContentValues args = new ContentValues();
        args.put(Constants.KEY_IMAGE, icon);
        args.put(Constants.KEY_NAME, name);
		return db.update(TABLE_BOOKMARKS, args, strCond, null);
	}
    
    /**
     * Deleted the bookmark from the both tables
     * @param bookmark ID
     * @return result
     */
    public boolean deleteBookmark(long rowId) 
    {
    	int nCnt = 0;
    	db.delete(TABLE_TRANSPORT_LINES, Constants.KEY_BOOKMARK_ID + "=" + rowId, null);
    	nCnt = db.delete(TABLE_BOOKMARKS, Constants.KEY_ROWID + 
        		"=" + rowId, null);
    	return nCnt > 0;
    }
	
    public boolean deleteBookmarkTransportLine(long transportLineId, long bookmarkId) 
    {
    	int nCnt = 0;
    	
    	// delete one transport
    	nCnt = db.delete(TABLE_TRANSPORT_LINES, Constants.KEY_ROWID + " = ?", new String [] { transportLineId + "" });
    	//Utils.log("Firstly, deleted " + nCnt + " lines from table " + TABLE_TRANSPORT_LINES + " in inner DB (last deleted ID=" + transportLineId + ")");
    	
    	// how many transports left belonging for this bookmark
    	Cursor cursor = executeRawQuery("SELECT * FROM " + TABLE_TRANSPORT_LINES + " WHERE " + Constants.KEY_BOOKMARK_ID + " = " + bookmarkId);
    	if(!cursor.moveToFirst()) {
    		// if there is no other transports (e.g. bookmark is empty) then remove this bookmark
    		nCnt = db.delete(TABLE_BOOKMARKS, Constants.KEY_ROWID + "= " + bookmarkId, null);
        	//Utils.log("Secondly, bookmark ID=" + bookmarkId + " was fully deleted (removed " + nCnt + " lines)");
    	}
    	
    	return nCnt > 0;
    }
    
    
    /**
     * [DEMO]
     * Creates new list.
     * @param title - list name
     * @param isSex - supports a gender in users's native language
     * @return created list's ID
     */
    @Deprecated
    public long createNewList(String title, boolean isSex) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(Constants.KEY_NAME, title);
        return db.insert(TABLE_BOOKMARKS, null, initialValues);
    }

    
    /**
     * [DEMO]
     * Deleted a list and all its contacts
     * @param rowId
     * @return
     */
    @Deprecated
    public boolean deleteTitle(long rowId) 
    {
    	int nCnt = 0;
    	db.delete(TABLE_TRANSPORT_LINES, Constants.KEY_ROWID + "=" + rowId, null);
    	nCnt = db.delete(TABLE_BOOKMARKS, Constants.KEY_ROWID + 
        		"=" + rowId, null);
    	return nCnt > 0;
    }

	
	/**
	 * [DEMO]
	 * Set setting is_first_time to 0. It means, that Dialog message "Welcome" will never appear
	 */
    @Deprecated
	public void setDonShowMeAgain() {
		String strCond = Constants.KEY_ROWID + "= '" + Constants.BREADCRUMBS_FIRST_ITEM + "'";
		ContentValues args = new ContentValues();
        args.put(Constants.KEY_ROWID, 0);
		db.update(TABLE_BOOKMARKS, args, strCond, null);
	}
}