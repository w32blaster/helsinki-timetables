package com.redblaster.hsl.common;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.redblaster.hsl.main.R;

/**
 * Helping class to create a loadable sector view. When it is too slowly to load all data in one query,
 * you may just split it and load only when necessary.
 *
 * This class must contain all functionality for rendering.
 * All business logic must be stored this class's client inside.
 * 
 * @author Ilja Hamalainen
 */
public class LoadableSectorsBuilder {
	
	private LinearLayout linearLayout;
	private TableLayout currentTable;
	private TableRow currentRow;
	private Context context;
	private HashMap<Integer, Integer> setImages = new HashMap<Integer, Integer>();
	private boolean isIconsUsed = true;
	
	/**
	 * Constructor with initialization
	 * @param ln
	 * @param ctx
	 */
	public LoadableSectorsBuilder(Context ctx) {
		this.context = ctx;
		
		this.linearLayout = new LinearLayout(ctx);
		this.linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		buildIconsSet();
	}
	
	/**
	 * Constructor.
	 * 
	 * This constructor prepares only the icon's set. If some function needs take any icon, it may get access
	 * to it via this constructor and it saves memory, because other properties will be without initialization
	 */
	public LoadableSectorsBuilder() {
		buildIconsSet();
	}

	/**
	 * Fills the set of Icons
	 */
	private void buildIconsSet() {
		setImages.put(-1, R.drawable.spacer);
		setImages.put(1, R.drawable.ic_bookmark_1);
		setImages.put(2, R.drawable.ic_bookmark_2);
		setImages.put(3, R.drawable.ic_bookmark_3);
		setImages.put(4, R.drawable.ic_bookmark_4);
		setImages.put(5, R.drawable.ic_bookmark_5);

		setImages.put(6, R.drawable.ic_bookmark_6);
		setImages.put(7, R.drawable.ic_bookmark_7);
		setImages.put(8, R.drawable.ic_bookmark_8);
		setImages.put(9, R.drawable.ic_bookmark_9);
		setImages.put(10, R.drawable.ic_bookmark_10);
		setImages.put(11, R.drawable.ic_bookmark_11);

		setImages.put(12, R.drawable.ic_bookmark_12);
		setImages.put(13, R.drawable.ic_bookmark_13);
		setImages.put(14, R.drawable.ic_bookmark_14);
		setImages.put(15, R.drawable.ic_bookmark_15);
		setImages.put(16, R.drawable.ic_bookmark_16);

		setImages.put(17, R.drawable.ic_bookmark_17);
		setImages.put(18, R.drawable.ic_bookmark_18);
		setImages.put(19, R.drawable.ic_bookmark_19);
		setImages.put(20, R.drawable.ic_bookmark_20);
		setImages.put(21, R.drawable.ic_bookmark_21);

		setImages.put(22, R.drawable.ic_bookmark_22);
		setImages.put(23, R.drawable.ic_bookmark_23);
		setImages.put(24, R.drawable.ic_bookmark_24);
		setImages.put(25, R.drawable.ic_bookmark_25);
		setImages.put(26, R.drawable.ic_bookmark_26);

		setImages.put(27, R.drawable.ic_bookmark_27);
		setImages.put(28, R.drawable.ic_bookmark_28);
		setImages.put(29, R.drawable.ic_bookmark_29);
		setImages.put(30, R.drawable.ic_bookmark_30);
		setImages.put(31, R.drawable.ic_bookmark_31);

		setImages.put(32, R.drawable.ic_bookmark_32);
		setImages.put(33, R.drawable.ic_bookmark_33);
	}
	
	/**
	 * Add one section to be loaded (icon, name and button "expand")
	 * 
	 * @param hasLeftRightMargin - whether layout has left and right margins or not
	 * @param strHeaderName
	 * @param nImage (if -1, then icon will be ignored)
	 */
	public Button addNewLoadableSection(boolean hasLeftRightMargin, String strHeaderName, int nImage, OnClickListener buttonLstnr) {
		
		this.currentRow = new TableRow(this.context);
		this.currentRow.setBackgroundColor(ContextCompat.getColor(this.context, R.color.dark_blue));
		
		// set layout params (margin)
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
		
		if (hasLeftRightMargin) {
			params.setMargins(5, 40, 5, 5);
		}
		else {
			params.setMargins(0, 40, 0, 5);
		}
		this.currentRow.setLayoutParams(params);
		
		// add image
		if (nImage > -1) {
			this.currentRow.addView(this.getImage(nImage));
		}
		else {
			this.isIconsUsed = false;
		}
		
		// add header
		this.currentRow.addView(this.getHeader(strHeaderName));

		// add "expad/collapse" button
		Button btnExpand = this.getCollapsedIcon(buttonLstnr);
		this.currentRow.addView(btnExpand);

		this.currentTable.addView(this.currentRow);
		this.append();
		
		return btnExpand;
	}
		
	/**
	 * Appends to the table one row. String value is set to the center of it (center column) for the styling purposes.
	 * 
	 * @param strValue
	 */
	public void addLineWithSingleColumn(String strValue) {
		this.currentRow = new TableRow(this.context);
		TextView t = this.getSingleTextView(strValue);

		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		lp.column = 1;
		t.setLayoutParams(lp);
		
		this.currentRow.addView(t);
		this.currentTable.addView(this.currentRow);
	}

	/**
	 * @param strValue
	 * @return
	 */
	private TextView getSingleTextView(String strValue) {
		TextView t = new TextView(this.context);
		t.setText(strValue);
		t.setTextColor(this.context.getResources().getColor(R.color.dark_gray));
		
		return t;
	}
	
	/**
	 * Adds new line with animation bar and word "processing"
	 * @param strValue
	 */
	public void addLineWithLoadingBarAndString() {
		this.currentRow = new TableRow(this.context);
		
		LinearLayout ll = new LinearLayout(this.context);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		lp.column = this.isIconsUsed ? 1 : 0;
		ll.setLayoutParams(lp);
		
		// add preloader
		ProgressBar pb = new ProgressBar(this.context, null, android.R.attr.progressBarStyleSmallInverse);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(14, 14);
		layoutParams.setMargins(15, 0, 15, 0);
		pb.setLayoutParams(layoutParams);
		ll.addView(pb);
		
		// add "processing" string
		TextView t = this.getSingleTextView(this.context.getResources().getString(R.string.processing));
		ll.addView(t);
		
		this.currentRow.addView(ll);
		this.currentTable.addView(this.currentRow);
	}
	
	/**
	 * Creates new ROW and adds given view to it
	 * @param v
	 */
	public void addAbstractView(View v) {
		this.currentRow = new TableRow(this.context);
		
		this.currentRow.addView(v);
		this.currentTable.addView(this.currentRow);
	}
	
	/**
	 * Adds error message. Spans all three columns to one and puts a text label to it
	 * 
	 * @param strValue
	 */
	public void addOneSpannedRowWithErrorMessage(String strValue) {
		this.currentRow = new TableRow(this.context);
		
		TextView t = new TextView(this.context);
		t.setText(strValue);
		t.setTextColor(Color.RED);
		
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		lp.span = 3;
		lp.column = 0;
		lp.weight = 1;
		lp.gravity = Gravity.CENTER;
		t.setLayoutParams(lp);
		
		this.currentRow.addView(t);
		this.currentTable.addView(this.currentRow);
	}
	
	/**
	 * Appends one table row with a time and transport number
	 * 
	 * @param strTime
	 * @param strTransportName
	 */
	public void addNewTimeLine(String strTime, String strTransportName) {
		this.addLineWithSingleColumn(Utils.getFormattedTime(strTime) + " (" + strTransportName + ")");
	}
	
	/**
	 * Builds icon
	 * 
	 * @param strImage
	 * @return
	 */
	private ImageView getImage(int nImage) {
		ImageView image = new ImageView(this.context);
		image.setImageResource(this.setImages.get(nImage));

		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 5, 10, 5);
		image.setLayoutParams(lp);
		
		return image;	
	}

	/**
	 * Creates expand/collapse button
	 * 
	 * @return
	 */
	private Button getCollapsedIcon(OnClickListener listener) {
		Button button = new Button(this.context);
		Drawable icon = this.context.getResources().getDrawable(R.drawable.collapsed);
		button.setBackgroundDrawable(icon);
		button.setOnClickListener(listener);
		
		TableRow.LayoutParams lp = new TableRow.LayoutParams(icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
		lp.setMargins(10, 5, 10, 5);
		button.setLayoutParams(lp);
		
		return button;
	}
	
	/**
	 * Changes icon from "collapsed" to "expanded"
	 */
	public void setExpandedIcon() {
		TableRow firstRow = (TableRow) this.currentTable.getChildAt(0);
		if (null != firstRow) {
			Button btn = (Button) firstRow.getChildAt(firstRow.getChildCount() - 1);
			btn.setBackgroundDrawable(this.context.getResources().getDrawable(R.drawable.expanded));
			btn.setEnabled(false);
			btn.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * Builds Bookmark Header
	 * 
	 * @param strBookmark
	 * @return
	 */
	private View getHeader(String strBookmarkName) {
		TextView t = new TextView(this.context);
		t.setText(strBookmarkName);		
		t.setGravity(Gravity.CENTER_VERTICAL);
		t.setTypeface(null, Typeface.BOLD);
		t.setTextColor(Color.WHITE);
		t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT);
		lp.setMargins(5, 5, 10, 5);
		lp.weight = 1;
		t.setLayoutParams(lp);
		return t;
	}
	
	/**
	 * Removes last child (row) from table layout. Normally, last row may contain, for example, message "Processing..."
	 */
	public void removeLastRowFromTable() {
		this.currentTable.removeViewAt(this.currentTable.getChildCount() - 1);
	}
	/**
	 * Adds new bookmark to the storing layout
	 * 
	 * @param linearLayout layout bookmarks append to
	 */
	public void append() {
		if (null != this.currentTable) {
			linearLayout.addView(this.currentTable);
		}
	}
	
	/**
	 * Readers built bookmarks view to the external layout
	 * @param ln
	 */
	public void appendView(LinearLayout ln) {
		ln.addView(this.linearLayout);
	}

	/**
	 * @return the setImages
	 */
	public HashMap<Integer, Integer> getSetImages() {
		return setImages;
	}

	/**
	 * Creates a new instance of bookmark's container (table)
	 * 
	 * @return TableLayout
	 */
	public TableLayout createNewTable() {
		this.currentTable = new TableLayout(this.context);
		return this.currentTable;
	}

	/**
	 * @param currentTable the currentTable to set
	 */
	public void setCurrentTable(TableLayout currentTable) {
		this.currentTable = currentTable;
	}
}