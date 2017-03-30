package com.apppubs.d20.message.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.activity.BaseActivity;
import com.apppubs.d20.adapter.ViewHolder;
import com.apppubs.d20.bean.Department;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.model.SystemBussiness;
import com.apppubs.d20.message.model.UserBasicInfo;
import com.apppubs.d20.message.model.UserPickerHelper;
import com.apppubs.d20.message.widget.Breadcrumb;
import com.apppubs.d20.widget.CircleTextImageView;
import com.apppubs.d20.R;
import com.apppubs.d20.adapter.CommonAdapter;
import com.apppubs.d20.message.widget.UserSelectionBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPickerActivity extends BaseActivity implements UserSelectionBar.UserSelectionBarListener {

    public static final String ARG_STRING_SUPER_ID = "super_id";
    private String mSuperId;
    private ListView mListView;
    private CommonAdapter<Department> mDeptAdapter;
    private CommonAdapter<User> mUserAdapter;
    private List<Department> mDepartmentList;
    private List<User> mUserList;
    private Breadcrumb mBreadcrumb;
    private UserSelectionBar mUserSelectBar;
    private Map<String,Integer> mListViewOffsetMap;
    private UserPickerHelper mUserPickerHelper;


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
        final String showCountFlag = AppContext.getInstance(this).getAppConfig().getAdbookOrgCountFlag();
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
                ImageView checkBtnIv = holder.getView(R.id.item_user_picker_check_btn);
                checkCheckBtn(checkBtnIv,mUserPickerHelper.isSelected(user.getUserId()),mUserPickerHelper.isPreselected(user.getUserId()));
                CircleTextImageView imageView = holder.getView(R.id.item_user_picker_user_iv);
                imageView.setTextColor(Color.WHITE);
                imageView.setFillColor(getResources().getColor(R.color.common_btn_bg_gray));
                imageView.setText(user.getTrueName());
                UserBasicInfo userBasicInfo = mUserBussiness.getCachedUserBasicInfo(user.getUserId());
                if (userBasicInfo!=null){
                    mImageLoader.displayImage(userBasicInfo.getAtatarUrl(),imageView);
                    TextView registerTv = holder.getView(R.id.item_user_picker_user_title1_tv);
                    registerTv.setVisibility(!TextUtils.isEmpty(userBasicInfo.getAppCodeVersion())?View.GONE:View.VISIBLE);
                }
            }
        };

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                if (currentSuperIdIsLeaf()){
                    ImageView checkBtnIv = (ImageView) view.findViewById(R.id.item_user_picker_check_btn);
                    if (checkBtnIv.isEnabled()){
                        checkCheckBtn(checkBtnIv,!checkBtnIv.isSelected(),false);

                        User user = mUserList.get(position);
                        String userId = user.getUserId();
                        if (checkBtnIv.isSelected()){
                            mUserPickerHelper.selectUser(userId);
                            UserBasicInfo ui = mUserBussiness.getCachedUserBasicInfo(userId);
                            if (ui==null){
                                ui = new UserBasicInfo();
                                ui.setUserId(user.getUserId());
                                ui.setTrueName(user.getTrueName());
                                ui.setUsername(user.getUsername());
                            }
                            mUserSelectBar.addUser(ui);
                        }else{
                            mUserPickerHelper.removeUser(userId);
                            mUserSelectBar.removeUser(userId);
                        }
                    }
                }else{
                    Department dep = mDepartmentList.get(position);
                    String id = dep.getDeptId();
                    mBreadcrumb.push(dep.getName(),id);
                    displayBySuperId(id,false);
                }
            }
        });
    }

    private void checkCheckBtn(ImageView checkBtnIv, boolean check,boolean lock) {
        if (lock){
            checkBtnIv.setSelected(true);
            checkBtnIv.setEnabled(false);
            checkBtnIv.setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_ATOP);
        }else{
            if (check){
                checkBtnIv.setSelected(true);
                checkBtnIv.setColorFilter(mThemeColor, PorterDuff.Mode.SRC_ATOP);
            }else{
                checkBtnIv.setSelected(false);
                checkBtnIv.clearColorFilter();
            }
        }
    }

    private void initData() {
        mListViewOffsetMap = new HashMap<String,Integer>();
        String rootId = AppContext.getInstance(this).getAppConfig().getAdbookRootId();
        mSuperId =  rootId;
        mDepartmentList = mUserBussiness.listRootDepartment();
        mUserPickerHelper = UserPickerHelper.getInstance(this);
    }

    private void initView() {
        overridePendingTransition(R.anim.slide_in_from_bottom,R.anim.slide_out_to_top);
        setContentView(R.layout.act_user_picker);
        getTitleBar().setLeftBtnWithText("取消");
        getTitleBar().setLeftBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
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

        mUserSelectBar = (UserSelectionBar) findViewById(R.id.user_picker_user_selection_bar);
        mUserSelectBar.setMaxSelectCount(mUserPickerHelper.getRemainSelectionNum());
        mUserSelectBar.setListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        mUserPickerHelper.cancelSelect();
        overridePendingTransition(R.anim.slide_in_from_top,R.anim.slide_out_to_bottom);
    }

    public void displayBySuperId(final String superId,boolean needResume){

        //存储上一个列表的偏移量
        mListViewOffsetMap.put(mSuperId,mListView.getFirstVisiblePosition());
        if (!mUserBussiness.isLeaf(superId)) {
			if(mAppContext.getAppConfig().getChatAuthFlag()==1){
				mDepartmentList = mUserBussiness.listSubDepartment(superId,mAppContext.getCurrentUser().getChatPermissionString());
			}else{
				mDepartmentList = mUserBussiness.listSubDepartment(superId);
			}
            mDeptAdapter.setData(mDepartmentList);
            mListView.setAdapter(mDeptAdapter);
        } else {
            mUserList = mUserBussiness.listUser(superId);
            mUserAdapter.setData(mUserList);
            mListView.setAdapter(mUserAdapter);
            List<String> userIds = new ArrayList<String>();
            for (User user : mUserList){
                userIds.add(user.getUserId());
            }
            mUserBussiness.cacheUserBasicInfoList(userIds, new BussinessCallbackCommon<List<UserBasicInfo>>() {
                @Override
                public void onDone(List<UserBasicInfo> obj) {

//                    mUserAdapter.notifyDataSetChanged();

                }

                @Override
                public void onException(int excepCode) {

                }
            });
        }
        if (needResume){
            int offset = mListViewOffsetMap.get(superId)==null?0:mListViewOffsetMap.get(superId);
            mListView.setSelection(offset);
        }
        mSuperId = superId;

        hideOrShowAllCheckBtn(mUserBussiness.isLeaf(superId));
    }

    private void hideOrShowAllCheckBtn(boolean isShow) {
        if (isShow){
            getTitleBar().setRightBtnWithText("全选");
            getTitleBar().getRightView().setVisibility(View.VISIBLE);
            getTitleBar().setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) v;
                    if (tv.getText().equals("全选")){
                        for (User user: mUserList){
                            boolean success = mUserPickerHelper.selectUser(user.getUserId());
                            if (success){
                                UserBasicInfo userBasicInfo = mUserBussiness.getCachedUserBasicInfo(user.getUserId());
                                if (userBasicInfo==null){
                                    userBasicInfo.setUsername(user.getUsername());
                                    userBasicInfo.setTrueName(user.getTrueName());
                                    userBasicInfo.setUserId(user.getUserId());
                                }
                                mUserSelectBar.addUser(userBasicInfo);
                                mUserAdapter.notifyDataSetChanged();
                            }

                        }
                        getTitleBar().setRightText("取消");
                    }else{

                        List<String> userIds = new ArrayList<String>();
                        for (User user: mUserList){
                            userIds.add(user.getUserId());
                        }
                        mUserPickerHelper.removeUsers(userIds);
                        mUserSelectBar.removeUsers(userIds);
                        mUserAdapter.notifyDataSetChanged();
                        getTitleBar().setRightText("全选");
                    }
                }
            });
        }else{

            View view = getTitleBar().getRightView();
            if (view!=null){
                view.setVisibility(View.GONE);
            }
        }
    }

    private boolean currentSuperIdIsLeaf(){
        return mUserBussiness.isLeaf(mSuperId);
    }


    @Override
    public void onItemClick(String userId) {
        mUserPickerHelper.removeUser(userId);
        if (currentSuperIdIsLeaf()){
            mUserAdapter.notifyDataSetChanged();
        }else{
            mDeptAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDoneClick() {
        if (mUserPickerHelper.getUserPickerListener()!=null){
            mUserPickerHelper.getUserPickerListener().onPickDone(mUserPickerHelper.getSelectedUserIds());
            finish();
        }
    }


}
