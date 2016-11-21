package com.redblaster.hsl.main.wizard;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.TextView;

import com.redblaster.hsl.main.AbstractWizardStep;
import com.redblaster.hsl.main.R;

public class WizardStepOne extends AbstractWizardStep {

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getContentItems()
	 */
	@Override
	protected List<View> getContentItems() {
		List<View> lstItems = new ArrayList<View>();
		
		//Text:
		TextView text = new TextView(getApplicationContext());
		text.setText(R.string.wizard_page_one_text);
		text.setTextColor(getResources().getColor(R.color.dark_gray));
		lstItems.add(text);
		
		return lstItems;
	}

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getCurrentStep()
	 */
	@Override
	protected int getCurrentStep() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getHeaderText()
	 */
	@Override
	protected int getHeaderText() {
		return R.string.wizard_page_one;
	}

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getNextClass()
	 */
	@Override
	protected Class<?> getNextClass() {
		return WizardStepTwo.class;
	}
}