package com.redblaster.hsl.main.wizard;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.main.AbstractWizardStep;
import com.redblaster.hsl.main.MainPage;
import com.redblaster.hsl.main.R;

public class WizardStepFive extends AbstractWizardStep {
	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getContentItems()
	 */
	@Override
	protected List<View> getContentItems() {
		List<View> lstItems = new ArrayList<View>();
		
		//Text:
		TextView text = new TextView(getApplicationContext());
		if (super.N_SELECTED_WAY_TO_DOWNLOAD == Constants.DOWNLOAD_AUTOMATICALLY) {
			text.setText(R.string.wizard_page_five_finished);			
		}
		else if (super.N_SELECTED_WAY_TO_DOWNLOAD == Constants.DOWNLOAD_MANUALLY) {
			text.setText(R.string.wizard_page_five_need_importing);
		}

		text.setTextColor(getResources().getColor(R.color.dark_gray));
		lstItems.add(text);
		
		return lstItems;
	}

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getCurrentStep()
	 */
	@Override
	protected int getCurrentStep() {
		return 5;
	}

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getHeaderText()
	 */
	@Override
	protected int getHeaderText() {
		return R.string.wizard_page_five;
	}

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getBackClass()
	 */
	@Override
	protected Class<? extends AbstractWizardStep> getBackClass() {
		return WizardStepFour.class;
	}
	
	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getBackClass()
	 */
	@Override
	protected Class<?> getNextClass() {
		return MainPage.class;
	}

	/* (non-Javadoc)
	 * @see com.redblaster.hsl.main.AbstractWizardStep#getNextButtonLabel()
	 */
	@Override
	protected int getNextButtonLabel() {
		return R.string.finish;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void getEventForNextStepButton() {
		if (super.N_SELECTED_WAY_TO_DOWNLOAD == Constants.DOWNLOAD_MANUALLY) {
			  super.onDestroy();
		      this.finish();
		}
		else if (super.N_SELECTED_WAY_TO_DOWNLOAD == Constants.DOWNLOAD_AUTOMATICALLY) {
			super.getEventForNextStepButton();
		}
	}
}
