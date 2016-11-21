package com.redblaster.hsl.widget;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.redblaster.hsl.common.LoadableSectorsBuilder;
import com.redblaster.hsl.common.Constants;
import com.redblaster.hsl.common.ErrorMessage;
import com.redblaster.hsl.common.WidgetCache;
import com.redblaster.hsl.dto.BookmarkDTO;
import com.redblaster.hsl.dto.TransportLineDTO;
import com.redblaster.hsl.exceptions.DatabaseException;
import com.redblaster.hsl.main.R;
import com.redblaster.hsl.main.bookmarks.BookmarkDataProvider;

public class HslWidgetProvider extends AppWidgetProvider {
	public static String ACTION_WIDGET_PREV = "ActionReceiverWidgetPrev";
	public static String ACTION_WIDGET_REFRESH = "ActionReceiverWidgetRefresh";
	public static String ACTION_WIDGET_NEXT = "ActionReceiverWidgetNext";
	private static int order = 0;
	private static boolean isNext = true;
	private static boolean isBookmarkExists = true;
	
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	RemoteViews remoteViews = setListenersToButtons(context);
        
        BookmarkDTO bookmark = null;
        appWidgetManager.updateAppWidget(appWidgetIds, (null == bookmark ? remoteViews :  buildUpdate(context,bookmark)));
    }

	/**
	 * Common action.
	 * Assigns pending intent (a-ka listeners) to three button. 
	 * 
	 * @param context
	 * @return Remote View
	 */
	private RemoteViews setListenersToButtons(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
    	
        Intent prevIntent = new Intent(context, HslWidgetProvider.class);
        prevIntent.setAction(ACTION_WIDGET_PREV);
        
        Intent refreshIntent = new Intent(context, HslWidgetProvider.class);
        refreshIntent.setAction(ACTION_WIDGET_REFRESH);
        
        Intent nextIntent = new Intent(context, HslWidgetProvider.class);
        nextIntent.setAction(ACTION_WIDGET_NEXT);

        PendingIntent actionPrevPendingIntent = PendingIntent.getBroadcast(context, 0, prevIntent, 0);
        PendingIntent actionRefreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, 0);
        PendingIntent actionNextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, 0);
        
        remoteViews.setOnClickPendingIntent(R.id.btnWidgetPrevID, actionPrevPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.btnWidgetRefreshID, actionRefreshPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.btnWidgetNextID, actionNextPendingIntent);
		return remoteViews;
	}
    
	private void saveCurrentOrdedIndexToLayout(int index, RemoteViews views) {
		views.setTextViewText(R.id.lastShownId, index + "");
	}
	

	
    @Override
    public void onReceive(Context context, Intent intent) {
		BookmarkDTO bookmark = null;
		ComponentName thisWidget = new ComponentName(context, HslWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
		
		final String action = intent.getAction();
		if (ACTION_WIDGET_PREV.equals(action)) {
        	if (order > 0) {
        		order--;
        		manager.updateAppWidget(thisWidget, activateLoaderIndicator(context));
        		bookmark = this.getBookmark(context, false);
        	}
        }
        else if (ACTION_WIDGET_REFRESH.equals(action)) {
        	manager.updateAppWidget(thisWidget, activateLoaderIndicator(context));
        	bookmark = this.getBookmark(context, true);
        }
        else if (ACTION_WIDGET_NEXT.equals(action)) {
        	if (isNext) {
        		order++;
        		manager.updateAppWidget(thisWidget, activateLoaderIndicator(context));
        		bookmark = this.getBookmark(context, false);
        	}
        }
		
        if (null != bookmark) {	        
			manager.updateAppWidget(thisWidget, this.buildUpdate(context, bookmark));
        }
    	super.onReceive(context, intent);
    }
    
    /**
     * Makes query to the database and collects it as BookmarkDTO container
     * 
     * @param context
     * @param isForceLoadFromDB - does the function force loads bookmark from DB (if TRUE) or it tries to search it in cache (if FALSE)
     * @return bookmarkDTO object
     * @throws DatabaseException 
     */
    private BookmarkDTO getBookmark(Context context, boolean isForceLoadFromDB) {
    	BookmarkDTO bookmarkDto = null;
    	BookmarkDataProvider bookmarkDataProvider = new BookmarkDataProvider(context, true);
		try {
			bookmarkDataProvider.openInternalDatabase();
			
			/*
			 * arrIds[0] = current bookmark ID
			 * arrIds[1] = next bookmark ID
			 */
			long[] arrIds = bookmarkDataProvider.getBookmarkBySequenceIndex(order);
			isNext = (arrIds[1] != -1);
			
			if (arrIds[0] == -1) {
				order = 0;
				/*
				 * if the previous query returns empty result, this doesn't mean, that database is really empty.
				 * Consider situation: user has four bookmarks, displays on the widget last one. Then he deletes last two 
				 * bookmarks and then widget points to the already deleted fourth object. Of course, it will return empty
				 * result. But there are also two first bookmakrs.
				 * 
				 * Here we check this case. is the database really empty or not. If not - make query once again
				 */
				if (bookmarkDataProvider.isDatabaseReallyEmpty()) {
					isBookmarkExists = false;
				}
				else {
					arrIds = bookmarkDataProvider.getBookmarkBySequenceIndex(order);
					isNext = (arrIds[1] != -1);
					
					if (!isForceLoadFromDB) {
						// check, whether cache contains current bookmark. If true - exit from this function
						bookmarkDto = WidgetCache.getInstance().getCachedBookmark(arrIds[0]);
						if (bookmarkDto != null) {
							bookmarkDataProvider.closeInternalDatabase();
							return bookmarkDto;
						}
					}
					
					bookmarkDataProvider.getBookmarkCommonData(arrIds[0]);
				}
			}
			else {
				isBookmarkExists = true;
				
				if (!isForceLoadFromDB) {
					// check, whether cache contains current bookmark. If true - exit from this function
					bookmarkDto = WidgetCache.getInstance().getCachedBookmark(arrIds[0]);
					if (bookmarkDto != null) {
						bookmarkDataProvider.closeInternalDatabase();
						return bookmarkDto;
					}
				}
				
				bookmarkDataProvider.getBookmarkCommonData(arrIds[0]);
			}
			
			bookmarkDataProvider.closeInternalDatabase();
			bookmarkDataProvider.getFullBookmarkData();
		} catch (DatabaseException e) {
			Log.e("Error in DB","Error: " + e.getMessage());
			bookmarkDataProvider.getBookmarkDto().setErrorCode(e.getErrorCode());
		}
		bookmarkDataProvider.closeCursor();
		bookmarkDto = bookmarkDataProvider.getBookmarkDto();
		
		// set updated time
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    	Date date = new Date();
    	bookmarkDto.setLastTimeUpdated(dateFormat.format(date));
		
		// cache retrieved bookmark
		WidgetCache.getInstance().putBookmarkToCache(bookmarkDto);
    	
		return bookmarkDto;
    }
 
    
    
    /**
     * Activates animated icon for loading process and set the word "processing" near it.
     * 
     * @param context
     * @return
     */
    private RemoteViews activateLoaderIndicator(Context context)
	{
    	RemoteViews views = setListenersToButtons(context);
    	views.setImageViewResource(R.id.widgetUpdatedIcon, R.drawable.progress_small);
    	views.setTextViewText(R.id.updatedTime, context.getResources().getString(R.string.processing));
    	
    	this.setPrevButtonVisibility(views, false);
    	views.setViewVisibility(R.id.btnWidgetRefreshID, View.INVISIBLE);
    	this.setNextButtonVisibility(views, false);
    	
    	return views;
	}
    
    /**
     * Redraw whole view. Fills all fields by the data
     * 
     * @param context
     * @param bookmark
     * @return
     */
    private RemoteViews buildUpdate(Context context, BookmarkDTO bookmark)
	{
    	RemoteViews views = setListenersToButtons(context);
    	if (bookmark.getErrorCode() != Constants.DB_ERROR_ALL_RIGHT) {
    		// something happens bag
    		int errMsg = ErrorMessage.getByID(bookmark.getErrorCode()).getMessageResource();
    		fillLayoutIfException(errMsg, views, context);
    	}
    	else if (-1 == bookmark.getId()) {
    		if (isBookmarkExists) {
    			// this bookmark exists, but at this moment its transport lines are not working (may be it's late...)
    			fillLayoutIfBookmarkIsEmpty(bookmark, views, context);
    			
    		} else {
    			// that means, that the there are really no any bookmarks at all.
    			fillLayoutIfNoData(bookmark, views, context);
    		}
    	}
    	else {
    		fillLayoutIfDataAvailable(bookmark, views);
    	}
    	
    	return views;
	}

    /**
     * Fill layout if bookmarks exists, but there are o trips in it (may be it's night?)
     * 
     * @param bookmark
     * @param views
     */
    private void fillLayoutIfBookmarkIsEmpty(BookmarkDTO bookmark, RemoteViews views, Context context) {
    	views.setViewVisibility(R.id.messageId, View.VISIBLE);
    	views.setTextViewText(R.id.messageId, context.getResources().getString(R.string.widget_bookmark_is_empty));
		
    	this._hideTimesOnView(views);

    	// header
		views.setTextViewText(R.id.header, bookmark.getName());
    	
    	// icon
    	LoadableSectorsBuilder bookmarkBuilder = new LoadableSectorsBuilder();
    	views.setImageViewResource(R.id.widgetIcon, bookmarkBuilder.getSetImages().get(bookmark.getImage()));
    	views.setImageViewResource(R.id.widgetUpdatedIcon, R.drawable.updated);
    	views.setViewVisibility(R.id.widgetUpdatedIcon, View.VISIBLE);
    	
    	// set refresh time
    	views.setTextViewText(R.id.updatedTime, bookmark.getLastTimeUpdated());
    	
    	// hide the buttons
    	this.setPrevButtonVisibility(views, (order > 0));
    	views.setViewVisibility(R.id.btnWidgetRefreshID, View.VISIBLE);
    	this.setNextButtonVisibility(views, isNext);
    }

    /**
     * Fill layout if some exception occured
     * 
     * @param bookmark
     * @param views
     */
    private void fillLayoutIfException(int intErrorMsg, RemoteViews views, Context context) {
    	
    	views.setViewVisibility(R.id.messageId, View.VISIBLE);
    	views.setTextViewText(R.id.messageId, context.getResources().getString(intErrorMsg));
		
    	// header
		views.setTextViewText(R.id.header, Constants.STR_EMPTY);
		
		// icon
    	views.setImageViewResource(R.id.widgetIcon, R.drawable.spacer);
    	views.setImageViewResource(R.id.widgetUpdatedIcon, R.drawable.spacer);
    	views.setViewVisibility(R.id.widgetUpdatedIcon, View.INVISIBLE);
    	
    	this._hideTimesOnView(views);
    	
    	// hide the buttons
    	this.setPrevButtonVisibility(views, false);
    	views.setViewVisibility(R.id.btnWidgetRefreshID, View.VISIBLE);
    	this.setNextButtonVisibility(views, false);
    }
    
    /**
     * Fill layout if there are no bookmarks in he database
     * 
     * @param bookmark
     * @param views
     */
    private void fillLayoutIfNoData(BookmarkDTO bookmark, RemoteViews views, Context context) {
    	
    	views.setViewVisibility(R.id.messageId, View.VISIBLE);
    	views.setTextViewText(R.id.messageId, context.getResources().getString(R.string.widget_there_is_no_data));
		
    	// header
		views.setTextViewText(R.id.header, Constants.STR_EMPTY);
		
		// icon
    	views.setImageViewResource(R.id.widgetIcon, R.drawable.spacer);
    	views.setImageViewResource(R.id.widgetUpdatedIcon, R.drawable.spacer);
    	views.setViewVisibility(R.id.widgetUpdatedIcon, View.INVISIBLE);
    	
    	this._hideTimesOnView(views);
    	
    	// hide the buttons
    	this.setPrevButtonVisibility(views, false);
    	views.setViewVisibility(R.id.btnWidgetRefreshID, View.VISIBLE);
    	this.setNextButtonVisibility(views, false);
    }

	/**
	 * "Hides" all elements on view (sets an empty string)
	 * @param views
	 */
	private void _hideTimesOnView(RemoteViews views) {

		// set times
    	views.setTextViewText(R.id.time1, Constants.STR_EMPTY);
    	views.setTextViewText(R.id.time2, Constants.STR_EMPTY);
    	views.setTextViewText(R.id.time3, Constants.STR_EMPTY);
    	views.setTextViewText(R.id.time4, Constants.STR_EMPTY);
    	views.setTextViewText(R.id.time5, Constants.STR_EMPTY);
    	
    	// set refresh time
    	views.setTextViewText(R.id.updatedTime, Constants.STR_EMPTY);
	}
    
	/**
	 * Fill layout if there are any bookmark exists
	 * 
	 * @param bookmark
	 * @param views
	 */
	private void fillLayoutIfDataAvailable(BookmarkDTO bookmark, RemoteViews views) {
		
		views.setViewVisibility(R.id.messageId, View.GONE);
		views.setTextViewText(R.id.header, bookmark.getName());
    	
    	// icon
    	LoadableSectorsBuilder bookmarkBuilder = new LoadableSectorsBuilder();
    	views.setImageViewResource(R.id.widgetIcon, bookmarkBuilder.getSetImages().get(bookmark.getImage()));
    	views.setImageViewResource(R.id.widgetUpdatedIcon, R.drawable.updated);
    	views.setViewVisibility(R.id.widgetUpdatedIcon, View.VISIBLE);
    	
    	// set times
    	Iterator<TransportLineDTO> it = bookmark.getLinesAsSortedList().iterator();
    	views.setTextViewText(R.id.time1, (it.hasNext() ? it.next().getFormattedTimeWithTransportNumber() : Constants.STR_EMPTY));
    	views.setTextViewText(R.id.time2, (it.hasNext() ? it.next().getFormattedTimeWithTransportNumber() : Constants.STR_EMPTY));
    	views.setTextViewText(R.id.time3, (it.hasNext() ? it.next().getFormattedTimeWithTransportNumber() : Constants.STR_EMPTY));
    	views.setTextViewText(R.id.time4, (it.hasNext() ? it.next().getFormattedTimeWithTransportNumber() : Constants.STR_EMPTY));
    	views.setTextViewText(R.id.time5, (it.hasNext() ? it.next().getFormattedTimeWithTransportNumber() : Constants.STR_EMPTY));
    	
    	// show the buttons
    	this.setPrevButtonVisibility(views, (order > 0));
    	views.setViewVisibility(R.id.btnWidgetRefreshID, View.VISIBLE);    	
    	this.setNextButtonVisibility(views, isNext);
    	
    	// set refresh time
    	views.setTextViewText(R.id.updatedTime, bookmark.getLastTimeUpdated());
	}

	/**
	 * Sets visibility of "Prev" button. If isVisible is FALSE, then shows the "disabled" button
	 * 
	 * @param views
	 * @param isVisible
	 */
	private void setPrevButtonVisibility(RemoteViews views, boolean isVisible) {
		views.setViewVisibility(R.id.btnWidgetPrevID, isVisible ? View.VISIBLE : View.GONE);
    	views.setViewVisibility(R.id.btnWidgetPrevDisabledID, isVisible ? View.GONE : View.VISIBLE);
	}

	/**
	 * Sets visibility of "Next" button. If isVisible is FALSE, then shows the "disabled" button
	 * 
	 * @param views
	 * @param isVisible
	 */
	private void setNextButtonVisibility(RemoteViews views, boolean isVisible) {
		views.setViewVisibility(R.id.btnWidgetNextID, isVisible ? View.VISIBLE : View.GONE);
    	views.setViewVisibility(R.id.btnWidgetNextDisabledID, isVisible ? View.GONE : View.VISIBLE);
	}
	
	
    /**
     * {@inheritDoc}
     */
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}
}