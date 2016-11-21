package com.redblaster.hsl.layout.items;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TableRow.LayoutParams;

import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.main.R;

/**
 * One item (button) of breadcrumbs panel.
 * 
 * Principle of using this class is simple: create instance of this class via the constructor,
 * which makes initialization of all parametres, and then call method buildItem(), which creates
 * a ready UI object to render on layout.
 * 
 * @author Ilja Hamalainen
 *
 */
public class Breadcrumb {
	private Context context;
	private String text;
	private int position;
	private int drawableIcon;
	private int drawablePressedIcon;
	private Resources resources;
	private boolean last;
	private OnClickListener onClickListener;
	private Button btnItem;
	
	Breadcrumb(Context context, Resources res, String strText, int position, int drawable, OnClickListener onButtonClickListener, int drawablePressed) {
		this.resources = res;
		this.context = context;
		this.text = strText;
		this.position = position;
		this.drawableIcon = drawable;
		this.drawablePressedIcon = drawablePressed;
		this.onClickListener = onButtonClickListener;
		this.setLast(this.position == Constants.BREADCRUMBS_LAST_ITEM);
	}
	
	public Breadcrumb(Context context, Resources res, String text, int position, OnClickListener onButtonClickListener) {
		this(context, res, text, position, -1, onButtonClickListener, -1);
	}
	
	public Breadcrumb(Context context, Resources res, int drawable, int drawablePressed, int position, OnClickListener onButtonClickListener) {
		this(context, res, null, position, drawable, onButtonClickListener, drawablePressed);
	}
	
	/**
	 * Builds ready button for breadcrums panel
	 * 
	 * @return button
	 */
	public Button buildItem() {
		LayoutParams param = null;
		btnItem = new Button(this.context);
		btnItem.setPadding(3, 0, 3, 0);
		btnItem.setGravity(Gravity.CENTER);
		btnItem.setTextColor(getResources().getColor(R.color.dark_blue));
		
		Drawable icon = resources.getDrawable(this.drawableIcon);
		
		switch (position) {
			case Constants.BREADCRUMBS_FIRST_ITEM:
				btnItem.setOnTouchListener(this.getTouchListenerForDrawable());
				btnItem.setBackgroundResource(R.drawable.breadcrumbs_middle_repeatable);
				btnItem.setCompoundDrawablesWithIntrinsicBounds(icon,null, null, null);
				btnItem.setPadding(10, 0, 3, 0);
				param = new LayoutParams(icon.getIntrinsicWidth() + 13, LayoutParams.WRAP_CONTENT);
				break;
	
			case Constants.BREADCRUMBS_LAST_ITEM:
				btnItem.setCompoundDrawablesWithIntrinsicBounds(icon,null, null, null);
				btnItem.setBackgroundResource(R.drawable.breadcrumbs_last_repeatable);
				float weight = 1;
				param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, weight);			
				break;
				
			default:
				btnItem.setBackgroundResource(R.drawable.breadcrumbs_middle_repeatable);
				btnItem.setCompoundDrawablesWithIntrinsicBounds(icon,null, null, null);
				btnItem.setOnTouchListener(this.getTouchListenerForDrawable());
				btnItem.offsetLeftAndRight(0);
				// 6px - padding
				param = new LayoutParams(icon.getIntrinsicWidth() + 6, LayoutParams.WRAP_CONTENT);
					
				break;
		}
		
		btnItem.setLayoutParams(param);
		
		if (null != this.onClickListener) {
			btnItem.setOnClickListener(this.onClickListener);
		}
		return btnItem;
	}

	/**
	 * Returns touch event for button with drawable
	 * 
	 * @return OnTouchListener
	 */
	private OnTouchListener getTouchListenerForDrawable() {
		return new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// here isPressed returns reverse result (TRUE if not pressed)
				if (btnItem.isPressed()) {
					btnItem.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(drawableIcon),null, null, null);
				}
				else {
					btnItem.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(drawablePressedIcon),null, null, null);
				}
				return false;
			}
			
		};
	}
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the drawableIcon
	 */
	public int getDrawableIcon() {
		return drawableIcon;
	}

	/**
	 * @param drawableIcon the drawableIcon to set
	 */
	public void setDrawableIcon(int drawableIcon) {
		this.drawableIcon = drawableIcon;
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

	public void setLast(boolean last) {
		this.last = last;
	}

	public boolean isLast() {
		return last;
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

	/**
	 * @return the btnItem
	 */
	public Button getBtnItem() {
		return btnItem;
	}
}