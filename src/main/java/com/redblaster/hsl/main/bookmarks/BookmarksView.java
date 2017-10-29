package com.redblaster.hsl.main.bookmarks;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.layout.items.Breadcrumb;
import com.redblaster.hsl.main.AbstractView;
import com.redblaster.hsl.main.MainPage;
import com.redblaster.hsl.main.R;

import java.util.ArrayList;
import java.util.List;

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
        t.setTextColor(ContextCompat.getColor(this, R.color.dark_gray));
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tlp.setMargins(20, 20, 20, 20);
        t.setLayoutParams(tlp);
        lLayoutBookmarks.addView(t);

        Button btn = new Button(getApplicationContext());
        btn.setText(R.string.menu_add_bookmark);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        blp.gravity = Gravity.CENTER;
        btn.setLayoutParams(blp);
        btn.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.add), null, null, null);
        btn.setCompoundDrawablePadding(10);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                goToBookmarkAddingView(getApplicationContext(), new Intent());
            }

        });
        btn.setGravity(Gravity.CENTER);
        btn.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        lLayoutBookmarks.addView(btn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addLayoutElements(LinearLayout outerLinearLayout) {
        super.isThreadUsed = false;

        LinearLayout lLayoutBookmarks = new LinearLayout(getApplicationContext());
        lLayoutBookmarks.setOrientation(LinearLayout.VERTICAL);
        lLayoutBookmarks.setId(R.id.table_bookmarks_id);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
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

        if (isBookmarksExist) {
            // show the floating button when some bookmarks exist
           this.addFloatingActionButton(super.cl);
        }

        linearLayout.addView(lLayoutBookmarks);
    }

    /**
     * Add floating button on layout
     *
     * @param relativeLayout
     */
    private void addFloatingActionButton(final CoordinatorLayout relativeLayout) {

        // Action button
        FloatingActionsMenu menu = new FloatingActionsMenu(this.getApplicationContext());

        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        lp.anchorGravity = Gravity.BOTTOM | Gravity.RIGHT | Gravity.END;
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT | Gravity.END;

        menu.setLayoutParams(lp);


        // "delete" button
        FloatingActionButton deleteButton = new FloatingActionButton(this.getApplicationContext());
        deleteButton.setIcon(android.R.drawable.ic_menu_delete);
        deleteButton.setTitle(getResources().getString(R.string.menu_delete_bookmark));
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), BookmarksDeleteView.class);
                startActivity(intent);
                finish();
            }
        });
        menu.addButton(deleteButton);

        // "add new bookmark" button
        FloatingActionButton addButton = new FloatingActionButton(this.getApplicationContext());
        addButton.setIcon(android.R.drawable.ic_input_add);
        addButton.setTitle(getResources().getString(R.string.menu_add_bookmark));
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBookmarkAddingView(getApplicationContext(), new Intent());
            }
        });
        menu.addButton(addButton);

        relativeLayout.addView(menu);
    }


    /**
     * Go to the view "Add New Bookmark"
     *
     * @param intent
     */
    private void goToBookmarkAddingView(final Context context, final Intent intent) {
        intent.setClass(context, BookmarksAddNewSelectStation.class);
        startActivity(intent);
        finish();
    }
}