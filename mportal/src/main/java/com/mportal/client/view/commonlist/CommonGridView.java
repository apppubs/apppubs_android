package com.mportal.client.view.commonlist;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.mportal.client.R;
import com.mportal.client.view.HeaderGridView;

public class CommonGridView extends FrameLayout  implements OnScrollListener
{  

	private MyGridView mGridView;
//	private Drawable mListSel;
	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private CommonListViewListener mListViewListener;

	// -- header view
	private CommonListViewHeaderBackground mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = false;
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private CommonListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	final static int SCROLL_DURATION = 300; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	private final static float OFFSET_RADIO = 3f; // support iOS like pull
													// feature.

	private View mListHeader;//list顶部的item 透明装，改变其高度可以显示出后面的图层
	/**
	 * @param context
	 */
	public CommonGridView(Context context) {
		super(context);
		initWithContext(context);
	}

	public CommonGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.CommonListView);
//		mListSel = ta.getDrawable(R.styleable.CommonListView_list_sel);
//		ta.recycle();
		initWithContext(context);
	}

	public CommonGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void initWithContext(Context context) {
		//初始化设置请listview样式,listview的背景为透明，每个item赋给的颜色，留下，header 和 footer为透明滑动时改变header的高度
		mGridView = new MyGridView(context);
		if(Build.VERSION.SDK_INT>8){
			mGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		
		
		mScroller = new Scroller(context, new DecelerateInterpolator());
		//  need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		mGridView.setOnScrollListener(this);
		mListHeader = new View(context);
		mGridView.setDrawSelectorOnTop(true);
		mGridView.addHeaderView(mListHeader);
//		mGridView.setSelector(mListSel);
		// init header view
		mHeaderView = new CommonListViewHeaderBackground(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.clv_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.clv_header_time);
		addView(mHeaderView);
		addView(mGridView);
		
		// init footer view
		mFooterView = new CommonListViewFooter(context);

//		// init header height
		mHeaderViewHeight = (int) context.getResources().getDimension(R.dimen.commonlistview_header_h);
	}

	public void setAdapter(ListAdapter adapter) {
		mGridView.setAdapter(adapter);
		// make sure CommonListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
//			mListView.addFooterView(mFooterView);
		}
	}

	
	public long getItemAtPosition(int position){
		return mGridView.getItemIdAtPosition(position);
	}
	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
//			mGridView.addFooterView(mFooterView);
			mFooterView.show();
			mFooterView.setState(CommonListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
			mHeaderView.setState(CommonListViewHeaderBackground.STATE_DONE);
			Log.v("CommonListView","停止刷新。。。。");
		}
	}
	
	/**
	 * 手动刷新
	 */
	public void refresh(){
		startRefresh();
		mScroller.startScroll(0, 0, 0, mHeaderViewHeight,
				SCROLL_DURATION);
		
		// trigger computeScroll
		invalidate();

	}

	private void startRefresh(){
		if (mPullRefreshing)
			return;
		mPullRefreshing = true;
		mHeaderView.setState(CommonListViewHeaderBackground.STATE_REFRESHING);
		if (mListViewListener != null) {
			mListViewListener.onRefresh();
		}
		
		mFooterView.reset();
	}
	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(CommonListViewFooter.STATE_NORMAL);
		}
	}
	
	/**
	 * 已经加载了全部
	 */
	public void haveLoadAll(){
		mFooterView.setState(CommonListViewFooter.STATE_ALL_LOADED);
	}

	/**
	 * set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	
	//顶部拉动是执行，开始运行动画
	private void onHeaderPull(float offset) {
		Log.v("CommonListView","onHeaderPull"+offset);
			mHeaderView.setProgressPieProgress(offset);
	}

	private void updateHeaderHeight(float delta) {
		if(!mEnablePullRefresh) return ;
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mListHeader.getLayoutParams();
		lp.height +=delta;
		mListHeader.setLayoutParams(lp);
		
		
		
		if (mEnablePullRefresh && !mPullRefreshing) { 
			if (lp.height > mHeaderViewHeight) {
				mHeaderView.setState(CommonListViewHeaderBackground.STATE_READY);
			} else {
				mHeaderView.setState(CommonListViewHeaderBackground.STATE_NORMAL);
			}
		}
		if(lp.height<=mHeaderViewHeight){
			onHeaderPull(lp.height/(float)mHeaderViewHeight);
		}
		
		
//		mListView.setSelection(0); // scroll to top each time
	}
	
	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
//		int height = mHeaderView.getVisiableHeight();
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mListHeader.getLayoutParams();
		Log.v("commonListview:","resetHeaderHeight li.height"+lp.height+"headerheight:"+mHeaderViewHeight);
		if (lp.height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && lp.height <=mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && lp.height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, lp.height, 0, finalHeight - lp.height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
													// more.
				mFooterView.setState(CommonListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(CommonListViewFooter.STATE_NORMAL);
			}
		}
		 mFooterView.setBottomMargin(height);

		 mGridView.setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		if(mFooterView.getState()==CommonListViewFooter.STATE_ALL_LOADED){
			return;
		}
		mPullLoading = true;
		mFooterView.setState(CommonListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}


	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mListHeader.getLayoutParams();
				lp.height = mScroller.getCurrY();
				mListHeader.setLayoutParams(lp);
				if(mHeaderViewHeight>=lp.height){
					onHeaderPull(lp.height/(float)mHeaderViewHeight);
				}
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
		}
		super.computeScroll();
	}

	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	public void setCommonListViewListener(CommonListViewListener l) {
		mListViewListener = l;
	}

//	/**
//	 * you can listen ListView.OnScrollListener or this one. it will invoke
//	 * onXScrolling when header/footer scroll back.
//	 */
//	public interface OnCommonListViewScrollListener {
//		public void onScroll(float offset);
//	}

	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mGridView.setOnItemClickListener(listener);
	}
	
	public void addHeaderView(View v) {
		mGridView.addHeaderView(v);
	}
	
	class MyGridView extends HeaderGridView{

		public MyGridView(Context context) {
			super(context);
			
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			
			Log.v("MyListView","onTouchEvent "+ev.getActionMasked());
			if (mLastY == -1) {
				mLastY = ev.getRawY();
			}

			switch (ev.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				mLastY = ev.getRawY();
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				Log.v("MyListView","ACTION_POINTER_DOWN "+ev.getActionMasked());
			case MotionEvent.ACTION_POINTER_UP:
				Log.v("MyListView","ACTION_POINTER_UP "+ev.getActionMasked());
			case MotionEvent.ACTION_MOVE:
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mListHeader.getLayoutParams();
				final float deltaY = ev.getRawY() - mLastY;
				mLastY = ev.getRawY();
				if (mGridView.getFirstVisiblePosition() == 0
						&& (lp.height > 0 || deltaY > 0)) {
					// the first item is showing, header has shown or pull down.
					updateHeaderHeight(deltaY / OFFSET_RADIO);
					return true;
				} else if (mGridView.getLastVisiblePosition() == mTotalItemCount - 1
						&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
					// last item, already pulled up or want to pull up.
					updateFooterHeight(-deltaY / OFFSET_RADIO);
					
				}
				break;
			case MotionEvent.ACTION_UP:
				mLastY = -1; // reset
				if (mGridView.getFirstVisiblePosition() == 0) {
					// invoke refresh
					FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) mListHeader.getLayoutParams();
					Log.v("CommonListView"," lp1.height"+ lp1.height+":mHeaderViewHeight"+mHeaderViewHeight);
					if (mEnablePullRefresh&& lp1.height> mHeaderViewHeight) {
						startRefresh();
					}
					resetHeaderHeight();
					FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) mListHeader.getLayoutParams();
					if(lp2.height>0)
						return true;
				} else if (mGridView.getLastVisiblePosition() == mTotalItemCount - 1) {
					// invoke load more.
					if (mEnablePullLoad&&!mPullRefreshing&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
							&& !mPullLoading) {
						startLoadMore();
					}
					resetFooterHeight();
				}
				break;
			 case MotionEvent.ACTION_CANCEL:
				 
		        	return false;
			default:
				break;
			}
			return super.onTouchEvent(ev);
		}
		float lastY = -1 ,lastX = -1,xDistance, yDistance;
	}
    
    
    
}  


