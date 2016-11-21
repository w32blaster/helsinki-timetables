package com.redblaster.hsl.common;

import java.util.LinkedList;
import java.util.Queue;

import com.redblaster.hsl.dto.BookmarkDTO;

/**
 * Cache for widget. It stores last used bookmarks, in order user doesn't load data every time from the database, but
 * would take it from this storage
 * 
 * @author Ilja Hamalainen
 *
 */
public class WidgetCache {
	private static WidgetCache instance;
	private Queue<BookmarkDTO> queue = new LinkedList<BookmarkDTO>();
	
	private WidgetCache() {};
	
	/**
	 * Provides the singleton functionality
	 * 
	 * @return WidgetCache
	 */
	public static WidgetCache getInstance() {
		if (instance == null) {
			instance = new WidgetCache(); 
		}
		
		return instance;
	}
	
	/**
	 * Checks, whether queue contains the bookmark with given ID
	 * 
	 * @param bookmarkID
	 * @return
	 */
	public boolean isContaining(long bookmarkID) {
		boolean isFound = false;
		
		for (BookmarkDTO bookmark : queue) {
			if (bookmark.getId() == bookmarkID) {
				isFound = true;
				break;
			}
		}
		
		return isFound;
	}
	
	/**
	 * Gets the cached bookmark
	 * 
	 * @param bookmarkID
	 * @return
	 */
	public BookmarkDTO getCachedBookmark(long bookmarkID) {
		BookmarkDTO ret = null;
		
		for (BookmarkDTO bookmark : queue) {
			if (bookmark.getId() == bookmarkID) {
				ret = bookmark;
				Utils.log("Found cached bookmark!");
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * adds the bookmark to the cache queue
	 * 
	 * @param bookmarDto
	 */
	public void putBookmarkToCache(BookmarkDTO bookmarDto) {
		final long boomarkId = bookmarDto.getId();
		if (boomarkId != -1 && !this.isContaining(boomarkId)) {
			queue.add(bookmarDto);
			Utils.log("Added to cache bookmark with ID=" + boomarkId);
			
			if (queue.size() >= Constants.INT_CACHE_SIZE) {
				long id = queue.poll().getId();
				Utils.log("Removed old bookmarks from cache with ID = " + id);
			}
		}
		else {
			Utils.log("Try to add new bookmark, but queue already contains it");
		}
	}
}