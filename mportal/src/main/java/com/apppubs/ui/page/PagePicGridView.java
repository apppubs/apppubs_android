package com.apppubs.ui.page;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayout;
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

import com.apppubs.d20.R;
import com.apppubs.ui.widget.Indicator;
import com.apppubs.util.Utils;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2017/9/29.
 */

public class PagePicGridView extends RelativeLayout implements View.OnClickListener, ViewPager
        .OnPageChangeListener {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Indicator mIndicator;

    private Model mModel;

    private OnItemClickListener mListener;

    public PagePicGridView(Context context) {
        super(context);
        initView();
    }

    public PagePicGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public interface OnItemClickListener {
        void onItemClick(String action);
    }

    private void initView() {
        setBackgroundColor(Color.WHITE);
        mViewPager = new ViewPager(getContext());
        mViewPager.setBackgroundColor(Color.WHITE);
        mViewPager.setId(R.id.temp_id);
        mViewPager.addOnPageChangeListener(this);
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                .MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        this.addView(mViewPager, lp1);
    }

    public void setModel(final Model model) {
        this.mModel = model;
        if (model == null) {
            throw new IllegalArgumentException("model不可为空");
        }
        initAdapter(model);
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
    private void initAdapter(final Model model) {
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

    private int getViewPagerHeight(Model model) {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int itemWidth = width / model.getColumn();
        float itemHeight = itemWidth * model.cellHeightWidthRatio;
        int pageHeight = 0;
        int indicatorHeight = Utils.dip2px(getContext(), 30);
        if (model.getTotalPage() > 1) {
            pageHeight = (int) itemHeight * model.getRealMaxRow() ;
        } else {
            pageHeight = (int) itemHeight * model.getRealMaxRow();
        }
        return pageHeight;
    }

    private void addIndicator(Model model) {
        mIndicator = new Indicator(getContext(), model.getTotalPage(), Indicator.STYLE_DARK);
        mIndicator.setPadding(0, 0, 0, Utils.dip2px(getContext(), 10));
        mIndicator.setCurItem(0);
        mIndicator.setGravity(Gravity.CENTER);
        LayoutParams indicatorLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .WRAP_CONTENT);

        indicatorLp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        indicatorLp.addRule(RelativeLayout.BELOW,mViewPager.getId());
        addView(mIndicator, indicatorLp);
    }

    @NonNull
    private GridLayout getGridLayout(int index) {
        GridLayout gridLayout = new GridLayout(getContext());
        gridLayout.setColumnCount(mModel.getColumn());
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        List<ModelItem> items = mModel.getItemsForPage(index);

        for (int i = -1; ++i < items.size(); ) {
            ModelItem item = items.get(i);
            RelativeLayout rl = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R
                    .layout.item_menu_pic_gv, null);
            View verticalLine = rl.findViewById(R.id.vertical_line);
            verticalLine.setVisibility(View.GONE);
            ImageView iv = (ImageView) rl.findViewById(R.id.menu_iv);
            Glide.with(getContext()).load(item.getPicURL()).into(iv);
            TextView badgeTV = (TextView) rl.findViewById(R.id.menu_reddot);
            Integer badgeNum = item.getBadgeNum();
            if (!Utils.isEmpty(badgeNum)
                    && badgeNum > 0) {
                badgeTV.setText(badgeNum + "");
                badgeTV.setVisibility(View.VISIBLE);
            } else {
                badgeTV.setVisibility(View.GONE);
            }

            rl.setOnClickListener(this);
            rl.setTag(item.getAction());
            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = width / gridLayout.getColumnCount();
            glp.height = (int) (glp.width * mModel.getCellHeightWidthRatio());
//            glp.setGravity(Gravity.FILL);
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

    public class Model {
        private int column;
        private int maxRow;
        private float cellHeightWidthRatio;
        private List<ModelItem> items;

        public Model(String jsonStr) {
            try {
                JSONObject jo = new JSONObject(jsonStr);
                this.column = jo.getInt("column");
                this.maxRow = jo.getInt("maxRow");
                this.cellHeightWidthRatio = (float) jo.getDouble("cellHeightWidthRatio");
                JSONArray items = jo.getJSONArray("items");
                List<ModelItem> list = new ArrayList<ModelItem>();
                for (int i = -1; ++i < items.length(); ) {
                    ModelItem item = new ModelItem(items.getString(i));
                    list.add(item);
                }
                this.items = list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public int getMaxRow() {
            return maxRow;
        }

        public void setMaxRow(int maxRow) {
            this.maxRow = maxRow;
        }

        public float getCellHeightWidthRatio() {
            return cellHeightWidthRatio;
        }

        public void setCellHeightWidthRatio(float cellHeightWidthRatio) {
            this.cellHeightWidthRatio = cellHeightWidthRatio;
        }

        public List<ModelItem> getItems() {
            return items;
        }

        public void setItems(List<ModelItem> items) {
            this.items = items;
        }

        public List<ModelItem> getItemsForPage(int pageIndex) {
            List<ModelItem> list = new ArrayList<ModelItem>();
            int pageSize = maxRow * column;
            int pageStartIndex = pageSize * pageIndex;
            if (items.size() > pageStartIndex + pageSize) {
                list.addAll(items.subList(pageStartIndex, pageStartIndex + pageSize));
            } else if (items.size() > pageStartIndex) {
                list.addAll(items.subList(pageStartIndex, items.size()));
            } else {
                //do nothing
            }
            return list;
        }

        public int getTotalPage() {
            return items.size() % (maxRow * column) == 0 ? items.size() / (maxRow *
                    column) : items
                    .size() / (maxRow * column) + 1;
        }

        public int getRealMaxRow() {
            if (items.size() <= maxRow * column) {
                return items.size() % column == 0 ? items.size() / column : items.size() / column
                        + 1;
            } else {
                return maxRow;
            }
        }
    }

    public class ModelItem {
        private String picURL;
        private String action;
        private int badgeNum;

        public ModelItem(String jsonStr) {
            try {
                JSONObject jo = new JSONObject(jsonStr);
                this.picURL = jo.getString("picURL");
                this.action = jo.getString("URL");
                this.badgeNum = jo.has("badgeNum") ? jo.getInt("badgeNum") : null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getPicURL() {
            return picURL;
        }

        public void setPicURL(String picURL) {
            this.picURL = picURL;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public int getBadgeNum() {
            return badgeNum;
        }

        public void setBadgeNum(int badgeNum) {
            this.badgeNum = badgeNum;
        }
    }
}
