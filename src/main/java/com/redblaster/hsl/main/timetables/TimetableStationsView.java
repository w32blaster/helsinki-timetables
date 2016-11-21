package com.redblaster.hsl.main.timetables;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapterExternal;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractTimetableView;
import com.redblaster.hsl.main.R;

public class TimetableStationsView extends AbstractTimetableView {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return TimetableDirectionsView.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getNextActivityClassName() {
		return TimetableTimesView.class;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Breadcrumb> setListOfBreadcrumbs() {
		List<Breadcrumb> lstBreadcrubms = new ArrayList<Breadcrumb>();
		
		// Trips:
		Breadcrumb brTrips = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_lines, R.drawable.brcrmb_lines_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(TimetableTripsView.class);
			}
		});
		lstBreadcrubms.add(brTrips);
		
		// Directions:
		Breadcrumb brDirs = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_one_directions, R.drawable.brcrmb_one_directions_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(TimetableDirectionsView.class);
			}
		});
		lstBreadcrubms.add(brDirs);
		
		// Now is selecting of station:
		Breadcrumb brTimes = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_list_stations, R.drawable.brcrmb_list_stations_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brTimes);
		
		return lstBreadcrubms;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) {
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.addView(super.addHeaderPanel(super.nVehicleType, super.strHeaderName));
		
		LinearLayout lLayoutStations = new LinearLayout(getApplicationContext());
		lLayoutStations.setOrientation(LinearLayout.HORIZONTAL);
		
		//add a arrow line
		ImageView imgArrow = new ImageView(this);
		imgArrow.setImageResource(R.drawable.down);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(5, 10, 5, 0);
		imgArrow.setLayoutParams(lp);
		lLayoutStations.addView(imgArrow);

		TableLayout table = new TableLayout(getApplicationContext());
		TableRow.LayoutParams tableParams = new TableRow.LayoutParams();
		tableParams.setMargins(10, 10, 10, 10);
		table.setLayoutParams(tableParams);
		table.setId(R.id.table_stations_id);

		lLayoutStations.addView(table);
		linearLayout.addView(lLayoutStations);
	}
	
	private String getNextViewHeaderName(String strStationName) {
		return String.format(getResources().getString(R.string.header_times), super.strTripName, strStationName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processTheLayoutOperations(Cursor curs) {
		TableLayout table = (TableLayout) findViewById(R.id.table_stations_id);
		String strStationName = null;
		
		if (null != curs) {
			try {
				int nStationID = 0;
				int nStationName = 1;
				TableRow row = null;
				
				if (curs.moveToFirst()) {
					do {
						row = new TableRow(getApplicationContext());
						strStationName = curs.getString(nStationName);
						Button btn = getTableButton(strStationName, -1L, null, this.getNextViewHeaderName(strStationName), -1L, null, curs.getLong(nStationID), strStationName, null, -1L, -1L);
						btn.setGravity(Gravity.LEFT);
						row.addView(btn);
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
			DBAdapterExternal db = new DBAdapterExternal(this);
			db.open();
			curs = db.getAllStationForGivenTransportLine(super.lTripID);
			db.close();
			startManagingCursor(curs);
		} catch (SQLException e) {
			Log.e("ERROR","Error: " + e.getMessage());
		}
		return curs;
	}
}