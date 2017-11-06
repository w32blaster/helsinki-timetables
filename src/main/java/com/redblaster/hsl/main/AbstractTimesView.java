package com.redblaster.hsl.main;

import android.database.Cursor;
import android.database.SQLException;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapterExternal;
import com.redblaster.hsl.common.Utils;
import com.redblaster.hsl.exceptions.DatabaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
		
		TableLayout table = new TableLayout(this);
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
	private static String getHours(String strVal) {
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

		table.setStretchAllColumns(true);
        table.setShrinkAllColumns(true);

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
							Cell cell = new Cell(btn, strTime);
							lstWorkdays.add(cell);
						}
						
						if (isSat == 1) {
							Button btn = getTableButton(this.getMinutes(strTime), -1L, null, this.getNextViewHeaderName(), -1L, null, -1L, strStationName, strTime, curs.getLong(nTripID), -1L);
							Cell cell = new Cell(btn, strTime);
							lstSat.add(cell);
						}
						
						if (isSun == 1) {
							Button btn = getTableButton(this.getMinutes(strTime), -1L, null, this.getNextViewHeaderName(), -1L, null, -1L, strStationName, strTime, curs.getLong(nTripID), -1L);
							Cell cell = new Cell(btn, strTime);
							lstSun.add(cell);
						}
						
				   } while (curs.moveToNext());
					
					final String strNow = Utils.getRawCurrentTime();

					// now, group all the hours for three days (workdays, saturady and sunday). We need to find the max minutes within whole week
                    Object[] result = groupByHours(lstWorkdays);
                    final Map<Integer, List<Cell>> mapOfWorkdays = (Map<Integer, List<Cell>>) result[0];
                    final int maxMinutesWorkdays = (int) result[1];

                    result = groupByHours(lstSat);
                    final Map<Integer, List<Cell>> mapOfSats = (Map<Integer, List<Cell>>) result[0];
                    final int maxMinutesSats = (int) result[1];

                    result = groupByHours(lstSun);
                    final Map<Integer, List<Cell>> mapOfSuns = (Map<Integer, List<Cell>>) result[0];
                    final int maxMinutesSuns = (int) result[1];

                    final int totalMaxMinutes = Math.max(maxMinutesWorkdays, Math.max(maxMinutesSuns, maxMinutesSats));

					// workdays
                    renderTableSectionWithHeader(table, trParams, strNow, mapOfWorkdays, totalMaxMinutes, R.string.mon_fri);

                    // sat
                    renderTableSectionWithHeader(table, trParams, strNow, mapOfSats, totalMaxMinutes, R.string.saturday);

                    // sun
                    renderTableSectionWithHeader(table, trParams, strNow, mapOfSuns, totalMaxMinutes, R.string.sunday);
                }
				curs.close();
			} catch (Exception e) {	
				Log.e("ERROR", "Error in loop: " + e);
			}
		}
		
	}

    /**
     * Render one section in the table with header (such as "Mon-Fri" and the timetable)
     *
     * @param table
     * @param trParams
     * @param strNow
     * @param mapOfCells
     * @param totalMaxMins
     * @param headerText
     */
    private void renderTableSectionWithHeader(final TableLayout table, final TableRow.LayoutParams trParams, final String strNow, final Map<Integer, List<Cell>> mapOfCells, int totalMaxMins, int headerText) {
        if (!mapOfCells.isEmpty()) {

            TableRow rowSat = new TableRow(this);
            TextView ts = new TextView(this);
            ts.setText(headerText);
            ts.setTextSize(COMPLEX_UNIT_DIP, 17);
            ts.setTextColor(ContextCompat.getColor(this, R.color.dark_gray));

            // set collSpan to this row cell
            ts.setLayoutParams(trParams);
            rowSat.addView(ts);
            table.addView(rowSat);

            this.addTableSegment(mapOfCells, totalMaxMins, table, strNow);

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
			//startManagingCursor(curs);
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
	static class Cell {
		final Button btn;
		final String time; // number, such as "829" or "1659", without any delimeter

        public Cell(final Button btn, final String time) {
            this.btn = btn;
            this.time = time;
        }
    }
	
	/**
	 * Adds one timetable segment to main layout. Timetable usually has three segments:
	 * 1. Workdays
	 * 2. Saturdays
	 * 3. Sundays
	 * 
	 * @param mapOfCells
	 * @param table
	 */
	private void addTableSegment(final Map<Integer, List<Cell>> mapOfCells, int maxMins, TableLayout table, final String strNow) {


        // make sure that we iterate over hours in correct order
        final List<Integer> listHours = Lists.newArrayList(mapOfCells.keySet());
        Collections.sort(listHours);

        // one hour is one table row. The first cell is cell (text), the rest are minutes (buttons)
        for (Integer hour : listHours) {

            TableRow row = this.buildOneHourRow(mapOfCells, maxMins, strNow, hour);
            table.addView(row);
        }
	}

    /**
     * Build one table row that represents one hour.
     *
     * This row has three pars:
     * 1 - the first cell is a hour (number, simple text)
     * 2 - the several cells are minutes (number, buttons)
     * 3 - empty cells as a compensation for width when the row is too short (empty texts)
     *
     * @param mapOfCells
     * @param maxMins
     * @param strNow
     * @param hour
     * @return
     */
    private TableRow buildOneHourRow(Map<Integer, List<Cell>> mapOfCells, int maxMins, String strNow, Integer hour) {
        TableRow row = new TableRow(this);

        // 1. Hour cell:
        TextView t = new TextView(this);
        t.setText(hour + "");
        row.addView(t, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

        // 2. Minutes cells:
        for (Cell cell : mapOfCells.get(hour)) {

            if (isLaterThanCurrentTime(strNow, cell.time)) {
                cell.btn.setTextColor(ContextCompat.getColor(this, (R.color.gray)));
            }

            // minutes button
            row.addView(cell.btn, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        }

        // 3. And here goes the compensation. If we want to make the cells to be equal width, we
        // need to add empty cells. This way we can make all the rows the same size and as result -
        // the same width
        int diff = maxMins - mapOfCells.get(hour).size();
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                row.addView(new TextView(this), new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            }
        }
        return row;
    }

    /**
     * We need to group all the times by hours to correctly display them in the table with times
     *
     * @return Two objects:
     *    [0] - Map<Integer, List<Cell>> - map of cells, grouped by hour
     *    [1] - max minutes (cells, trips) in one hour
     */
	static Object[] groupByHours(final List<Cell> cells) {

	    if (cells.isEmpty())
	        return new Object[]{ ImmutableMap.of(), 0 };

	    int maxMinsInHours = 0;
        final Map<Integer, List<Cell>> mapCells = Maps.newHashMap();

        // iterate over cells and group by hour
        for (Cell cell : cells) {
            final Integer hour = Integer.parseInt(getHours(cell.time));
            if (mapCells.containsKey(hour)) {

                // The current hour exists in a map, then update the cells list
                mapCells.get(hour).add(cell);

                // update the max hours if this line is too long
                if (mapCells.get(hour).size() > maxMinsInHours) {
                    maxMinsInHours = mapCells.get(hour).size();
                }
            }
            else{

                // there is no such hour in the map, lets create new record
                mapCells.put(hour, Lists.newArrayList(cell));
            }
        }

        return new Object[]{ ImmutableMap.copyOf(mapCells), maxMinsInHours };
    }
	
	private boolean isLaterThanCurrentTime(String currentTime, String time) {
		return currentTime.compareTo(time.length() == 3 ? Constants.STR_ZERO + time : time) > 0;
	}
}
