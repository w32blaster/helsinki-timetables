package com.redblaster.hsl.main;

import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapterExternal;
import com.redblaster.hsl.exceptions.DatabaseException;

/**
 * Abstract view for searching stations. Here is a listView with a filtering editText.
 * On every user's key pressing this class makes another query to the database (a-ka Google Search)
 * 
 * @author Ilja Hamalainen
 *
 */
public class AbstractSearchStationView extends AbstractTimetableView {
	
	private Cursor curs = null;
	private String strSearchedText = Constants.STR_EMPTY;
	private ListAdapter mAdapter;
	protected ListView listView;
	private DBAdapterExternal db;
	
    final String[] columns = new String[] { Constants.KEY_NAME };
    final int[] names = new int[] { R.id.rowId };
	
    /**
     * Populates the layout. You may override the addLayoutElements(LinearLayout linearLayout) to
     * set your custom footer
     * 
     * @param linearLayout
     * @param footer
     * @throws DatabaseException 
     * @throws SQLException 
     */
    protected void addLayoutElements(LinearLayout linearLayout, LinearLayout footer) throws DatabaseException {
		super.isThreadUsed = false;
		super.isScrollable = false;
		
		linearLayout.setOrientation(LinearLayout.VERTICAL);
	
		EditText searchField = new EditText(this);
        searchField.setWidth(LayoutParams.MATCH_PARENT);
		searchField.addTextChangedListener(watcher);
		searchField.setTextColor(ContextCompat.getColor(this, R.color.dark_gray));
		searchField.setHint(R.string.search_field_hint);
		
		listView = new ListView(getApplicationContext());
		listView.setId(R.id.list_stations_id);
		listView.setBackgroundColor(Color.TRANSPARENT);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setDivider(ContextCompat.getDrawable(this, R.drawable.list_devider));
		
		listView.setOnItemClickListener(new OnItemClickListener() {
            
			@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				curs.close();
				db.close();
				// please, don't be confused: this is not real station ID. This is ONE OF those stations, which have the same name. 
				// On the next screen this variable will be corrected in function getAllStationsID().
				lStationId = id;
				goToActivity(getNextActivityClassName());
            }
        });
		
		db = new DBAdapterExternal(this);
		db.open();
		
		curs = db.getAllStationsWithOneLetter(strSearchedText);
		//startManagingCursor(curs);
		
		this.Load(curs);
		
		if (null != footer) {
			// here we modify our layout to Relative. It it necessary to stick footer to the screen's bottom, even if listView is short.
			RelativeLayout relLayout = new RelativeLayout(getApplicationContext());
			relLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
			// add searching field, wrapped to the RelativeLayout
			RelativeLayout relSearchLayout = new RelativeLayout(getApplicationContext());
			relSearchLayout.setId(R.id.search_field_wrapper_id);
			RelativeLayout.LayoutParams searchlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			relSearchLayout.setLayoutParams(searchlp);
			searchField.setLayoutParams(searchlp);
			relSearchLayout.addView(searchField);
			relLayout.addView(relSearchLayout);
			
			// add footer
			relLayout.addView(footer);
			
			// add list
			RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			llp.addRule(RelativeLayout.BELOW, R.id.search_field_wrapper_id);
			llp.addRule(RelativeLayout.ABOVE, R.id.footer_id);
			listView.setLayoutParams(llp);
			relLayout.addView(listView);
			
			linearLayout.addView(relLayout);
		}
		else {
			// add searched textField to the layout
			linearLayout.addView(searchField);
			
			LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			llp.weight = 1;
			listView.setLayoutParams(llp);
			
			linearLayout.addView(listView);
		}
    }
    
	/**
	 * {@inheritDoc}
	 * @throws DatabaseException 
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) throws DatabaseException {
		this.addLayoutElements(linearLayout, null);
	}
	
	/**
	 * Common function for loading the new adapter to the list
	 * 
	 * @param cursor
	 */
	public void Load(Cursor cursor) {
	    mAdapter = new SimpleCursorAdapter(this, R.layout.row_list, cursor, columns, names);
	    listView.setAdapter(mAdapter);
	}
	
	/**
	 * Watcher for searching field
	 */
	TextWatcher watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			strSearchedText = s.toString();
		
				try {
					curs = db.getAllStationsWithOneLetter(strSearchedText);
				} catch (SQLException e) {
					Log.e("SQL error","afterTextChanged(): SQL error. " + e.getMessage());
				} catch (DatabaseException e) {
					Log.e("DatabaseException","afterTextChanged(): DB error. Code: " + e.getErrorCode() + ", message: " + e.getMessage());
				}
			
			startManagingCursor(curs);
			
			Load(curs);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
	};
}
