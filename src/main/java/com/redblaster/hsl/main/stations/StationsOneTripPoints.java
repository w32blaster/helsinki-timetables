package com.redblaster.hsl.main.stations;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractOneTripView;
import com.redblaster.hsl.main.R;

public class StationsOneTripPoints extends AbstractOneTripView {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return StationsTimesForSelectedTrip.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getNextActivityClassName() {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Breadcrumb> setListOfBreadcrumbs() {
		List<Breadcrumb> lstBreadcrubms = new ArrayList<Breadcrumb>();
		
		// Find station:
		Breadcrumb brStations = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_search_station, R.drawable.brcrmb_search_station_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(StationsSearchList.class);
			}
		});
		lstBreadcrubms.add(brStations);
		
		// Trips:
		Breadcrumb brTrips = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_one_directions, R.drawable.brcrmb_one_directions_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(StationsListOfTrips.class);
			}
		});
		lstBreadcrubms.add(brTrips);
		
		
		// Times for selected list:
		Breadcrumb brTimes = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_one_station, R.drawable.brcrmb_one_station_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(getPreviuosActivityClassName());
			}
		});
		lstBreadcrubms.add(brTimes);
		
		
		// Details for one trip now:
		Breadcrumb brTrip = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_one_trip, R.drawable.brcrmb_one_trip_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brTrip);
		
		return lstBreadcrubms;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LinearLayout getHeaderPanelWithIco() {
		return super.addHeaderPanel(getResources().getDrawable(R.drawable.ico_search), super.strHeaderName);
	}
}