package com.mportal.client.message.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mportal.client.R;
import com.mportal.client.activity.BaseActivity;
import com.mportal.client.activity.ContainerActivity;
import com.mportal.client.adapter.CommonAdapter;
import com.mportal.client.adapter.ViewHolder;
import com.mportal.client.bean.Department;
import com.mportal.client.business.SystemBussiness;
import com.mportal.client.constant.Constants;
import com.mportal.client.message.fragment.AddressBookOrganizationFragement;
import com.mportal.client.message.fragment.AddressBookUserListFragment;
import com.mportal.client.util.FileUtils;
import com.mportal.client.util.LogM;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserPickerActivity extends BaseActivity {


    public static final String ARG_STRING_SUPER_ID = "super_id";

    private String mSuperId;
    private ListView depLv;
    private CommonAdapter<Department> adapter;
    private List<Department> mDepartmentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_user_picker);
        overridePendingTransition(R.anim.slide_in_from_bottom,R.anim.slide_out_to_top);

        String rootId = SystemBussiness.getInstance(this).getAppConfig().getAdbookRootId();
        mSuperId =  rootId;
        mDepartmentList = new ArrayList<Department>();
        final int totalRow = mDepartmentList.size();

        final String showCountFlag = SystemBussiness.getInstance(this).getAppConfig().getAdbookOrgCountFlag();
        adapter = new CommonAdapter<Department>(this,mDepartmentList,R.layout.item_organization_lv) {

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

        initRootView();
        depLv.setAdapter(adapter);
        depLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                Department dep = mDepartmentList.get(position);
                String id = dep.getDeptId();
                if (!mUserBussiness.isLeaf(id)) {
                    mSuperId = id;
                    refreshList();
                } else {
                    LogM.log(this.getClass(), "是叶子节点");
                    Bundle b = new Bundle();
                    b.putString(AddressBookUserListFragment.ARG_STRING_SUPER_ID, id);
                    if (dep.getTotalNum() == 0) {
                        Toast.makeText(UserPickerActivity.this, "没有联系人", Toast.LENGTH_SHORT).show();
                    } else {
                        ContainerActivity.startActivity(UserPickerActivity.this, AddressBookUserListFragment.class, b,
                                dep.getName());
                    }
                }
            }
        });

        refreshList();
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


    private void initRootView() {

        depLv = new ListView(this);
        depLv.setSelector(R.drawable.sel_common_item);
        depLv.setDivider(null);
        depLv.setBackgroundColor(getResources().getColor(R.color.window_color));
        setContentView(depLv);
    }

    public void refreshList(){
        mDepartmentList = mUserBussiness.listSubDepartment(mSuperId);
        adapter.notifyDataSetChanged(mDepartmentList);
    }
}
