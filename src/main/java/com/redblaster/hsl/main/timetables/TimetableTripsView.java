package com.redblaster.hsl.main.timetables;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapter;
import com.redblaster.hsl.common.DBAdapterExternal;
import com.redblaster.hsl.common.ErrorMessage;
import com.redblaster.hsl.common.LoadableSectorsBuilder;
import com.redblaster.hsl.common.Utils;
import com.redblaster.hsl.common.Vehicle;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractTimetableView;
import com.redblaster.hsl.main.MainPage;
import com.redblaster.hsl.main.R;


public class TimetableTripsView extends AbstractTimetableView {
	
	private DBAdapter db;
	private LoadableSectorsBuilder bBuilder;
	private TableLayout table;


	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return MainPage.class;
	}

	@Override
	protected Class<?> getNextActivityClassName() {
		return TimetableDirectionsView.class;
	}
	
	
	/**
	 * Returns formatted text for header
	 * 
	 * @return
	 */
	private String getHeaderText() {
		int rString = Vehicle.getById(super.nVehicleType).getStringResource();
		return String.format(getResources().getString(R.string.header_trips), getResources().getString(rString));
	}

	private String getNextViewHeaderText(String strTransportNumber) {
		return String.format(getResources().getString(R.string.header_trips_directions), strTransportNumber);
	}
	
	@Override
	protected List<Breadcrumb> setListOfBreadcrumbs() {
		List<Breadcrumb> lstBreadcrubms = new ArrayList<Breadcrumb>();
		
		// Now is all trips for one vehicle type:
		Breadcrumb brTrips = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_lines, R.drawable.brcrmb_lines_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brTrips);
		
		return lstBreadcrubms;
	}

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractTimetableView#addLayoutElements(android.widget.LinearLayout)
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) {
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.addView(super.addHeaderPanel(super.nVehicleType, this.getHeaderText()));

		TableLayout table = new TableLayout(getApplicationContext());
		TableRow.LayoutParams tableParams = new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		tableParams.setMargins(30, 5, 30, 10);
		table.setLayoutParams(tableParams);
		table.setId(R.id.table_stations_id);

		linearLayout.addView(table);
	}
	
	@Override
	public void processTheLayoutOperations(Cursor curs) {
		final TableLayout tableWrapper = (TableLayout) findViewById(R.id.table_stations_id);
		this.bBuilder = new LoadableSectorsBuilder(getApplicationContext());
		Button btnDefault = null;
		
		long lLastOpenedTransportMode = this.getLastOpenedSection();
		
		try {
			if (null != curs) {
				final int nTransportModeID = 1;
				final int nTransportMode = 2;
				Button btn = null;
				
				if (curs.moveToFirst()) {
					do {
						
						TableLayout table = this.bBuilder.createNewTable();
						
						long lTransportModeID = curs.getLong(nTransportModeID);
						btn = this.bBuilder.addNewLoadableSection(false, 
								curs.getString(nTransportMode),
								-1 /* don't show any icon */,
								getListenerToExpandTransportMode(table, lTransportModeID));
						
						// here we decide which btn reference we need to open by default
						if (lLastOpenedTransportMode == -1 && btnDefault == null) {
							// if there is no any cached value, then get the very first btn instance
							btnDefault = btn;
						}
						else if (lLastOpenedTransportMode == lTransportModeID) {
							// get btn if this is the last cached (visited) section
							btnDefault = btn;
						}
						
					} while (curs.moveToNext());
				}
			}
		}
		finally {
			curs.close();
		}
		
		this.bBuilder.appendView(tableWrapper);

		// open default or last opened sector
		if (null != btnDefault) btnDefault.performClick();
	}
	
	/**
	 * Prepare the listener for the "collapse" button. Loads
	 * 
	 * @param bBuilder
	 * @param table
	 * @param transportModeID
	 * @return
	 */
	private OnClickListener getListenerToExpandTransportMode(final TableLayout table, final long transportModeID) {
		
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// set the table (bookmark container). All lines with times will be added to this table
				bBuilder.setCurrentTable(table);
				bBuilder.addLineWithLoadingBarAndString();	
				
				/*
				 * Start new thread to retrieve data and fill the layout.
				 */
				new Thread() {
					
					@Override
					public void run() {
						Cursor curs = null;
						DBAdapterExternal db = null;
						
						try {
							
							// open external database
							db = new DBAdapterExternal(getApplicationContext());
							db.open();

							// get all line numbers for the selected region
							curs = db.getAllTripsByVehicleTypeAndTransportMode(nVehicleType, transportModeID);

							// build the table with obtained data
							int result = fillTableWithAllTripsForGivenTransportMode(curs);

							// cache selected value in internal database
							if (!cacheVisitedSection(transportModeID)) {
								result = ErrorMessage.SQL_ERROR.toInt();
							}
							
							// send result to main thread
							Utils.sendMessage(loaclHandler, 0, result);
							
						} catch (DatabaseException e) {
							Utils.sendMessage(loaclHandler, 0, ErrorMessage.SQL_ERROR.toInt());
						}
						finally {
							if (null != curs) curs.close();
							if (null != db) db.close();
						}

					}
					
				}.start();
			}
		};
	}
	
	/**
	 * Cache selected section. Thus we will open last saved (cached) section by default when user
	 * next time will open this view
	 * 
	 * @param transportModeID
	 */
	private boolean cacheVisitedSection(long transportModeID) {
		this.db = new DBAdapter(this);
		this.db.open();
		
		try {
			return db.saveCachedValue(Vehicle.getById(super.nVehicleType).name(), transportModeID);
		}
		finally {
			this.db.close();
		}
	}
	
	/**
	 * Return last pressed section value. It must me represented by transportTypeID
	 * 
	 * @return TransportTypeID or -1
	 */
	private long getLastOpenedSection() {
		this.db = new DBAdapter(this);
		this.db.open();
		
		try {
			return db.getCachedValue(Vehicle.getById(super.nVehicleType).name());
		}
		finally {
			this.db.close();
		}
	}
	
	protected Handler loaclHandler = new Handler() {
        
		@Override
        public void handleMessage(Message msg) {
        	int result = msg.getData().getInt(Constants.STR_HANDLER_MESSAGE_VALUE);

        	// remove preloader and message "Processing..."
			bBuilder.removeLastRowFromTable();
			bBuilder.setExpandedIcon();
			
        	if (result == 0 && null != table) {
   				bBuilder.addAbstractView(table);    				
        	}
        	else {
        		// if something happens, show the error message
    			int resMsg = ErrorMessage.getByID(result).getMessageResource();
    			bBuilder.addOneSpannedRowWithErrorMessage(getApplicationContext().getResources().getString(R.string.error) + Constants.STR_SPACE + getApplicationContext().getResources().getString(resMsg));
        	}
        };
	};
	
	/**
	 * Returns list of all bookmarks.
	 * Makes query to the internal database
	 * @return 
	 */
	public Cursor getBookmarkCommonData(long bookmarkID) {
		return db.getAllBookmarksData(bookmarkID);
	}
	
	/**
	 * Builds interface from database
	 * 
	 * @param curs
	 */
	public int fillTableWithAllTripsForGivenTransportMode(Cursor curs) {
		
		int result = 0; // all right by default
		String strMode = Constants.STR_EMPTY;
		String strCurNumber = Constants.STR_EMPTY;
		
		table = new TableLayout(getApplicationContext()); 
		
		if (null != curs) {
			try {

				final int nTransportNumberID = 0;
				final int nTransportNumber = 1;
				final int nTransportMode = 2;
				final int nServiceName = 3;
				
				int i = 0;
				TableRow row = null;
				
				String strTransportNumber = null;
				
				if (curs.moveToFirst()) {
					do {
						
						if (i == 0) {
							row = new TableRow(getApplicationContext());
						}
						else if (i % Constants.RECORDS_PER_ROW == 0) {
							table.addView(row);
							row = new TableRow(getApplicationContext());
						}
						
						strMode = curs.getString(nTransportMode);
						
						// if the header name is changed, then draw the new header in the table
						/*if (!strMode.equals(strCurNumber)) {
							rowsCnt = this.addNewRow(strMode, table, i, rowsCnt, row);
							
							row = new TableRow(getApplicationContext());
							strCurNumber = strMode;
							i = 0;
						}*/
						strTransportNumber = curs.getString(nTransportNumber);
						
						if (super.isGrouped) {
							strTransportNumber = Character.toUpperCase(strTransportNumber.charAt(0)) + strTransportNumber.substring(1);
							strTransportNumber += " (" + curs.getString(nServiceName) + ")";
						}

						Utils.log("Line: " + strTransportNumber + ", mode=" + strMode);

						row.addView(getTableButton(strTransportNumber, curs.getLong(nTransportNumberID), strTransportNumber, getNextViewHeaderText(strTransportNumber), -1L, null, -1L, null, null, -1L, -1L));
						i++;
						
						if (super.isGrouped) {
							table.addView(row);
							row = new TableRow(getApplicationContext());
						}
						
						
				   } while (curs.moveToNext());
					
					// add the last line to the table 
					table.addView(row);
					
				}
			} 
			catch (Exception e) {
				result = ErrorMessage.SQL_ERROR.toInt();
				Log.e("ERROR", "Error: " + e.getMessage());
			}
			finally {
				Log.d("TRACE", "cursor is closed");
				curs.close();
			}
		}
		
		return result;
	}
	
	private int addNewRow(String strMode, TableLayout table, int i, int rowsCnt, TableRow row) {
		TextView header;
		addEmptyCells(i, row); // fill the rest cells with the empty strings				
		table.addView(row);
		rowsCnt++;
		
		row = new TableRow(getApplicationContext());

		header = new TextView(getApplicationContext());
		header.setTextColor(Color.BLACK);
		header.setText(strMode);
		header.setTypeface(null, Typeface.BOLD);
		
		// set collSpan to this row cell
		TableRow.LayoutParams trParams = new TableRow.LayoutParams();
		trParams.span = Constants.RECORDS_PER_ROW;
		trParams.topMargin = 20;
		header.setLayoutParams(trParams);
		
		row.addView(header);
		table.addView(row);
		
		return rowsCnt;
	}
	
	/**
	 * Adds empty cells to the table row
	 * @param cnt
	 * @param row
	 */
	private void addEmptyCells(int index, TableRow row) {
		int cnt = Constants.RECORDS_PER_ROW - (index % Constants.RECORDS_PER_ROW);
		if (cnt != Constants.RECORDS_PER_ROW) {
			for (int i = 0; i < cnt; i++) {
				TextView emptyText = new TextView(getApplicationContext());
				emptyText.setText(Constants.STR_EMPTY);
				row.addView(emptyText);
				emptyText = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractTimetableView#processTheDatabaseQuery()
	 */
	@Override
	protected Cursor processTheDatabaseQuery() throws DatabaseException {
		DBAdapterExternal db = new DBAdapterExternal(this);
		db.open();
		
		//Cursor curs = db.getAllTripsByVehicleType(super.nVehicleType);
		
		Cursor curs = db.getAllTransportModesByVehicleType(super.nVehicleType);
		
		db.close();
		startManagingCursor(curs);
		
		return curs;
	}
}