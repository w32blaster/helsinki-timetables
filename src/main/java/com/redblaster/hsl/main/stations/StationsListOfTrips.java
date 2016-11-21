package com.redblaster.hsl.main.stations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapterExternal;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractTimetableView;
import com.redblaster.hsl.main.R;

/**
 * View of all possible routes for selected station.
 * Please note, that database may contain several stations with the same name, so we need here to find
 * trips for all of them.
 * 
 * @author Ilja Hamalainen
 *
 */
public class StationsListOfTrips extends AbstractTimetableView {
	protected DBAdapterExternal db;
	protected HashSet<Integer> setOfIDs = new HashSet<Integer>(); 
	protected String strStationName = null;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return StationsSearchList.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getNextActivityClassName() {
		return StationsTimesForSelectedTrip.class;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Breadcrumb> setListOfBreadcrumbs() {
		List<Breadcrumb> lstBreadcrubms = new ArrayList<Breadcrumb>();
		
		// Find station:
		Breadcrumb brStations = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_search_station, R.drawable.brcrmb_search_station_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(getPreviuosActivityClassName());
			}
		});
		lstBreadcrubms.add(brStations);
		
		// Now is list of all possible routes on this station:
		Breadcrumb brTimes = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_list_stations, R.drawable.brcrmb_list_stations_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brTimes);
		
		return lstBreadcrubms;
	}
	
	/**
	 * Finds all station's ID with a given name. There are may be several stations with the same name. So we need to find
	 * trips for all of them.
	 * 
	 * @throws DatabaseException 
	 * @throws SQLException 
	 */
	protected void getAllStationsID() throws SQLException, DatabaseException {
		db = new DBAdapterExternal(this);
		db.open();
		Cursor curs = db.getStationIDwithASingleName(super.lStationId);
		if (null != curs) {
			if(curs.moveToFirst()) {
				final int intIdColumn = 0;
				final int intNameColumn = 1;
				do {
					setOfIDs.add(curs.getInt(intIdColumn));
					if (null == strStationName) strStationName = curs.getString(intNameColumn);
				} while (curs.moveToNext());
			}
		}
		curs.close();
	}
	
	/**
	 * Combines well formatted header for this view
	 * @return
	 */
	protected String getHeader() {
		return String.format(getResources().getString(R.string.stations_list_of_trips_header), strStationName);
	}
	
	/**
	 * Combines well formatted header for this view
	 * @return
	 */
	private String getHeaderNextScreen(String strTrip) {
		return String.format(getResources().getString(R.string.header_times), strTrip, strStationName);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws DatabaseException 
	 * @throws SQLException 
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) throws SQLException, DatabaseException {
		this.getAllStationsID();
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.addView(super.addHeaderPanel(getResources().getDrawable(R.drawable.ico_search), getHeader()));
		
		LinearLayout lLayoutStations = new LinearLayout(getApplicationContext());
		lLayoutStations.setOrientation(LinearLayout.HORIZONTAL);
		
		TableLayout table = new TableLayout(getApplicationContext());
		TableRow.LayoutParams tableParams = new TableRow.LayoutParams();
		tableParams.setMargins(5, 20, 0, 10);
		table.setLayoutParams(tableParams);
		table.setId(R.id.table_stations_id);

		lLayoutStations.addView(table);
		linearLayout.addView(lLayoutStations);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processTheLayoutOperations(Cursor curs) {
		TableLayout table = (TableLayout) findViewById(R.id.table_stations_id);
		curs.getCount();
		
		if (null != curs) {
			try {
				int nTransportId = 0;
				int nTransportName = 1;
				int nStationStartName = 2;
				int nStationEndName = 3;
				int nStationStartID = 4;
				int nTripID = 5;
				int nStationID = 6;
				int nTransportDescr = 7;
				
				StringBuffer sbName = new StringBuffer();
				
				if (curs.moveToFirst()) {
					do {
							if (null == strHeaderName) strHeaderName = curs.getString(nTransportName);
							TableRow row = new TableRow(getApplicationContext());
							sbName.append(curs.getString(nTransportName));
							sbName.append(" (");
							sbName.append(curs.getString(nStationStartName));
							sbName.append(" >> ");
							sbName.append(curs.getString(nStationEndName));
							sbName.append(")");
							
							row.addView(getTableButton(sbName.toString(), curs.getLong(nTransportId), null, getHeaderNextScreen(curs.getString(nTransportDescr)), curs.getLong(nTripID), sbName.toString(), curs.getLong(nStationID), null, null, -1L, curs.getLong(nStationStartID)));
							
							sbName.delete(0, sbName.length());
							table.addView(row);
				   } while (curs.moveToNext()); 
				}
				curs.close();
			} catch (Exception e) {	
				Log.e("ERROR", "Error in loop: " + e.getMessage());
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @throws DatabaseException 
	 */
	@Override
	protected Cursor processTheDatabaseQuery() throws DatabaseException {
		Cursor curs = null;
		try {
			curs = db.getAllTripsForGivenStation(this.setOfIDs);
			db.close();
			startManagingCursor(curs);
		} catch (SQLException e) {
			Log.e("ERROR","Error: " + e.getMessage());
		}
		return curs;
	}
}
