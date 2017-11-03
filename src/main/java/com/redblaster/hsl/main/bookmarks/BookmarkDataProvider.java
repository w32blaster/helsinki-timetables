package com.redblaster.hsl.main.bookmarks;

import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.redblaster.hsl.common.LoadableSectorsBuilder;
import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapter;
import com.redblaster.hsl.common.DBAdapterExternal;
import com.redblaster.hsl.common.Utils;
import com.redblaster.hsl.dto.BookmarkDTO;
import com.redblaster.hsl.dto.TransportLineDTO;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.main.R;

import com.redblaster.hsl.common.ErrorMessage;

/**
 * This class provides only data for the bookmarks. This class doesn't know, where it is used -
 * in the widget or in Activity.
 * 
 * @author Ilja Hamalainen
 *
 */
public class BookmarkDataProvider {
	private Context context;
	private boolean isWidget = false;
	private boolean isBookmarksExist = true;
	private LoadableSectorsBuilder bBuilder;
	private BookmarkDTO bookmarkDto;
	private Cursor curs = null;
	private DBAdapter db;
	
	public BookmarkDataProvider(Context ctx, boolean bIsWidget) {
		this.context = ctx;
		this.isWidget = bIsWidget;
		bookmarkDto = new BookmarkDTO(-1l, Constants.STR_EMPTY);
		
		if (!this.isWidget) {
			bBuilder = new LoadableSectorsBuilder(this.context);
		}
	}
	
	/**
	 * Opens internal database, storing the bookmarks
	 */
	public void openInternalDatabase() {
		this.db = new DBAdapter(this.context);
		this.db.open();
	}
	
	/**
	 * Closes internal database, storing the bookmarks
	 */
	public void closeInternalDatabase() {
		this.db.close();
	}
	
	/**
	 * Returns list of all bookmarks.
	 * Makes query to the internal database
	 */
	public void getBookmarkCommonData(long bookmarkID) {
		curs = db.getAllBookmarksData(bookmarkID);
	}
	
	/**
	 * Closes cursor
	 */
	public void closeCursor() {
		curs.close();
	}
	
	/**
	 * Makes request to the DB for the bookmarks names and draws the tables.
	 * Note, that this method don't retrieves all data for the each bookmarks, 
	 * but only names and IDs. Times will be loaded on "expand" button pressing.
	 * 
	 * @return TRUE if at least one bookmark exists, FALSE if there are no bookmarks at all
	 */
	public boolean retrieveOnlyBookmarksNames() {
		
		// fill the table by bookmarks objects
		DBAdapter db = new DBAdapter(this.context);
		db.open();
		Cursor curs = db.getListOfAllBookmarks();
		db.close();
		if (null != curs) {
			final int nBookmarkId = 0;
			final int nBookmarkName = 1;
			final int nBookmarkImages = 2;
			
			if (curs.moveToFirst()) {
				do {
					
					TableLayout table = bBuilder.createNewTable();
					bBuilder.addNewLoadableSection(true, 
							curs.getString(nBookmarkName), 
							curs.getInt(nBookmarkImages),
							geteListenerToExpandBookmark(table, curs.getLong(nBookmarkId)));
					
				} while (curs.moveToNext()); 
			}
		}
		
		final boolean result = curs.getCount() > 0;
		curs.close();
		
		return result;
	}
	
	/**
	 * Return the listener for button. On pressing those button this listener will fired. It will expand collapsed bookmark
	 * 
	 * @param bookmarkID
	 * @return
	 */
	private OnClickListener geteListenerToExpandBookmark(final TableLayout table, final long bookmarkID) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				// set the table (bookmark container). All lines with times will be added to this table
				bBuilder.setCurrentTable(table);
				bBuilder.addLineWithLoadingBarAndString();	
				
				/*
				 * Start new thread to retrieve data.
				 */
				new Thread() {
					
					@Override
					public void run() {
						openInternalDatabase();
						getBookmarkCommonData(bookmarkID);
						closeInternalDatabase();
						
						int result = 0;
						try {
							
							// reset the bookmarkDto, in order to delete previous bookmarks from it
							bookmarkDto = new BookmarkDTO(-1l, Constants.STR_EMPTY);
							
							// and fill it
							getFullBookmarkData();
							
						} catch (DatabaseException e) {
							result = e.getErrorCode();
							Log.e("Error", "DatabaseException");
						}
						
						closeCursor();
						Utils.sendMessage(handler, 0, result);
					}
					
				}.start();
			}
		};
	}
	
	/**
	 * Handler to draw the lines with time, when the full data is loaded.
	 * It looks like "17:52 (65)" and it must be 5 results at all 
	 */
	protected Handler handler = new Handler() {
        
		@Override
        public void handleMessage(Message msg) {
        	int result = msg.getData().getInt(Constants.STR_HANDLER_MESSAGE_VALUE);
        	
        	if (result == 0) {
    			bBuilder.removeLastRowFromTable();
    			bBuilder.setExpandedIcon();
    			
    			BookmarkDTO bookmarkDto = getBookmarkDto();
    			Iterator<TransportLineDTO> it = bookmarkDto.getLinesAsLimitedSortedList().iterator();
    			TransportLineDTO transportLine = null;
    			
    			while(it.hasNext()) {
    				transportLine = it.next();
    				bBuilder.addNewTimeLine(transportLine.getTime(), transportLine.getTransportLine());
    			}	
        	}
        	else {
        		// if something happens, show the error message
    			bBuilder.removeLastRowFromTable();
    			bBuilder.setExpandedIcon();
    			
    			int resMsg = ErrorMessage.getByID(result).getMessageResource();
    			bBuilder.addOneSpannedRowWithErrorMessage(context.getResources().getString(R.string.error) + Constants.STR_SPACE + context.getResources().getString(resMsg));
        	}
        };
	};
	
	/**
	 * Finds a real ID of the current bookmark by sequence number.
	 * Saves result of this query in the global cursor.
	 * Returns array of current and next ID's. It is used to determine, if 
	 * the "next" button must be disabled or not.
	 * 
	 * @param index - sequence index of current bookmark
	 * @return long[]. Array contains two ID's:
	 * 				long[0] = current bookmark ID
	 * 				long[1] = next bookmark ID
	 */
	public long[] getBookmarkBySequenceIndex(int index) {
		long[] arrRes = new long[2];
		arrRes[0] = -1;
		arrRes[1] = -1;
		
		curs = db.getIDofCurrnetAndNextFollowingBookmarks(index);
		if (null != curs) {
			final int nBookmarkID = 0;
			short i = 0;
			if (this.curs.moveToFirst()) {
				do {
					if (i == 0) {
						arrRes[0] = this.curs.getLong(nBookmarkID);
					}
					else if (i == 1) {
						// if this case occurs, then next bookmark is exists
						arrRes[1] = this.curs.getLong(nBookmarkID);
					}
					i++;
				}while (this.curs.moveToNext()); 
			}
			// close the cursor of INNER database
			curs.close();
		}
		return arrRes;
	}
	
	/**
	 * Make sure, that the database is really empty and no one bookmark exists 
	 * 
	 * @return boolean TRUE if database "bookmarks" is empty
	 */
	public boolean isDatabaseReallyEmpty() {
		Cursor curs = db.getListOfAllBookmarks();
		final boolean bRes = (curs.getCount() == 0);
		curs.close();
		return bRes;
	}
	
	/**
	 * Make outer query. It loads all additional data for the derived bookmarks: times,
	 * transport lines...
	 * @throws DatabaseException 
	 */
	public void getFullBookmarkData() throws DatabaseException {
		Cursor cursExt = null;
		
		if (null != this.curs) {
			final int nBookmarkID = 0;
			final int nBookmarkName = 1;
			final int nBookmarkImage = 2;
			final int nBookmarkStationID = 3;
			final int nLinesTrNumberID = 4;
			final int nLinesStationStart = 5;
			
			final String strNow = Utils.getRawCurrentTime();
			final short sDayOfWeek = Utils.getCurrentDayType();
			
			DBAdapterExternal extDb = new DBAdapterExternal(this.context);
			
			if (null != extDb.open()) {
					if (this.curs.moveToFirst()) {

						// set Name and Image, because we want display it even if all transport for this bookmark is not going right now
						this.setNameAndImageToBookmarkDTO(this.curs.getString(nBookmarkName), this.curs.getString(nBookmarkImage));
						
						do {
							cursExt = extDb.getDataForOneBookmark(this.curs.getString(nBookmarkName),
									this.curs.getString(nBookmarkImage),
									this.curs.getLong(nBookmarkID),
									this.curs.getLong(nBookmarkStationID), 
									this.curs.getLong(nLinesTrNumberID), 
									this.curs.getLong(nLinesStationStart),
									strNow,
									sDayOfWeek);
	
							// adds all record to DTO. As result DTO must contain info about current bookmark and some (no more, then 5) time lines
							this.fillDTOwithOneTimeLine(cursExt);
							
					   } while (this.curs.moveToNext());
						
						cursExt.close();
					}
					extDb.close();
					this.curs.close();
			}
			else {
				Log.e("Error","External database is not exists!");
			}
		}
	}
	
	/**
	 * Sets initial name and image to the bookmark DTO
	 * 
	 * @param name
	 * @param image
	 */
	private void setNameAndImageToBookmarkDTO(String name, String strImage) {
		bookmarkDto.setName(name);
		int image = -1;
		try {
			image = Integer.parseInt(strImage);
		} catch (NumberFormatException e) {
			Log.e("ERROR", "BookmarkDataProvider.setNameAndImageToBookmarkDTO(): can't parse from string to int. strImage=" + strImage);
		}
		bookmarkDto.setImage(image);
	}
	
	/**
	 * Adds to the DTO object new transport line
	 * @param curs
	 */
	private void fillDTOwithOneTimeLine(Cursor curs) {
		if (null != curs) {
			try {
				if (curs.moveToFirst()) {
					final int nName = 0;
					final int nImage = 1;
					final int nBookmarkID = 2;
					final int nTime = 3;
					final int nTrLine = 4;
					final int nTrLineID = 5; 
					
					do {
						bookmarkDto.addNewRecod(curs.getLong(nBookmarkID), 
								curs.getString(nName),
								curs.getInt(nImage),
								curs.getString(nTrLine), 
								curs.getString(nTime),
								curs.getLong(nTrLineID));
				    } while (curs.moveToNext());
				}
				curs.close();
			} catch (Exception e) {	
				Log.e("ERROR", "BookmarkDataProvider.fillDTOwithOneTimeLine(): error in loop: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Renders all collected into the BookmarkBuilder bookmarks to the layer
	 * 
	 * @param layoutView - layout render bookmark to
	 */
	public void renderCollectedBookmarks(LinearLayout layoutView) {
		bBuilder.appendView(layoutView);
	}

	/**
	 * @return the bookmarkDto
	 */
	public BookmarkDTO getBookmarkDto() {
		return bookmarkDto;
	}

	/**
	 * @return the isBookmarksExist
	 */
	public boolean isBookmarksExist() {
		return isBookmarksExist;
	}
}