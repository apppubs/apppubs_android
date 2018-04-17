package com.apppubs.ui.page;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.bean.AddressModel;
import com.apppubs.presenter.AddressPickerPresenter;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.ui.adapter.ViewHolder;

import java.util.List;

public class AddressPickerActivity extends BaseActivity implements IAddressPickerView {

    public static final String EXTRA_KEY_ROOT_ID = "root_id";

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_SUCCESS = 1;

    public static final String RESULT_EXTRA_KEY_ADDRESS = "address";

    private String mRootId;

    private List<AddressModel> mModels;
    private List<AddressModel> mCurSecondaryModels;

    private AddressPickerPresenter mPresenter;

    private ListView mListView1;

    private ListView mListView2;

    private CommonAdapter<AddressModel> mAdapter1;

    private CommonAdapter<AddressModel> mAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_address_picker);
        mRootId = getIntent().getStringExtra(EXTRA_KEY_ROOT_ID);
        initView();
        initAdapter();
        initPresenter();
        mPresenter.onCreateView();
    }

    @Override
    public String getAddressRootId() {
        return mRootId;
    }

    @Override
    public void setModels(List<AddressModel> models) {
        mModels = models;
        mAdapter1.setData(models);
        mAdapter1.notifyDataSetChanged();
        if (models.size() > 0) {
//            mAdapter2.notifyDataSetChanged(models.get(0).getItems());
        }
    }

    //IaddressPickerView
    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    //private
    private void initView() {
        mListView1 = (ListView) findViewById(R.id.act_address_picker_lv1);
        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddressModel model = mModels.get(position);
                mCurSecondaryModels = model.getItems();
                mAdapter2.notifyDataSetChanged(mCurSecondaryModels);
                for (int i = 0; i < parent.getCount(); i++) {
                    View item = parent.getChildAt(i).findViewById(R.id.item_address_picker1_tv);
                    TextView tv = (TextView) item.findViewById(R.id.item_address_picker1_tv);
                    if (position == i) {//当前选中的Item改变背景颜色
                        item.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        tv.setTextColor(getThemeColor());
                    } else {
                        item.setBackgroundColor(Color.parseColor("#F2F2F2"));
                        tv.setTextColor(mContext.getResources().getColor(R.color.common_text_gray));
                    }
                }
            }
        });
        mListView2 = (ListView) findViewById(R.id.act_address_picker_lv2);
        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra(RESULT_EXTRA_KEY_ADDRESS, mCurSecondaryModels.get(position));
                setResult(RESULT_SUCCESS, data);
                finish();
            }
        });
    }

    private void initAdapter() {
        mAdapter1 = new CommonAdapter<AddressModel>(mContext, R.layout.item_address_picker1_lv) {
            @Override
            protected void fillValues(ViewHolder holder, AddressModel bean, int position) {
                TextView tv = holder.getView(R.id.item_address_picker1_tv);
                tv.setText(bean.getName());
            }
        };
        mListView1.setAdapter(mAdapter1);
        mAdapter2 = new CommonAdapter<AddressModel>(mContext, R.layout.item_address_picker2_lv) {
            @Override
            protected void fillValues(ViewHolder holder, AddressModel bean, int position) {
                TextView tv = holder.getView(R.id.item_address_picker2_tv);
                tv.setText(bean.getName());
            }
        };
        mListView2.setAdapter(mAdapter2);
    }

    private void initPresenter() {
        mPresenter = new AddressPickerPresenter(this);
    }
}
