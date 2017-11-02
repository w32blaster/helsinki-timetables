package com.redblaster.hsl.main.bookmarks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapter;
import com.redblaster.hsl.common.Utils;
import com.redblaster.hsl.dto.BookmarkDTO;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.R;
import com.redblaster.hsl.main.stations.StationsListOfTrips;

/**
 * Add new bookmark.
 * View #2: select the trip
 * 
 * @author Ilja Hamalainen
 *
 */
public class BookmarksAddNewSelectTrip extends StationsListOfTrips {
	private ListView listView;
	private final String[] columns = new String[] { Constants.KEY_NAME };
    private final int[] names = new int[] { R.id.rowId };

    private List<HashMap<String,String>> lstTransportLines = new ArrayList<HashMap<String,String>>();
    private int nCntChecked = 0;
    private Set<TripContainer> setCheckedTrips = new HashSet<TripContainer>();
    private BookmarksAddNewSelectTrip instance = this;
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return BookmarksAddNewSelectStation.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getNextActivityClassName() {
		return BookmarksAddNewSetCustoms.class;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Breadcrumb> setListOfBreadcrumbs() {
		List<Breadcrumb> lstBreadcrubms = new ArrayList<Breadcrumb>();
		
		//Bookmark list:
		Breadcrumb brBookmark = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_bookmark, R.drawable.brcrmb_bookmark_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(BookmarksView.class);
			}
		});
		lstBreadcrubms.add(brBookmark);
		
		//Bookmark list:
		Breadcrumb brSearchStation = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_search_station, R.drawable.brcrmb_search_station_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(getPreviuosActivityClassName());
			}
		});
		lstBreadcrubms.add(brSearchStation);
		
		// Now is list of all possible routes on this station:
		Breadcrumb brTimes = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_list_stations, R.drawable.brcrmb_list_stations_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brTimes);
		
		return lstBreadcrubms;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processTheLayoutOperations(Cursor curs) {
		
		if (null != curs) {
			try {
				final int nTransportNumberID = 0;
				final int nTransportNumberName = 1;
				final int nStation1Name = 2;
				final int nStation2Name = 3;
				final int nStation1ID = 4;
				final int nStationID = 6; // real station ID
				
				StringBuffer sbName = new StringBuffer();
				
				if (curs.moveToFirst()) {
					do {
						sbName.append(curs.getString(nTransportNumberName));
						sbName.append(" (");
						sbName.append(curs.getString(nStation1Name));
						sbName.append(" >> ");
						sbName.append(curs.getString(nStation2Name));
						sbName.append(")");
						
						HashMap<String,String> map = new HashMap<String,String>();
						map.put(Constants.KEY_ROWID, curs.getString(nTransportNumberID));
						map.put(Constants.KEY_NAME, sbName.toString());
						map.put(Constants.KEY_STATION_START, curs.getString(nStation1ID));
						map.put(Constants.KEY_STATION_ID, curs.getString(nStationID));
						lstTransportLines.add(map);
						sbName.delete(0, sbName.length());
						
				   } while (curs.moveToNext()); 
				}
				curs.close();
			} catch (Exception e) {	
				Log.e("ERROR", "Error in loop: " + e);
			}
		}
		
		listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    listView.setAdapter(new AddStationAdapter(this, lstTransportLines, 
				R.layout.row_list_view,
				columns, 
				names));
	}
	
	/**
	 * {@inheritDoc}
	 * @throws DatabaseException 
	 * @throws SQLException 
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) throws SQLException, DatabaseException {
		super.isScrollable = false;
		super.getAllStationsID();
		
		linearLayout.setOrientation(LinearLayout.VERTICAL);		
		
		// here we modify our layout to Relative. It it necessary to stick footer to the screen's bottom, even if listView is short.
		RelativeLayout relLayout = new RelativeLayout(getApplicationContext());
		relLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		
		listView = new ListView(getApplicationContext());
		listView.setId(R.id.list_stations_id);
		listView.setBackgroundColor(Color.TRANSPARENT);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setDivider(getResources().getDrawable(R.drawable.list_devider));
		
		// add footer to relative layout
		LinearLayout footer = this.combineFooter();
		relLayout.addView(footer);
		
		// add list to relative layout
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);			
		rlp.addRule(RelativeLayout.ABOVE, R.id.footer_id);
		listView.setLayoutParams(rlp);
		relLayout.addView(listView);
		
		linearLayout.addView(relLayout);
	}

	/**
	 * Combines the footer with two buttons
	 * 
	 * @return
	 */
	private LinearLayout combineFooter() {
		LinearLayout footer = new LinearLayout(getApplicationContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		footer.setLayoutParams(lp);
		footer.setId(R.id.footer_id);
		footer.setBackgroundResource(R.drawable.wizard_bottom_repeatable);
		footer.setOrientation(LinearLayout.HORIZONTAL);
		
		// add two buttons to footer "back" and "next"
		LinearLayout.LayoutParams butLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		butLP.weight = 1;
		butLP.setMargins(5, 5, 5, 5);
		
		Button btnBack = new Button(getApplicationContext());
		btnBack.setText(R.string.back);
		btnBack.setGravity(Gravity.CENTER);
		btnBack.setLayoutParams(butLP);
		btnBack.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
		btnBack.setOnClickListener(this.getBackButtonListener());
		footer.addView(btnBack);
		
		Button btnNext = new Button(getApplicationContext());
		btnNext.setText(R.string.next);
		btnNext.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(this, R.drawable.ic_arrow_right_bold), null);
		btnNext.setGravity(Gravity.CENTER);
		btnNext.setLayoutParams(butLP);
		btnNext.setId(R.id.btnNextId);
		btnNext.setEnabled(false);
		btnNext.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
		btnNext.setOnClickListener(this.getNextButtonListener());
		
		footer.addView(btnNext);
		return footer;
	}
	
	/**
	 * Creates listener for NEXT button
	 * @return
	 */
	private OnClickListener getNextButtonListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				BookmarkDTO bookmark = new BookmarkDTO(-1L, strStationName);
				for (TripContainer trip : setCheckedTrips) {
					bookmark.addLine(trip.transport_number_id, trip.station_start_id, trip.station_id);
				}
				
				// put a temporary data to the database, default name is station name.
				// On next screen user may change this default value to custom
				DBAdapter db = new DBAdapter(instance);
				db.open();
				final long newCreatedBookmarkID = db.createBookmark(bookmark);
				db.close();
				Utils.log("Created bookmark ID = " + newCreatedBookmarkID);
				if (-1 != newCreatedBookmarkID) {
					
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), getNextActivityClassName());
					intent.putExtra(Constants.STR_BOOKMARK_ID, newCreatedBookmarkID);
					intent.putExtra(Constants.STR_BOOKMARK_NAME, strStationName);
					setBundleVariables(intent);
					
					startActivity(intent);
					finish();
				}
				else {
					Log.e("ERROR","Inserting returns result -1. Bookmark creation was failed.");
				}
			}
		};
	}
	
	/**
	 * creates the listener for the BACK button
	 * @return
	 */
	private OnClickListener getBackButtonListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToActivity(getPreviuosActivityClassName());
			}
		};
	}
	
	/**
	 * Just a container to store a checked item. We created this class, because transport number ID is not unique value.
	 * Also, this class must contain methods equals() and hashCode(), because we need to implement
	 * hashSet comparition functionality.
	 * 
	 * @author Ilja Hamalainen
	 *
	 */
	private class TripContainer {
		long transport_number_id;
		long station_start_id;
		long station_id;
		
		TripContainer(long trNbmID, long station_id_start, long station_ID) {
			this.transport_number_id = trNbmID;
			this.station_start_id = station_id_start;
			this.station_id = station_ID;
		}
		
		/**
		 * Compares two objects (must be for set.contains() method!)
		 */
		public boolean equals(Object o) {
			if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            
			return (((TripContainer) o).station_start_id == this.station_start_id &&
					((TripContainer) o).transport_number_id == this.transport_number_id);
		}
		
		/**
		 * Returns a hash code (must be for set.contains() method!)
		 */
		public int hashCode () {
			return (int)(5999 - this.transport_number_id + this.station_start_id + (99 * this.station_id));
		}
	}

	/**
	 * Special adapter for the list view. Main feature of this: check, whether the each ckeckbox is selected or not
	 * 
	 * @author Ilja Hamalainen
	 *
	 */
	public class AddStationAdapter extends SimpleAdapter {
		private Context context;
		private int resource;

		public AddStationAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.context = context;
			this.resource = resource;
		}

		@Override
		public View getView(int position, View row, ViewGroup parent) {
			ViewDelWrapper wrapper = null;
			HashMap<String, String> mapping = lstTransportLines.get(position);

			if (null == row) {
				LayoutInflater inflater = LayoutInflater.from(context);
				row = inflater.inflate(resource, null);

				wrapper = new ViewDelWrapper(row);
				row.setTag(wrapper);
			} else {
				wrapper = (ViewDelWrapper) row.getTag();
			}

			// Initialization
			wrapper.getTxtName().setText(mapping.get(Constants.KEY_NAME));
			final long lTransportNbrId = Long.parseLong(mapping.get(Constants.KEY_ROWID));
			final long lStationIdStart = Long.parseLong(mapping.get(Constants.KEY_STATION_START));
			final long lStationId = Long.parseLong(mapping.get(Constants.KEY_STATION_ID));
			
			CheckerListener listener = new CheckerListener(lTransportNbrId, lStationIdStart, lStationId);
			wrapper.getCheckBox().setOnCheckedChangeListener(listener);
			
			if (setCheckedTrips.contains(new TripContainer(lTransportNbrId, lStationIdStart, lStationId))) {
				wrapper.getCheckBox().setChecked(true);
			} else {
				wrapper.getCheckBox().setChecked(false);
			}	
			return row;
		}
	    
		/**
		 * My custom class, which defines a callback-function for the each checkbox
		 * in listView.
		 * Note, taht here we store ID of selected station. We had to do it, because
		 * here we consider all trips, that have a one station's name, but may be several
		 * stations with one name.
		 * 
		 * @author Ilja Hamalainen
		 * 
		 */
		class CheckerListener implements CompoundButton.OnCheckedChangeListener {
			private long lTransportNumberID;
			private long lStationStartID;
			private long lStationID;
			
			/**
			 * initialize constructor
			 * 
			 * @param trNumberID
			 * @param stationStartID
			 */
			public CheckerListener(long trNumberID, long stationStartID, long stationID) {
				this.lTransportNumberID = trNumberID;
				this.lStationStartID = stationStartID;
				this.lStationID = stationID;
			}
			
			/**
			 * Set the button active or inactive, depending on the checked checkboxes count
			 */
			private void setRightButtonState() {
				Button btnDelete = (Button) findViewById(R.id.btnNextId);
				btnDelete.setEnabled(nCntChecked > 0);
			}

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				
				if (isChecked) {
					if (setCheckedTrips.add(new TripContainer(this.lTransportNumberID, this.lStationStartID, this.lStationID))) {
						nCntChecked++;
						setRightButtonState();
					}
				} else {
					if (nCntChecked > 0 && setCheckedTrips.remove(new TripContainer(this.lTransportNumberID, this.lStationStartID, this.lStationID))) {
						nCntChecked--;
						setRightButtonState();
					}
				}
			}
		}

		
		/**
		 * Wrapper. Stores all data
		 * 
		 * @author Ilja Hamalainen
		 * 
		 */
		private class ViewDelWrapper {
			private View base;
			private TextView txtName;
			private CheckBox chBox;

			ViewDelWrapper(View v) {
				this.base = v;
				
				if (null == txtName) {
					txtName = (TextView) this.base.findViewById(R.id.rowNameID);
				}
				
				if (null == chBox) {
					chBox = (CheckBox) this.base.findViewById(R.id.checkBoxForDeleteId);
				}
			}

			/**
			 * Gets a checkbox
			 * 
			 * @return
			 */
			public CheckBox getCheckBox() {
				return chBox;
			}

			/**
			 * @return the txtName
			 */
			public TextView getTxtName() {
				return txtName;
			}
		}
	}
}