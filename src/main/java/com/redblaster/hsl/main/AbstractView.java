package com.redblaster.hsl.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.ErrorMessage;
import com.redblaster.hsl.common.Utils;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.layout.TimetableLayout;
import com.redblaster.hsl.layout.items.Breadcrumb;

import java.util.ArrayList;
import java.util.List;

public class AbstractView extends AppCompatActivity implements Runnable{
	protected static int BREADCRUMBS_ACTUAL_HEIGHT = 0;
	private static List<Breadcrumb> lstBreadcrumbs;
	private static OnClickListener goToMainPage;
	private static TimetableLayout timetableLayoutBuilder;
	protected boolean isGrouped = false;
	protected LinearLayout linearLayout;
	protected CoordinatorLayout cl; // compatibility layout to attach some widgets from new Android
	protected boolean isScrollable = true;
	
	protected static ProgressDialog pd;
	protected boolean isThreadUsed = true;
	protected Cursor c;
	
	/**
	 * Set custom rules, whether the list must be grouped or not
	 * @return
	 */
	protected boolean _needToGroupTransportLines() {
		return false;
	}
	
    /**
     * Set the current set of breadcrumbs without the first item
     */
    protected List<Breadcrumb> setListOfBreadcrumbs() {
    	return new ArrayList<Breadcrumb>();
    }
    
    protected void startThread() {
		Thread thread = new Thread(this);
		thread.start();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		cl = new CoordinatorLayout(this);

		lstBreadcrumbs = this.setListOfBreadcrumbs();
        goToMainPage = this.getListenerGoToMainPage();
        isGrouped = _needToGroupTransportLines();
        
        linearLayout = createTimetableLayout(getApplicationContext(), getResources());
        
        LinearLayout linearLayoutContent = new LinearLayout(getApplicationContext());
		linearLayoutContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		linearLayoutContent.setGravity(Gravity.FILL);
        
        try {
			this.addLayoutElements(linearLayoutContent);
		} catch (DatabaseException e) {
			// if exception occured while layout building, then prevent starting the thread and show error message
			isThreadUsed = false;
			isScrollable = false;
			addErrorMessageToLayout(e.getErrorCode());
		}
        
        // Wrap content to the ScrollView if needed
        if (isScrollable) {
        	ScrollView scrollArea = new ScrollView(getApplicationContext());
        	scrollArea.addView(linearLayoutContent);
        	linearLayout.addView(scrollArea);
        }
        else {
        	linearLayout.addView(linearLayoutContent);
        }

        cl.addView(linearLayout);
        setContentView(cl);
        
        if (isThreadUsed) {
            pd = ProgressDialog.show(this, null, getResources().getString(R.string.loading_descr), true, false);
            this.startThread();
        }
    }

	/**
     * Builds the layout for this view
     * 
     * @param context
     * @param resources
     * @return
     */
    public static final LinearLayout createTimetableLayout(Context context, Resources resources) {
		timetableLayoutBuilder = new TimetableLayout(context, resources, lstBreadcrumbs, goToMainPage);
    	return timetableLayoutBuilder.build();
    }
    
    /**
     * Add custom Layout elements for each different view
     * @param linearLayout
     * @throws DatabaseException 
     */
    protected void addLayoutElements(LinearLayout linearLayout) throws DatabaseException {}
    
	/**
	 * Move to the very first page of this application
	 */
	private OnClickListener getListenerGoToMainPage() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainPage.class);
				startActivity(intent);
				finish();
			}
		};
	}

	/**
	 * Process the query for the separate thread 
	 * @return
	 */
	protected Cursor processTheDatabaseQuery() throws DatabaseException{
		return null;
	}
	
	/**
	 * Putting the data to the layout. Method will be called by the thread
	 */
	protected void processTheLayoutOperations(Cursor c) {}
	
	@Override
	public void run() {
		int result = 0;
		try {
			c = processTheDatabaseQuery();
		} catch (DatabaseException e) {
			result = e.getErrorCode();
		}
		Utils.sendMessage(handler, 0, result);
	}

	/**
	 * @return the timetableLayoutBuilder
	 */
	protected TimetableLayout getTimetableLayoutBuilder() {
		return timetableLayoutBuilder;
	}
	
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int result = msg.getData().getInt(Constants.STR_HANDLER_MESSAGE_VALUE);
			
			if (null != c && result == 0 && c.getCount() > 0) {
				// all is fine
				processTheLayoutOperations(c);
			}
			else if (null == c || c.getCount() == 0) {
				// the database is empty.
				addErrorMessageToLayout(Constants.DB_ERROR_DATABASE_IS_EMPTY);
			}
			else {
				// something going wrong here...
				addErrorMessageToLayout(result);
			}
		    pd.dismiss();
		}
	};
	
	/**
	 * Adds a label to layout with an error message
	 */
	protected void addErrorMessageToLayout(int errorCode) {
		TextView v = new TextView(getApplicationContext());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		lp.setMargins(20, 20, 20, 20);
		v.setLayoutParams(lp);
		int resMsg = ErrorMessage.getByID(errorCode).getMessageResource();
		v.setText(getResources().getString(resMsg));
		v.setTextColor(Color.RED);
		linearLayout.addView(v);
	}
	
    /**
     * Returns Activity class for nect view
     * 
     * @return
     */
    protected Class<?> getNextActivityClassName() {
    	return null;
    }
    
    /**
     * Returns Activity class for previuos view
     * 
     * @return
     */
    protected Class<?> getPreviuosActivityClassName() {
    	return null;
    }
    
    /**
     * Overridable method.
     * Sets a set of repeatable parameters, which migrated from one view to another.
     * 
     * @param intent
     */
    protected void setBundleVariables(Intent intent) {};
    
    /**
     * Implementation for moving to the previous view
     */
	protected void goToActivity(Class<?> className) {
		if (className != null) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), className);
			this.setBundleVariables(intent);
			startActivity(intent);
			finish();			
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goToActivity(getPreviuosActivityClassName());
			return false;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
