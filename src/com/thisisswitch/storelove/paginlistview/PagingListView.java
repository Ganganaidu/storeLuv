package com.thisisswitch.storelove.paginlistview;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


public class PagingListView extends ListView {

	public interface Pagingable {
		void onLoadMoreItems();
		void onPauseItemsLoad(boolean isLoading);
	}

	private boolean isLoading;
	private boolean hasMoreItems;
	private Pagingable pagingableListener;
	private LoadingView loadinView;

	public PagingListView(Context context) {
		super(context);
		init();
	}

	public PagingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PagingListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public boolean isLoading() {
		return this.isLoading;
	}

	public void setIsLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public void setPagingableListener(Pagingable pagingableListener) {
		this.pagingableListener = pagingableListener;
	}

	public void setHasMoreItems(boolean hasMoreItems) {
		this.hasMoreItems = hasMoreItems;
		if(!this.hasMoreItems) {
			removeFooterView(loadinView);
		}
	}

	public boolean hasMoreItems() {
		return this.hasMoreItems;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onFinishLoading(boolean hasMoreItems, List<? extends Object> newItems) {
		setHasMoreItems(hasMoreItems);
		setIsLoading(false);
		if(newItems != null && newItems.size() > 0) {
			ListAdapter adapter = ((HeaderViewListAdapter)getAdapter()).getWrappedAdapter();
			if(adapter instanceof PagingBaseAdapter ) {
				((PagingBaseAdapter)adapter).addMoreItems(newItems);
			}
		}
	}

	private void init() {
		isLoading = false;
		loadinView = new LoadingView(getContext());
		addFooterView(loadinView);
		setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//DO NOTHING...
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					//imageLoader.resume();
					if(pagingableListener != null) {
						pagingableListener.onPauseItemsLoad(false);
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					if(pagingableListener != null) {
						pagingableListener.onPauseItemsLoad(true);
					}
					//					if (pauseOnScroll) {
					//						imageLoader.pause();
					//					}
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					if(pagingableListener != null) {
						pagingableListener.onPauseItemsLoad(true);
					}
					//					if (pauseOnFling) {
					//						imageLoader.pause();
					//					}
					break;
				}
				//			if (externalListener != null) {
				//				externalListener.onScrollStateChanged(view, scrollState);
				//			}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalItemCount > 0) {
					int lastVisibleItem = firstVisibleItem + visibleItemCount;
					if (!isLoading && hasMoreItems && (lastVisibleItem == totalItemCount) ) {
						if(pagingableListener != null) {
							isLoading = true;
							pagingableListener.onLoadMoreItems();
						}
					}
				}
			}
		});
	}
}
