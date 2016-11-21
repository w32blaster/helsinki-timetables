package com.redblaster.hsl.main.wizard;

import java.util.ArrayList;
import java.util.List;

import android.text.Html;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.main.AbstractWizardStep;
import com.redblaster.hsl.main.R;

public class WizardStepTwo extends AbstractWizardStep {
	RadioButton rbAuto = null;
	RadioButton rbManual = null;
	
	@Override
	protected List<View> getContentItems() {
		List<View> lstItems = new ArrayList<View>();
		
		//Text:
		TextView text = new TextView(getApplicationContext());
		text.setText(getResources().getString(R.string.wizard_page_two_text));
		text.setTextColor(getResources().getColor(R.color.dark_gray));
		lstItems.add(text);

		RadioGroup rg = new RadioGroup(getApplicationContext());
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams( 
                RadioGroup.LayoutParams.FILL_PARENT, 
                RadioGroup.LayoutParams.WRAP_CONTENT); 
        rg.setLayoutParams(layoutParams);
		
		rbAuto = new RadioButton(getApplicationContext());
		rbAuto.setId(R.id.checkbox_auto_id);
		rbAuto.setText(Html.fromHtml(getResources().getString(R.string.wizard_page_two_radio_auto_text)));
		rbAuto.setTextColor(getResources().getColor(R.color.dark_gray));
		rbAuto.setChecked(true);
		RadioGroup.LayoutParams layout = new RadioGroup.LayoutParams( 
                RadioGroup.LayoutParams.WRAP_CONTENT, 
                RadioGroup.LayoutParams.WRAP_CONTENT);
		layout.setMargins(5, 5, 5, 5);
		rg.addView(rbAuto, 0,  layout); 
		
		rbManual = new RadioButton(getApplicationContext());
		rbManual.setId(R.id.checkbox_man_id);
		rbManual.setText(Html.fromHtml(getResources().getString(R.string.wizard_page_two_radio_manu_text)));
		rbManual.setTextColor(getResources().getColor(R.color.dark_gray));
		RadioGroup.LayoutParams layout2 = new RadioGroup.LayoutParams( 
                RadioGroup.LayoutParams.WRAP_CONTENT, 
                RadioGroup.LayoutParams.WRAP_CONTENT);
		layout2.setMargins(5, 5, 5, 5);
		rg.addView(rbManual, 1,  layout2);
		
		lstItems.add(rg);
		
		return lstItems;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getCurrentStep() {
		return 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getHeaderText() {
		return R.string.wizard_page_two;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<? extends AbstractWizardStep> getBackClass() {
		return WizardStepOne.class;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getNextClass() {
		return WizardStepThree.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void beforeNextStep() {
		if (rbAuto.isChecked()) {
			super.N_SELECTED_WAY_TO_DOWNLOAD = Constants.DOWNLOAD_AUTOMATICALLY;
		}
		else if (rbManual.isChecked()) {
			super.N_SELECTED_WAY_TO_DOWNLOAD = Constants.DOWNLOAD_MANUALLY;
		}
	}
}