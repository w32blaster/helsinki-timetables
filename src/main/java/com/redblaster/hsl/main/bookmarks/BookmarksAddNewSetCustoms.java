package com.redblaster.hsl.main.bookmarks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.redblaster.hsl.common.LoadableSectorsBuilder;
import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapter;
import com.redblaster.hsl.common.Utils;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractTimetableView;
import com.redblaster.hsl.main.R;

/**
 * Add new bookmark.
 * View #3: add name and icon
 * 
 * @author Ilja Hamalainen
 *
 */
public class BookmarksAddNewSetCustoms extends AbstractTimetableView {
	private final int MAX_NAME_LEHGTH = 30;
	private String strBoomarkName = Constants.STR_EMPTY;
	private long strBoomarkID = -1;
	private final static String STR_KEY_ICONS = "icons";
	private final static String STR_KEY_VALUES = "value";
	private static int selectedIcon = -1;
	private BookmarksAddNewSetCustoms instance = this;
	//private int mSpinnerDropDown;
	
	
	private EditText ed;
	
	/**
	 * {@inheritDoc}
	 */
	protected Class<?> getPreviuosActivityClassName() {
		return BookmarksAddNewSelectTrip.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getNextActivityClassName() {
		return BookmarksView.class;
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
				goToActivity(BookmarksAddNewSelectStation.class);
			}
		});
		lstBreadcrubms.add(brSearchStation);

		//Bookmark list:
		Breadcrumb brTimes = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_list_stations, R.drawable.brcrmb_list_stations_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(getPreviuosActivityClassName());
			}
		});
		lstBreadcrubms.add(brTimes);
		
		// Now is list of all possible routes on this station:
		Breadcrumb brName = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.breadcrumbs_start_page, R.drawable.breadcrumbs_start_page, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brName);
		
		return lstBreadcrubms;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void getAdditionBundleVariables(Bundle extras) {
		strBoomarkName = super.getStr(extras, Constants.STR_BOOKMARK_NAME);
		strBoomarkID = extras.getLong(Constants.STR_BOOKMARK_ID, -1);
		super.getAdditionBundleVariables(extras);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) {
		super.isScrollable = false;
		super.isThreadUsed = false;
		
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		// here we modify our layout to Relative. It it necessary to stick footer to the screen's bottom, even if listView is short.
		RelativeLayout relLayout = new RelativeLayout(getApplicationContext());
		relLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

		// add footer
		relLayout.addView(combineFooter());
		
		// add content to relative layout
		LinearLayout contantHolder = combineContentHolder();
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		rlp.addRule(RelativeLayout.ABOVE, R.id.footer_id);
		rlp.setMargins(10, 10, 10, 10);
		contantHolder.setLayoutParams(rlp);
		relLayout.addView(contantHolder);
		
		linearLayout.addView(relLayout);
	}

	/**
	 * Combines the container, holding all controls (editView and spinner with some labels)
	 * 
	 * @return
	 */
	private LinearLayout combineContentHolder() {
		LinearLayout contentHolder = new LinearLayout(getApplicationContext());
		contentHolder.setOrientation(LinearLayout.VERTICAL);

		// Label:
		TextView tv = new TextView(getApplicationContext());
		tv.setText(R.string.bookmarks_name);
		tv.setTextColor(ContextCompat.getColor(this, R.color.dark_gray));
		contentHolder.addView(tv);

		// Edit field:
		ed = new EditText(getApplicationContext());
		ed.setId(R.id.bookmark_name_id);
		ed.setText(strBoomarkName);
		ed.setTextColor(ContextCompat.getColor(this, R.color.dark_gray));
		contentHolder.addView(ed);

		// Label "select the icon"
		TextView tv2 = new TextView(getApplicationContext());
		tv2.setText(R.string.bookmarks_image);
		tv2.setTextColor(ContextCompat.getColor(this, R.color.dark_gray));
		contentHolder.addView(tv2);
		
		contentHolder.addView(getIconsSpinner());
		return contentHolder;
	}

	/**
	 * Combines footer with buttons
	 * 
	 * @return LinearLayout footer
	 */
	private LinearLayout combineFooter() {
		LinearLayout footer = new LinearLayout(getApplicationContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		footer.setLayoutParams(lp);
		footer.setBackgroundResource(R.drawable.wizard_bottom_repeatable);
		footer.setOrientation(LinearLayout.HORIZONTAL);
		footer.setId(R.id.footer_id);
		
		// add two butoms to footer "back" and "next"
		LinearLayout.LayoutParams butLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		butLP.weight = 1;
		butLP.setMargins(5, 5, 5, 5);
		
		Button btnBack = new Button(getApplicationContext());
		btnBack.setLayoutParams(butLP);
		btnBack.setVisibility(View.INVISIBLE);
		footer.addView(btnBack);
		
		Button btnFinish = new Button(getApplicationContext());
		btnFinish.setText(R.string.finish);
		btnFinish.setGravity(Gravity.CENTER);
		btnFinish.setLayoutParams(butLP);
		btnFinish.setId(R.id.btnNextId);
		btnFinish.setOnClickListener(this.getFinishkButtonListener());
		
		footer.addView(btnFinish);
		return footer;
	}

	/**
	 * Creates a spinner with a list of different images
	 * @return Spinner object
	 */
	private Spinner getIconsSpinner() {
		ArrayList<HashMap<String, Integer>> list = new ArrayList<HashMap<String, Integer>>();
		
		LoadableSectorsBuilder bookmarkBuilder = new LoadableSectorsBuilder();
		HashMap<Integer, Integer> mapIcons = bookmarkBuilder.getSetImages();
		
		// fill the spinner with the list of icons
		for (Map.Entry<Integer, Integer> entry : mapIcons.entrySet()) {
			HashMap<String, Integer> map = new HashMap();
	        map.put(STR_KEY_ICONS, entry.getValue());
	        map.put(STR_KEY_VALUES, entry.getKey());
	        list.add(map);	    
		}
        
		Spinner spinner = new Spinner(this);
		
		MyCustomAdapter adapter = new MyCustomAdapter(this, list,
                R.layout.row_spinner_icon, new String[] { STR_KEY_ICONS, STR_KEY_VALUES },
                new int[] { R.id.icon, R.id.value });
		spinner.setAdapter(adapter);
		
		LinearLayout.LayoutParams spinnerLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		spinner.setLayoutParams(spinnerLP);
		OnItemSelectedListener onSelectSpinner = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedIcon = (int) id;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				selectedIcon = -1;
			}
			
		};
		spinner.setOnItemSelectedListener(onSelectSpinner);
		spinner.setSelection(0);

		return spinner;
	}
	
	/**
	 * creates the listener for the BACK button
	 * @return
	 */
	private OnClickListener getFinishkButtonListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				DBAdapter db = new DBAdapter(instance);
				db.open();
				String strName = ed.getText().toString();
				if (strName.length() > MAX_NAME_LEHGTH) {
					strName = strName.substring(0, MAX_NAME_LEHGTH);
				}
				db.updateBookmarksProperties(strBoomarkID, selectedIcon, strName);
				db.close();
				goToActivity(getNextActivityClassName());
			}
		};
	}
	
	/**
	 * Special adapter to create spinner with images (icons)
	 * 
	 * @author Ilja Hamalainen
	 *
	 */
	private class MyCustomAdapter extends SimpleAdapter {
		private ArrayList<HashMap<String, Integer>> list;
		private Context context;
		
		@SuppressWarnings("unchecked")
		public MyCustomAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
	            super(context, data, resource, from, to);
	            list = (ArrayList<HashMap<String, Integer>>) data;
	            this.context = context;
	    }
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
	            LayoutInflater inflater = LayoutInflater.from(context);
	            convertView = inflater.inflate(R.layout.row_spinner_icon, parent, false);
	        }
			
			View superParent = (View) parent.getParent();
			
			if (superParent != null) {
				ViewGroup.LayoutParams paramsPar = parent.getLayoutParams();
				parent.setLayoutParams(paramsPar);
				parent.requestLayout();
				
				ViewGroup.LayoutParams params = superParent.getLayoutParams();
				superParent.setLayoutParams(params);
				superParent.requestLayout();
			}
			
			LinearLayout.LayoutParams butLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			butLP.setMargins(25, 15, 25, 15);
			butLP.gravity = Gravity.CENTER;
			
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
			icon.setLayoutParams(butLP);
			icon.setImageResource(list.get(position).get(STR_KEY_ICONS));
			//icon.setImageTintList(ContextCompat.getColorStateList(this.context, R.color.dark_gray));
			
			TextView tv = (TextView) convertView.findViewById(R.id.value);
			tv.setText(list.get(position).get(STR_KEY_ICONS).toString());

	        return convertView;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long getItemId(int position) {
			HashMap<String, Integer> map = list.get(position);
			return map.get(STR_KEY_VALUES);
		}
	}

}