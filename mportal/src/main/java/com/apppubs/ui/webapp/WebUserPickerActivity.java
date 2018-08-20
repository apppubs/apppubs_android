package com.apppubs.ui.webapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.bean.webapp.DeptModel;
import com.apppubs.bean.webapp.DeptPickerDTO;
import com.apppubs.bean.webapp.DeptPickerResultItem;
import com.apppubs.bean.webapp.SearchDeptHttpResult;
import com.apppubs.bean.webapp.SearchHttpResult;
import com.apppubs.bean.webapp.UserModel;
import com.apppubs.bean.webapp.UserPickerDTO;
import com.apppubs.d20.R;
import com.apppubs.model.message.UserBasicInfo;
import com.apppubs.presenter.WebUserPickerPresenter;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.ui.widget.Breadcrumb;
import com.apppubs.ui.widget.ProgressHUD;
import com.apppubs.ui.widget.UserSelectionBar;
import com.apppubs.ui.widget.deptselection.DeptSelectionBar;
import com.apppubs.ui.widget.deptselection.DeptSelectionItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2018/1/8.
 */

public class WebUserPickerActivity extends BaseActivity implements IWebUserPickerView {

    public static final String EXTRA_SERIALIZABLE_USER_PICKER_VO = "user_picker_vo";
    public static final String EXTRA_SERIALIZABLE_DEPT_PICKER_VO = "dept_picker_vo";
    public static final String EXTRA_BOOL_IS_DEPT_SELECT = "is_dept_select";

    private boolean isDeptSelect;
    private UserPickerDTO mUserPickerDTO;
    private DeptPickerDTO mDeptPickerDTO;
    private WebUserPickerPresenter mPresenter;

    private Breadcrumb mBreadcrumb;
    private ListView mLv;
    private ListView mSearchResultLV;
    private SearchView mSearchView;
    private UserSelectionBar mUserSelectBar;
    private DeptSelectionBar mDeptSelectionBar;


    private CommonAdapter<DeptModel> mDeptAdapter;
    private CommonAdapter<UserModel> mUserAdapter;
    private CommonAdapter<SearchHttpResult> mSearchResultAdapter;
    private CommonAdapter<SearchDeptHttpResult> mSearchDeptResultAdapter;

    private List<DeptModel> mDepts;
    private List<UserModel> mUsers;
    private List<SearchHttpResult> mSearchResults;
    private List<SearchDeptHttpResult> mSearchDeptResults;

    private static UserPickerListener mListener;
    private static DeptPickerListener mDeptPickerListener;

    public interface UserPickerListener {
        void onPickDone(List<UserModel> users);
    }

    public interface DeptPickerListener {
        void onDeptPickerDone(List<DeptPickerResultItem> result);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
        fillVariablesWithBundleData();
        initView();
        initAdapter();
        initPresenter();
    }

    //IWebUserPickerView
    @Override
    public void showLoading() {
        ProgressHUD.show(this, null, true, false, null);
    }

    @Override
    public void hideLoading() {
        ProgressHUD.dismissProgressHUDInThisContext(this);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setDepts(List<DeptModel> depts) {
        mDepts = depts;
        mDeptAdapter.setData(depts);
        mDeptAdapter.notifyDataSetChanged();
        if (!mDeptAdapter.equals(mLv.getAdapter())) {
            mLv.setAdapter(mDeptAdapter);
            mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!isUserListShow()) {
                        mPresenter.onDeptItemClick(mDepts.get(position));
                    } else {
                        mPresenter.onUserItemClick(mUsers.get(position));
                    }
                }
            });
        }
    }

    @Override
    public void setUsers(List<UserModel> users) {
        mUsers = users;
        mUserAdapter.setData(users);
        mLv.setAdapter(mUserAdapter);
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.onUserItemClick(mUsers.get(position));
            }
        });
    }

    @Override
    public void setSearchDepts(List<SearchDeptHttpResult> searchDeptList) {
        mSearchDeptResults = searchDeptList;
        mSearchDeptResultAdapter.notifyDataSetChanged(mSearchDeptResults);
        mSearchResultLV.setAdapter(mSearchDeptResultAdapter);
    }

    @Override
    public void setSearchUsers(List<SearchHttpResult> searchUsers) {
        mSearchResults = searchUsers;
        mSearchResultAdapter.notifyDataSetChanged(searchUsers);
        mSearchResultLV.setAdapter(mSearchResultAdapter);
    }

    @Override
    public void refreshUserList(List<UserModel> users) {
        if (isUserListShow()) {
            mUserAdapter.setData(users);
            mUserAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void resreshSearchUserList(List<SearchHttpResult> searchUsers) {
        mSearchResultAdapter.setData(searchUsers);
        mSearchResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideSearchLv() {
        mSearchResultLV.setVisibility(View.GONE);
    }

    @Override
    public void pushBreadcrumb(String name, String tag) {
        mBreadcrumb.push(name, tag);
    }

    @Override
    public void addSelectedBarUser(UserModel vo) {
        UserBasicInfo ubi = new UserBasicInfo();
        ubi.setUserId(vo.getId());
        ubi.setTrueName(vo.getName());
        mUserSelectBar.addUser(ubi);
    }

    @Override
    public void addSelectedBarDept(DeptModel model) {
        DeptSelectionItemModel viewModel = new DeptSelectionItemModel();
        viewModel.setId(model.getId());
        viewModel.setName(model.getName());
        mDeptSelectionBar.addDept(viewModel);
    }

    @Override
    public void removeSelectedBarDept(String id) {
        mDeptSelectionBar.removeDept(id);
    }

    @Override
    public void removeSelectedBarUser(String userId) {
        mUserSelectBar.removeUser(userId);
    }

    @Override
    public UserPickerDTO getUserPickerVO() {
        return mUserPickerDTO;
    }

    @Override
    public DeptPickerDTO getDeptPickerDTO() {
        return mDeptPickerDTO;
    }

    @Override
    public boolean isDeptSelection() {
        return isDeptSelect;
    }

    @Override
    public UserPickerListener getListener() {
        return mListener;
    }

    @Override
    public DeptPickerListener getDeptPickerListener() {
        return mDeptPickerListener;
    }

    @Override
    public void finishActivity() {
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
        this.finish();
    }

    public static void startActivity(Context context, UserPickerDTO vo, UserPickerListener listener) {
        Intent intent = new Intent(context, WebUserPickerActivity.class);
        intent.putExtra(BaseActivity.EXTRA_STRING_TITLE, "选择人员");
        intent.putExtra(WebUserPickerActivity.EXTRA_SERIALIZABLE_USER_PICKER_VO, vo);
        context.startActivity(intent);
        mListener = listener;
    }

    public static void startActivity(Context context, DeptPickerDTO dto, DeptPickerListener listener) {
        Intent intent = new Intent(context, WebUserPickerActivity.class);
        intent.putExtra(BaseActivity.EXTRA_STRING_TITLE, "选择部门");
        intent.putExtra(WebUserPickerActivity.EXTRA_SERIALIZABLE_DEPT_PICKER_VO, dto);
        intent.putExtra(WebUserPickerActivity.EXTRA_BOOL_IS_DEPT_SELECT, true);
        context.startActivity(intent);
        mDeptPickerListener = listener;
    }

    private void fillVariablesWithBundleData() {
        Intent intent = getIntent();
        isDeptSelect = intent.getBooleanExtra(EXTRA_BOOL_IS_DEPT_SELECT, false);
        if (isDeptSelect) {
            mDeptPickerDTO = (DeptPickerDTO) intent.getSerializableExtra(EXTRA_SERIALIZABLE_DEPT_PICKER_VO);
        } else {
            mUserPickerDTO = (UserPickerDTO) intent.getSerializableExtra(EXTRA_SERIALIZABLE_USER_PICKER_VO);
        }
    }

    private void initView() {
        setContentView(R.layout.act_web_user_picker);
        mLv = (ListView) findViewById(R.id.web_user_picker_lv);
        mBreadcrumb = (Breadcrumb) findViewById(R.id.web_user_picker_bc);

        mBreadcrumb.setOnItemClickListener(new Breadcrumb.OnItemClickListener() {
            @Override
            public void onItemClick(int index, String tag) {
                mPresenter.onBreadcrumbClicked(tag);
            }
        });
        if (isDeptSelect) {
            initDeptSelectorBar();
        } else {
            initUserSelectorBar();
        }

        mSearchResultLV = (ListView) findViewById(R.id.user_picker_search_result_lv);
        mSearchView = (SearchView) findViewById(R.id.web_user_picker_sv);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String searchText) {
                if (!TextUtils.isEmpty(searchText)) {
                    mSearchView.clearFocus();
                    mPresenter.onQueryTextChange(searchText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {
                if (TextUtils.isEmpty(searchText)) {
                    setVisibilityOfViewByResId(R.id.user_picker_search_result_lv, View.GONE);
                    mSearchView.clearFocus();
                } else {
                    setSearchUsers(new ArrayList<SearchHttpResult>());
                    setVisibilityOfViewByResId(R.id.user_picker_search_result_lv, View.VISIBLE);
                }
                return true;
            }
        });

        mSearchResultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isDeptSelect){
                    mPresenter.onSearchDeptItemClick(mSearchDeptResults.get(position));
                }else{
                    mPresenter.onSearchUserItemClick(mSearchResults.get(position));
                }
            }
        });
    }

    private void initDeptSelectorBar() {
        mDeptSelectionBar = (DeptSelectionBar) findViewById(R.id.user_picker_dept_selection_bar);

        if (mDeptPickerDTO.getSelectMode() == DeptPickerDTO.SELECT_MODE_MULTI) {
            mDeptSelectionBar.setVisibility(View.VISIBLE);
            mDeptSelectionBar.setListener(new DeptSelectionBar.DeptSelectionBarListener() {
                @Override
                public void onItemClick(String deptId) {
                    mPresenter.onRemoveDeptClicked(deptId);
                }

                @Override
                public void onDoneClick() {
                    mPresenter.onDoneBtnClicked();
                }
            });
        } else {
            mDeptSelectionBar.setVisibility(View.GONE);
        }
    }

    private void initUserSelectorBar() {
        mUserSelectBar = (UserSelectionBar) findViewById(R.id.user_picker_user_selection_bar);
        if (mUserPickerDTO.getmSelectMode() == UserPickerDTO.SELECT_MODE_MULTI) {
            mUserSelectBar.setVisibility(View.VISIBLE);
            mUserSelectBar.setMaxSelectCount(mUserPickerDTO.getMaxSelectedNum());
            mUserSelectBar.setListener(new UserSelectionBar.UserSelectionBarListener() {
                @Override
                public void onItemClick(String userId) {
                    mPresenter.onRemoveSelecedtUser(userId);
                }

                @Override
                public void onDoneClick() {
                    mPresenter.onDoneBtnClicked();
                }
            });
        } else {
            mUserSelectBar.setVisibility(View.GONE);
        }
    }

    private void initAdapter() {
        mDeptAdapter = new CommonAdapter<DeptModel>(this, R.layout.item_web_user_picker_organization_lv) {

            @Override
            protected void fillValues(ViewHolder holder, DeptModel bean, int position) {
                if (isDeptSelect) {
                    holder.getView(R.id.item_web_user_picker_content_ll).setVisibility(View.VISIBLE);
                    ((TextView) holder.getView(R.id.item_web_user_picker_name_tv)).setText(bean.getName());
                    ImageView arrow = holder.getView(R.id.item_web_user_picker_arrow);
                    if (bean.isLeaf()){
                        arrow.setVisibility(View.GONE);
                    }else{
                        arrow.setVisibility(View.VISIBLE);
                    }
                    ImageView checkBtn = holder.getView(R.id.item_web_user_picker_check_btn);
                    checkCheckBtn(checkBtn, bean.isSelected(), bean.isPreselected());
                    checkBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPresenter.onDeptCheckBtnClicked(bean);
                        }
                    });
                } else {
                    ((TextView) holder.getView(R.id.item_web_user_picker_name_tv1)).setText(bean.getName());
                }
            }
        };

        mUserAdapter = new CommonAdapter<UserModel>(this, R.layout.item_web_user_picker_user_lv) {
            @Override
            protected void fillValues(ViewHolder holder, UserModel user, int position) {
                TextView titleTv = holder.getView(R.id.item_user_picker_user_title_tv);
                titleTv.setText(user.getName());
//				TextView desTv = holder.getView(R.id.item_user_picker_user_des_tv);
                ImageView checkBtnIv = holder.getView(R.id.item_web_user_picker_check_btn);
                checkCheckBtn(checkBtnIv, user.isSelected(), user.isPreSelected());
            }
        };

        mSearchResultAdapter = new CommonAdapter<SearchHttpResult>(this, R.layout.item_web_user_picker_user_lv) {

            @Override
            protected void fillValues(ViewHolder holder, SearchHttpResult user, int position) {

                TextView titleTv = holder.getView(R.id.item_user_picker_user_title_tv);
                titleTv.setText(user.getName());
                ImageView checkBtnIv = holder.getView(R.id.item_web_user_picker_check_btn);
                checkCheckBtn(checkBtnIv, user.isSelected(), user.isPreSelected());
            }
        };

        mSearchDeptResultAdapter = new CommonAdapter<SearchDeptHttpResult>(this, R.layout
                .item_web_user_picker_organization_lv) {
            @Override
            protected void fillValues(ViewHolder holder, SearchDeptHttpResult bean, int position) {
                holder.getView(R.id.item_web_user_picker_content_ll).setVisibility(View.VISIBLE);
                ((TextView) holder.getView(R.id.item_web_user_picker_name_tv)).setText(bean.getName());
                ImageView checkBtn = holder.getView(R.id.item_web_user_picker_check_btn);
                checkCheckBtn(checkBtn, bean.isSelected(), bean.isPreSelected());
            }
        };
    }

    private void initPresenter() {
        mPresenter = new WebUserPickerPresenter(this, this);
        mPresenter.onCreate();
    }

    private boolean isUserListShow() {
        return mLv.getAdapter() == mUserAdapter;
    }

    private void checkCheckBtn(ImageView checkBtnIv, boolean check, boolean lock) {
        if (lock) {
            checkBtnIv.setSelected(true);
            checkBtnIv.setEnabled(false);
            checkBtnIv.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        } else {
            if (check) {
                checkBtnIv.setSelected(true);
                checkBtnIv.setColorFilter(mThemeColor, PorterDuff.Mode.SRC_ATOP);
            } else {
                checkBtnIv.setSelected(false);
                checkBtnIv.clearColorFilter();
            }
        }
    }

}
