package com.apppubs.ui.message.fragment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.bean.TUser;
import com.apppubs.bean.UserInfo;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.AppContext;
import com.apppubs.ui.adbook.UserInfoActivity;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.bean.App;
import com.apppubs.bean.AppConfig;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.util.JSONResult;
import com.apppubs.util.LogM;
import com.apppubs.model.AbstractBussinessCallback;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.ProgressHUD;
import com.apppubs.ui.widget.SegmentedGroup;
import com.apppubs.ui.widget.TitleBar;
import com.apppubs.d20.R;
import com.apppubs.constant.URLs;

public class AddressBookFragement extends BaseFragment {

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
                    case R.id.segmented_button2:
                        fg = mFrgArr[1];
                        if (fg == null) {

                            fg = new AddressBookAllUserFragment();
                            mFrgArr[1] = fg;
                        }
                        break;
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
                new ConfirmDialog(mContext, new ConfirmDialog.ConfirmListener() {

                    @Override
                    public void onOkClick() {
                        sync();
                    }

                    @Override
                    public void onCancelClick() {

                    }
                }, "确定同步？","同步可能需要几秒到几分钟的时间！", "取消", "确定").show();
            }
        });

        if (needBack){
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
				if(TextUtils.isEmpty(searchText)){
					setVisibilityOfViewByResId(mRootView, R.id.addressbook_search_result_lv, View.GONE);
				}else{
					setVisibilityOfViewByResId(mRootView, R.id.addressbook_search_result_lv, View.VISIBLE);
					mSearchResultList = mUserBussiness.searchUser(searchText);
					mSearchResultAdapter.notifyDataSetChanged(mSearchResultList);
				}
				return false;
			}
		});
		mSearchResultList = new ArrayList<TUser>();
		mSearchResultAdapter = new CommonAdapter<TUser>(mHostActivity,mSearchResultList,R.layout.item_addressbook_search_result) {
			
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
		init();
	}

	/**
	 * 获得根组织
	 *
	 * @return
	 */
	private Fragment getRootFragment() {
		Fragment fg = new AddressBookOrganizationFragement();
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
			init();
		}
	}

	private void init() {
		// 本地的版本小于服务器版本则需要更新
		AppConfig appconfig = AppContext.getInstance(mContext).getAppConfig();
		if(appconfig.getAdbookVersion()>mAppContext.getApp().getAddressbookLocalVersion()){
			onNewVerisonFound();
		}else if (mUserBussiness.countAllUser() == 0) {

			Dialog dialog = new ConfirmDialog(mHostActivity, new ConfirmDialog.ConfirmListener() {

				@Override
				public void onOkClick() {
					sync();
				}

				@Override
				public void onCancelClick() {

				}

			}, "是否同步？","同步可能需要几秒到几分钟时间！", "取消", "同步");
			dialog.show();
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

		}
	}

	private void onNewVerisonFound() {
		if (mAppContext.getApp().getNeedForceUploadAddressbook() == App.NEED_FORCE_UPDATE_ADDRESSBOOK_YES) {

			LogM.log(getClass(), "直接更新。。。。。。");
			sync();
		} else {
			Dialog dialog = new ConfirmDialog(mHostActivity, new ConfirmDialog.ConfirmListener() {

				@Override
				public void onOkClick() {
					sync();
				}

				@Override
				public void onCancelClick() {

				}

			}, "通讯录有更新 是否同步？", "取消", "同步");
			dialog.show();

		}
	}

	/**
	 * 同步数据
	 */
	private void sync() {
		mProgressHUD = ProgressHUD.show(mHostActivity, "同步中", true, false, null);
		syncAddressbook();

	}

	private void syncAddressbook() {
		mUserBussiness.sycnAddressBook(new AbstractBussinessCallback<Object>() {

			@Override
			public void onException(APError excepCode) {
				Toast.makeText(mHostActivity, "同步失败", Toast.LENGTH_SHORT).show();
				mProgressHUD.dismiss();
			}

			@Override
			public void onDone(Object obj) {
				// 完成更新后更改版本
				App app = AppContext.getInstance(mContext).getApp();
				app.setAddressbookLocalVersion(mAppContext.getApp().getAddressbookVersion());
				AppContext.getInstance(mContext).setApp(app);
				AppContext.getInstance(mContext).serializeApp();

				if (mAppContext.getApp().getAddressbookNeedPermission() == App.NEED) {
					final UserInfo currentUser = AppContext.getInstance(mContext).getCurrentUser();
					String url = String.format(URLs.URL_ADDRESS_PERMISSION,URLs.baseURL,URLs.appCode,currentUser.getUserId());
					mRequestQueue.add(new StringRequest(url, new Listener<String>() {

						@Override
						public void onResponse(String response) {
							JSONResult jr = JSONResult.compile(response);
							currentUser.setAddressbookPermissionString(jr.result);
							AppContext.getInstance(mContext).setCurrentUser(currentUser);
							refreshDepartAndUserFragmentIfExist();

							Toast.makeText(mHostActivity, "同步完成", Toast.LENGTH_SHORT).show();
							mProgressHUD.dismiss();
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(mHostActivity, "同步失败", Toast.LENGTH_SHORT).show();
							mProgressHUD.dismiss();
						}
					}));
				} else {
					refreshDepartAndUserFragmentIfExist();
					Toast.makeText(mHostActivity, "同步完成", Toast.LENGTH_SHORT).show();
					mProgressHUD.dismiss();
				}

			}

			@Override
			public void onProgressUpdate(float progress) {
				NumberFormat nf   =   NumberFormat.getPercentInstance();
				nf.setMinimumFractionDigits(0);
				mProgressHUD.setMessage(nf.format(progress));
			}
		});
	}

	private void refreshDepartAndUserFragmentIfExist() {
		if (mFrgArr[0] != null) {
			((AddressBookOrganizationFragement) mFrgArr[0]).refreshList();
		}
		if (mFrgArr[1] != null) {
			((AddressBookAllUserFragment) mFrgArr[1]).refreshList();
		}
	}

	protected void changeContent(Fragment fragment) {
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
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

}
