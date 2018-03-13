package com.apppubs.d20.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.MportalApplication;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.BaseActivity;
import com.apppubs.d20.activity.HomeBottomMenuActivity;
import com.apppubs.d20.activity.ViewCourier;
import com.apppubs.d20.adapter.PageFragmentPagerAdapter;
import com.apppubs.d20.asytask.AsyTaskCallback;
import com.apppubs.d20.asytask.AsyTaskExecutor;
import com.apppubs.d20.constant.Constants;
import com.apppubs.d20.fragment.BaseFragment;
import com.apppubs.d20.fragment.ChannelFragment;
import com.apppubs.d20.fragment.ChannelFragmentFactory;
import com.apppubs.d20.fragment.TitleMenuFragment;
import com.apppubs.d20.model.VersionInfo;
import com.apppubs.d20.presenter.VersionPresenter;
import com.apppubs.d20.util.FileUtils;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.StringUtils;
import com.apppubs.d20.util.Utils;
import com.apppubs.d20.view.IVersionView;
import com.apppubs.d20.webapp.WebAppFragment;
import com.apppubs.d20.widget.CheckableFlowLayout;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.widget.DraggableGridView;
import com.apppubs.d20.widget.DraggableGridView.OnRearrangeListener;
import com.apppubs.d20.widget.EditTextDialog;
import com.apppubs.d20.widget.HotArea;
import com.apppubs.d20.widget.HotAreaView;
import com.apppubs.d20.widget.HotAreaView.HotAreaClickListener;
import com.apppubs.d20.widget.RatioLayout;
import com.apppubs.d20.widget.ScrollTabs;
import com.apppubs.d20.widget.SlidePicView;
import com.apppubs.d20.widget.TitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageFragment extends TitleMenuFragment implements OnClickListener, IPageView,
        IVersionView {

    public static final String EXTRA_STRING_NAME_PAGE_ID = "page_id";
    public static final String CUSTOM_WEB_APP_URL_SERIALIZED_FILE_NAME = "custom_web_app_url_map";


    private final int ASY_TASK_TAG_RESOLVE_HOTAREAS = 100;//异步解析热区信息
    private String mPageId;

    private FrameLayout mRootFL;//包含loading和RootLl
    private AVLoadingIndicatorView mLoadingView;
    private LinearLayout mRootLl;//包含titlebar和contentRL
    private RelativeLayout mContentRL;//包含导航和滚动fragment或者ScrollView
    private ScrollTabs mScrollTabs;
    private ScrollView mScrollView;//包含mContainerLl
    private LinearLayout mContainerLl;//包含components

    private List<View> mAnchorPointerViewList;//锚点view
    private ViewPager mViewPager;
    private String mCachedResponse;
    private LinearLayout mColumnView;
    private List<String> mSelectedTabs;
    private List<String> mUnselectedTabs;

    private PagePresenter mPresenter;
    private VersionPresenter mVersionPrsenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAnchorPointerViewList = new ArrayList<View>();
        initPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPageId = bundle.getString(EXTRA_STRING_NAME_PAGE_ID);
        }
        initView();
        mPresenter.onCreateView();
        return mRootFL;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onVisiable();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.onVisiable();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInflater = LayoutInflater.from(mContext);
//		loadCache();
//		loadRemoteData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddressPickerActivity.REQUEST_CODE && resultCode ==
                AddressPickerActivity.RESULT_SUCCESS) {
            AddressModel address = (AddressModel) data.getSerializableExtra(AddressPickerActivity
                    .RESULT_EXTRA_KEY_ADDRESS);
            mPresenter.onAddressSelected(address);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showTitleBar(TitleBarModel model) {

        TitleBar titleBar = buildTitleBar(model);
        replaceTitleBar(titleBar);
    }

    @Override
    public void setTitleBarAddress(String text) {
        mTitleBar.setAddressText(text);
    }

    @Override
    public void showContentView(PageContentModel model) {
        resetContentView();
        if (model instanceof PageNormalContentModel) {
            try {
                renderNormalContent((PageNormalContentModel) model);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "普通页面渲染错误", Toast.LENGTH_LONG).show();
            }
        } else {
            PageNavContentModel pageNav = (PageNavContentModel) model;
            try {
                refreshNav(pageNav.getNavBar());//刷新导航条
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //构造viewpager
            List<String> urls = new ArrayList<String>();
            JSONArray items = pageNav.getNavItems();
            for (String str : mSelectedTabs) {
                for (int i = -1; ++i < items.length(); ) {
                    try {

                        if (str.equals(items.getJSONObject(i).getString("title"))) {
                            urls.add(items.getJSONObject(i).getString("url"));
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                refreshFragmentsScrollView(urls);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showLoadingView() {
        mLoadingView.show();
    }

    @Override
    public void hideLoadingView() {
        mLoadingView.hide();
    }

    @Override
    public void showErrorView() {
        Toast.makeText(mContext, "加载失败！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getPageId() {
        return mPageId;
    }

    @Override
    public void showVersionInfo(final VersionInfo vi) {
        if (!vi.isNeedUpdate()) {
            Toast.makeText(mHostActivity, "当前已是最新版本", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = String.format("检查到有新版 %s", TextUtils.isEmpty(vi.getVersion()) ? "" : "V" +
                vi.getVersion());
        ConfirmDialog dialog = new ConfirmDialog(getContext(), new ConfirmDialog.ConfirmListener() {

            @Override
            public void onCancelClick() {
            }

            @Override
            public void onOkClick() {
                mVersionPrsenter.startDownloadApp(vi.getUpdateUrl());
                Toast.makeText(mContext, "正在下载中，请稍候", Toast.LENGTH_SHORT).show();
            }
        }, title, vi.getUpdateDescribe(), "下次", "更新");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    //pragma private
    private void replaceTitleBar(TitleBar titlebar) {
        if (titlebar == null) {
            return;
        }
        View oldTitleBar = mRootLl.findViewById(R.id.page_title);
        mRootLl.removeView(oldTitleBar);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams
                .MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.title_height));
        titlebar.setId(R.id.page_title);
        mRootLl.addView(titlebar, 0, lp);
    }

    private void resetContentView() {
        if (mContentRL != null) {
            mRootLl.removeView(mContentRL);
        }
        mContentRL = new RelativeLayout(mContext);
        mRootLl.addView(mContentRL);
    }

    /**
     * 初始化rootview
     */

    private void initPresenter() {
        mPresenter = new PagePresenter(mContext, this);
        mVersionPrsenter = new VersionPresenter(mContext, this);
    }

    private void initView() {
        LinearLayout rootLl = new LinearLayout(mContext);
        rootLl.setOrientation(LinearLayout.VERTICAL);
        mRootLl = rootLl;

        mRootFL = new FrameLayout(mContext);
        mRootFL.addView(mRootLl);
        mLoadingView = new AVLoadingIndicatorView(mContext);
        mLoadingView.setIndicator("BallBeatIndicator");
        mLoadingView.setIndicatorColor(Color.parseColor("#C0C0C0"));
        FrameLayout.LayoutParams loadingLP = new FrameLayout.LayoutParams(Utils.dip2px(mContext,
                40), Utils.dip2px(mContext, 40));
        loadingLP.gravity = Gravity.CENTER;
        mRootFL.addView(mLoadingView, loadingLP);
    }

    private void parse(JSONObject info) throws JSONException {

        mRootLl.removeAllViews();
        //解析titlebar
//		if(info.has("titlebar")){
//			TitleBar titlebar = buildTitleBar(info);
//			titlebar.setId(R.id.page_title);
//			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams
// .MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.title_height));
//			mRootLl.addView(titlebar,lp);
//		}

        //优先级1.导航，2.webview，3.components
        if (info.has("navbar")) {
            resetContentView();
            JSONObject navBarObj = info.getJSONObject("navbar");
            refreshNav(navBarObj);//刷新导航条
            //构造viewpager
            List<String> urls = new ArrayList<String>();
            JSONArray items = navBarObj.getJSONArray("items");
            for (String str : mSelectedTabs) {
                for (int i = -1; ++i < items.length(); ) {
                    if (str.equals(items.getJSONObject(i).getString("title"))) {
                        urls.add(items.getJSONObject(i).getString("url"));
                        break;
                    }
                }
            }
            refreshFragmentsScrollView(urls);
        } else {
//			renderContent(info);
        }

    }

    private void refreshFragmentsScrollView(List<String> urls) throws JSONException {
        mViewPager = new ViewPager(mContext);
        PageFragmentPagerAdapter mFragmentAdapter = new PageFragmentPagerAdapter
                (getChildFragmentManager());
        List<BaseFragment> channels = new ArrayList<BaseFragment>();
        for (int i = -1; ++i < urls.size(); ) {
            channels.add(getFragmentByUrl(urls.get(i)));
        }
        mFragmentAdapter.setData(channels);
        mViewPager.setId(R.id.temp_id);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            private int curPos;

            @Override
            public void onPageSelected(int position) {
                curPos = position;

            }

            @Override
            public void onPageScrolled(int position, float offset, int arg2) {
                mScrollTabs.onPageScrolled(position, offset);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mScrollTabs.setCurrentTab(curPos);
                    LogM.log(this.getClass(), "onPageScrollStateChanged 0 mCurPos" + curPos);
                }
            }
        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.widget
                .RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout
                .LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.BELOW, R.id.page_nav);
        mContentRL.addView(mViewPager, lp);
        mScrollTabs.setOnItemClickListener(new ScrollTabs.OnItemClickListener() {

            @Override
            public void onclick(int pos) {
                mViewPager.setCurrentItem(pos, false);
            }
        });

    }


    private BaseFragment getFragmentByUrl(String url) {
        BaseFragment result = null;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            WebAppFragment frg = new WebAppFragment();
            Bundle args = new Bundle();
            args.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
            frg.setArguments(args);
            result = frg;
        } else if (url.matches("apppubs:\\/\\/channel\\/[0-9]\\/[A-Za-z0-9]*")) {
            String[] arr = StringUtils.getPathParams(url);
            ChannelFragment cf = ChannelFragmentFactory.getChannelFragment(Integer.parseInt
                    (arr[1]));
            Bundle args = new Bundle();
            args.putString(ChannelFragment.ARG_KEY, arr[2]);
            cf.setArguments(args);
            result = cf;
        } else if (url.matches("apppubs:\\/\\/page\\/[\\S]*")) {
            PageFragment pageF = new PageFragment();
            String[] pathParams = StringUtils.getPathParams(url);
            Bundle args = new Bundle();
            args.putString(PageFragment.EXTRA_STRING_NAME_PAGE_ID, pathParams[1]);
            pageF.setArguments(args);
            result = pageF;
        }
        return result;
    }

    private void refreshNav(JSONObject navBarObj) throws JSONException {
        mContentRL.removeView(mScrollTabs);
        mScrollTabs = new ScrollTabs(mContext);
        int bgColor = Color.parseColor(navBarObj.getString("bgcolor"));
        mScrollTabs.setBackgroundColor(bgColor);
        if (isLightColor(bgColor)) {
            mScrollTabs.setSelectedTextColor(mHostActivity.getThemeColor());
            mScrollTabs.setTextColor(Color.parseColor("#8b8b8b"));
        } else {
            mScrollTabs.setSelectedTextColor(Color.WHITE);
            mScrollTabs.setTextColor(Color.parseColor("#D0D0D0"));
        }
        if ("2".equals(navBarObj.getString("navtype"))) {
            mScrollTabs.setHaveDownArrow(true);
        }
        mScrollTabs.setSelectedSize(Utils.dip2px(mContext, 15));
        mScrollTabs.setTextSize(Utils.dip2px(mContext, 13));
        JSONArray items = navBarObj.getJSONArray("items");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                Utils.dip2px(mContext, 40));
        List<String> savedTabList = (List<String>) FileUtils.readObj(mContext, String.format
                (Constants.FILE_NAME_SELECTED_NAV_TGABS, mPageId));
        if (savedTabList == null || savedTabList.size() < 1) {
            mSelectedTabs = new ArrayList<String>();
            mUnselectedTabs = new ArrayList<String>();
            for (int i = -1; ++i < items.length(); ) {
                JSONObject item = items.getJSONObject(i);
                mScrollTabs.addTab(item.getString("title"));
                mSelectedTabs.add(item.getString("title"));
            }
            FileUtils.writeObj(mContext, mSelectedTabs, String.format(Constants
                    .FILE_NAME_SELECTED_NAV_TGABS, mPageId));
        } else {
            mSelectedTabs = new ArrayList<String>();
            ;
            mUnselectedTabs = new ArrayList<String>();
            //将未保存的所有的均作为为选定的标签
            for (int i = -1; ++i < items.length(); ) {

                if (!savedTabList.contains(items.getJSONObject(i).getString("title"))) {
                    mUnselectedTabs.add(items.getJSONObject(i).getString("title"));
                }
            }
            //验证已经存储的标签是否还存在于服务端如果存在则显示否则不显示
            for (int i = -1; ++i < savedTabList.size(); ) {
                String tempStr = savedTabList.get(i);
                for (int j = -1; ++j < items.length(); ) {
                    if (tempStr.equals(items.getJSONObject(j).getString("title"))) {
                        mSelectedTabs.add(tempStr);
                        mScrollTabs.addTab(tempStr);
                        break;
                    }
                }
            }

        }

        mScrollTabs.setId(R.id.page_nav);
        mContentRL.addView(mScrollTabs, lp);

        mScrollTabs.setOnColumnBtnClickListener(new ScrollTabs.OnColunmBtnClickListener() {

            @Override
            public void onClick(boolean isOpen) {
                openOrClose(true, mSelectedTabs, mUnselectedTabs);
            }
        });
    }

    private void openOrClose(boolean isOpen, List<String> selected, List<String> unselectList) {
        Animation showAnim = AnimationUtils.loadAnimation(mHostActivity, R.anim.slide_in_from_top);
        Animation hideAnim = AnimationUtils.loadAnimation(mHostActivity, R.anim.slide_out_to_top);
        if (isOpen) {
            mColumnView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout
                    .page_column_config_pannel, null);
            mColumnView.startAnimation(showAnim);
            mColumnView.findViewById(R.id.page_up_arrow_iv).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    openOrClose(false, null, null);
                }
            });
            final DraggableGridView dgv = (DraggableGridView) mColumnView.findViewById(R.id
                    .page_dgv);
            for (String name : selected) {
                //初始化栏目
                LayoutInflater li = LayoutInflater.from(mHostActivity);
                TextView tv = (TextView) li.inflate(R.layout.page_column_sort_item, null);
                tv.setText(name);
                dgv.addView(tv);
            }

            dgv.setOnRearrangeListener(new OnRearrangeListener() {

                @Override
                public void onRearrange(int oldIndex, int newIndex) {
                    Log.v("ChannelsF", "onRearrange oldIndex:" + oldIndex + "new Index:" +
                            newIndex);
                    if (oldIndex == newIndex) {
                        return;
                    } else if (newIndex > oldIndex) {
                        String temp = mSelectedTabs.get(oldIndex);
                        for (int i = oldIndex; i < newIndex; i++) {
                            mSelectedTabs.set(i, mSelectedTabs.get(i + 1));
                        }
                        mSelectedTabs.set(newIndex, temp);
                    } else {
                        String temp = mSelectedTabs.get(oldIndex);
                        for (int i = oldIndex; i > newIndex; i--) {
                            mSelectedTabs.set(i, mSelectedTabs.get(i - 1));
                        }
                        mSelectedTabs.set(newIndex, temp);
                    }
                    FileUtils.writeObj(mContext, mSelectedTabs, String.format(Constants
                            .FILE_NAME_SELECTED_NAV_TGABS, mPageId));
                }

                @Override
                public void onRemove(View view, int index) {
                    Log.v("ChannelsF", "onRemove index:" + index);
                }

                @Override
                public void onAdd() {
                    Log.v("ChannelsF", "onAdd");
                }
            });
            final LinearLayout unselectedContainerLL = (LinearLayout) mColumnView.findViewById(R
                    .id.page_column_unselect_container_ll);
            dgv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    dgv.removeViewAt(pos);
                    TextView tv = (TextView) view;
                    tv.setGravity(Gravity.CENTER);
                    LayoutInflater li = LayoutInflater.from(mHostActivity);
                    RelativeLayout rl = (RelativeLayout) li.inflate(R.layout
                            .page_column_unselected_item, null);
                    TextView titleTv = (TextView) rl.findViewById(R.id
                            .page_column_unselected_title_tv);
                    titleTv.setText(tv.getText());
                    rl.setOnClickListener(new OnUnselectItemClickListener(unselectedContainerLL,
                            dgv));
                    unselectedContainerLL.addView(rl);
                    mSelectedTabs.remove(tv.getText());
                    mUnselectedTabs.add(tv.getText().toString());
                    FileUtils.writeObj(mContext, mSelectedTabs, String.format(Constants
                            .FILE_NAME_SELECTED_NAV_TGABS, mPageId));
                }
            });


            for (String name : unselectList) {
                //初始化栏目
                LayoutInflater li = LayoutInflater.from(mHostActivity);
                RelativeLayout rl = (RelativeLayout) li.inflate(R.layout
                        .page_column_unselected_item, null);
                TextView titleTv = (TextView) rl.findViewById(R.id.page_column_unselected_title_tv);
                titleTv.setText(name);
                rl.setOnClickListener(new OnUnselectItemClickListener(unselectedContainerLL, dgv));
                unselectedContainerLL.addView(rl);
            }

            mContentRL.addView(mColumnView, new LayoutParams(android.view.ViewGroup.LayoutParams
                    .MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));

        } else {
            mColumnView.startAnimation(hideAnim);
            mColumnView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mColumnView.setVisibility(ViewGroup.GONE);
                    JSONResult jr = JSONResult.compile(mCachedResponse);
                    try {
                        parse(new JSONObject(jr.result));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogM.log(this.getClass(), e);
                    }
                }
            }, hideAnim.getDuration());
            FileUtils.writeObj(mContext, mSelectedTabs, String.format(Constants
                    .FILE_NAME_SELECTED_NAV_TGABS, mPageId));
            mContentRL.removeView(mColumnView);
        }
    }

    class OnUnselectItemClickListener implements OnClickListener {
        ViewGroup mUnselectContainer;
        DraggableGridView mDragGridView;

        public OnUnselectItemClickListener(ViewGroup container, DraggableGridView dgv) {
            mUnselectContainer = container;
            mDragGridView = dgv;
        }

        @Override
        public void onClick(View v) {
            mUnselectContainer.removeView(v);
            TextView titleView = (TextView) v.findViewById(R.id.page_column_unselected_title_tv);
            LayoutInflater li = LayoutInflater.from(mHostActivity);
            TextView tv = (TextView) li.inflate(R.layout.page_column_sort_item, null);
            tv.setText(titleView.getText());
            mDragGridView.addView(tv);
            mSelectedTabs.add(titleView.getText().toString());
            mUnselectedTabs.remove(titleView.getText());
            FileUtils.writeObj(mContext, mSelectedTabs, String.format(Constants
                    .FILE_NAME_SELECTED_NAV_TGABS, mPageId));
        }
    }

    private void renderNormalContent(PageNormalContentModel model) throws JSONException {
        resetContentViewWithScrollView();
        List<PageComponent> components = model.getComponents();
        for (int i = -1; ++i < components.size(); ) {
            PageComponent component = components.get(i);
            String comType = component.getCode();
            if (comType.equals(Constants.PAGE_COMPONENT_SINGLE_PIC_DEFAULT)) {
                addSinglePicComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_PIC)) {
                addSimplePicComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_BLANK_ROW)) {
                addBlank(20);
            } else if (comType.equals(Constants.PAGE_COMPONENT_HORIZONTALL_LINE)) {
                addDivider();
            } else if (comType.equals(Constants.PAGE_COMPONENT_TAG)) {
                addTagComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_TAB_WITH_ANCHOR)) {
                addTabWithAnchorComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_PIC_TEXT_LIST_DEFAULT)) {
                addDefaultPicTextList(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_SLIDE_PIC_DEFAULT)) {
                addDefaultSlidePic(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_SLIDE_PIC_WITH_PAGE_CONTROL_ONLY)) {
                addSlidePicWithPageControlOnlyComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_ICON_LIST_DEFAULT)) {
                addDefaultIconListComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_ICON_LIST_3_COLUMN)) {
                addIconListWith3ColumnComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_ICON_LIST)) {
                addIconListComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_ICON_PURE_TEXT_LIST)) {
                addFlowTagComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_HOT_AREA_DEFAULT)) {
                addDefaultHotArea(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_HOT_AREA_SINGLE_PAGE)) {
                addSinglePageHotAreaComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_ICON_LIST_VERTICAL)) {
                addVerticalIconListComponent(component);
            } else if (comType.equals(Constants.PAGE_COMPONENT_DEFAULT_USER_INFO)) {
                addDefaultUserInfoComponent(component);
            }
        }
    }

    private void resetContentViewWithScrollView() {
        if (mScrollView != null) {
            mContentRL.removeView(mScrollView);
        }
        ScrollView sv = new ScrollView(mContext);
        mScrollView = sv;
        mContentRL.addView(sv);

        //首先清除容器内的所有view
        mContainerLl = new LinearLayout(mContext);
        mContainerLl.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams llLp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mScrollView.addView(mContainerLl, llLp);
    }

    private void addDefaultUserInfoComponent(PageComponent pc) {
        JSONObject component = pc.getJSONObject();
        DefaultUserinfoComponent userInfoComponent = (DefaultUserinfoComponent) pc;
        double heightWidthRatio = 0;
        int bgColor = 0;
        try {
            heightWidthRatio = component.getDouble("widthheightratio");
            String colorStr = component.getString("bgcolor");
            bgColor = Color.parseColor(colorStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RatioLayout rl = (RatioLayout) LayoutInflater.from(mContext).inflate(R.layout
                .page_component_default_userinfo, null, false);
        rl.setRatio((float) heightWidthRatio);
        LinearLayout ll = (LinearLayout) rl.findViewById(R.id.default_userinfo_ll);
        ImageView iv = (ImageView) rl.findViewById(R.id.default_userinfo_ri);
        TextView usernameTV = (TextView) rl.findViewById(R.id.default_userinfo_tv);

        ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCourier.getInstance(mContext).execute(mContext, "apppubs://user_account");
            }
        });

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.userinfo)
                .showImageOnFail(R.drawable.userinfo)
                .showImageForEmptyUri(R.drawable.userinfo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .denyNetworkDownload(MportalApplication.systemState.getNetworkState() !=
                        ConnectivityManager.TYPE_WIFI && !mAppContext.getSettings()
                        .isAllowDowPicUse2G())
                .build();
        mImageLoader.displayImage(userInfoComponent.getAvatarURL(), iv, options);
        usernameTV.setText(TextUtils.isEmpty(userInfoComponent.getUsername()) ? "匿名用户" :
                userInfoComponent.getUsername());
        rl.setBackgroundColor(bgColor);
        mContainerLl.addView(rl);
    }

    private void addVerticalIconListComponent(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        JSONArray items = component.getJSONArray("items");
        for (int j = -1; ++j < items.length(); ) {
            JSONObject item = items.getJSONObject(j);
            RelativeLayout rl = (RelativeLayout) mInflater.inflate(R.layout
                    .page_component_icon_list_item, null);
            TextView tv = (TextView) rl.findViewById(R.id.menu_tv);
            tv.setText(item.getString("title"));
            ImageView iv = (ImageView) rl.findViewById(R.id.menu_iv);

            String picUrl = item.getString("picurl");
            if (!TextUtils.isEmpty(picUrl)) {
                mImageLoader.displayImage(picUrl, iv);
            } else {
                iv.setVisibility(View.GONE);
            }
            rl.setTag(item.getString("url"));
            rl.setOnClickListener(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams
                    .MATCH_PARENT, Utils.dip2px(mContext,
                    50));
            mContainerLl.addView(rl, lp);

            //分割线
            if (j != items.length() - 1) {
                View line = new View(mHostActivity);
                LayoutParams lp1 = new LayoutParams(Utils.dip2px(mContext, 20), 1);
                line.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mContainerLl.addView(line, lp1);
            }
        }
    }

    private void addSinglePageHotAreaComponent(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        RelativeLayout picCon = new RelativeLayout(mContext);

        double ratio = component.getDouble("picheightwidthratio");
        HotAreaView iv = new HotAreaView(mContext);
        JSONArray items = component.getJSONArray("items");
        List<HotArea> hotAreas = new ArrayList<HotArea>();
        iv.setPicWidth(component.getInt("picwidth"));
        for (int j = -1; ++j < items.length(); ) {
            JSONObject item = items.getJSONObject(j);
            HotArea ha = new HotArea();
            if (item.has("type")) {
            }
            if (item.has("shape")) {
                ha.setShape(item.getString("shape"));
            }
            if (item.has("url")) {
                ha.setUrl(item.getString("url"));
            }
            if (item.has("coords")) {
                ha.setCoords(item.getString("coords"));
            }
            if (item.has("news")) {
                ha.setUrl("");
            }
            hotAreas.add(ha);
        }
        iv.setHotAreas(hotAreas);
        iv.setListener(new HotAreaClickListener() {

            @Override
            public void onItemClick(int index, HotArea hotArea) {
                resolveUrl(hotArea.getUrl());
            }

            @Override
            public void onItemLongClick(int index, HotArea hotArea) {

            }
        });
        WindowManager wm = mHostActivity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = (int) (ratio * width);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageLoader.displayImage(component.getString("picurl"), iv);
        iv.setBackgroundColor(Color.BLACK);
        picCon.addView(iv, lp);

        Rect rectangle = new Rect();
        Window window = mHostActivity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int contentHeight = rectangle.height();
        if (null != mRootLl.findViewById(R.id.page_title)) {
            contentHeight -= mContext.getResources().getDimensionPixelSize(R.dimen
                    .title_height);
        }
        if (mHostActivity instanceof HomeBottomMenuActivity) {
            contentHeight -= mContext.getResources().getDimensionPixelSize(R.dimen
                    .menubar_home_bottom_height);
        }
        //状态栏高度

        LayoutParams lp1 = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, contentHeight);

        mContainerLl.addView(picCon, lp1);
    }

    private void addDefaultHotArea(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        double ratio = component.getDouble("picheightwidthratio");
        HotAreaView iv = new HotAreaView(mContext);
        JSONArray items = component.getJSONArray("items");
        UUID uuid = UUID.randomUUID();
        iv.setTag(uuid.toString());
        iv.setPicWidth(component.getInt("picwidth"));

        AsyTaskExecutor.getInstance().startTask(ASY_TASK_TAG_RESOLVE_HOTAREAS, new
                AsyTaskCallback() {

                    @Override
                    public void onTaskSuccess(Integer tag, Object obj) {
                        Map<String, Object> map = (Map<String, Object>) obj;
                        String viewTag = (String) map.get("viewTag");
                        List<HotArea> hotAreas = (List<HotArea>) map.get("hotareas");
                        HotAreaView iv = (HotAreaView) mContainerLl
                                .findViewWithTag(viewTag);
                        if (iv != null) {
                            iv.setHotAreas(hotAreas);
                        }
                    }

                    @Override
                    public void onTaskFail(Integer tag, Exception e) {

                    }

                    @Override
                    public Object onExecute(Integer tag, String[] params) throws Exception {
                        System.out.println(params[0]);
                        List<HotArea> areas = resolveHotareaItems(params[0]);
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("viewTag", params[1]);
                        map.put("hotareas", areas);
                        return map;
                    }
                }, new String[]{items.toString(), uuid.toString()});
        iv.setListener(new HotAreaClickListener() {

            @Override
            public void onItemClick(int index, HotArea hotArea) {
                System.out.println("PageFragment 点击热区 index:"+index);
                if (!TextUtils.isEmpty(getUrlReplacement(hotArea.getUrl()))) {
                    resolveUrl(convertUri(hotArea.getUrl()));
                } else {
                    resolveUrl(hotArea.getUrl());
                }
            }

            @Override
            public void onItemLongClick(int index, final HotArea hotArea) {
                if (configurable(hotArea.getUrl())) {


                    EditTextDialog dialog = new EditTextDialog(mContext, new
                            EditTextDialog.ConfirmListener() {
                                @Override
                                public void onOkClick(String result) {
                                    setUrlReplecement(hotArea.getUrl(), result);
                                }

                                @Override
                                public void onCancelClick() {

                                }
                            }, "自定义服务地址", "格式示例 \n http://www.example.com/", "取消", "确定");
                    dialog.setDefaultText(getUrlReplacement(hotArea.getUrl()));
                    dialog.show();
                }
            }

            private String convertUri(String originalUrl) {
                String tempUri = originalUrl;
                String replacement = getUrlReplacement(originalUrl);

                if (!TextUtils.isEmpty(replacement)) {
                    Pattern pattern = Pattern.compile("(http|https)://[^/]+");
                    Matcher matcher = pattern.matcher(originalUrl);
                    matcher.find();
                    tempUri = matcher.replaceFirst(replacement);
                }
                return tempUri;
            }

            private String getUrlReplacement(String url) {
                Map<String, String> customIpMap = (Map<String, String>) FileUtils.readObj
                        (mContext, CUSTOM_WEB_APP_URL_SERIALIZED_FILE_NAME);
                if (customIpMap == null) {
                    customIpMap = new HashMap<String, String>();
                }

                return customIpMap.get(url);
            }

            private void setUrlReplecement(String url, String replacement) {
                Map<String, String> customIpMap = (Map<String, String>) FileUtils.readObj
                        (mContext, CUSTOM_WEB_APP_URL_SERIALIZED_FILE_NAME);
                if (customIpMap == null) {
                    customIpMap = new HashMap<String, String>();
                }
                customIpMap.put(url, replacement);
                FileUtils.writeObj(mContext, customIpMap,
                        CUSTOM_WEB_APP_URL_SERIALIZED_FILE_NAME);
            }

            private boolean configurable(String url) {
                if (TextUtils.isEmpty(url)) {
                    return false;
                }
                String configurable = StringUtils.getQueryParameter(url, "configurable");
                if ("1".equals(configurable)) {
                    return true;
                }
                return false;
            }
        });
        WindowManager wm = mHostActivity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        LayoutParams lp = new LayoutParams(width, (int) (ratio
                * width));
        mImageLoader.displayImage(component.getString("picurl"), iv, getDefaultImageLoaderOptions
                ());
        mContainerLl.addView(iv, lp);
    }

    private void addFlowTagComponent(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        View view = LayoutInflater.from(mContext).inflate(R.layout
                .page_component_flow_tags, null);
        view.setBackgroundColor(Color.WHITE);
        CheckableFlowLayout cfl = (CheckableFlowLayout) view.findViewById(R.id.tag_fl);
        final JSONArray items = component.getJSONArray("items");
        for (int j = -1; ++j < items.length(); ) {
            cfl.addTag(items.getJSONObject(j).getString("title"));
        }
        cfl.setOnItemClickListener(new CheckableFlowLayout
                .OnItemClickListener() {

            @Override
            public void onItemClick(int pos, String tag, boolean isSelect) {
                try {
                    JSONObject jo = items.getJSONObject(pos);
                    View v = new View(mContext);
                    v.setTag(jo.getString("url"));
                    PageFragment.this.onClick(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onExceedMaxSelectedNum() {

            }
        });
        mContainerLl.addView(view);
    }

    private void addIconListComponent(PageComponent pc) {
        JSONObject component = pc.getJSONObject();
        PageGridView gridView = new PageGridView(getContext());
        GridViewModel m = new GridViewModel(component.toString());
        LayoutParams lp = new LayoutParams(LayoutParams
                .MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        gridView.setModel(m);
        gridView.setOnItemClickListener(new PageGridView.OnItemClickListener() {
            @Override
            public void onItemClick(String action) {
                resolveUrl(action);
            }
        });
        mContainerLl.addView(gridView, lp);
    }

    private void addIconListWith3ColumnComponent(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        JSONArray items = component.getJSONArray("items");
        FrameLayout fl = new FrameLayout(mHostActivity);
        fl.setBackgroundColor(Color.WHITE);
        int padding = Utils.dip2px(mContext, 10);
        fl.setPadding(padding, padding, padding, padding);
        GridLayout gl = new GridLayout(mHostActivity);
        gl.setBackgroundColor(Color.WHITE);
        LayoutParams lp1 = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        gl.setColumnCount(3);
        WindowManager wm = mHostActivity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();

        for (int j = -1; ++j < items.length(); ) {

            JSONObject item = items.getJSONObject(j);
            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = (width - padding * 2) / gl.getColumnCount();
            glp.setGravity(Gravity.FILL);
            RelativeLayout rl = (RelativeLayout) LayoutInflater.from(mContext).inflate(
                    R.layout.item_menu_gv, null);
            View verticalLine = rl.findViewById(R.id.vertical_line);
            verticalLine.setVisibility(View.GONE);
            TextView tv = (TextView) rl.findViewById(R.id.menu_tv);
            tv.setText(item.getString("title"));
            ImageView iv = (ImageView) rl.findViewById(R.id.menu_iv);
            mImageLoader.displayImage(item.getString("picurl"), iv);
            rl.setOnClickListener(this);
            rl.setTag(item.getString("url"));
            gl.addView(rl, glp);

        }
        fl.addView(gl, lp1);
        mContainerLl.addView(fl);
    }

    private void addDefaultIconListComponent(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        JSONArray items = component.getJSONArray("items");
        FrameLayout fl = new FrameLayout(mHostActivity);
        fl.setBackgroundColor(Color.WHITE);
        int padding = Utils.dip2px(mContext, 10);
        fl.setPadding(padding, padding, padding, padding);
        GridLayout gl = new GridLayout(mHostActivity);
        gl.setBackgroundColor(Color.WHITE);
        LayoutParams lp1 = new LayoutParams(LayoutParams
                .MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        gl.setColumnCount(4);
        WindowManager wm = mHostActivity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();

        for (int j = -1; ++j < items.length(); ) {

            JSONObject item = items.getJSONObject(j);
            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = (width - padding * 2) / gl.getColumnCount();
            glp.setGravity(Gravity.FILL);
            RelativeLayout rl = (RelativeLayout) LayoutInflater.from(mContext).inflate(R
                    .layout.item_menu_gv, null);
            View verticalLine = rl.findViewById(R.id.vertical_line);
            verticalLine.setVisibility(View.GONE);
            TextView tv = (TextView) rl.findViewById(R.id.menu_tv);
            tv.setText(item.getString("title"));
            ImageView iv = (ImageView) rl.findViewById(R.id.menu_iv);
            mImageLoader.displayImage(item.getString("picurl"), iv);
            rl.setOnClickListener(this);
            rl.setTag(item.getString("url"));
            gl.addView(rl, glp);

        }
        fl.addView(gl, lp1);
        mContainerLl.addView(fl);
    }

    private void addSlidePicWithPageControlOnlyComponent(PageComponent pc) throws
            JSONException {
        JSONObject component = pc.getJSONObject();
        float ratio = (float) component.getDouble("widthheightratio");
        SlidePicView spv = new SlidePicView(mContext, SlidePicView
                .STYLE_PAGE_CONTROL_ONLY, ratio);
        spv.setBackgroundColor(Color.WHITE);
        List<SlidePicView.SlidePicItem> list = new ArrayList<SlidePicView.SlidePicItem>();
        JSONArray items = component.getJSONArray("items");
        for (int j = -1; ++j < items.length(); ) {
            SlidePicView.SlidePicItem sp = new SlidePicView.SlidePicItem();
            JSONObject item = items.getJSONObject(j);
            sp.picURL = item.getString("picurl");
            sp.linkValue = item.getString("url");
            list.add(sp);
        }
        spv.setOnItemClickListener(new SlidePicView.OnItemClickListener() {

            @Override
            public void onClick(int pos, SlidePicView.SlidePicItem item) {
                resolveUrl(item.linkValue);
            }
        });
        spv.setData(list);

        mContainerLl.addView(spv);
    }

    private void addDefaultSlidePic(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        SlidePicView spv = new SlidePicView(mContext, SlidePicView.STYLE_UNDER_PIC);
        spv.setBackgroundColor(Color.WHITE);
        List<SlidePicView.SlidePicItem> list = new ArrayList<SlidePicView.SlidePicItem>();
        JSONArray items = component.getJSONArray("items");
        for (int j = -1; ++j < items.length(); ) {
            SlidePicView.SlidePicItem sp = new SlidePicView.SlidePicItem();
            JSONObject item = items.getJSONObject(j);
            sp.picURL = item.getString("picurl");
            sp.title = item.getString("title");
            sp.linkValue = item.getString("url");
            list.add(sp);
        }
        spv.setOnItemClickListener(new SlidePicView.OnItemClickListener() {

            @Override
            public void onClick(int pos, SlidePicView.SlidePicItem item) {
                resolveUrl(item.linkValue);
            }
        });
        spv.setData(list);

        mContainerLl.addView(spv);
    }

    private void addDefaultPicTextList(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        JSONArray items = component.getJSONArray("items");
        for (int j = -1; ++j < items.length(); ) {
            View view = LayoutInflater.from(mContext).inflate(R.layout
                    .page_component_pic_text_list_item, null);
            ImageView iv = (ImageView) view.findViewById(R.id.pic_text_iv);
            TextView tv = (TextView) view.findViewById(R.id.pic_text_title_tv);
            TextView pubTv = (TextView) view.findViewById(R.id.pic_text_pubtime_tv);
            JSONObject item = items.getJSONObject(j);
            if (TextUtils.isEmpty(item.getString("picurl"))) {
                iv.setVisibility(View.GONE);
            } else {
                mImageLoader.displayImage(item.getString("picurl"), iv);
            }
            tv.setText(item.getString("title"));
            pubTv.setText(item.getString("pubtime"));
            view.setOnClickListener(this);
            view.setTag(item.get("url"));
            mContainerLl.addView(view);
        }
    }

    private void addTabWithAnchorComponent(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        String title = component.getString("title");
        TextView tv = new TextView(mContext);
        int padding = Utils.dip2px(mContext, 10);
        tv.setPadding(padding, padding, padding, padding);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tv.setTextColor(mContext.getResources().getColor(R.color.common_text));
        tv.setText(title);
        tv.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        mContainerLl.addView(tv, lp);
        tv.setTag(component.getString("anchor"));
        mAnchorPointerViewList.add(tv);
    }

    private void addTagComponent(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        String title = component.getString("title");
        TextView tv = new TextView(mContext);
        int padding = Utils.dip2px(mContext, 10);
        tv.setPadding(padding, padding, padding, padding);
        tv.setBackgroundColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tv.setTextColor(mContext.getResources().getColor(R.color.common_text));
        tv.setText(title);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        mContainerLl.addView(tv, lp);
    }

    private void addSimplePicComponent(PageComponent pc) throws JSONException {
        JSONObject component = pc.getJSONObject();
        double ratio = component.getDouble("picheightwidthratio");
        RatioLayout rl = new RatioLayout(mContext, (float) ratio);
        ImageView iv = new ImageView(mContext);
        iv.setScaleType(ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams picLp = new RelativeLayout.LayoutParams(LayoutParams
                .MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rl.addView(iv, picLp);
        mImageLoader.displayImage(component.getString("picurl"), iv);
        mContainerLl.addView(rl);
        rl.setTag(component.getString("url"));
        rl.setOnClickListener(this);
    }

    private void addSinglePicComponent(PageComponent pc) throws JSONException {
        JSONObject jo = pc.getJSONObject();
        View view = LayoutInflater.from(mContext).inflate(R.layout.page_item_single_pic,
                null);
        ImageView iv = (ImageView) view.findViewById(R.id.single_pic_iv);
        TextView tv = (TextView) view.findViewById(R.id.single_pic_title_tv);
        tv.setText(jo.getString("title"));
        mImageLoader.displayImage(jo.getString("picurl"), iv);
        mContainerLl.addView(view);
        view.setTag(jo.getString("url"));
        view.setOnClickListener(this);
    }

    //解析热区，取出热区内动态数据，最后放到热区内
    private List<HotArea> resolveHotareaItems(String itemsStr) throws JSONException {
        List<HotArea> hotAreas = new ArrayList<HotArea>();
        JSONArray items = new JSONArray(itemsStr);
        for (int j = -1; ++j < items.length(); ) {
            JSONObject item = items.getJSONObject(j);
            HotArea ha = new HotArea();
            if (item.has("type")) {
                ha.setType(item.getString("type"));
            }
            if (item.has("shape")) {
                ha.setShape(item.getString("shape"));
            }
            if (item.has("url")) {
                ha.setUrl(item.getString("url"));
            }
            if (item.has("coords")) {
                ha.setCoords(item.getString("coords"));
            }
            if (item.has("textcolor") && !TextUtils.isEmpty(item.getString("textcolor"))) {
                try {
                    ha.setTextColor(Color.parseColor(item.getString("textcolor")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (item.has("textsize") && !TextUtils.isEmpty(item.getString("textsize"))) {
                ha.setTextSize(item.getInt("textsize"));
            }

            if (item.has("textalign")) {
                ha.setTextAlign(item.getString("textalign"));
            }

            if (item.has("bgcolor") && !TextUtils.isEmpty(item.getString("bgcolor"))) {
                try {
                    ha.setBgColor(Color.parseColor(item.getString("bgcolor")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (item.has("imageurl")) {
                if ("apppubs://macro/text/useravatarurl".equals(item.getString("imageurl"))) {
                    ha.setImage(mImageLoader.loadImageSync(AppContext.getInstance(mContext)
                            .getCurrentUser().getAvatarUrl()));
                } else {
                    ha.setImage(mImageLoader.loadImageSync(item.getString("imageurl")));
                }
            }
            ha.setText(item.has("text") ? item.getString("text") : "");
            hotAreas.add(ha);
        }
        return hotAreas;
    }

    private TitleBar buildTitleBar(TitleBarModel model) {
        if (model == null) {
            return null;
        }
        TitleBar titlebar = null;
        if (model.getType().equals("0")) {
            TitleBarNomalModel normalMordel = (TitleBarNomalModel) model;
            titlebar = new TitleBar(mContext);
            String title = normalMordel.getTitle();
            titlebar.setTitle(title);
            titlebar.setTitleTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R
                    .dimen.title_text_size));
            titlebar.setBackgroundColor(normalMordel.getBgColor());
            if (isLightColor(normalMordel.getBgColor())) {
                if (normalMordel.getBgColor() == mHostActivity.getThemeColor()) {
                    titlebar.setTitleTextColor(Color.BLACK);
                } else {
                    titlebar.setTitleTextColor(mHostActivity.getThemeColor());
                }
            } else {
                titlebar.setTitleTextColor(Color.WHITE);
            }


            //显示图片
            if (!TextUtils.isEmpty(normalMordel.getTitleImgUrl())) {
                ImageView iv = new ImageView(mContext);
                iv.setScaleType(ScaleType.CENTER_INSIDE);
                mImageLoader.displayImage(normalMordel.getTitleImgUrl(), iv);
                titlebar.setTitleView(iv);
            }
            //显示左边按钮
            if (!TextUtils.isEmpty(normalMordel.getLeftImgUrl())) {
                titlebar.addLeftBtnWithImageUrlAndClickListener(normalMordel.getLeftImgUrl(),
                        normalMordel
                        .getLeftAction(), PageFragment.this);
            }
            //显示右边按钮
            if (!TextUtils.isEmpty(normalMordel.getRightImgUrl())) {
                titlebar.addRightBtnWithImageUrlAndClickListener(normalMordel.getRightImgUrl(),
                        normalMordel
                        .getRightAction(), PageFragment.this);
            }
            titlebar.setUnderlineColor(normalMordel.getUnderlineColor());
        } else if (model.getType().equals(TitleBarModel.TYPE_ADDRESS)) {
            TitleBarAddressModel addressModel = (TitleBarAddressModel) model;
            titlebar = new TitleBar(mContext);
            String title = addressModel.getTitle();
            titlebar.setTitle(title);
            titlebar.setTitleTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R
                    .dimen.title_text_size));
            titlebar.setBackgroundColor(addressModel.getBgColor());
            if (isLightColor(addressModel.getBgColor())) {
                if (addressModel.getBgColor() == mHostActivity.getThemeColor()) {
                    titlebar.setTitleTextColor(Color.BLACK);
                } else {
                    titlebar.setTitleTextColor(mHostActivity.getThemeColor());
                }
            } else {
                titlebar.setTitleTextColor(Color.WHITE);
            }

            //显示图片
            if (!TextUtils.isEmpty(addressModel.getTitleImgUrl())) {
                ImageView iv = new ImageView(mContext);
                iv.setScaleType(ScaleType.CENTER_INSIDE);
                mImageLoader.displayImage(addressModel.getTitleImgUrl(), iv);
                titlebar.setTitleView(iv);
            }
            //显示地址
            if (!TextUtils.isEmpty(addressModel.getDefaultAddress())) {
                titlebar.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, AddressPickerActivity.class);
                        intent.putExtra(BaseActivity.EXTRA_STRING_TITLE, "选择地址");
                        startActivityForResult(intent, AddressPickerActivity.REQUEST_CODE);
                    }
                });
            }
            //显示右边按钮
            if (!TextUtils.isEmpty(addressModel.getRightImgUrl())) {
                titlebar.addRightBtnWithImageUrlAndClickListener(addressModel.getRightImgUrl(),
                        addressModel
                                .getRightAction(), PageFragment.this);
            }
            titlebar.setUnderlineColor(addressModel.getUnderlineColor());
        }
        return titlebar;
    }

    private boolean isLightColor(int color) {
        int colorWithoutAlpha = color & 0xFFFFFF;
        float redRatio = ((colorWithoutAlpha & 0xFF0000) >> 16) / 255.0f;
        float greenRatio = ((colorWithoutAlpha & 0x00FF00) >> 8) / 255.0f;
        float blueRatio = (colorWithoutAlpha & 0x0000FF) / 255.0f;
        return redRatio * greenRatio * blueRatio > 0.5;
    }

    /**
     * 增加20dp的空白行
     */
    private void addBlank(int height) {
        View v = new View(mContext);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(mHostActivity,
                height));
        mContainerLl.addView(v, lp);
    }

    /**
     * 增加水平分割线
     */
    private void addDivider() {
        View lineV = new View(mContext);
        LayoutParams lineLp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        lineV.setBackgroundColor(getResources().getColor(R.color.common_divider));
        mContainerLl.addView(lineV, lineLp);
    }


    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        resolveUrl(tag);
    }

    //解析url
    private void resolveUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if ((url.startsWith("apppubs://") && url.contains("anchorpointer"))) {
            mScrollView.scrollTo(0, 100);
            for (int i = -1; ++i < mAnchorPointerViewList.size(); ) {
                View view = mAnchorPointerViewList.get(i);
                String[] params = StringUtils.getPathParams(url);

                if (params[1].equals(view.getTag().toString())) {
                    mScrollView.smoothScrollTo(0, view.getTop());
                }
            }
        } else if (url.startsWith("apppubs://checkversion")) {
            mVersionPrsenter.checkUpdate();
        } else {
            ViewCourier.getInstance(mHostActivity).execute(mHostActivity, url);
        }
    }

}
