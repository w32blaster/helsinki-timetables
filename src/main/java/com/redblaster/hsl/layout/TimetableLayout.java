package com.redblaster.hsl.layout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.R;

import java.util.List;

/**
 * Class contains all logic for building the layout of Timetables View
 * 
 * @author Ilja Hamalainen
 *
 */
public class TimetableLayout {
	private Context context;
	private Resources resources;
	private List<Breadcrumb> breadCrumbs;
	private OnClickListener onClickListener;
	
	public TimetableLayout(Context ctx, Resources res, List<Breadcrumb> lstBreadcrubms, OnClickListener goToFirstPage) {
		this.context = ctx;
		this.resources = res;
		this.breadCrumbs = lstBreadcrubms;
		this.onClickListener = goToFirstPage;
	}
	
	public TimetableLayout(Context ctx, Resources res) {
		this.context = ctx;
		this.resources = res;
	}
	
	/**
	 * Adds a foo-button with arrow image.
	 * 
	 * @param isLast - is this is the last arrow in the chain
	 * @return
	 */
	private Button createBreadcrumbsArrow(final boolean isLast) {
		final Button arrow = new Button(this.context);

		final Drawable arrowIcon = isLast ?
			this.context.getDrawable(R.drawable.breadcrumbs_last_arrow) :
		 	this.context.getDrawable(R.drawable.breadcrumbs_middle_arrow);

		arrow.setBackground(arrowIcon);
		arrow.setPadding(0, 0, 0, 0);
		arrow.setEnabled(false);

		arrow.setLayoutParams(new TableRow.LayoutParams(arrowIcon.getIntrinsicWidth(), TableRow.LayoutParams.WRAP_CONTENT));

		return arrow;
	}
	
	/**
	 * Adds the breadcrumbs items to the container
	 * 
	 * @param brcrPanel
	 */
	private void addBreadCrumbsItemsToPanel(LinearLayout brcrPanel) {
		//get the first item which links to the main page. This items must be always
		Breadcrumb brcrMainPage = new Breadcrumb(this.context, this.resources, R.drawable.breadcrumbs_start_page, R.drawable.breadcrumbs_start_page_pressed, Constants.BREADCRUMBS_FIRST_ITEM, onClickListener);
		brcrPanel.addView(brcrMainPage.buildItem());
		
		// get user defined breadcrumbs
		if (!this.breadCrumbs.isEmpty()) {
			for (Breadcrumb item : this.breadCrumbs) {
				brcrPanel.addView(this.createBreadcrumbsArrow(item.isLast()));
				brcrPanel.addView(item.buildItem());
			}
		}
	}
	
	/**
	 * Main method of this builder: it returns a ready layout of page
	 * @return
	 */
	public LinearLayout build() {

		LinearLayout linear = new LinearLayout(this.context);
    	linear.setId(R.id.global_container_id);

		linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		linear.setGravity(Gravity.FILL);

		linear.setOrientation(LinearLayout.VERTICAL);


		//Add a breadcrumbs panel
		TableLayout breadcrumbsPanel = new TableLayout(this.context);
		breadcrumbsPanel.setPadding(0, 0, 0, 0);
		TableRow tableRow = new TableRow(this.context);
		tableRow.setPadding(0, 0, 0, 0);
		this.addBreadCrumbsItemsToPanel(tableRow);
		breadcrumbsPanel.addView(tableRow);
		linear.addView(breadcrumbsPanel);

		return linear;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the resources
	 */
	public Resources getResources() {
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(Resources resources) {
		this.resources = resources;
	}

	/**
	 * @return the breadCrumbs
	 */
	public List<Breadcrumb> getBreadCrumbs() {
		return breadCrumbs;
	}

	/**
	 * @param breadCrumbs the breadCrumbs to set
	 */
	public void setBreadCrumbs(List<Breadcrumb> breadCrumbs) {
		this.breadCrumbs = breadCrumbs;
	}

	/**
	 * @param onClickListener the onClickListener to set
	 */
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	/**
	 * @return the onClickListener
	 */
	public OnClickListener getOnClickListener() {
		return onClickListener;
	}
}