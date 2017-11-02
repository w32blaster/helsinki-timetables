package com.redblaster.hsl.main.wizard;

import java.util.ArrayList;
import java.util.List;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redblaster.hsl.main.AbstractWizardStep;
import com.redblaster.hsl.main.R;

/**
 * Page "don't forget to use our widget
 *
 */
public class WizardStepFour extends AbstractWizardStep {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<View> getContentItems() {
		List<View> lstItems = new ArrayList<View>();
		
		//Text:
		TextView text = new TextView(getApplicationContext());
		text.setText(R.string.wizard_page_four_text);
		text.setTextColor(getResources().getColor(R.color.dark_gray));
		lstItems.add(text);
		
		ImageView imgWidget = new ImageView(getApplicationContext());
		imgWidget.setBackgroundResource(R.drawable.widget_screenshot);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		lp.topMargin = 20;
		imgWidget.setLayoutParams(lp);
		lstItems.add(imgWidget);
		
		return lstItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getCurrentStep() {
		return 4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getHeaderText() {
		return R.string.wizard_page_four;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<? extends AbstractWizardStep> getBackClass() {
		return WizardStepThree.class;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getNextClass() {
		return WizardStepFive.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getNextButtonLabel() {
		return R.string.next;
	}
}