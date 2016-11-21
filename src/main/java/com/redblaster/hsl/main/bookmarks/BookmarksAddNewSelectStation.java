package com.redblaster.hsl.main.bookmarks;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractSearchStationView;
import com.redblaster.hsl.main.R;

/**
 * Add new bookmark.
 * View #1: select the station
 * 
 * @author Ilja Hamalainen
 *
 */
public class BookmarksAddNewSelectStation extends AbstractSearchStationView {
	
	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return BookmarksView.class;
	}
	
	@Override
	protected Class<?> getNextActivityClassName() {
		return BookmarksAddNewSelectTrip.class;
	}
	
	@Override
	protected List<Breadcrumb> setListOfBreadcrumbs() {
		List<Breadcrumb> lstBreadcrubms = new ArrayList<Breadcrumb>();
		
		//Bookmark list:
		Breadcrumb brBookmark = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_bookmark, R.drawable.brcrmb_bookmark_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(getPreviuosActivityClassName());
			}
		});
		lstBreadcrubms.add(brBookmark);
		
		Breadcrumb brStations = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_search_station, R.drawable.brcrmb_search_station_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brStations);
		
		return lstBreadcrubms;
	}

	/**
	 * {@inheritDoc}
	 * @throws DatabaseException 
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) throws DatabaseException {
		
		LinearLayout footer = new LinearLayout(getApplicationContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		footer.setLayoutParams(lp);
		footer.setId(R.id.footer_id);
		footer.setBackgroundResource(R.drawable.wizard_bottom_repeatable);
		
		super.addLayoutElements(linearLayout, footer);
	}
}