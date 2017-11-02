package com.redblaster.hsl.common;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.main.R;
import com.redblaster.hsl.main.bookmarks.migration.MigrationBookmark;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.redblaster.hsl.common.Constants.ALLOW_SD_CARD;

/**
 * This class contains all drawing and import logic. Here is tracer (log displaying on screen current operation
 * and its result [OK] or [ERROR] and the download progress bar as well) and downloader.
 * 
 * @author Ilja Hamalainen
 *
 */
/**
 * @author w32blaster
 *
 */
public class DBImporter {
	private final Activity activity;
	private final LinearLayout mainContainer;
	private boolean isUpdate;
	private List<MigrationBookmark> lstBookmarks = null;
	private LinearLayout tableMetadata;
	private Button btnStartImport;
	private Map<String, String> mapMetadata;
	private Boolean isRunning = false;
	private Thread downloadingThread = null;
	
	public DBImporter(Activity act, LinearLayout mainCnt) {
		this.activity = act;
		this.mainContainer = mainCnt;
	}

	/**
	 * Draws default text message and "Download" button.
	 * 
	 * @param lstItems
	 * @param isUpdating - is this the Updating or the the first importing
	 */
	public void init(List<View> lstItems, boolean isUpdating) {
		this.isUpdate = isUpdating;
		
		if (!this.isUpdate) {
			TextView text = new TextView(this.activity.getApplicationContext());
			text.setText(R.string.wizard_page_automatically_imprting_chosen);
			text.setTextColor(ContextCompat.getColor(this.activity, R.color.dark_gray));
			text.setId(R.id.wizard_descr_label_id);
			lstItems.add(text);
		}
		
		// add description text
		TextView text = new TextView(this.activity.getApplicationContext());
		text.setText(R.string.wizard_page_automatically_imprting);
		text.setTextColor(this.activity.getResources().getColor(R.color.dark_gray));
		text.setId(R.id.wizard_descr_label_id);
		lstItems.add(text);
		
		// block with metadata information. Shows information about available database
		lstItems.add(this.createMetadataPreviewBlock());
		
		// button "Download"
		this.btnStartImport = new Button(this.activity.getApplicationContext());
		this.btnStartImport.setText(R.string.wizard_page_three_button_start);
		this.btnStartImport.setOnClickListener(getOnClickListener());
		this.btnStartImport.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this.activity, R.drawable.ic_download), null, null, null);
		this.btnStartImport.setId(R.id.wizard_start_btn_id);
		this.btnStartImport.setCompoundDrawablePadding(10);
        this.btnStartImport.setGravity(Gravity.CENTER);
        this.btnStartImport.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.light_gray));


        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		blp.gravity = Gravity.CENTER;
		blp.setMargins(10, 10, 10, 10);
		this.btnStartImport.setLayoutParams(blp);
		this.btnStartImport.setEnabled(false);
		lstItems.add(this.btnStartImport);
	}
	
	/**
	 * creates table with metadata about the available database ready to be downloaded
	 * 
	 * @return TableLayout table
	 */
	private LinearLayout createMetadataPreviewBlock() {
		this.tableMetadata = new LinearLayout(this.activity.getApplicationContext());
		this.tableMetadata.setBackground(ContextCompat.getDrawable(this.activity, R.drawable.embedder_border));
		this.tableMetadata.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		blp.gravity = Gravity.CENTER;
		blp.setMargins(0, 10, 0, 10);
		this.tableMetadata.setLayoutParams(blp);
		this.tableMetadata.setPadding(0, 0, 0, 10);
		
		// create initial line "loading metadata...". After data will be loaded, this line will be removed
		LinearLayout tr = this.createRowForMetadataTable(this.activity.getResources().getString(R.string.wizard_import_request_metadata), 
				null, Constants.BYTE_LOADER);
		
		this.tableMetadata.addView(tr);

		new Thread() {

			@Override
			public void run() {
				super.run();
			
				MetaData metaDataDownloader = new MetaData();
				mapMetadata = metaDataDownloader.loadMetadata();
				
				Utils.sendMessage(handlerPreloadInfo, Constants.INT_HM_REQUEST_METADATA, Utils.getResultValue(null != mapMetadata && mapMetadata.size() > 0));
			}
			
		}.start();

		return this.tableMetadata;
	}

	/**
	 * Creates one ready row for the table
	 * 
	 * @param partOne
	 * @param partTwo
	 * @return
	 */
	private LinearLayout createRowForMetadataTable(String partOne, String partTwo, byte type) {
		LinearLayout tr = new LinearLayout(this.activity.getApplicationContext());
		tr.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		tr.setLayoutParams(layoutParams);
		
		switch (type) {
			case Constants.BYTE_LOADER:
				
				tr.setPadding(10, 10, 10, 10);
				
				// add progress bar
				ProgressBar pb = new ProgressBar(this.activity.getApplicationContext(), null, android.R.attr.progressBarStyleSmallInverse);
				pb.setLayoutParams(new LinearLayout.LayoutParams(14, 14));
			
				tr.addView(pb);
				tr.addView(this.createTextView(partOne, false, true));
				break;
			
			case Constants.BYTE_ICON_DB:
				
				tr.setPadding(10, 10, 10, 10);
				
				// set icon "Database"
				ImageView icon = new ImageView(this.activity.getApplicationContext());
				icon.setImageDrawable(ContextCompat.getDrawable(this.activity, R.drawable.ic_database));

				tr.addView(icon);
				
				tr.addView(this.createTextView(partOne, false, true));
				break;
				
			case Constants.BYTE_ICON_WARN:
				
				tr.setPadding(10, 20, 10, 10);
				
				// set icon "Warning"
				ImageView iconWarn = new ImageView(this.activity.getApplicationContext());
				iconWarn.setImageDrawable(ContextCompat.getDrawable(this.activity, R.drawable.ic_alert));
				tr.addView(iconWarn);
				
				TextView txt = this.createTextView(partOne, false, true);
				txt.setTextColor(ContextCompat.getColor(this.activity, R.color.red_warn));
				tr.addView(txt);
				break;
			
			case Constants.BYTE_ERR:
				tr.setPadding(10, 10, 10, 10);
				
				// show error message
				TextView t = this.createTextView(partOne, false, true);
				t.setTextColor(ContextCompat.getColor(this.activity, R.color.red));
				t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				tr.addView(t);
				break;
			
			default:
				tr.setPadding(10, 0, 10, 0);
				tr.addView(this.createTextView(partOne, false, true));
				tr.addView(this.createTextView(partTwo, false, false));
				break;
		}
		
		return tr;
	}

	/**
	 * Creates the Text view with all necessary styles and adds it to the Table Row
	 * 
	 * @param value
	 */
	private TextView createTextView(String value, boolean isColSpan, boolean isBold) {
		TextView t = new TextView(this.activity.getApplicationContext());
		t.setText(value);
		t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		t.setTextColor(ContextCompat.getColor(this.activity, R.color.dark_gray));
		t.setPadding(10, 0, 0, 0);
		
		if (isBold) {
			t.setTypeface(null,Typeface.BOLD);
		}
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		t.setLayoutParams(lp);
		
		return t; 
	}
	
	/**
	 * Handler for thread to communicate with layout.
	 * Called when metadata loader succeeds
	 * 
	 */
	private Handler handlerPreloadInfo = new Handler() {
		
		@Override
        public void handleMessage(Message msg) {
			//final int command = msg.getData().getInt(Constants.STR_HANDLER_MESSAGE_COMMAND);
        	final int value = msg.getData().getInt(Constants.STR_HANDLER_MESSAGE_VALUE);
        	
        	// clear table
        	tableMetadata.removeAllViews();

    		if (value == Constants.INT_VALUE_SUCCESS) {    			
    			try {
    				
    				// print header "Information about available database:"
    				tableMetadata.addView(createRowForMetadataTable(activity.getResources().getString(R.string.metadata_header),
    						null, Constants.BYTE_ICON_DB));
					
    				// Print date:
    				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
    			    Date date = (Date) formatter.parse(mapMetadata.get("date-export"));
    			    String str = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault()).format(date);
    			    
    			    tableMetadata.addView(createRowForMetadataTable(activity.getResources().getString(R.string.metadata_export_date), str, (byte)0));
    			    
    			    // Print size:
    			    tableMetadata.addView(createRowForMetadataTable(activity.getResources().getString(R.string.metadata_db_size),
    			    		mapMetadata.get("size-db-h"), (byte)0));
    			    
    			    // is it recommended to download the current database
    			    final boolean isRecommended = Boolean.valueOf(mapMetadata.get("recommended"));
    			    if (!isRecommended) {
    			    	// error message
    			    	final String strErrMessage = String.format(activity.getResources().getString(R.string.metadata_warn_size_big), TextUtils.htmlEncode(mapMetadata.get("error-message")));
    			    	tableMetadata.addView(createRowForMetadataTable(strErrMessage, null, Constants.BYTE_ICON_WARN));
    			    }
    			    
    			    // enable button
    			    btnStartImport.setEnabled(true);
					
				} catch (ParseException e) {
					Log.e("ERROR","DBImporter, handlerPreloadInfo. Error: " + e.getMessage());
				}
    			
        	}
        	else {
        		LinearLayout tr = createRowForMetadataTable(activity.getResources().getString(R.string.metadata_err), null, Constants.BYTE_ERR);
    			tableMetadata.addView(tr);
        	}

		}
	};
		
	/**
	 * Callback function. 
	 * It is the main action method containing all importing/updating flow.  
	 * 
	 * Fires on pressing "Download" button and runs tracer
	 * 
	 * @return Listener
	 */
	private OnClickListener getOnClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {

				// before begin, request user permissions to write to the SD card
				if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

					// don't have permissions to access SD card. Show request and cancel so far. Later we can do it again.
					ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ALLOW_SD_CARD);
					return;
				}

				LinearLayout container = getMainContainer();
				container.removeAllViews();
				setIsRunning(true);
				
				downloadingThread = new Thread() {

	    			/**
	    			 * Runs the thread
	    			 */
	    			public void run() {
                        try{
                        	
                        	DatabaseDownloader dd = new DatabaseDownloader();

                        	// make request to the server for metadata 
                        	Utils.sendMessage(handler, Constants.INT_HM_REQUEST_METADATA, Constants.INT_VALUE_IN_PPROGRESS);
                        	boolean result = dd.getMetadata();
                        	Utils.sendMessage(handler, Constants.INT_HM_REQUEST_METADATA, Utils.getResultValue(result));
                        	
                        	if (result) {

	                        	if (isUpdate) {
	                        		
		                    		// Retrieve old all bookmarks and store them to the local class variable
	                        		Utils.sendMessage(handler, Constants.INT_HM_CACHE_OLD_BOOKMARKS, Constants.INT_VALUE_IN_PPROGRESS);
	                    			result = retrieveBookmarks();
	                    			Utils.sendMessage(handler, Constants.INT_HM_CACHE_OLD_BOOKMARKS, Utils.getResultValue(result));

	                    			// delete old database file from the file system
	                        		File gzFile = new File(Utils.getHSLfolderName(), Constants.DATABASE_NAME + ".sqlite");
		                    		if (gzFile.exists()) {
		                    			Utils.sendMessage(handler, Constants.INT_HM_OLD_DATABASE, Constants.INT_VALUE_IN_PPROGRESS);
		                    			result = gzFile.delete();
		                    			Utils.sendMessage(handler, Constants.INT_HM_OLD_DATABASE, Utils.getResultValue(result));
		                    		}
	                        	}
	                        	
	                        	if (result) {
		                        	// Check for available free space on the SD card
		                        	Utils.sendMessage(handler, Constants.INT_HM_AVAILABLE_DISC_SPACE, Constants.INT_VALUE_IN_PPROGRESS);
		                        	if (!dd.checkAvailableDiscSpace()) {
		                        		Utils.sendMessage(handler, Constants.INT_HM_AVAILABLE_DISC_SPACE, Constants.INT_VALUE_ERROR);
		                        		
		                        		// exit with error message
		                        		Utils.sendMessage(handler, Constants.INT_HM_FINISH, Constants.INT_VALUE_ERROR);
		                        	}
		                        	else {
		                        		Utils.sendMessage(handler, Constants.INT_HM_AVAILABLE_DISC_SPACE, Constants.INT_VALUE_SUCCESS);
		                        		
		                        		// Download main hsl.gz archive
		                        		Utils.sendMessage(handler, Constants.INT_HM_DOWNLOAD_ARCHIVE, Constants.INT_VALUE_IN_PPROGRESS);
		                        		result = dd.downloadDatabaseArchieve(this, handler, activity.getWindowManager());
		                        		
		                        		if (result && !this.isInterrupted()) {
		                        			Utils.sendMessage(handler, Constants.INT_HM_DOWNLOAD_ARCHIVE, Constants.INT_VALUE_SUCCESS);
		                        			
		                        			// verify it
		                            		Utils.sendMessage(handler, Constants.INT_HM_VERIFY_CHECKSUM, Constants.INT_VALUE_IN_PPROGRESS);
		                            		result = dd.checkTheChecksumOfDownloadedArhcive(this);
		                                    Utils.sendMessage(handler, Constants.INT_HM_VERIFY_CHECKSUM, Utils.getResultValue(result));
		                                    
		                                    if (result  && !this.isInterrupted()) {
		                                    	// unpack it
		                                    	Utils.sendMessage(handler, Constants.INT_HM_UNPACK_ARCHIVE, Constants.INT_VALUE_IN_PPROGRESS);
		                                    	result = dd.unpackArchive(this);
		                                    	Utils.sendMessage(handler, Constants.INT_HM_UNPACK_ARCHIVE, Utils.getResultValue(result));

		                                    	if (result  && !this.isInterrupted()) {
		                                    		if (isUpdate) {

		                                    			// Correct the bookmarks
		                                    			Utils.sendMessage(handler, Constants.INT_HM_CORRECT_BOOKMARK_IDS, Constants.INT_VALUE_IN_PPROGRESS);
		                                    			final int nCorrectedCnt = correctBookmarkIDs();
		                                    			final String customLabel = "[" + nCorrectedCnt + "/" + lstBookmarks.size() + "]";
		                                    			Utils.sendMessage(handler, Constants.INT_HM_CORRECT_BOOKMARK_IDS, Utils.getResultValue(nCorrectedCnt != 0), customLabel);
		                                    		}

		                                    		// Finish
		                                    		Utils.sendMessage(handler, Constants.INT_HM_FINISH, Constants.INT_VALUE_SUCCESS);	

		                                    	}
		                                    	else {
		                                    		
		                                    		if (this.isInterrupted()) {
			                                    		Utils.log("an user presses 'Back' while extraction. Remove the file");
			                                    		cleanUpDownloadedFile();
			                                    	}
		                                    		
		                                    		// Finish
		                                    		Utils.sendMessage(handler, Constants.INT_HM_FINISH, Constants.INT_VALUE_ERROR);	
		                                    	}                            
		                                    }
		                                    else {
		                                    	
		                                    	if (this.isInterrupted()) {
		                                    		Utils.log("an user presses 'Back' while verification. Remove the file");
		                                    		cleanUpDownloadedFile();
		                                    	}
		                                    	
		                                    	// exit with error message
		                                    	Utils.sendMessage(handler, Constants.INT_HM_FINISH, Constants.INT_VALUE_ERROR);
		                                    }
		                        		}
		                        		else {
		                        			Utils.sendMessage(handler, Constants.INT_HM_DOWNLOAD_ARCHIVE, Constants.INT_VALUE_ERROR);
		                        			
		                            		// exit with error message
		                            		Utils.sendMessage(handler, Constants.INT_HM_FINISH, Constants.INT_VALUE_ERROR);
		                        		}
		                        	}
	                        	}
                        	}
                        	
                        } catch (Exception e) {
                        	Utils.log("Error in thread: " + e.getMessage());
                        } finally {
                        	setIsRunning(false);
                        }
	                }
	    		};
	    		
	    		downloadingThread.start();
			}
		};
	};
	
	private Handler handler = new Handler() {
		
        @Override
        public void handleMessage(Message msg) {
        	final int command = msg.getData().getInt(Constants.STR_HANDLER_MESSAGE_COMMAND);
        	final int value = msg.getData().getInt(Constants.STR_HANDLER_MESSAGE_VALUE);
        	final String customLabel = msg.getData().getString(Constants.STR_HANDLER_MESSAGE_LABEL);
        	
        	switch (command) {
	    		case Constants.INT_HM_REQUEST_METADATA: 
	    			showTraceRow(value, R.id.wizard_trace_request_label_id, activity.getResources().getString(R.string.wizard_import_request_metadata));
	    			break;
	    			
	    		case Constants.INT_HM_AVAILABLE_DISC_SPACE: 
	    			showTraceRow(value, R.id.wizard_trace_free_space_id, activity.getResources().getString(R.string.wizard_import_disk_space));
	    			break;
	    			
	    		case Constants.INT_HM_DOWNLOAD_ARCHIVE: 
	    			if (value == Constants.INT_VALUE_IN_PPROGRESS) {
	    				LinearLayout linLay = getHorizontalProgressBar(R.id.wizard_trace_download_progress_id, R.id.wizard_trace_download_progress_lbl_id);
	    				getMainContainer().addView(linLay);
	    			}
	    			else if (value == Constants.INT_VALUE_ERROR || value == Constants.INT_VALUE_SUCCESS) {
	    				LinearLayout lc = getMainContainer();
	    				lc.removeViewAt(lc.getChildCount() - 1);
	    				setReadyResultRow(value);
	    			}
	    			else {
	    				ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.wizard_trace_download_progress_id);
	    				progressBar.setProgress(value);
	    				
	    				TextView textViewValue = (TextView) activity.findViewById(R.id.wizard_trace_download_progress_lbl_id);
	    				textViewValue.setText(activity.getResources().getString(R.string.wizard_import_downloaded) + value + "%");
	    			}
	    			break;
	    			
	    		case Constants.INT_HM_VERIFY_CHECKSUM: 
	    			showTraceRow(value, R.id.wizard_trace_verify_id, activity.getResources().getString(R.string.wizard_import_verify));
	    			break;
	    		
	    		case Constants.INT_HM_OLD_DATABASE: 
	    			showTraceRow(value, R.id.wizard_trace_old_database_id, activity.getResources().getString(R.string.wizard_import_old_database));
	    			break;
	    		
	    		case Constants.INT_HM_CORRECT_BOOKMARK_IDS:
	    			showTraceRow(value, R.id.wizard_trace_update_bookmarks_id, activity.getResources().getString(R.string.wizard_import_correct_bookmarks), customLabel);
	    			break;
	    		
	    		case Constants.INT_HM_CACHE_OLD_BOOKMARKS:
	    			showTraceRow(value, R.id.wizard_trace_old_bookmarks_id, activity.getResources().getString(R.string.wizard_import_old_bookmarks));
	    			break;
	    			
	    		case Constants.INT_HM_UNPACK_ARCHIVE: 
	    			showTraceRow(value, R.id.wizard_trace_unpack_id, activity.getResources().getString(R.string.unzip_process_title));
	    			break;
	    			
	    		case Constants.INT_HM_FINISH:
	    			
	    			View btnNext = activity.findViewById(R.id.btnNextId); 
	    			if (null != btnNext) {
	    				btnNext.setEnabled(true);
	    			}
	    			
	    			TextView textViewValue = getCommonRowTextView(Constants.STR_EMPTY, true, 0);
	    			if (value == Constants.INT_VALUE_SUCCESS) {
	    				textViewValue.setText(R.string.wizard_import_finished_success);
	    				textViewValue.setTextColor(activity.getResources().getColor(R.color.light_blue));
	    			}
	    			else if (value == Constants.INT_VALUE_ERROR) {
	    				textViewValue.setText(R.string.wizard_import_finished_failure);
	    				textViewValue.setTextColor(activity.getResources().getColor(R.color.red));
	    			}
	    			getMainContainer().addView(textViewValue);
	    			break;
        	}	

        }
    	
		/**
		 * Simply adds to layout new row with result like that: "Dowloading..... [OK]"
		 * 
		 * @param value - result of downloading (success or error)
		 */
		private void setReadyResultRow(int value) {
			LinearLayout ll = getCommonRowContainer();
			TextView textViewCommand = getCommonRowTextView(activity.getResources().getString(R.string.wizard_import_downloading), false, 0);
			TextView textViewValue = getCommonRowTextView(Constants.STR_EMPTY, true, R.id.wizard_trace_download_progress_lbl_id);
			
			if (value == Constants.INT_VALUE_SUCCESS) {
				textViewValue.setText(R.string.wizard_import_ok);
				textViewValue.setTextColor(activity.getResources().getColor(R.color.light_blue));
			}
			else if (value == Constants.INT_VALUE_ERROR) {
				textViewValue.setText(R.string.wizard_import_error);
				textViewValue.setTextColor(activity.getResources().getColor(R.color.red));
			}
			
			ll.addView(textViewCommand);
			ll.addView(textViewValue);
			
			getMainContainer().addView(ll);
		}
	};
	
	/**
	 * Remove downloaded file. It could be useful if user interrupts the thread.
	 * 
	 * @return result
	 */
	private boolean cleanUpDownloadedFile() {
		final String datafile = Utils.getHSLfolderName() + "/" + Constants.STR_ARCHIVE_NAME;
    	return new File(datafile).delete();
	}
	
	private LinearLayout getMainContainer() {
		return this.mainContainer;
	}
	
	/**
	 * Corrects all bookmark ID's after database updating.
	 * The main principle of correcting is update only these fields:
	 * <ul>
	 * 	<li>transport_number_id</li>
	 *  <li>station_id_start</li>
	 * 	<li>station_id</li>
	 * </ul>
	 * Because new database was generated from the scratch, all ID's are different. It is possible, that names are also
	 * changed, but in this way can't do something and we had to delete bookmark at all. User should create this bookmark
	 * manually once again. But if new database contains the all needed station names, then it is OK and correcting will provide normally.
	 * 
	 * @return result {Boolean}
	 * @throws DatabaseException 
	 */
	private int correctBookmarkIDs() {
		int nCorrectedCnt = 0;
		if (lstBookmarks.size() > 0) {
			DBAdapter db = null;
			DBAdapterExternal extDb = null;
			Cursor curs = null;
			try {
				extDb = new DBAdapterExternal(activity);
				extDb.open();
				
				db = new DBAdapter(activity);
				db.open();
				
				for(MigrationBookmark bookmark : lstBookmarks) {
					
					curs = extDb.retreiveIDsForBookmarkMigrtion(bookmark.getName(), bookmark.getStationStartName(), bookmark.getStationName()); 
					//Utils.log("[TRACE]: Bookmark '" + bookmark.getBookmarkNameHumanReadable() + "', bus:'" + bookmark.getName() + "' , count: " + curs.getCount());
					
					if (null == curs || curs.getCount() == 0) {
						
						// we can't explicitly define new ID's for our fields. We had to delete it, because it won't be work correctly:
						db.deleteBookmarkTransportLine(bookmark.getBookmarkLineID(), bookmark.getId());
					}
					else {
						
						// update bookmarks ID's
						if (curs.moveToFirst()) {
							//Utils.log("TransportID was " + bookmark.getTransportNumberId() + " now " + curs.getLong(0));
							if (db.updateBookmarksIDs(bookmark.getBookmarkLineID(), curs.getLong(0), curs.getLong(1), curs.getLong(2))) {
								nCorrectedCnt++;
							}
						}
					}
					

				}
				
			} catch (DatabaseException e) {
				Utils.log("Error during the bookmark correction. Msg:" + e.getMessage());
				nCorrectedCnt = 0;
			} finally {
				if (null != curs) curs.close();
				if (null != db) db.close();
				if (null != extDb) extDb.close();
			}
		}
		return nCorrectedCnt;
	}
	
	/**
	 * Gets all existed bookmarks and stores it into the class's internal field "lstBookmarks".
	 * After the database refreshing all bookmarks will be corrected using this field.
	 * 
	 * @return result {Boolean}
	 */
	private boolean retrieveBookmarks() {
		DBAdapter db = null;
		Cursor curs = null;
		DBAdapterExternal extDb = null;
		try {
			db = new DBAdapter(activity);
			db.open();
			curs = db.getAllBookmarksData(-1);
			
			if (null != curs) {
				extDb = new DBAdapterExternal(activity);
				extDb.open();
				
				final int nBookmarkID = 0;
				final int nBookmarkName = 1;
				//final int nImageID = 2;
				final int nBookmarkStationID = 3;
				final int nTransportNumberID = 4;
				final int nLinesStationStart = 5;
				final int nLineID = 6;
				
				Cursor extCursor = null;
				long transportNumberLineId = 0L;
				long stationId = 0L;
				long stationIdStart = 0L;
				long bookmarkLineID = 0L;
				long bookmarkID = 0L;
				
				lstBookmarks = new ArrayList<MigrationBookmark>();
				
				if (curs.moveToFirst()) {
					do {
						bookmarkID = curs.getLong(nBookmarkID);
						transportNumberLineId = curs.getLong(nTransportNumberID);
						bookmarkLineID = curs.getLong(nLineID);
						stationId = curs.getLong(nBookmarkStationID);
						stationIdStart = curs.getLong(nLinesStationStart);
						
						Utils.log("Retrieve IDs for bookmark " + bookmarkLineID);
						
						extCursor = extDb.retrieveBookmarkDataForMigration(transportNumberLineId, stationIdStart, stationId);
						
						if(extCursor.moveToFirst()) { 
						
							MigrationBookmark bookmark = new MigrationBookmark(bookmarkID, 
									bookmarkLineID, 
									extCursor.getString(0), 
									transportNumberLineId, 
									extCursor.getString(1), 
									extCursor.getString(2), 
									curs.getString(nBookmarkName));
							
							lstBookmarks.add(bookmark);
							
							//Utils.log("Cached bookmark: " + bookmark.toString());
						}
						else {
							Utils.log("Cant retrieve bookmark!!!!");
						}
						
						extCursor.close();
						
					} while (curs.moveToNext()); 
				}
				
				curs.close();
				
			}
			return true;
			
		} catch (SQLException e) {
			Utils.log("Error occured while the retrieving of old bookmarks (SQLException)! Error: " + e.getMessage());
			return false;
			
		} catch (DatabaseException e) {
			Utils.log("Error occured while the retrieving of old bookmarks (DatabaseException)! Error: " + e.getMessage());
			return false;
			
		} finally {
			if (null != curs) curs.close();
			if (null != db) db.close();
			if (null != extDb) extDb.close();
		}
	}
	
	/**
	 * Creates a common text field
	 * 
	 * @param strValue - text value
	 * @param isResult - result (ok/error) must have own color and must be placed on the right side
	 * @return TextView
	 */
	private TextView getCommonRowTextView(String strValue, boolean isResult, int id) {
		TextView t = new TextView(this.activity.getApplicationContext());
		t.setText(strValue);
		t.setTextColor(this.activity.getResources().getColor(R.color.dark_gray));
		
		if (id != 0) t.setId(id);
		if (!isResult) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			t.setLayoutParams(lp);
		}
		else {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			t.setLayoutParams(lp);			
		}

		return t;
	}
	
	/**
	 * Makes a common container for one row in downloader tracer
	 * @return LinearLayout
	 */
	private LinearLayout getCommonRowContainer() {
		LinearLayout ll = new LinearLayout(this.activity.getApplicationContext());
		ll.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 5, 0, 5);
		ll.setLayoutParams(lp);
		return ll;
	}
	
	/**
	 * Creates a horizontal bar and small label with a percentage
	 * 
	 * @return progressBar
	 */
	private LinearLayout getHorizontalProgressBar(int progressBarID, int progressLabelID) {
		LinearLayout ll = new LinearLayout(this.activity.getApplicationContext());
		ll.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 5, 0, 5);
		ll.setLayoutParams(lp);
		
		// label "Downloaded: 21%"
		TextView t = new TextView(this.activity.getApplicationContext());
		t.setId(progressLabelID);
		t.setGravity(Gravity.RIGHT);
		t.setText(Constants.STR_EMPTY);
		t.setTextColor(this.activity.getResources().getColor(R.color.dark_gray));
		ll.addView(t);
		
		// Progress Bar
		Drawable background = new ColorDrawable(this.activity.getResources().getColor(R.color.dark_gray));
        Drawable progress = new ColorDrawable(this.activity.getResources().getColor(R.color.light_blue));
        ClipDrawable clipProgress = new ClipDrawable(progress, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        LayerDrawable layerlist = new LayerDrawable(new Drawable[] { background, clipProgress });
        layerlist.setId(0, android.R.id.background);
        layerlist.setId(1, android.R.id.progress);
        
        ProgressBar progressBar = new ProgressBar(this.activity.getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        plp.height = 8;
        
        progressBar.setLayoutParams(plp);
        progressBar.setProgressDrawable(layerlist);
        progressBar.setId(progressBarID);
        ll.addView(progressBar);
        
        return ll;
	}
	
	/**
	 * Common action. Draws a valid row in program tracing. Looks like "Check... [OK]"
	 * 
	 * @param value
	 * @param id1
	 * @param strLabel
	 * @param customLabel
	 */
	private void showTraceRow(int value, int id1, String strLabel) {
		showTraceRow(value, id1, strLabel, null);
	}
	
	/**
	 * Common action. Draws a valid row in program tracing. Looks like "Check... [OK]"
	 * 
	 * @param value
	 * @param id1
	 * @param strLabel
	 * @param customLabel
	 */
	private void showTraceRow(int value, int id1, String strLabel, String customLabel) {
		if (value == Constants.INT_VALUE_IN_PPROGRESS) {
			
			// add one line with message and circular progress bar, showing that new task is in progress
			LinearLayout ll = getCommonRowContainer();
			TextView textViewCommand = getCommonRowTextView(strLabel, false, 0);
			
			ProgressBar pb = new ProgressBar(this.activity.getApplicationContext(), null, android.R.attr.progressBarStyleSmallInverse);
			pb.setLayoutParams(new LinearLayout.LayoutParams(14, 14));
			pb.setId(id1);
			
			ll.addView(textViewCommand);
			ll.addView(pb);
			
			getMainContainer().addView(ll);
		}
		else {
			//remove circular progressBar
			ProgressBar pb = (ProgressBar) this.activity.findViewById(id1);
			LinearLayout ll = (LinearLayout) pb.getParent();
			ll.removeView(pb);
			
			// And instead of it set a new label with result: [OK] or [ERROR]
			TextView textViewValue = getCommonRowTextView(Constants.STR_EMPTY, true, id1);
			
			if (value == Constants.INT_VALUE_SUCCESS) {
				textViewValue.setText(R.string.wizard_import_ok);
				textViewValue.setTextColor(this.activity.getResources().getColor(R.color.light_blue));
			}
			else if (value == Constants.INT_VALUE_ERROR) {
				textViewValue.setText(R.string.wizard_import_error);
				textViewValue.setTextColor(this.activity.getResources().getColor(R.color.red));
			}
			
			if (null != customLabel) {
				textViewValue.setText(customLabel);
			}
			
			ll.addView(textViewValue);
		}
	}

	/**
	 * Checks whether the Importer is running or not.
	 * @return
	 */
	public synchronized boolean isRunning() {
		return this.isRunning;
	}
	
	/**
	 * Set the isRunning status
	 * @param isRun
	 */
	public synchronized void setIsRunning(boolean isRun) {
		this.isRunning = isRun;
	}

	public void interruptDownloading() {
		this.downloadingThread.interrupt();
	}
}