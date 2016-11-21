package com.redblaster.hsl.main.timetables;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapterExternal;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractTimetableView;
import com.redblaster.hsl.main.R;

public class TimetableDirectionsView extends AbstractTimetableView {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return TimetableTripsView.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getNextActivityClassName() {
		return TimetableStationsView.class;
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
		
		// Now is selecting of diretions:
		Breadcrumb brTimes = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_one_directions, R.drawable.brcrmb_one_directions_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
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
		
		TableLayout table = new TableLayout(getApplicationContext());
		TableRow.LayoutParams tableParams = new TableRow.LayoutParams();
		tableParams.setMargins(10, 20, 10, 10);
		table.setLayoutParams(tableParams);
		table.setId(R.id.table_directions_id);

		lLayoutStations.addView(table);
		linearLayout.addView(lLayoutStations);
	}
	
	/**
	 * Get Header string
	 * @param strTripName
	 * @return
	 */
	private String getNextViewHeaderName(String strTripName) {
		return String.format(getResources().getString(R.string.header_stations), super.strTransportNumberName, strTripName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processTheLayoutOperations(Cursor curs) {
		TableLayout table = (TableLayout) findViewById(R.id.table_directions_id);
		curs.getCount();
		
		if (null != curs) {
			try {
				final int nTripId = 0;
				final int nTripFullName = 1;
				final int nTripStart = 2;
				final int nTripEnd = 3;
				final int nStationStartID = 4;
				
				TextView headerText = (TextView) findViewById(R.id.view_header_id);
				String strHeaderName = null;
				StringBuffer sbName = new StringBuffer();
				
				if (curs.moveToFirst()) {
					do {
							if (null == strHeaderName) strHeaderName = curs.getString(nTripFullName);
							TableRow row = new TableRow(getApplicationContext());
							sbName.append(curs.getString(nTripStart));
							sbName.append(" >> ");
							sbName.append(curs.getString(nTripEnd));
							
							row.addView(getTableButton(sbName.toString(), -1L, null, this.getNextViewHeaderName(sbName.toString()), curs.getLong(nTripId), sbName.toString(), -1L, null, null, -1L, curs.getLong(nStationStartID)));
							
							sbName.delete(0, sbName.length());
							table.addView(row);
				   } while (curs.moveToNext()); 
				}
				
				// set new name
				headerText.setText(headerText.getText() + " '" + strHeaderName + "'");
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
			curs = db.getAllDirectionsForGivenTransportLine(super.lTransportNumberId);
			db.close();
			startManagingCursor(curs);
		} catch (SQLException e) {
			Log.e("ERROR","Error: " + e.getMessage());
		}
		return curs;
	}
}