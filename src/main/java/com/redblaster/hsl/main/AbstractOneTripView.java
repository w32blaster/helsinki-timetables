package com.redblaster.hsl.main;

import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.redblaster.hsl.common.DBAdapterExternal;
import com.redblaster.hsl.common.Utils;
import com.redblaster.hsl.exceptions.DatabaseException;

abstract public class AbstractOneTripView extends AbstractTimetableView {
	
	/**
	 * Combines the header panel. May be overridable
	 * 
	 * @return
	 */
	protected LinearLayout getHeaderPanelWithIco() {
		return super.addHeaderPanel(super.nVehicleType, super.strHeaderName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) {
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.addView(this.getHeaderPanelWithIco());

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
		table.setId(R.id.table_times_id);

		
		lLayoutStations.addView(table);
		linearLayout.addView(lLayoutStations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processTheLayoutOperations(Cursor curs) {
		TableLayout table = (TableLayout) findViewById(R.id.table_times_id);
		curs.getCount();
		
		if (null != curs) {
			try {
				int nTime = 0;
				int nStationName = 1;
				String strTime = null;
				if (curs.moveToFirst()) {
					do {
						TableRow row = new TableRow(getApplicationContext());
						
						TextView stationName = new TextView(getApplicationContext());
						stationName.setText(curs.getString(nStationName));
						stationName.setTypeface(null, Typeface.BOLD);
						
						
						TextView time = new TextView(getApplicationContext());
						strTime = curs.getString(nTime);
						time.setText(Utils.getFormattedTime(strTime));
					
						if (strTime.equals(super.strStationTime)) {
							stationName.setTextColor(getResources().getColor(R.color.dark_blue));
							time.setTextColor(getResources().getColor(R.color.dark_blue));
						}
						else {
							stationName.setTextColor(getResources().getColor(R.color.dark_gray));
							time.setTextColor(getResources().getColor(R.color.dark_gray));
						}
						
						row.addView(stationName);
						row.addView(time);
						
						table.addView(row);
						
				   } while (curs.moveToNext()); 
				}
				curs.close();
			} catch (Exception e) {	
				Log.e("ERROR", "Error in loop: " + e);
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
			curs = db.getAllStationsInOneTrip(super.lTripOneID);
			db.close();
			startManagingCursor(curs);
		} catch (SQLException e) {
			Log.e("ERROR","Error: " + e.getMessage());
		}
		return curs;
	}
}
