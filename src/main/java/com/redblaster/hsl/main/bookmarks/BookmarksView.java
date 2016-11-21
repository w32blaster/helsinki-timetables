package com.redblaster.hsl.main.bookmarks;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractView;
import com.redblaster.hsl.main.MainPage;
import com.redblaster.hsl.main.R;

public class BookmarksView extends AbstractView {
	private BookmarkDataProvider bookmarkDataProvider;
	private boolean isBookmarksExist = false;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getPreviuosActivityClassName() {
		return MainPage.class;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Breadcrumb> setListOfBreadcrumbs() {
		List<Breadcrumb> lstBreadcrubms = new ArrayList<Breadcrumb>();
		
		Breadcrumb brStations = new Breadcrumb(getApplicationContext(), getResources(), R.drawable.brcrmb_bookmark, R.drawable.brcrmb_bookmark_pressed, Constants.BREADCRUMBS_LAST_ITEM, null);
		lstBreadcrubms.add(brStations);
		
		return lstBreadcrubms;
	}

	/**
	 * Appends to layout text "you don't have bookmarks.." and
	 * the simple button to add new one
	 */
	private void appendToLayoutTextAndButton(LinearLayout lLayoutBookmarks) {
		TextView t = new TextView(getApplicationContext());
		t.setText(R.string.bookmarks_you_dont_have_bookmarks);
		t.setGravity(Gravity.CENTER);
		t.setTextColor(getResources().getColor(R.color.dark_gray));
		LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		tlp.setMargins(20, 20, 20, 20);
		t.setLayoutParams(tlp);
		lLayoutBookmarks.addView(t);
		
		Button btn = new Button(getApplicationContext());
		btn.setText(R.string.menu_add_bookmark);
		LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		blp.gravity = Gravity.CENTER;
		btn.setLayoutParams(blp);
		btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.add), null, null, null);
		btn.setCompoundDrawablePadding(10);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToBookmarkAddingView(new Intent());
			}
			
		});
		btn.setGravity(Gravity.CENTER);
		lLayoutBookmarks.addView(btn);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addLayoutElements(LinearLayout linearLayout) {
		super.isThreadUsed = false;
		
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout lLayoutBookmarks = new LinearLayout(getApplicationContext());
		lLayoutBookmarks.setOrientation(LinearLayout.VERTICAL);
		lLayoutBookmarks.setId(R.id.table_bookmarks_id);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		lp.setMargins(10, 10, 10, 10);
		lLayoutBookmarks.setLayoutParams(lp);

		// here we load from the internal database only the bookmark's names and images
		bookmarkDataProvider = new BookmarkDataProvider(getApplicationContext(), false);
		bookmarkDataProvider.openInternalDatabase();
		isBookmarksExist = bookmarkDataProvider.retrieveOnlyBookmarksNames();
		bookmarkDataProvider.closeInternalDatabase();
		
		if (isBookmarksExist) {
			bookmarkDataProvider.renderCollectedBookmarks(lLayoutBookmarks);
		}
		else {
			this.appendToLayoutTextAndButton(lLayoutBookmarks);
		}
		
		linearLayout.addView(lLayoutBookmarks);
	}

	/**
	 * Creates the menu items
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, Constants.MENU_ADD_BOOKMARK, 0, R.string.menu_add_bookmark).setIcon(android.R.drawable.ic_menu_add);
	    menu.add(0, Constants.MENU_DELETE_BOOKMARK, 1, R.string.menu_delete_bookmark).setIcon(android.R.drawable.ic_menu_delete).setEnabled(isBookmarksExist);
	    return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(); 
		switch (item.getItemId()) {
		    case Constants.MENU_ADD_BOOKMARK:
		    	goToBookmarkAddingView(intent);
				break;
			
		    case Constants.MENU_DELETE_BOOKMARK:
				intent.setClass(getApplicationContext(), BookmarksDeleteView.class);
				startActivity(intent);
				finish();
		    	break;
		}
	    return false;
	}

	/**
	 * Go to the view "Add New Bookmark"
	 * 
	 * @param intent
	 */
	private void goToBookmarkAddingView(Intent intent) {
		intent.setClass(this, BookmarksAddNewSelectStation.class);
		startActivity(intent);
		finish();
	}
}