package com.redblaster.hsl.main.bookmarks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.redblaster.hsl.common.LoadableSectorsBuilder;
import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.DBAdapter;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractView;
import com.redblaster.hsl.main.R;


/**
 * DELETE View.
 * 
 * View for deleting the bookmarks.
 * it is build in the same way, as a BookmarksView, just added one column with the button "Delete".
 * 
 * @author Ilja Hamalainen
 *
 */
public class BookmarksDeleteView extends AbstractView {
	private BookmarksDeleteView instance = this;
	private TableLayout table;
	private int bookmarksCount = 0;
	
	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return BookmarksView.class;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Breadcrumb> setListOfBreadcrumbs() {
		List<Breadcrumb> lstBreadcrubms = new ArrayList<Breadcrumb>();
				
		//Bookmark list:
		Breadcrumb brBookmarks = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_bookmark, R.drawable.brcrmb_bookmark_pressed, Constants.BREADCRUMBS_MIDDLE_ITEM, new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToActivity(getPreviuosActivityClassName());
			}
		});
		lstBreadcrubms.add(brBookmarks);
		
		// Now is delining of old bookmarks:
		Breadcrumb brDelete = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_one_directions, R.drawable.brcrmb_one_directions_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brDelete);
		
		return lstBreadcrubms;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws DatabaseException 
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) throws DatabaseException {
		super.isThreadUsed = false;
				
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		table = new TableLayout(getApplicationContext());
		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams();
		tableParams.setMargins(10, 20, 10, 10);
		tableParams.width = TableRow.LayoutParams.MATCH_PARENT;
		tableParams.height = TableRow.LayoutParams.MATCH_PARENT;
		table.setLayoutParams(tableParams);
		table.setId(R.id.table_directions_id);

		// fill the table by bookmarks objects
		DBAdapter db = new DBAdapter(getApplicationContext());
		db.open();
		Cursor curs = db.getListOfAllBookmarks();
		bookmarksCount = curs.getCount();
		db.close();
		if (null != curs) {
			final int nBookmarkId = 0;
			final int nBookmarkName = 1;
			final int nBookmarkImages = 2;

			LoadableSectorsBuilder bb = new LoadableSectorsBuilder();
			HashMap<Integer, Integer> setImages = bb.getSetImages();
			
			TableRow.LayoutParams lpr = new TableRow.LayoutParams();
			lpr.width = TableRow.LayoutParams.MATCH_PARENT;
			lpr.height = TableRow.LayoutParams.MATCH_PARENT;

			TableRow.LayoutParams lpText = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lpText.weight = 1;
			lpText.leftMargin = 10;
			
			TableRow.LayoutParams ilp = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			ilp.topMargin = 13;
			
			String strBookmarkName = null;
			if (curs.moveToFirst()) {
				do {
					strBookmarkName = curs.getString(nBookmarkName);
					
					TableRow tr = new TableRow(getApplicationContext());
					tr.setLayoutParams(lpr);
					
					ImageView image = new ImageView(getApplicationContext());
					image.setLayoutParams(ilp);
					image.setBackgroundResource(setImages.get(curs.getInt(nBookmarkImages)));
					tr.addView(image);
					
					TextView t = new TextView(getApplicationContext());
					t.setText(strBookmarkName);
					t.setTextColor(getResources().getColor(R.color.dark_gray));
					t.setLayoutParams(lpText);
					tr.addView(t);
					
					Button btn = new Button(getApplicationContext());
					btn.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.delete,0);
					btn.setOnClickListener(getOnClikListener(curs.getLong(nBookmarkId), strBookmarkName, tr));
					tr.addView(btn);
					
					table.addView(tr);
					
				} while (curs.moveToNext()); 
			}
		}
		curs.close();
		linearLayout.addView(table);

		super.addLayoutElements(linearLayout);
	}
	
	/**
	 * Prepares OnClick event for the button "delete"
	 * 
	 * @param id BookmarkID
	 * @param name bookmark name. It is used in confirm message
	 * 
	 * @return OnClickListener
	 */
	private OnClickListener getOnClikListener(final long bookmarkId, final String name, final TableRow tr) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(instance);
				builder.setMessage(String.format(getResources().getString(R.string.dialog_confirm_delete_bookmarks), name))
				       .setCancelable(false)
				       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                deleteTheBookmark(bookmarkId);
				                bookmarksCount--;
				                table.removeView(tr);
				                
				                if (bookmarksCount == 0) {
				                	// redirect to the "Bookmarks View" page:
				                	goToBookmarkView();
				                }
				           }
				       })
				       .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				builder.create().show();
			}
			
		};
	}
	
	/**
	 * Go to the view "Bookmark View" page
	 */
	private void goToBookmarkView() {
		Intent intent = new Intent();
		intent.setClass(this, BookmarksView.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * 
	 * Deletes the bookmark from the database
	 * 
	 * @param bookmark Id
	 * @return result
	 */
	private boolean deleteTheBookmark(long bookmarkId) {
		DBAdapter db = new DBAdapter(getApplicationContext());
		db.open();
		final boolean result = db.deleteBookmark(bookmarkId);
		db.close();
		return result;
	}
}