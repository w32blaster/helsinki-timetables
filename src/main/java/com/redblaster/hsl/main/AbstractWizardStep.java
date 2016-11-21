package com.redblaster.hsl.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.redblaster.hsl.common.Constants;

public class AbstractWizardStep extends Activity {
	private static final int INT_WIZARD_PAGES_COUNT = 5;
	protected int N_SELECTED_WAY_TO_DOWNLOAD = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getDefaultLayout());
		
		setUpParamsFromIntent();
		
		// set page content
		LinearLayout textContainer = (LinearLayout) findViewById(R.id.mainWizardContentID);
		List<View> lstContentItems = this.getContentItems();
		for (View child : lstContentItems) {
			textContainer.addView(child);
		}
		
		// draw steps panel, showing the current page number of wizard (some circles, representing the current wizard progress)
		this.drawStepsPanel();
		
		// set the header text
		Button header = (Button) findViewById(R.id.headerWizardID);
		header.setText(getResources().getText(this.getHeaderText()));
		header.setTypeface(null, Typeface.BOLD);
		header.setTextColor(getResources().getColor(R.color.dark_white));
		
		// set event listener for the "Back" button
		Button back = (Button) findViewById(R.id.btnBackId);
		
		final Class<? extends AbstractWizardStep> backClazz = this.getBackClass();
		if (null == backClazz) {
			back.setVisibility(View.INVISIBLE);
		}
		else {
			back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getEventForBackButton(backClazz);
				}
			});
		}
		
		// set event listener for the "Next" button
		Button next = (Button) findViewById(R.id.btnNextId);
		if (null != getNextClass()) {
			next.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getEventForNextStepButton();
				}
			});
		}
		
		// set the "Next" button text
		next.setText(getResources().getText(getNextButtonLabel()));
	}
	
	/**
	 * Common action for the Back button
	 * @param backClazz
	 */
	protected void getEventForBackButton(final Class<? extends AbstractWizardStep> backClazz) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), backClazz);
		startActivity(intent);
		finish();
	}
	
	/**
	 * Common action for next button: move to the next step.
	 * May be overrided;
	 */
	protected void getEventForNextStepButton() {
		beforeNextStep();
		Intent intent = new Intent();
		setIntentParams(intent);
		intent.setClass(getApplicationContext(), getNextClass());
		startActivity(intent);
		finish();		
	}
	
	/**
	 * Returns default layout
	 * @return
	 */
	protected int getDefaultLayout() {
		return R.layout.wizard;
	}

	/**
	 * Sets all needed params to the Intent in order to get acces on the next step
	 */
	private void setIntentParams(Intent intent) {
		if (this.N_SELECTED_WAY_TO_DOWNLOAD != 0) {
			intent.putExtra(Constants.STR_VEHICLE_TYPE, this.N_SELECTED_WAY_TO_DOWNLOAD);
		}
	}
	
	/**
	 * Gets needed params from Intent and sets it to the class's scope
	 */
	private void setUpParamsFromIntent() {
		Bundle extras = getIntent().getExtras();
		if (null != extras) {
			this.N_SELECTED_WAY_TO_DOWNLOAD = extras.getInt(Constants.STR_VEHICLE_TYPE, 0);
		}
	}
	
	/**
	 * This method will be called before the wizard will go to the next step
	 */
	protected void beforeNextStep() {}
	
	/**
	 * Panel draws the panel with step icons (circles), showing the current startus of wizard 
	 */
	private void drawStepsPanel() {
		int nCurrentStep = this.getCurrentStep();
		LinearLayout stepsContainer = (LinearLayout) findViewById(R.id.botStepsID);
		for (int i = 0; i < INT_WIZARD_PAGES_COUNT; i++) {
			ImageView image = new ImageView(getApplicationContext());
			if (nCurrentStep == (i+1)) {
				image.setImageDrawable(getResources().getDrawable(R.drawable.wizard_active_step));
			}
			else {
				image.setImageDrawable(getResources().getDrawable(R.drawable.wizard_inactive_step));
			}
			stepsContainer.addView(image);
		}
	}
	
	/**
	 * Sets current page
	 * @return
	 */
	protected int getCurrentStep() {
		return 0;
	}
	
	/**
	 * Set the step header text
	 * @return
	 */
	protected int getHeaderText() {
		return 0;
	}
	
	/**
	 * get all content items as a list
	 * @return
	 */
	protected List<View> getContentItems() {
		return new ArrayList<View>();
	}
	
	/**
	 * Returns the class of the "Back" view. If null, the button will be hiden
	 * @return
	 */
	protected Class<? extends AbstractWizardStep> getBackClass() {
		return null;
	}
	
	/**
	 * Returns the class of the "Back" view. If null, the button will be hiden
	 * @return
	 */
	protected Class<?> getNextClass() {
		return null;
	}
	
	/**
	 * Returns the text for "Next" button
	 * @return
	 */
	protected int getNextButtonLabel() {
		return R.string.next;
	}
	
	/**
	 * Returns a main container. You can add your own UI items to this container
	 * @return LinearLayout
	 */
	protected LinearLayout getMainContainer() {
		return (LinearLayout) findViewById(R.id.mainWizardContentID);
	}
}