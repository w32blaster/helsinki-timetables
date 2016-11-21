package com.redblaster.hsl.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.Vehicle;

public class AbstractTimetableView extends AbstractView {
	protected int nVehicleType;
	protected long lTransportNumberId;
	protected long lTripID;
	protected long lTripOneID;
	protected long lStationId;
	protected long lStationStartId;
	protected String strTransportNumberName;
	protected String strHeaderName;
	protected String strTripName;
	protected String strStationName;
	protected String strStationTime;
	
	
    /**
     * Here we manually set up the types of vehicles, which has splitted trips
     * @return
     */
	@Override
    protected boolean _needToGroupTransportLines() {
    	// right now we need to group results only for trams, metro and ferry
    	return this.nVehicleType == Vehicle.TRAM.toInt() || this.nVehicleType == Vehicle.METRO.toInt() || this.nVehicleType == Vehicle.FARRY.toInt(); 
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.getBundleVariables();
		
		super.onCreate(savedInstanceState);
	}
    
	/**
	 * Combines header for the page with vehicle data.
	 * 
	 * @param vehicleType - (int) type of transport
	 * @param strHeaderText - (String) header text
	 * 
	 * @return LinearLayout
	 */
	protected LinearLayout addHeaderPanel(int vehicleType, String strHeaderText) {
		Drawable drawableVehicleType = null;
    	
    	if (0 < vehicleType) {
			Vehicle vehicle = Vehicle.getById(vehicleType);
			drawableVehicleType = getResources().getDrawable(vehicle.getIco());
    	}
		
    	return addHeaderPanel(drawableVehicleType, strHeaderText);
	}
    
	/**
	 * Combines common header for the page
	 * 
	 * @param drawableIco - (Drawable) icon
	 * @param strHeaderText - (String) header text
	 * 
	 * @return LinearLayout
	 */
    protected LinearLayout addHeaderPanel(Drawable drawableIco, String strHeaderText) {
    	LinearLayout linearLayoutHeader = new LinearLayout(getApplicationContext());
    	linearLayoutHeader.setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	lParams.setMargins(5, 5, 5, 5);
    	linearLayoutHeader.setLayoutParams(lParams);
    	
		// add ICO
    	if (null != drawableIco) {
			ImageView icon = new ImageView(getApplicationContext());
			icon.setImageDrawable(drawableIco);
			linearLayoutHeader.addView(icon);
    	}
	
    	// add header
		TextView text = new TextView(getApplicationContext());
		text.setText(strHeaderText);
		text.setGravity(Gravity.CENTER_VERTICAL);
		text.setId(R.id.view_header_id);
		LinearLayout.LayoutParams lParamsText = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lParamsText.leftMargin = 10;
		text.setLayoutParams(lParamsText);
		text.setTypeface(null, Typeface.BOLD);
		text.setTextColor(getResources().getColor(R.color.dark_blue));
		linearLayoutHeader.addView(text);
		

		return linearLayoutHeader;
    }

    protected String getStr(Bundle extras, String cnst) {
        String strValue = extras.getString(cnst);
        if (null == strValue) strValue = Constants.STR_EMPTY;
        return strValue;
    }
    
    /**
     * Extracts additional internal variables for each timetable view
     */
    protected void getAdditionBundleVariables(Bundle extras) {}
    
	/**
     * Extracts all internal variables for each timetable view
     */
    private final void getBundleVariables() {
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
        	this.getAdditionBundleVariables(extras);
	        nVehicleType = extras.getInt(Constants.STR_VEHICLE_TYPE, -1);
	        lTransportNumberId = extras.getLong(Constants.STR_TRANSPORT_NUMBER_ID, -1);
	        lTripID = extras.getLong(Constants.STR_TRIP_ID, -1);
	        lStationId = extras.getLong(Constants.STR_STATION_ID, -1);
	        lStationStartId = extras.getLong(Constants.STR_STATION_START_ID, -1);
	        lTripOneID = extras.getLong(Constants.STR_TRIP_ONE_NAME, -1);
	        
	        strTransportNumberName = this.getStr(extras, Constants.STR_TRANSPORT_NUMBER_NAME);
	        strHeaderName = this.getStr(extras, Constants.STR_HEADER_NAME);
	        strTripName = this.getStr(extras, Constants.STR_TRIP_NAME);
	        strStationName = this.getStr(extras, Constants.STR_STATION_NAME);
	        strStationTime = this.getStr(extras, Constants.STR_STATION_TIME);
        }
    }
    
	/**
	 * Sets all internal variables to the bundle. It is used to transfer one sort of vars from one view to another.
	 * Repeatable action.
	 * 
	 * @param intent
	 */
    @Override
	protected void setBundleVariables(Intent intent) {
		if (-1 != nVehicleType) intent.putExtra(Constants.STR_VEHICLE_TYPE, nVehicleType);
		if (lTransportNumberId != -1) intent.putExtra(Constants.STR_TRANSPORT_NUMBER_ID, lTransportNumberId);
		if (null != strHeaderName) intent.putExtra(Constants.STR_HEADER_NAME, strHeaderName);
		if (null != strTransportNumberName) intent.putExtra(Constants.STR_TRANSPORT_NUMBER_NAME, strTransportNumberName);
		if (-1 != lTripID) intent.putExtra(Constants.STR_TRIP_ID, lTripID);
		if (null != strTripName) intent.putExtra(Constants.STR_TRIP_NAME, strTripName);
		if (-1 != lStationId) intent.putExtra(Constants.STR_STATION_ID, lStationId);
		if (-1 != lStationStartId) intent.putExtra(Constants.STR_STATION_START_ID, lStationStartId);
		if (null != strStationName) intent.putExtra(Constants.STR_STATION_NAME, strStationName);
		if (null != strStationTime) intent.putExtra(Constants.STR_STATION_TIME, strStationTime);
		if (-1 != lTripOneID) intent.putExtra(Constants.STR_TRIP_ONE_NAME, lTripOneID);
	}
    
    /**
     * Common action.
     * Returns a common instance of button, which looks like a html link.
     * 
     * @param strText
     * @return
     */
    protected Button getAbstractLinkButton(String strText) {
    	Button btn = new Button(getApplicationContext());
    	
    	// set underlined text
		SpannableString content = new SpannableString(strText);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		btn.setText(content);
		btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		
		//set other params
		btn.setTextColor(getResources().getColor(R.color.dark_blue));
		btn.setBackgroundColor(Color.TRANSPARENT);
		btn.setGravity(Gravity.LEFT);
		btn.setPadding(10, 15, 10, 15);
		
		return btn;
    }
    
    /**
     * Set universal button
     * 
     * @param strText
     * @return
     */
    protected Button getTableButton(String strText, final long nTrNumberID, final String strTrNumberName, final String strHeaderame,
    		final long tripId, final String strTrip, final long stationID, final String stationName, final String time, final long tripOneId, final long stationStartID) {
    	
    	final Button btn = this.getAbstractLinkButton(strText);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btn.setBackgroundColor(getResources().getColor(R.color.light_gray));
				
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), getNextActivityClassName());
				intent.putExtra(Constants.STR_VEHICLE_TYPE, nVehicleType);
				intent.putExtra(Constants.STR_TRANSPORT_NUMBER_ID, (nTrNumberID == -1 ? lTransportNumberId : nTrNumberID));
				intent.putExtra(Constants.STR_HEADER_NAME, (null == strHeaderame ? strHeaderName : strHeaderame));
				intent.putExtra(Constants.STR_TRANSPORT_NUMBER_NAME, (null == strTrNumberName ? strTransportNumberName : strTrNumberName));
				intent.putExtra(Constants.STR_TRIP_ID, (tripId == -1 ? lTripID : tripId));
				intent.putExtra(Constants.STR_TRIP_NAME, (null == strTrip ? strTripName : strTrip));
				intent.putExtra(Constants.STR_STATION_ID, (-1 == stationID ? lStationId : stationID));
				intent.putExtra(Constants.STR_STATION_START_ID, (stationStartID == -1 ? lStationStartId : stationStartID));
				intent.putExtra(Constants.STR_STATION_NAME, (null == stationName ? strStationName : stationName));
				intent.putExtra(Constants.STR_STATION_TIME, (null == time ? strStationTime : time));
				intent.putExtra(Constants.STR_TRIP_ONE_NAME, (tripOneId == -1 ? lTripOneID : tripOneId));
				
				startActivity(intent);
				finish();
			}
		});
		
		
    	return btn;
    }
}