package com.redblaster.hsl.main;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.GZipUtils;
import com.redblaster.hsl.common.Utils;
import com.redblaster.hsl.common.Vehicle;
import com.redblaster.hsl.main.bookmarks.BookmarksView;
import com.redblaster.hsl.main.stations.StationsSearchList;
import com.redblaster.hsl.main.timetables.TimetableTripsView;
import com.redblaster.hsl.main.wizard.WizardStepOne;

/**
 * The very first page
 * 
 * @author Ilja Hamalainen
 *
 */
public class MainPage extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (this.isDatabaseEmpty()) {
        	this.openWizard();
        }
        else {
        	setContentView(R.layout.main);
        	assignToAllButtonsListeners();
        	this.unzipFileIfNeeded();
        }
    }
        /**
     * Assigns to each button it's own listener.
     */
    private void assignToAllButtonsListeners() {
    	for (final Vehicle vehicle : Vehicle.values()) {
    		
        	Button btnBus = (Button) findViewById(vehicle.getButtionId());
    		btnBus.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				openTimetablePage(vehicle.toInt());
    			}
    		});
    		
    	}
    	
    	// action for button "Stations"
    	Button btnGetStation = (Button) findViewById(R.id.btnStations);
    	btnGetStation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openStationsPage();
			}
		});
    	
    	// action for button "Bookmamrks"
    	Button btnBookmarks = (Button) findViewById(R.id.btnBookmarks);
    	btnBookmarks.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openBookmarkPage();
			}
		});
    	
    	// action for button "Update"
    	Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
    	btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToUpdatePageView();
			}
		});
    }
    
    /**
     * Opens Timetable View
     * 
     * @param nVehicleType - vehicle type
     */
    private void openTimetablePage(int nVehicleType) {
    	Intent intent = new Intent();
		intent.setClass(getApplicationContext(), TimetableTripsView.class);
		intent.putExtra(Constants.STR_VEHICLE_TYPE, nVehicleType);
		startActivity(intent);
		finish();
    }

    /**
     * Opens Stations View
     * 
     */
    private void openStationsPage() {
    	Intent intent = new Intent();
		intent.setClass(getApplicationContext(), StationsSearchList.class);
		startActivity(intent);
		finish();
    }

    /**
     * Opens Bookmarks View
     * 
     */
    private void openBookmarkPage() {
    	Intent intent = new Intent();
		intent.setClass(getApplicationContext(), BookmarksView.class);
		startActivity(intent);
		finish();
    }
    
    /**
     * Checks whether the folder is exists. Or it creates this folder otherwise.
     * 
     */
    private void createHSLfolderIfNeeded() {
    	File dbFolder = new File(Utils.getHSLfolderName());
		if (!dbFolder.exists()) {
			if (!dbFolder.mkdir()) {
				Log.e("ERROR","Folder is not created! Source: MainPage.createHSLfolderIfNeeded();");
			}
		}
    	dbFolder = null; 
    }
    
    /**
     * Check, if some .gz file is available at the HSL folder, then extract it as well.
     */
    private void unzipFileIfNeeded() {
    	File gzFile = new File(Utils.getHSLfolderName(), Constants.STR_ARCHIVE_NAME);
    	if (gzFile.exists()) {
    		Utils.log("Found GZ file: " + gzFile.getAbsolutePath());
    		final ProgressDialog pd = ProgressDialog.show(this, getResources().getString(R.string.unzip_process_title), getResources().getString(R.string.unzip_process_descr), true, false);
    		pd.setIcon(android.R.drawable.ic_dialog_info);
    		
    		// extract .gz file in separate thread
    		new Thread() {
                public void run() {
                        try{
                        	File gzFile = new File(Utils.getHSLfolderName(), Constants.STR_ARCHIVE_NAME);
                        	File dbFDir = new File(Utils.getHSLfolderName());
                    		int res = GZipUtils.gunzipFile(gzFile, dbFDir, Constants.DATABASE_NAME + ".sqlite", this);
                        	pd.dismiss();
                            
                        	Message msg = handler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putInt("result", res);
                            msg.setData(b);
                            
                        	handler.sendMessage(msg);
                        	
                        } catch (Exception e) {
                        	Log.e("ERROR","Error in thread: " + e.getMessage());
                        }
                        pd.dismiss();
                }
    		}.start();
    	}
    	gzFile = null;
    }
    
    /**
     * Checks whether the database is empty or not. Empty database means, that
     * this application was launched first time.
     *  
     * @return true
     */
    private boolean isDatabaseEmpty() {
    	File dbFile = null;
    	if (Utils.isSDcardMounted()) {
			dbFile = new File(Utils.getHSLfolderName(), Constants.DATABASE_NAME + ".sqlite");
			
			if (!dbFile.exists()) {
				this.createHSLfolderIfNeeded();
				dbFile = new File(Utils.getHSLfolderName(), Constants.STR_ARCHIVE_NAME);
			}
		} else {
			Log.e("ERROR","SD card is not mounted");
			return true;
		}
		
		return !dbFile.exists();
    }
    
    /**
     * Shows an Wizard, which helps user to download the data and displays any welcome information
     */
    private void openWizard() {
    	Intent intent = new Intent(); 
		intent.setClass(getApplicationContext(), WizardStepOne.class);
		startActivity(intent);
		finish();
    }
    
    /**
     * Handler class for displaying the Toast messages about extracting results
     */
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	int res = msg.getData().getInt("result");

        	switch (res) {
        	
        		case GZipUtils.STATUS_GUNZIP_FAIL: 
        			showDialogDeleteFile(getResources().getString(R.string.unzip_result_fail_extract));
        			break;
        			
        		case GZipUtils.STATUS_IN_FAIL:
        			showDialogDeleteFile(getResources().getString(R.string.unzip_result_fail_in));
        			break;
        			
        		case GZipUtils.STATUS_OK:
        			showToastOK(getResources().getString(R.string.unzip_result_ok));
        			break;
        	}
        }
    };
    
    /**
     * Shows dialog "The file is corrupted. Do you want to delete corrupted file? [Yes] [No]"
     * 
     * @param text
     */
    private void showDialogDeleteFile(CharSequence header) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(header + "\n\n" + getResources().getString(R.string.unzip_result_fail_do_you_want))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								// delete corrupted file from the SD-card
								deleteCorruptedFile();
								dialog.cancel();
								
								// open Wizard
								openWizard();
							}
						})
				.setNegativeButton(getResources().getString(R.string.no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								// Do nothing...
								dialog.cancel();
							}
						});

		builder.create().show();
    }
    
    /**
     * Deleted the corrupted file from the SD-card
     */
    private void deleteCorruptedFile() {
    	File gzFile = new File(Utils.getHSLfolderName(), Constants.STR_ARCHIVE_NAME);
    	if (gzFile.exists()) {
    		if (gzFile.delete()) {
    			this.showToastOK("Ok");
    		}
    	}
    }
    
	/**
	 * Shows the toast message "Extracting is OK."
	 * 
	 */
	private void showToastOK(CharSequence text) {
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(getApplicationContext(), text, duration);
		toast.show();
	}

	/**
	 * Go to the view "Update Database"
	 * 
	 * @param intent
	 */
	private void goToUpdatePageView() {
		Intent intent = new Intent();
		intent.setClass(this, UpdateDatabasePage.class);
		startActivity(intent);
		finish();
	}
}