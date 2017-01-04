package com.mportal.client.view.commonlist;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import com.mportal.client.R;
import com.mportal.client.util.LogM;

public class CommonListView extends FrameLayout implements OnScrollListener {

	
	private Context mContext;
	private MyListView mListView;
	private Drawable mListSel;
	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private CommonListViewListener mListViewListener;

	// -- header view
	private CommonListViewHeaderBackground mHeaderBgView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
//	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private float mHeaderViewHeightReciprocal;
	private boolean mEnablePullRefresh = true;
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
	private final static float OFFSET_RADIO = 0.4f; // support iOS like pull
													// feature.

	private View mListHeader;//list顶部的item 透明装，改变其高度可以显示出后面的图层
	
	private TextView mEmptyTv;//当列表为空时显示的文本框
	private String mEmptyString;//列表为空时显示的文字
	
	
	/**
	 * @param context
	 */
	public CommonListView(Context context) {
		super(context);
	}

	public CommonListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.CommonListView);
		mListSel = ta.getDrawable(R.styleable.CommonListView_list_sel);
		mEmptyString = ta.getString(R.styleable.CommonListView_empty_string);
		ta.recycle();
		initWithContext(context);
		
	}

	public CommonListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void initWithContext(Context context) {
		//初始化设置请listview样式,listview的背景为透明，每个item赋给的颜色，留下，header 和 footer为透明滑动时改变header的高度
		mListView = new MyListView(context);
		mListView.setDivider(null);
		if(Build.VERSION.SDK_INT>8){
			mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		mListView.setEmptyView(mEmptyTv);
		mEmptyTv = new TextView(mContext);
		mEmptyTv.setText(mEmptyString);
		
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		addView(mEmptyTv,lp);
		
		mScroller = new Scroller(context, new DecelerateInterpolator());
		//  need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		mListView.setOnScrollListener(this);
		mListHeader = new View(context);
		mListView.addHeaderView(mListHeader);
		if(mListSel!=null)
			mListView.setSelector(mListSel);
		// init header view
		mHeaderBgView = new CommonListViewHeaderBackground(context);
//		mHeaderViewContent = (RelativeLayout) mHeaderBgView
//				.findViewById(R.id.clv_header_content);
		mHeaderTimeView = (TextView) mHeaderBgView
				.findViewById(R.id.clv_header_time);
		mHeaderBgView.setVisibility(View.GONE);
		addView(mHeaderBgView);
		addView(mListView);
		
		// init footer view
		mFooterView = new CommonListViewFooter(context);

//		// init header height
		mHeaderViewHeight = (int) context.getResources().getDimension(R.dimen.commonlistview_header_h);
		mHeaderViewHeightReciprocal = 1.0f/mHeaderViewHeight;
	}

	public void setAdapter(ListAdapter adapter) {
		
		mListView.setAdapter(adapter);
		
		// make sure CommonListViewFooter is the last footer view, and only add once.
		if(!adapter.isEmpty()&&mIsFooterReady == false){
			
			mIsFooterReady = true;
			mListView.addFooterView(mFooterView);
		}
	}

	
	public long getItemAtPosition(int position){
		return mListView.getItemIdAtPosition(position);
	}
	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderBgView.setVisibility(View.GONE);
		} else {
			mHeaderBgView.setVisibility(View.VISIBLE);
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
//			mListView.addFooterView(mFooterView);
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
			mHeaderBgView.setState(CommonListViewHeaderBackground.STATE_DONE);
			Log.v("CommonListView","停止刷新。。。。");
		}
	}

	
	/**
	 * 手动刷新
	 */
	public void refresh(){
		mHeaderBgView.setVisibility(View.VISIBLE);
		startRefresh();
		mScroller.startScroll(0, 0, 0, mHeaderViewHeight,
				SCROLL_DURATION);
		LogM.log(this.getClass(), "开始刷新");
		// trigger computeScroll
		invalidate();

	}

	private void startRefresh(){
		if (mPullRefreshing)
			return;
		mPullRefreshing = true;
		mHeaderBgView.setState(CommonListViewHeaderBackground.STATE_REFRESHING);
		if (mListViewListener != null) {
			mListViewListener.onRefresh();
		}
		
		mFooterView.reset();
	}
	
	public boolean isRefreshing(){
		return mPullRefreshing;
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
			mHeaderBgView.setProgressPieProgress(offset);
	}
	private int getHeaderHeight(){
		AbsListView.LayoutParams lp = (AbsListView.LayoutParams) mListHeader.getLayoutParams();
		if(lp==null) return 0;
		return lp.height;
	}
	private void updateHeaderHeight(float delta) {
		if(mEnablePullRefresh&&mHeaderBgView.getVisibility()==View.GONE){
			mHeaderBgView.setVisibility(View.VISIBLE);
		}
		AbsListView.LayoutParams lp = (AbsListView.LayoutParams) mListHeader.getLayoutParams();
		int height = 0;
		if(lp!=null){
			
			height = (int) (lp.height + delta);
			lp.height = height;
			mListHeader.setLayoutParams(lp);
		}
		
		if (mEnablePullRefresh && !mPullRefreshing) { 
			if (height > mHeaderViewHeight) {
				mHeaderBgView.setState(CommonListViewHeaderBackground.STATE_READY);
			} else {
				mHeaderBgView.setState(CommonListViewHeaderBackground.STATE_NORMAL);
			}
		}
		if(mEnablePullRefresh&&height<=mHeaderViewHeight){
			onHeaderPull(height*mHeaderViewHeightReciprocal);
		}
		
		
//		mListView.setSelection(0); // scroll to top each time
	}
	
	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
//		int height = mHeaderView.getVisiableHeight();
//		Log.v("commonListview:","resetHeaderHeight li.height"+lp.height+"headerheight:"+mHeaderViewHeight);
		int headerHeight = getHeaderHeight();
		if (headerHeight == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && headerHeight <=mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && headerHeight > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, headerHeight, 0, finalHeight - headerHeight,
				SCROLL_DURATION);
		if(finalHeight==0){
			
			this.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mHeaderBgView.setVisibility(View.GONE);
				}
			}, SCROLL_DURATION);
		}
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

		 mListView.setSelection(mTotalItemCount - 1); // scroll to bottom
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
				AbsListView.LayoutParams lp = (android.widget.AbsListView.LayoutParams) mListHeader.getLayoutParams();
				if(lp==null){
					lp = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, mScroller.getCurrY());
				}else{
					lp.height = mScroller.getCurrY();
				}
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

	
	public void setOnItemClickListener(OnItemClickListener listener) {
		mListView.setOnItemClickListener(listener);
	}
	
	public void addHeaderView(View v) {
		mListView.addHeaderView(v);
	}
	
	public void removeHeaderView (View v){
		mListView.removeHeaderView(v);
	}
	public void setSelection(int position){
		mListView.setSelection(position);
	}
	
	public void setStackFromBottom(boolean b){
		mListView.setStackFromBottom(b);
	}
	public void setTranscriptMode(int mode){
		mListView.setTranscriptMode(mode);
	}
	class MyListView extends ListView{

		public MyListView(Context context) {
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
				final float deltaY = ev.getRawY() - mLastY;
				mLastY = ev.getRawY();
				if (mListView.getFirstVisiblePosition() == 0
						&& (getHeaderHeight()>0 || deltaY > 0)) {
					// the first item is showing, header has shown or pull down.
					updateHeaderHeight(deltaY *OFFSET_RADIO);
					return true;
				} else if (mListView.getLastVisiblePosition() == mTotalItemCount - 1
						&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
					// last item, already pulled up or want to pull up.
					updateFooterHeight(-deltaY *OFFSET_RADIO);
					
				}
				break;
			case MotionEvent.ACTION_UP:
				mLastY = -1; // reset
				if (mListView.getFirstVisiblePosition() == 0) {
					// invoke refresh
					if (mEnablePullRefresh&& getHeaderHeight()> mHeaderViewHeight) {
						startRefresh();
					}
					resetHeaderHeight();
					if(getHeaderHeight()>0)return true;
//					AbsListView.LayoutParams lp2 = (AbsListView.LayoutParams) mListHeader.getLayoutParams();
//					if(lp2!=null&&lp2.height>0)
//						return true;
				} else if (mListView.getLastVisiblePosition() == mTotalItemCount - 1) {
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
//		
	}
}
