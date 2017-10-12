package com.redblaster.hsl.main;

import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapterExternal;
import com.redblaster.hsl.common.Utils;
import com.redblaster.hsl.exceptions.DatabaseException;

import java.util.ArrayList;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * This is abstract view of times. It shows table of times like reittiopas book has
 * 
 * @author Ilja Hamalainen
 *
 */
abstract public class AbstractTimesView extends AbstractTimetableView {
	
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
		
		TableLayout table = new TableLayout(getApplicationContext());
        TableRow.LayoutParams tableParams = new TableRow.LayoutParams();
        tableParams.setMargins(5, 10, 5, 10);
        table.setLayoutParams(tableParams);
		table.setId(R.id.table_stations_id);


		linearLayout.addView(table);
	}
	
	/**
	 * Combines the formatted header name for the next view
	 * @return
	 */
	private String getNextViewHeaderName() {
		return String.format(getResources().getString(R.string.header_one_trip), super.strTripName);
	}
	
	/**
	 * Returns hours from the string time
	 * @param strVal
	 * @return
	 */
	private String getHours(String strVal) {
		if (strVal.length() == 3) strVal = Constants.STR_ZERO + strVal;
		String strHrs = strVal.substring(0, 2);
		int h = Integer.parseInt(strHrs);
		if (h >= 24) h = h - 24;
		return String.valueOf(h); 
	}
	
	/**
	 * Return minutes from the string time
	 * @param strVal
	 * @return
	 */
	private String getMinutes(String strVal) {
		return strVal.substring(strVal.length() - 2);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processTheLayoutOperations(Cursor curs) {
		TableLayout table = (TableLayout) findViewById(R.id.table_stations_id);

		// allow all cells to be shrinked, except the first one (hour label)
        table.setColumnShrinkable(0, false);
		table.setColumnShrinkable(1, true);
		table.setColumnShrinkable(2, true);
		table.setColumnShrinkable(3, true);
		table.setColumnShrinkable(4, true);
		table.setColumnShrinkable(5, true);
		table.setColumnShrinkable(6, true);
		table.setColumnShrinkable(7, true);
		table.setColumnShrinkable(8, true);
		table.setColumnShrinkable(9, true);
		table.setColumnShrinkable(10, true);

        String strStationName = null;
		curs.getCount();

		// layout for the row with subtitle, should be spanned across the row
		final TableRow.LayoutParams trParams = new TableRow.LayoutParams();
		trParams.span = 3;
		trParams.topMargin = 20;
		
		if (null != curs) {
			try {
				int nTripID = 0;
				int nStationTime = 1;
				int nIsWorkday = 2;
				int nIsSaturday = 3;
				int nIsSunday = 4;
				
				short isWorkday = -1;
				short isSat = -1;
				short isSun = -1;

				List<Cell> lstWorkdays = new ArrayList<Cell>();
				List<Cell> lstSat = new ArrayList<Cell>();
				List<Cell> lstSun = new ArrayList<Cell>();

				String strTime = "";
				
				if (curs.moveToFirst()) {
					do {
						
						strTime = curs.getString(nStationTime);
						isWorkday = curs.getShort(nIsWorkday);
						isSat = curs.getShort(nIsSaturday);
						isSun = curs.getShort(nIsSunday);
							
						//curs.getInt(nDay);
						
						if (isWorkday == 1) {
							Button btn = getTableButton(this.getMinutes(strTime), -1L, null, this.getNextViewHeaderName(), -1L, null, -1L, strStationName, strTime, curs.getLong(nTripID), -1L);
							Cell cell = new Cell();
							cell.btn = btn;
							cell.time = strTime;
							lstWorkdays.add(cell);
						}
						
						if (isSat == 1) {
							Button btn = getTableButton(this.getMinutes(strTime), -1L, null, this.getNextViewHeaderName(), -1L, null, -1L, strStationName, strTime, curs.getLong(nTripID), -1L);
							Cell cell = new Cell();
							cell.btn = btn;
							cell.time = strTime;
							lstSat.add(cell);
						}
						
						if (isSun == 1) {
							Button btn = getTableButton(this.getMinutes(strTime), -1L, null, this.getNextViewHeaderName(), -1L, null, -1L, strStationName, strTime, curs.getLong(nTripID), -1L);
							Cell cell = new Cell();
							cell.btn = btn;
							cell.time = strTime;
							lstSun.add(cell);	
						}
						
				   } while (curs.moveToNext());
					
					final String strNow = Utils.getRawCurrentTime();
					
					// workdays
					if  (lstWorkdays.size() > 0) {
						TableRow rowWork = new TableRow(getApplicationContext());					
						TextView t = new TextView(getApplicationContext());
						t.setText(R.string.mon_fri);
						t.setTextSize(COMPLEX_UNIT_DIP, 17);
						t.setTextColor(getResources().getColor(R.color.dark_gray));
						
						// set collSpan to this row cell
						t.setLayoutParams(trParams);
						rowWork.addView(t);
						table.addView(rowWork);
						
						this.addTableSegment(lstWorkdays, table, strNow);
					}
					
					// sat
					if (lstSat.size() > 0) {
						TableRow rowSat = new TableRow(getApplicationContext());					
						TextView ts = new TextView(getApplicationContext());
						ts.setText(R.string.saturday);
						ts.setTextSize(COMPLEX_UNIT_DIP, 17);
						ts.setTextColor(getResources().getColor(R.color.dark_gray));
						
						// set collSpan to this row cell
						ts.setLayoutParams(trParams);
						rowSat.addView(ts);					
						table.addView(rowSat);
						
						this.addTableSegment(lstSat, table, strNow);
	
					}
					
					if (lstSun.size() > 0) {
						// sun				
						TableRow rowSun = new TableRow(getApplicationContext());					
						TextView tsu = new TextView(getApplicationContext());
						tsu.setText(R.string.sunday);
						tsu.setTextSize(COMPLEX_UNIT_DIP, 17);
						tsu.setTextColor(getResources().getColor(R.color.dark_gray));
						
						// set collSpan to this row cell
						tsu.setLayoutParams(trParams);
						rowSun.addView(tsu);					
						table.addView(rowSun);
						
						this.addTableSegment(lstSun, table, strNow);
					}
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
			curs = db.getAllTimesForGivenStation(super.lTransportNumberId, super.lStationId, super.lStationStartId);
			db.close();
			startManagingCursor(curs);
		} catch (SQLException e) {
			Log.e("ERROR","Error: " + e.getMessage());
		}
		return curs;
	}
	
	/**
	 * Just a container, representing the cell of table
	 * @author Ilja Hamalainen
	 *
	 */
	private class Cell {
		Button btn;
		String time;
	}
	
	/**
	 * Adds one timetable segment to main layout. Timetable usually has three segments:
	 * 1. Workdays
	 * 2. Saturdays
	 * 3. Sundays
	 * 
	 * @param lstCells
	 * @param table
	 */
	private void addTableSegment(final List<Cell> lstCells, TableLayout table, String strNow) {
		String strHours = "";
		TableRow row = new TableRow(getApplicationContext());

        for (Cell cell : lstCells) {
			if (!strHours.equals(this.getHours(cell.time))) {

				// new line for the next hour
				table.addView(row);
				row = new TableRow(getApplicationContext());

				strHours = this.getHours(cell.time);
				TextView t = new TextView(getApplicationContext());
				t.setText(strHours);
				t.setGravity(Gravity.RIGHT);
				t.setTypeface(null, Typeface.BOLD);
				t.setTextSize(COMPLEX_UNIT_DIP, 18);
				t.setTextColor(getResources().getColor(R.color.dark_blue));
				row.addView(t);
			}
			
			if (isLaterThanCurrentTime(strNow, cell.time)) {
				cell.btn.setTextColor(getResources().getColor(R.color.gray));
			}

			// minutes button
            row.addView(cell.btn);
		}
		table.addView(row);
	}
	
	private boolean isLaterThanCurrentTime(String currentTime, String time) {
		return currentTime.compareTo(time.length() == 3 ? Constants.STR_ZERO + time : time) > 0;
	}
}
