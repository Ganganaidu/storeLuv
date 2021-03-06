package com.thisisswitch.storelove.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

public class ParallaxScollListView extends ListView implements OnScrollListener {

	ImageView mImageView;
	int mDrawableMaxHeight = -1;
	int mImageViewHeight = -1;
	public final static double NO_ZOOM = 1;
	public final static double ZOOM_X2 = 10;

	private interface OnOverScrollByListener {
		public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
	}

	private interface OnTouchEventListener {
		public void onTouchEvent(MotionEvent ev);
	}

	public ParallaxScollListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ParallaxScollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParallaxScollListView(Context context) {
		super(context);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		boolean isCollapseAnimation = false;

		isCollapseAnimation = scrollByListener.overScrollBy(deltaX, deltaY,
				scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
				maxOverScrollY, isTouchEvent)
				|| isCollapseAnimation;

		return isCollapseAnimation ? true : super.overScrollBy(deltaX, deltaY,
				scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
				maxOverScrollY, isTouchEvent);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		touchListener.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}

	public void setParallaxImageView(ImageView iv) {
		mImageView = iv;
		mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	}

	public void setViewsBounds(double zoomRatio) {
		if (mImageViewHeight == -1) {
			mImageViewHeight = mImageView.getHeight();
			double ratio = ((double) mImageView.getDrawable()
					.getIntrinsicWidth()) / ((double) mImageView.getWidth());

			mDrawableMaxHeight = (int) ((mImageView.getDrawable()
					.getIntrinsicHeight() / ratio) * (zoomRatio > 1 ? zoomRatio
					: 1));
		}
	}

	private OnOverScrollByListener scrollByListener = new OnOverScrollByListener() {
		@Override
		public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
			if (mImageView.getHeight() <= mDrawableMaxHeight && isTouchEvent) {
				if (deltaY < 0) {
					if (mImageView.getHeight() - deltaY / 2 >= mImageViewHeight) {
						mImageView.getLayoutParams().height = mImageView
								.getHeight() - deltaY / 2 < mDrawableMaxHeight ? mImageView
								.getHeight() - deltaY / 2
								: mDrawableMaxHeight;
						mImageView.requestLayout();
					}
				} else {
					if (mImageView.getHeight() > mImageViewHeight) {
						mImageView.getLayoutParams().height = mImageView
								.getHeight() - deltaY > mImageViewHeight ? mImageView
								.getHeight() - deltaY
								: mImageViewHeight;
						mImageView.requestLayout();
						return true;
					}
				}
			}
			return false;
		}
	};

	private OnTouchEventListener touchListener = new OnTouchEventListener() {
		@Override
		public void onTouchEvent(MotionEvent ev) {
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				if (mImageViewHeight - 1 < mImageView.getHeight()) {
					ResetAnimimation animation = new ResetAnimimation(
							mImageView, mImageViewHeight);
					animation.setDuration(300);
					mImageView.startAnimation(animation);
				}
			}
		}
	};

	public class ResetAnimimation extends Animation {
		int targetHeight;
		int originalHeight;
		int extraHeight;
		View mView;

		protected ResetAnimimation(View view, int targetHeight) {
			this.mView = view;
			this.targetHeight = targetHeight;
			originalHeight = view.getHeight();
			extraHeight = this.targetHeight - originalHeight;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {

			int newHeight;
			newHeight = (int) (targetHeight - extraHeight
					* (1 - interpolatedTime));
			mView.getLayoutParams().height = newHeight;
			mView.requestLayout();
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
		}

	}
}
