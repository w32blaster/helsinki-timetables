package com.redblaster.hsl.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;

import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Set of all functionalities to download the Database from the HSL site.
 * @author Ilja Hamalainen
 *
 */
public class DatabaseDownloader {
	private String STR_MD5;
	private String STR_GZ_SIZE;
	private String STR_DB_SIZE;
	
	/**
	 * Loads metadata
	 * 
	 * @return result
	 */
	public boolean getMetadata() {
		
		MetaData metaDataDownloader = new MetaData();
		Map<String, String> mapMetaData = metaDataDownloader.loadMetadata();
		
		if (null != mapMetaData && mapMetaData.size() > 0) {
		
			STR_MD5 = mapMetaData.get("md5");
			STR_DB_SIZE = mapMetaData.get("size-db");
			STR_GZ_SIZE = mapMetaData.get("size-gz");
			
			return true;
		}
		else {
			return false;
		}
	}


	
	/**
	 * Checks available disk space
	 * 
	 * @return
	 */
	public boolean checkAvailableDiscSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getBlockCount();
		
		long dbSize = Long.parseLong(STR_DB_SIZE);
		long gzSize = Long.parseLong(STR_GZ_SIZE);
		
		Utils.log("Available: " + bytesAvailable + "B, GZ size: " + gzSize + "B, database size: " + dbSize);
		
		return (bytesAvailable - (dbSize + gzSize)) > 0;
	}
	
	/**
	 * Downloads archive "hsl.gz" from site to the device.
	 * 
	 * @param handler - sends messages to the UI thread (showing progress state)
	 * @param windowManager 
	 * @return
	 */
	public boolean downloadDatabaseArchieve(Thread currentThread, Handler handler, WindowManager windowManager) {
		int count;
		
        try {
   
        	// get screen resolution
        	DisplayMetrics metrics = new DisplayMetrics();
        	windowManager.getDefaultDisplay().getMetrics(metrics);
        	
        	// generate URL to server and add locale and screen resolution for the Google Analytics
        	final String strURL = String.format(Constants.URL_SITE + Constants.URL_ARCHIVE_URL, Locale.getDefault().toString().replace('_', '-'), 
        			metrics.heightPixels, 
        			metrics.widthPixels);
   
        	URL url = new URL(strURL);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conexion.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(Utils.getHSLfolderName() + "/" + Constants.STR_ARCHIVE_NAME);

            byte data[] = new byte[1024];

            long total = 0;
            int progress = 0;
            
            while ( !currentThread.isInterrupted() && (count = input.read(data)) != -1) {
                total += count;
                progress = (int)(total*100/lenghtOfFile);
                
                // publishing the progress....
                Utils.sendMessage(handler, Constants.INT_HM_DOWNLOAD_ARCHIVE, progress);

                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            
            if (currentThread.isInterrupted()) {
            	// the thread where we tried to download the file was interrupted, so clean up partially downloaded file
            	final String datafile = Utils.getHSLfolderName() + "/" + Constants.STR_ARCHIVE_NAME;
            	new File(datafile).delete();
            }

        } catch (Exception e) {
        	Log.e("ERROR","Error while downloading the archive. Message: " + e);
        	return false;
        }
        return true;
	}
	
	/**
	 * Checks CheckSum (MD5) for just downloaded archive to make sure, that whole file is intact
	 * 
	 * (this function is found from Internet. Author is unknown)
	 * @return
	 */
	public boolean checkTheChecksumOfDownloadedArhcive(Thread currentThread) {
		final String datafile = Utils.getHSLfolderName() + "/" + Constants.STR_ARCHIVE_NAME;
		StringBuffer sb = new StringBuffer("");
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
		    FileInputStream fis = new FileInputStream(datafile);
		    byte[] dataBytes = new byte[1024];
		 
		    int nread = 0; 
		 
		    while ((nread = fis.read(dataBytes)) != -1) {
		      md.update(dataBytes, 0, nread);
		    };
		 
		    byte[] mdbytes = md.digest();
		 
		    //convert the byte to hex format
		    for (int i = 0; i < mdbytes.length; i++) {
		    	if (currentThread.isInterrupted()) break;
		    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		    }
		    
		    Utils.log("Digest(in hex format):: " + sb.toString() + ", originally MD5 is " + this.STR_MD5);
		    
		} catch (NoSuchAlgorithmException e) {
			Log.e("ERROR my","Error in CheckMD5(NoSuchAlgorithmException): " + e.getMessage());
			return false;
		} catch (FileNotFoundException e) {
			Log.e("ERROR my","Error in CheckMD5(FileNotFoundException): " + e.getMessage());
			return false;
		} catch (IOException e) {
			Log.e("ERROR my","Error in CheckMD5(IOException): " + e.getMessage());
			return false;
		}
	    
		return sb.toString().equalsIgnoreCase(STR_MD5);
	}
	
	/**
	 * Unpacks the archive
	 * 
	 * @return
	 */
	public boolean unpackArchive(Thread thread) {
		File gzFile = new File(Utils.getHSLfolderName(), Constants.STR_ARCHIVE_NAME);
    	File dbFDir = new File(Utils.getHSLfolderName());
		int res = GZipUtils.gunzipFile(gzFile, dbFDir, Constants.DATABASE_NAME + ".sqlite", thread);
		return res == GZipUtils.STATUS_OK;
	}
}
