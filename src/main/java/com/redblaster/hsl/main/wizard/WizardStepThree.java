package com.redblaster.hsl.main.wizard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBImporter;
import com.redblaster.hsl.main.AbstractWizardStep;
import com.redblaster.hsl.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Import database (automatically download archive and unpack it)
 *
 */
public class WizardStepThree extends AbstractWizardStep {
	
	private DBImporter traceBuilder;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<View> getContentItems() {
		List<View> lstItems = new ArrayList<View>();
		
		if (super.N_SELECTED_WAY_TO_DOWNLOAD == Constants.DOWNLOAD_AUTOMATICALLY) {
			findViewById(R.id.btnNextId).setEnabled(false);
			
			this.traceBuilder = new DBImporter(this, getMainContainer());
			this.traceBuilder.init(lstItems, false);
		}
		else if (super.N_SELECTED_WAY_TO_DOWNLOAD == Constants.DOWNLOAD_MANUALLY) {
			TextView text = new TextView(getApplicationContext());
			text.setText(Html.fromHtml(getResources().getString(R.string.wizard_page_manally_imprting)));
			text.setTextColor(getResources().getColor(R.color.dark_gray));
			lstItems.add(text);
		}
		
		return lstItems;
	}

	/**
	 * Get response from user about the system window "do you allow to write to the SD card?"
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(final int requestCode, final String permissions[], final int[] grantResults) {
		switch (requestCode) {

			case Constants.ALLOW_SD_CARD: {

				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
					Toast.makeText(this, "Granted! :)", Toast.LENGTH_SHORT).show();

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(this, "Sorry, we need your permissions to write database to the SD card", Toast.LENGTH_LONG).show();
				}
				return;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	protected void getEventForBackButton(final Class<? extends AbstractWizardStep> backClazz) {
		
		if (null != traceBuilder && traceBuilder.isRunning()){
			this.showConfirmDialogAndInterruptImporting(backClazz);
		}
		else {
			goBack(backClazz);
		}

	}

	/**
	 * Shows the confirmation dialog "Are you sure?", interrupts the impirting process in case of positive answer 
	 * and navigate to previous view.
	 * 
	 * @param backClazz
	 */
	private void showConfirmDialogAndInterruptImporting(final Class<? extends AbstractWizardStep> backClazz) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage(getResources().getString(R.string.are_you_sure_interrupt_downloading))
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int id) {
		    		   traceBuilder.interruptDownloading();
		    		   goBack(backClazz);
		    	   }
		       })
		       .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		
		builder.create().show();
	}

	private void goBack(final Class<? extends AbstractWizardStep> clazz) {
		super.getEventForBackButton(clazz);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getCurrentStep() {
		return 3;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getHeaderText() {
		return R.string.wizard_page_three;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<? extends AbstractWizardStep> getBackClass() {
		return WizardStepTwo.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getNextClass() {
		return WizardStepFour.class;
	}
}