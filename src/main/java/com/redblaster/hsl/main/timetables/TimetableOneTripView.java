package com.redblaster.hsl.main.timetables;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractOneTripView;
import com.redblaster.hsl.main.R;

/**
 * View for one trip
 * @author Ilja Hamalainen
 *
 */
public class TimetableOneTripView extends AbstractOneTripView {

	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return TimetableTimesView.class;
	}

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
		
		// Trips:
		Breadcrumb brTrips = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_lines, R.drawable.brcrmb_lines_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(TimetableTripsView.class);
			}
		});
		lstBreadcrubms.add(brTrips);
		
		// Directions:
		Breadcrumb brDirs = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_one_directions, R.drawable.brcrmb_one_directions_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(TimetableDirectionsView.class);
			}
		});
		lstBreadcrubms.add(brDirs);
		
		// Stations:
		Breadcrumb brStations = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_list_stations, R.drawable.brcrmb_list_stations_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(TimetableStationsView.class);
			}
		});
		lstBreadcrubms.add(brStations);

		// Times:
		Breadcrumb brTimes = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_one_station, R.drawable.brcrmb_one_station_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(TimetableTimesView.class);
			}
		});
		lstBreadcrubms.add(brTimes);
		
		// Details for one trip now:
		Breadcrumb brTrip = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_one_trip, R.drawable.brcrmb_one_trip_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brTrip);
		
		return lstBreadcrubms;
	}
}