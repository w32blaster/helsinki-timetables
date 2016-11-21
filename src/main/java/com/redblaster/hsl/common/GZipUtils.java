package com.redblaster.hsl.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import android.util.Log;

/**
 * This file contains functionality to working with GZ files
 * (this class is based on one open source project found in the Internet, author is not me)
 * 
 * @author unknown
 *
 */
public class GZipUtils {

	public final static int BUF_SIZE = 8192;

	public final static int STATUS_OK          = 0;
	public final static int STATUS_IN_FAIL     = 4; // No input stream.
	public final static int STATUS_GUNZIP_FAIL = 6; // No decompressed gzip file
	public final static int STATUS_INTERRUPTED = 8; // The process was interrupted

	
	/**
	 * Extracts gzipped file 
	 * 
	 * @param file_input
	 * @param dir_output
	 * @return
	 */
	public static int gunzipFile (File file_input, File dir_output, String file_output_name, Thread currentThread) {
		// Create a buffered gzip input stream to the archive file.
	    GZIPInputStream gzip_in_stream;
	    try {
	      FileInputStream in = new FileInputStream(file_input);
	      BufferedInputStream source = new BufferedInputStream (in);
	      gzip_in_stream = new GZIPInputStream(source);
	    }
	    catch (IOException e) {
	      return STATUS_IN_FAIL;
	    }

	    // Create the decompressed output file.
	    File output_file = new File (dir_output, file_output_name);

	    // Decompress the gzipped file by reading it via
	    // the GZIP input stream. Will need a buffer.
	    byte[] input_buffer = new byte[BUF_SIZE];
	    int len = 0;
	    try {
	      // Create a buffered output stream to the file.
	      FileOutputStream out = new FileOutputStream(output_file);
	      BufferedOutputStream destination = 
	        new BufferedOutputStream (out, BUF_SIZE);

	      // Now read from the gzip stream, which will decompress the data,
	      // and write to the output stream.
	      while ( !currentThread.isInterrupted() && (len = gzip_in_stream.read (input_buffer, 0, BUF_SIZE)) != -1) {
	    	  destination.write (input_buffer, 0, len);
	      }
	      
	      destination.flush(); // Insure that all data is written to the output.
	      out.close ();
	    }
	    catch (IOException e) {
	    	Log.e("GZIP", "Error in gzip: " + e.getMessage());
	    	return STATUS_GUNZIP_FAIL;
	    }

	    try {
	      gzip_in_stream.close();
	    }
	    catch (IOException e) {}
	    
	    // delete archive file
	    try {
			file_input.delete();
		} catch (Exception e) {}

	    if (currentThread.isInterrupted()) {
	    	// the process was interrupted, thus remove resulting file
	    	output_file.delete();
	    	return STATUS_INTERRUPTED;
	    }
	    else {
	    	return STATUS_OK;
	    }

	}
}