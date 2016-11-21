package com.redblaster.hsl.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.redblaster.hsl.common.DBImporter;

/**
 * This page allows user to delete old database and download new one.
 * 
 * @author Ilja Hamalainen
 *
 */
public class UpdateDatabasePage extends Activity {

	private DBImporter traceBuilder;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);
        
        List<View> lstContentItems = new ArrayList<View>();
        LinearLayout textContainer = (LinearLayout) findViewById(R.id.MainUpdateContentId);
        
        // create trace builder
        this.traceBuilder = new DBImporter(this, textContainer);
		traceBuilder.init(lstContentItems, true);
		
		for (View child : lstContentItems) {
			textContainer.addView(child);
		}
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			if (this.traceBuilder.isRunning()) {
				confirmAndInterrupt();
			}
			else {
				goToMainPage();
			}

			return false;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	/**
	 * Shows the confirmation dialog and interrupts the downloading appropriately
	 * 
	 */
	private void confirmAndInterrupt() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.are_you_sure_interrupt_downloading))
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		    	   public void onClick(DialogInterface dialog, int id) {
		    		   traceBuilder.interruptDownloading();
		    		   goToMainPage();
		    	   }
		       })
		       .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		
		builder.create().show();
	}
	
	/**
	 * Redirects to the Main View.
	 */
	private void goToMainPage() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), MainPage.class);
		startActivity(intent);
		finish();
	}
}