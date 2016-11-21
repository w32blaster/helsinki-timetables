package com.redblaster.hsl.main.stations;

import java.util.ArrayList;
import java.util.List;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractSearchStationView;
import com.redblaster.hsl.main.MainPage;
import com.redblaster.hsl.main.R;

/**
 * View with all possible stations. Here is a list with searching ("filtering") field. 
 * 
 * @author Ilja Hamalainen
 *
 */
public class StationsSearchList extends AbstractSearchStationView {
    
	
	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return MainPage.class;
	}
	
	@Override
	protected Class<?> getNextActivityClassName() {
		return StationsListOfTrips.class;
	}
	
	@Override
	protected List<Breadcrumb> setListOfBreadcrumbs() {
		List<Breadcrumb> lstBreadcrubms = new ArrayList<Breadcrumb>();
		
		Breadcrumb brStations = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_search_station, R.drawable.brcrmb_search_station_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brStations);
		
		return lstBreadcrubms;
	}
}