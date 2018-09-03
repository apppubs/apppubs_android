package com.apppubs.ui.message.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TUser;
import com.apppubs.d20.R;
import com.apppubs.model.AdbookBiz;
import com.apppubs.presenter.AdbookPresenter;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.ui.adbook.UserInfoActivity;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.ui.message.IAdbookView;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.ProgressHUD;
import com.apppubs.ui.widget.SegmentedGroup;
import com.apppubs.ui.widget.TitleBar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class AdbookFragement extends BaseFragment implements IAdbookView {

    // 首层组织的父id
    public static final String ARGS_ROOT_DEPARTMENT_SUPER_ID = "root_id";
    public static final String ACTION_REFRESH_LIST = "refresh_user_and_dept_list";

    private SearchView mSearchView;
    private ListView mSearchResultLV;
    private ProgressHUD mProgressHUD;
    private SegmentedGroup mSg;

    private CommonAdapter<TUser> mSearchResultAdapter;

    private List<TUser> mSearchResultList;
    private Fragment[] mFrgArr;
    private Fragment mCurFrg;
    private int mCurCheckedRadioBtnResId;// 当前选中的radio btn 的id用于下次恢复状态
    private AdbookPresenter mPresenter;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new AdbookPresenter(getContext(), this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return initRootView();
    }

    @Override
    protected TitleBar initTitleBar() {
        TitleBar titleBar = createTitleBar();
        return titleBar;
    }

    @NonNull
    private TitleBar createTitleBar() {
        TitleBar titleBar = new TitleBar(mContext);
        titleBar.setBackgroundColor(getThemeColor());
        View titleView = LayoutInflater.from(titleBar.getContext()).inflate(R.layout.segment_btn_address, null);
        mSg = (SegmentedGroup) titleView.findViewById(R.id.segmented);

        mSg.setTintColor(Color.WHITE, mDefaultColor);

        mSg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mCurCheckedRadioBtnResId = checkedId;
                Fragment fg = null;
                switch (checkedId) {
                    case R.id.segmented_button1:
                        fg = mFrgArr[0];
                        if (fg == null) {
                            fg = getRootFragment();
                            mFrgArr[0] = fg;

                        }
                        break;
//                    case R.id.segmented_button2:
//                        fg = mFrgArr[1];
//                        if (fg == null) {
//
//                            fg = new AddressBookAllUserFragment();
//                            mFrgArr[1] = fg;
//                        }
//                        break;
                    case R.id.segmented_button3:
                        fg = mFrgArr[2];
                        if (fg == null) {
                            fg = new AddressBookCommonlyUserListFragment();
                            mFrgArr[2] = fg;
                        }
                        break;
                    default:
                        break;
                }
                changeContent(fg);

            }
        });

        // 初始化时不是第一个被选中时，则执行选中事件
        if (mCurCheckedRadioBtnResId != R.id.segmented_button1 && mCurCheckedRadioBtnResId != 0) {
            mSg.check(mCurCheckedRadioBtnResId);
        }
        titleBar.setTitleView(titleView);
        titleBar.addRightBtnWithTextAndClickListener("同步", new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSyncDialog();
            }
        });

        if (needBack) {
            titleBar.setLeftBtnClickListener(this);
            titleBar.setLeftImageResource(R.drawable.top_back_btn);
        }

        return titleBar;
    }

    private View initRootView() {
        mRootView = mInflater.inflate(R.layout.frg_addressbook, null);
        mSearchView = (SearchView) mRootView.findViewById(R.id.addressbook_sv);
        mSearchResultLV = (ListView) mRootView.findViewById(R.id.addressbook_search_result_lv);
        mSearchView.setFocusable(false);
        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String searchText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {
                if (TextUtils.isEmpty(searchText)) {
                    setVisibilityOfViewByResId(mRootView, R.id.addressbook_search_result_lv, View.GONE);
                } else {
                    setVisibilityOfViewByResId(mRootView, R.id.addressbook_search_result_lv, View.VISIBLE);
                    mSearchResultList = AdbookBiz.getInstance(mContext).searchUser(searchText);
                    mSearchResultAdapter.notifyDataSetChanged(mSearchResultList);
                }
                return false;
            }
        });
        mSearchResultList = new ArrayList<TUser>();
        mSearchResultAdapter = new CommonAdapter<TUser>(mHostActivity, mSearchResultList, R.layout
                .item_addressbook_search_result) {

            @Override
            protected void fillValues(ViewHolder holder, TUser bean, int position) {
                TextView nameTv = holder.getView(R.id.name);
                nameTv.setText(bean.getTrueName());
            }
        };
        mSearchResultLV.setAdapter(mSearchResultAdapter);
        mSearchResultLV.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TUser user = mSearchResultList.get(position);
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra(UserInfoActivity.EXTRA_STRING_USER_ID, user.getUserId());
                getActivity().startActivity(intent);
            }
        });
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        Fragment fg = getRootFragment();
        // 提交修改
        mFrgArr = new Fragment[3];
        changeContent(fg);
        mFrgArr[0] = fg;
//		init();
        mPresenter.onCreateView();

        mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("正在更新通讯录！")
                .setSmallIcon(R.drawable.icon);
    }

    /**
     * 获得根组织
     *
     * @return
     */
    private Fragment getRootFragment() {
        AddressBookOrganizationFragement fg = new AddressBookOrganizationFragement();
        fg.setListener(new AddressBookOrganizationFragement.Listener() {
            @Override
            public void onCreateView() {
                mPresenter.onOrgFragmentCreate();
            }

            @Override
            public void onDeptSelected(String deptId) {
                mPresenter.onDeptSelected(deptId);
            }

            @Override
            public void onCreateDiscussClicked(String deptId) {
                mPresenter.onCreateDiscussClicked(deptId);
            }
        });
        return fg;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.onVisible();
        }
    }

    private void changeContent(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments == null || !fragments.contains(fragment)) {
//			transaction.remove(fragment);
            transaction.add(R.id.fragment_container, fragment);
        }
        if (mCurFrg != null) {
            transaction.hide(mCurFrg);
        }
        transaction.show(fragment);
        mCurFrg = fragment;
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return false;
    }

    @Override
    public void showSyncDialog() {
        Dialog dialog = new ConfirmDialog(mHostActivity, new ConfirmDialog.ConfirmListener() {

            @Override
            public void onOkClick() {
                mPresenter.onUpdateConfirmed();
            }

            @Override
            public void onCancelClick() {

            }

        }, "确定同步？", "同步可能需要几秒到几分钟时间！", "取消", "同步");
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void showSyncLoading() {
//        mBuilder.setContentText("同步中");
//        mNotifyManager.notify(1,mBuilder.build());
        mProgressHUD = ProgressHUD.show(getContext(), "同步中...", true, false, null);
    }

    @Override
    public void setSyncProgress(Float progress) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(0);
        mProgressHUD.setMessage(nf.format(progress));
//        mBuilder.setProgress(100, (int) (progress*100), false);
//        // Displays the progress bar for the first time.
//        mNotifyManager.notify(1, mBuilder.build());
    }

    @Override
    public void setSyncLoadText(String text) {
        mProgressHUD.setMessage(text);
//        mBuilder.setContentText(text);
//        mNotifyManager.notify(1, mBuilder.build());
    }

    @Override
    public void hideSyncLoading() {
        mProgressHUD.dismiss();
//        mNotifyManager.cancel(1);
    }

    @Override
    public void showHaveNewVersion(String updateTime) {
        Dialog dialog = new ConfirmDialog(mHostActivity, new ConfirmDialog.ConfirmListener() {

            @Override
            public void onOkClick() {
                showSyncDialog();
            }

            @Override
            public void onCancelClick() {

            }

        }, "通讯录有新版本！", "更新时间：" + updateTime, "取消", "更新");
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void showDepts(List<TDepartment> depts) {
        ((AddressBookOrganizationFragement) mFrgArr[0]).setDetps(depts);
    }

    @Override
    public void showUsers(List<TUser> users) {
        ((AddressBookOrganizationFragement) mFrgArr[0]).setUsers(users);
    }

    @Override
    public void clearBreadcrumb(TDepartment department) {
        ((AddressBookOrganizationFragement) mFrgArr[0]).clearBreadcrumb(department);
    }

    @Override
    public void showParsingLoading() {
        setVisibilityOfViewByResId(mRootView,R.id.adbook_wait_tv,View.VISIBLE);
        mProgressHUD.setMessage("解析中...");
    }

    @Override
    public void hideParsingLoading() {
        setVisibilityOfViewByResId(mRootView,R.id.adbook_wait_tv,View.GONE);
        mProgressHUD.dismiss();
    }

}
