package com.redblaster.hsl.main.wizard;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBImporter;
import com.redblaster.hsl.main.AbstractWizardStep;
import com.redblaster.hsl.main.R;

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
	 * {@inheritDoc}
	 */	
	@Override
	protected void getEventForBackButton(final Class<? extends AbstractWizardStep> backClazz) {
		
		if (traceBuilder.isRunning()){
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