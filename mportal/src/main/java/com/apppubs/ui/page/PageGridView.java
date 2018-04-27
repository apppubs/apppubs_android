package com.apppubs.ui.page;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apppubs.bean.page.GridViewItem;
import com.apppubs.bean.page.GridViewModel;
import com.apppubs.d20.R;
import com.apppubs.util.StringUtils;
import com.apppubs.util.Utils;
import com.apppubs.ui.widget.Indicator;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by zhangwen on 2017/9/29.
 */

public class PageGridView extends RelativeLayout implements View.OnClickListener, ViewPager
        .OnPageChangeListener {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Indicator mIndicator;

    private GridViewModel mModel;

    private OnItemClickListener mListener;

    public PageGridView(Context context) {
        super(context);
        initView();
    }

    public PageGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public interface OnItemClickListener {
        void onItemClick(String action);
    }

    private void initView() {
        mViewPager = new ViewPager(getContext());
        mViewPager.setBackgroundColor(Color.WHITE);
        mViewPager.addOnPageChangeListener(this);
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                .MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        this.addView(mViewPager, lp1);
    }

    public void setModel(final GridViewModel model) {
        this.mModel = model;
        if (model == null) {
            throw new IllegalArgumentException("model不可为空");
        }
        getAdapter(model);
        mViewPager.setAdapter(mPagerAdapter);
        if (model.getTotalPage() > 1) {
            addIndicator(model);
        }
        int pageHeight = getViewPagerHeight(model);
        ViewGroup.LayoutParams lp = mViewPager.getLayoutParams();
        lp.height = pageHeight;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //pargma private
    private void getAdapter(final GridViewModel model) {
        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return model.getTotalPage();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                GridLayout gridLayout = getGridLayout(position);
                container.addView(gridLayout);
                return gridLayout;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }
        };
    }

    private int getViewPagerHeight(GridViewModel model) {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int itemWidth = width / model.getColumn();
        int pageHeight = 0;
        int indicatorHeight = Utils.dip2px(getContext(), 30);
        if (model.getTotalPage() > 1) {
            pageHeight = (int) (itemWidth * 1.1) * model.getRealMaxRow() + indicatorHeight;
        } else {
            pageHeight = (int) (itemWidth * 1.1) * model.getRealMaxRow();
        }
        return pageHeight;
    }

    private void addIndicator(GridViewModel model) {
        mIndicator = new Indicator(getContext(), model.getTotalPage(), Indicator.STYLE_DARK);
        mIndicator.setPadding(0, 0, 0, Utils.dip2px(getContext(), 10));
        mIndicator.setCurItem(0);
        mIndicator.setGravity(Gravity.CENTER);
        LayoutParams indicatorLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .WRAP_CONTENT);
        indicatorLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        indicatorLp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        addView(mIndicator, indicatorLp);
    }

    @NonNull
    private GridLayout getGridLayout(int index) {
        GridLayout gridLayout = new GridLayout(getContext());
        gridLayout.setColumnCount(mModel.getColumn());
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        List<GridViewItem> items = mModel.getItemsForPage(index);
        for (int i = -1; ++i < items.size(); ) {
            GridViewItem item = items.get(i);
            RelativeLayout rl = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R
                    .layout.item_menu_gv, null);
            View verticalLine = rl.findViewById(R.id.vertical_line);
            verticalLine.setVisibility(View.GONE);
            TextView tv = (TextView) rl.findViewById(R.id.menu_tv);
            tv.setText(item.getTitle());
            ImageView iv = (ImageView) rl.findViewById(R.id.menu_iv);
            ImageLoader.getInstance().displayImage(item.getPicUrl(), iv);

            TextView badgeTV = (TextView) rl.findViewById(R.id.menu_reddot);
            Integer badgeNum = item.getBadgeNum();
            if (!Utils.isEmpty(badgeNum)
                    && badgeNum > 0) {
                badgeTV.setText(badgeNum+"");
                badgeTV.setVisibility(View.VISIBLE);
            } else {
                badgeTV.setVisibility(View.GONE);
            }

            rl.setOnClickListener(this);
            rl.setTag(item.getAction());
            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = width / gridLayout.getColumnCount();
            glp.height = (int) (glp.width * 1.1);
            glp.setGravity(Gravity.FILL);
            gridLayout.addView(rl, glp);
        }
        return gridLayout;
    }

    //OnClickListener
    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onItemClick(v.getTag().toString());
        }
    }

    //OnPageChangeListener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mIndicator.setCurItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
