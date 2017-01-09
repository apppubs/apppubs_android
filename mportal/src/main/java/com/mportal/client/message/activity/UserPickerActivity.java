package com.mportal.client.message.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mportal.client.R;
import com.mportal.client.activity.BaseActivity;
import com.mportal.client.activity.UserInfoActivity;
import com.mportal.client.adapter.CommonAdapter;
import com.mportal.client.adapter.ViewHolder;
import com.mportal.client.bean.Department;
import com.mportal.client.bean.User;
import com.mportal.client.business.SystemBussiness;
import com.mportal.client.message.widget.Breadcrumb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPickerActivity extends BaseActivity {


    public static final String ARG_STRING_SUPER_ID = "super_id";

    private String mSuperId;
    private ListView mListView;
    private CommonAdapter<Department> mDeptAdapter;
    private CommonAdapter<User> mUserAdapter;
    private List<Department> mDepartmentList;
    private List<User> mUserList;
    private Breadcrumb mBreadcrumb;
    private Map<String,Integer> mListViewOffsetMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initAdapter();
        displayBySuperId(mSuperId,false);
    }

    private void initAdapter() {
        final int totalRow = mDepartmentList.size();
        final String showCountFlag = SystemBussiness.getInstance(this).getAppConfig().getAdbookOrgCountFlag();
        mDeptAdapter = new CommonAdapter<Department>(this,mDepartmentList, R.layout.item_organization_lv) {

            @Override
            protected void fillValues(ViewHolder holder, Department bean, int position) {

                ((TextView)holder.getView(R.id.org_item_name_tv)).setText(bean.getName());
                TextView detailTv = ((TextView) holder.getView(R.id.org_item_count_tv));
                if(showCountFlag.equals("1")){
                    detailTv.setVisibility(View.VISIBLE);
                    detailTv.setText("共" + bean.getTotalNum() + "人");
                }else{
                    detailTv.setVisibility(View.GONE);
                }
                View line = holder.getView(R.id.line);
                View longerLine = holder.getView(R.id.line_longer);
                //最后一行的宽度是全屏的.隐藏item里的线
                if((totalRow-1)==position){
                    line.setVisibility(View.GONE);
                    longerLine.setVisibility(View.VISIBLE);
                }else{
                    line.setVisibility(View.VISIBLE);
                    longerLine.setVisibility(View.GONE);
                }
            }
        };

        mUserAdapter = new CommonAdapter<User>(this,mUserList,R.layout.item_user_picker_user_lv) {
            @Override
            protected void fillValues(ViewHolder holder, User user, int position) {
                TextView titleTv = holder.getView(R.id.item_user_picker_user_title_tv);
                titleTv.setText(user.getTrueName());
                TextView desTv = holder.getView(R.id.item_user_picker_user_des_tv);
                if(!TextUtils.isEmpty(user.getWorkTEL())&&!TextUtils.isEmpty(user.getMobile())){
                    desTv.setText("手机:"+user.getMobile()+" 电话:"+user.getWorkTEL());
                }else if(TextUtils.isEmpty(user.getWorkTEL())&&!TextUtils.isEmpty(user.getMobile())){
                    desTv.setText("手机:"+user.getMobile());
                }else if(!TextUtils.isEmpty(user.getWorkTEL())&&TextUtils.isEmpty(user.getMobile())){
                    desTv.setText("电话:"+user.getWorkTEL());
                }
            }
        };

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                if (currentSuperIdIsLeaf()){
                    User user = mUserList.get(position);
                    Intent intent = new Intent(UserPickerActivity.this, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.EXTRA_STRING_USER_ID, user.getUserId());
                    UserPickerActivity.this.startActivity(intent);
                }else{
                    Department dep = mDepartmentList.get(position);
                    String id = dep.getDeptId();
                    mBreadcrumb.push(dep.getName(),id);
                    displayBySuperId(id,false);
                }
            }
        });
    }

    private void initData() {
        mListViewOffsetMap = new HashMap<String,Integer>();
        String rootId = SystemBussiness.getInstance(this).getAppConfig().getAdbookRootId();
        mSuperId =  rootId;
        mDepartmentList = mUserBussiness.listRootDepartment();
    }

    private void initView() {
        overridePendingTransition(R.anim.slide_in_from_bottom,R.anim.slide_out_to_top);
        setContentView(R.layout.act_user_picker);
        getTitleBar().setLeftBtnWithText("取消");
        mListView = (ListView) findViewById(R.id.user_picker_lv);
        mListView.setEmptyView(findViewById(R.id.user_picker_empty_tv));

        mBreadcrumb = (Breadcrumb) findViewById(R.id.user_picker_bc);
        mBreadcrumb.setTextColor(Color.BLACK);
        mBreadcrumb.push("组织",mSuperId);

        mBreadcrumb.setTextColor(getResources().getColor(R.color.common_text));
        mBreadcrumb.setOnItemClickListener(new Breadcrumb.OnItemClickListener() {
            @Override
            public void onItemClick(int index,String tag) {
                displayBySuperId(tag,true);
            }
        });
    }

    public static void startActivity(Context context){
        Intent startIntent = new Intent(context,UserPickerActivity.class);
        startIntent.putExtra(BaseActivity.EXTRA_STRING_TITLE,"选择人员");
        context.startActivity(startIntent);
    }

    public static void startActivity(Context context,String title){
        Intent startIntent = new Intent(context,UserPickerActivity.class);
        if (TextUtils.isEmpty(title)){
            startIntent.putExtra(BaseActivity.EXTRA_STRING_TITLE,"选择人员");
        }else{
            startIntent.putExtra(BaseActivity.EXTRA_STRING_TITLE,title);
        }
        context.startActivity(startIntent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_top,R.anim.slide_out_to_bottom);
    }

    public void displayBySuperId(final String superId,boolean needResume){

        if (!mUserBussiness.isLeaf(superId)) {
            mDepartmentList = mUserBussiness.listSubDepartment(superId);
            mDeptAdapter.setData(mDepartmentList);
            //存储上一个列表的偏移量
            mListViewOffsetMap.put(mSuperId,mListView.getFirstVisiblePosition());

            mListView.setAdapter(mDeptAdapter);
            if (needResume){
                int offset = mListViewOffsetMap.get(superId)==null?0:mListViewOffsetMap.get(superId);
                mListView.setSelection(offset);
            }

        } else {
            mUserList = mUserBussiness.listUser(superId);
            mUserAdapter.setData(mUserList);
            mListView.setAdapter(mUserAdapter);
        }

        mSuperId = superId;
    }

    private boolean currentSuperIdIsLeaf(){
        return mUserBussiness.isLeaf(mSuperId);
    }
}
