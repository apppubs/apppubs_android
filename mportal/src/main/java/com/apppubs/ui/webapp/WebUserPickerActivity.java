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

import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.presenter.WebUserPickerPresenter;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.model.message.UserBasicInfo;
import com.apppubs.ui.widget.widget.Breadcrumb;
import com.apppubs.ui.widget.widget.UserSelectionBar;
import com.apppubs.bean.webapp.DeptVO;
import com.apppubs.bean.webapp.SearchVO;
import com.apppubs.bean.webapp.UserPickerVO;
import com.apppubs.bean.webapp.UserVO;
import com.apppubs.ui.widget.ProgressHUD;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2018/1/8.
 */

public class WebUserPickerActivity extends BaseActivity implements IWebUserPickerView {

	public static final String EXTRA_SERIALIZA_USER_PICKER_VO = "user_picker_vo";

	private UserPickerVO mUserPickerVO;
	private WebUserPickerPresenter mPresenter;

	private Breadcrumb mBreadcrumb;
	private ListView mLv;
	private ListView mSearchResultLV;
	private SearchView mSearchView;
	private UserSelectionBar mUserSelectBar;


	private CommonAdapter<DeptVO> mDeptAdapter;
	private CommonAdapter<UserVO> mUserAdapter;
	private CommonAdapter<SearchVO> mSearchResultAdapter;

	private List<DeptVO> mDepts;
	private List<UserVO> mUsers;
	private List<SearchVO> mSearchResults;

	private static UserPickerListener mListener;

	public interface UserPickerListener {
		void onPickDone(List<UserVO> users);
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
		Toast.makeText(this,error,Toast.LENGTH_LONG).show();
	}

	@Override
	public void setDepts(List<DeptVO> depts) {
		mDepts = depts;
		mDeptAdapter.setData(depts);
		mDeptAdapter.notifyDataSetChanged();
		if (!mDeptAdapter.equals(mLv.getAdapter())) {
			mLv.setAdapter(mDeptAdapter);
			mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (!isUserListShow()){
						mPresenter.onDeptItemClick(mDepts.get(position));
					}else{
						mPresenter.onUserItemClick(mUsers.get(position));
					}
				}
			});
		}


	}

	@Override
	public void setUsers(List<UserVO> users) {
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
	public void setSearchUsers(List<SearchVO> searchUsers) {
		mSearchResults = searchUsers;
		mSearchResultAdapter.notifyDataSetChanged(searchUsers);
		mSearchResultLV.setAdapter(mSearchResultAdapter);
	}

	@Override
	public void refreshUserList(List<UserVO> users) {
		if (isUserListShow()) {
			mUserAdapter.setData(users);
			mUserAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void resreshSearchUserList(List<SearchVO> searchUsers) {
		mSearchResultAdapter.setData(searchUsers);
		mSearchResultAdapter.notifyDataSetChanged();
	}

	@Override
	public void pushBreadcrumb(String name, String tag) {
		mBreadcrumb.push(name, tag);
	}

	@Override
	public void addSelectedBarUser(UserVO vo) {
		UserBasicInfo ubi = new UserBasicInfo();
		ubi.setUserId(vo.getId());
		ubi.setTrueName(vo.getName());
		mUserSelectBar.addUser(ubi);
	}

	@Override
	public void removeSelectedBarUser(String userId) {
		mUserSelectBar.removeUser(userId);
	}

	@Override
	public UserPickerVO getUserPickerVO() {
		return mUserPickerVO;
	}

	@Override
	public UserPickerListener getListener() {
		return mListener;
	}

	@Override
	public void finishActivity() {
		overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
		this.finish();
	}

	public static void startActivity(Context context, UserPickerVO vo, UserPickerListener listener) {
		Intent intent = new Intent(context, WebUserPickerActivity.class);
		intent.putExtra(BaseActivity.EXTRA_STRING_TITLE, "选择人员");
		intent.putExtra(WebUserPickerActivity.EXTRA_SERIALIZA_USER_PICKER_VO, vo);
		context.startActivity(intent);
		mListener = listener;
	}

	private void fillVariablesWithBundleData() {
		Intent intent = getIntent();
		mUserPickerVO = (UserPickerVO) intent.getSerializableExtra(EXTRA_SERIALIZA_USER_PICKER_VO);
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
		initUserSelectorBar();

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
					setSearchUsers(new ArrayList<SearchVO>());
					setVisibilityOfViewByResId(R.id.user_picker_search_result_lv, View.VISIBLE);
				}
				return true;
			}
		});

		mSearchResultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPresenter.onSearchUserItemClick(mSearchResults.get(position));
			}
		});
	}

	private void initUserSelectorBar() {
		mUserSelectBar = (UserSelectionBar) findViewById(R.id.user_picker_user_selection_bar);
		if (mUserPickerVO.getmSelectMode() == UserPickerVO.SELECT_MODE_MULTI) {
			mUserSelectBar.setMaxSelectCount(mUserPickerVO.getMaxSelectedNum());
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
		mDeptAdapter = new CommonAdapter<DeptVO>(this, R.layout.item_web_user_picker_organization_lv) {

			@Override
			protected void fillValues(ViewHolder holder, DeptVO bean, int position) {
				((TextView) holder.getView(R.id.item_web_user_picker_name_tv)).setText(bean.getName());
			}
		};

		mUserAdapter = new CommonAdapter<UserVO>(this, R.layout.item_web_user_picker_user_lv) {
			@Override
			protected void fillValues(ViewHolder holder, UserVO user, int position) {
				TextView titleTv = holder.getView(R.id.item_user_picker_user_title_tv);
				titleTv.setText(user.getName());
//				TextView desTv = holder.getView(R.id.item_user_picker_user_des_tv);
				ImageView checkBtnIv = holder.getView(R.id.item_web_user_picker_check_btn);
				checkCheckBtn(checkBtnIv, user.isSelected(), false);
			}
		};

		mSearchResultAdapter = new CommonAdapter<SearchVO>(this, R.layout.item_web_user_picker_user_lv) {

			@Override
			protected void fillValues(ViewHolder holder, SearchVO user, int position) {

				TextView titleTv = holder.getView(R.id.item_user_picker_user_title_tv);
				titleTv.setText(user.getName());
				ImageView checkBtnIv = holder.getView(R.id.item_web_user_picker_check_btn);
				checkCheckBtn(checkBtnIv, user.isSelected(), false);

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
