package com.apppubs.ui.message.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.AppContext;
import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TUser;
import com.apppubs.bean.http.UserBasicInfosResult;
import com.apppubs.d20.BuildConfig;
import com.apppubs.d20.R;
import com.apppubs.model.UserBiz;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.ui.adbook.UserInfoActivity;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.ui.widget.CircleTextImageView;
import com.apppubs.ui.widget.Breadcrumb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressBookOrganizationFragement extends BaseFragment {

    private int TAG_DEPT = 1;
    private int TAG_USER = 2;

    private String mSuperId;
    private ListView mListView;
    private CommonAdapter<TDepartment> mDeptAdapter;
    private CommonAdapter<TUser> mUserAdapter;
    private List<TDepartment> mDepartmentList;
    private List<TUser> mUserList;
    private Breadcrumb mBreadcrumb;
    private TextView mHeaderNumberTV;//listview头部显示当前列表item数量的textview
    private Map<String, Integer> mListViewOffsetMap;
    private Listener mListener;

    public interface Listener {
        void onCreateView();

        void onDeptSelected(String deptId);

        void onCreateDiscussClicked(String deptId);
    }

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initData();
        mRootView = initView(inflater);
        initAdapter();

        return mRootView;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    private void initAdapter() {
//		final int totalRow = mDepartmentList.size();
        final String showCountFlag = AppContext.getInstance(mContext).getAppConfig().getAdbookOrgCountFlag();
        mDeptAdapter = new CommonAdapter<TDepartment>(mContext, mDepartmentList, R.layout.item_organization_lv) {

            @Override
            protected void fillValues(ViewHolder holder, TDepartment bean, int position) {

                ((TextView) holder.getView(R.id.org_item_name_tv)).setText(bean.getName());
                TextView detailTv = ((TextView) holder.getView(R.id.org_item_count_tv));
                if (showCountFlag.equals("1")) {
                    detailTv.setVisibility(View.VISIBLE);
                    detailTv.setText("共" + bean.getTotalNum() + "人");
                } else {
                    detailTv.setVisibility(View.GONE);
                }
                View line = holder.getView(R.id.line);
                View longerLine = holder.getView(R.id.line_longer);
                //最后一行的宽度是全屏的.隐藏item里的线
//				if((totalRow-1)==position){
//					line.setVisibility(View.GONE);
//					longerLine.setVisibility(View.VISIBLE);
//				}else{
//					line.setVisibility(View.VISIBLE);
//					longerLine.setVisibility(View.GONE);
//				}
            }
        };

        mUserAdapter = new CommonAdapter<TUser>(mContext, mUserList, R.layout.item_adbook_org_user_lv) {
            @Override
            protected void fillValues(ViewHolder holder, TUser user, int position) {
                TextView titleTv = holder.getView(R.id.item_user_picker_user_title_tv);
                titleTv.setText(user.getTrueName());
                TextView desTv = holder.getView(R.id.item_user_picker_user_des_tv);
                if (!TextUtils.isEmpty(user.getWorkTEL()) && !TextUtils.isEmpty(user.getMobile())) {
                    desTv.setText("手机:" + user.getMobile() + " 电话:" + user.getWorkTEL());
                } else if (TextUtils.isEmpty(user.getWorkTEL()) && !TextUtils.isEmpty(user.getMobile())) {
                    desTv.setText("手机:" + user.getMobile());
                } else if (!TextUtils.isEmpty(user.getWorkTEL()) && TextUtils.isEmpty(user.getMobile())) {
                    desTv.setText("电话:" + user.getWorkTEL());
                } else {
                    desTv.setText("");
                }
                CircleTextImageView imageView = holder.getView(R.id.item_user_picker_user_iv);
                imageView.setTextColor(Color.WHITE);
                imageView.setFillColor(getResources().getColor(R.color.common_btn_bg_gray));
                imageView.setText(user.getTrueName());
                UserBasicInfosResult.Item userBasicInfo = UserBiz.getInstance(mContext).getCachedUserBasicInfo(user
                        .getUserId());
                if (userBasicInfo != null) {
                    if (!TextUtils.isEmpty(userBasicInfo.getAvatarURL())) {
                        mImageLoader.displayImage(userBasicInfo.getAvatarURL(), imageView);
                    }else{
                        imageView.setImageDrawable(null);
                    }
                    if (BuildConfig.ENABLE_CHAT) {
                        TextView registerTv = holder.getView(R.id.item_user_picker_user_title1_tv);
                        registerTv.setVisibility(userBasicInfo.getAppVersionCode() > 0 ? View.GONE :
                                View.VISIBLE);
                        ImageView chatBubble = holder.getView(R.id.item_user_picker_bubble_iv);
                        chatBubble.setVisibility(userBasicInfo.getAppVersionCode() > 0 ? View.VISIBLE
                                : View.GONE);
                    }
                } else {
                    TextView registerTv = holder.getView(R.id.item_user_picker_user_title1_tv);
                    registerTv.setVisibility(View.GONE);
                    ImageView chatBubble = holder.getView(R.id.item_user_picker_bubble_iv);
                    chatBubble.setVisibility(View.GONE);
                    imageView.setImageDrawable(null);
                }
            }
        };

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                if (BuildConfig.ENABLE_CHAT && position == 0) {
                    prepareForCreateDiscuss();
                } else if ((Integer) adapterView.getTag() == TAG_USER) {
                    TUser user = (TUser) adapterView.getAdapter().getItem(position);
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.EXTRA_STRING_USER_ID, user.getUserId());
                    getActivity().startActivity(intent);
                } else {
                    TDepartment dep = (TDepartment) adapterView.getAdapter().getItem(position);
                    String id = dep.getDeptId();
                    mBreadcrumb.push(dep.getName(), id);
                    mSuperId = id;
                    if (mListener != null) {
                        mListener.onDeptSelected(dep.getDeptId());
                    }
                }
            }
        });

        if (mListener != null) {
            mListener.onCreateView();
        }
    }

    private void prepareForCreateDiscuss() {
        if (mListener != null) {
            mListener.onCreateDiscussClicked(mSuperId);
        }
    }

    private void initData() {
        mListViewOffsetMap = new HashMap<String, Integer>();
    }

    private View initView(LayoutInflater inflater) {
        mRootView = inflater.inflate(R.layout.frg_addressbook_org, null);
        mListView = (ListView) mRootView.findViewById(R.id.user_picker_lv);
        mListView.setEmptyView(mRootView.findViewById(R.id.user_picker_empty_tv));
        View header = inflater.inflate(R.layout.header_adbook_org_lv, mListView, false);
        mHeaderNumberTV = (TextView) header.findViewById(R.id.header_adbook_org_lv_text_tv);
        if (BuildConfig.ENABLE_CHAT) {
            View createDiscussLl = header.findViewById(R.id.adbook_org_header_create_discuss_ll);
            createDiscussLl.setVisibility(View.VISIBLE);
        }
        mListView.addHeaderView(header);


        mBreadcrumb = (Breadcrumb) mRootView.findViewById(R.id.user_picker_bc);
        mBreadcrumb.setTextColor(Color.BLACK);

        mBreadcrumb.setTextColor(getResources().getColor(R.color.common_text));
        mBreadcrumb.setOnItemClickListener(new Breadcrumb.OnItemClickListener() {
            @Override
            public void onItemClick(int index, String tag) {
                mSuperId = tag;
                if (mListener != null) {
                    mListener.onDeptSelected(tag);
                }
            }
        });
        return mRootView;
    }

    public void setDetps(List<TDepartment> departments) {
        mDepartmentList = departments;
        mHeaderNumberTV.setText("部门(" + mDepartmentList.size() + ")");

        mDeptAdapter.setData(mDepartmentList);
        mListView.setAdapter(mDeptAdapter);
        mListView.setTag(TAG_DEPT);
    }

    public void setUsers(List<TUser> users) {
        mUserList = users;
        mHeaderNumberTV.setText("人员(" + mUserList.size() + ")");

        mUserAdapter.setData(mUserList);
        mListView.setAdapter(mUserAdapter);
        mListView.setTag(TAG_USER);
    }

    public void clearBreadcrumb(TDepartment dept) {
        mBreadcrumb.clear();
        if (dept != null) {
            mBreadcrumb.push(dept.getName(), dept.getDeptId());
        }
        mSuperId = dept.getDeptId();
    }
}
