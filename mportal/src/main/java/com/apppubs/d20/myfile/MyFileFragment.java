package com.apppubs.d20.myfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.adapter.CommonAdapter;
import com.apppubs.d20.adapter.ViewHolder;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.fragment.BaseFragment;
import com.apppubs.d20.net.WMHHttpErrorCode;
import com.apppubs.d20.net.WMHRequestListener;
import com.apppubs.d20.util.FileUtils;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.JSONUtils;
import com.apppubs.d20.util.StringUtils;
import com.apppubs.d20.widget.ProgressHUD;
import com.apppubs.d20.widget.commonlist.CommonListView;
import com.apppubs.d20.widget.commonlist.CommonListViewListener;
import com.apppubs.d20.widget.menudialog.MenuDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MyFileFragment extends BaseFragment implements OnClickListener {

	private SearchView mSearchView;
	private CommonListView mLv;
	private CommonAdapter<MyFileModel> mAdapter;
	private int mCurPage;
	private List<MyFileModel> mDatas;
	private List<MyFileModel> mSearchResults;
	private boolean isSearchMode;
	private String mQueryText;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.act_my_file, null);
		initViews();

		return mRootView;
	}

	private void initViews() {
		mLv = (CommonListView) mRootView.findViewById(R.id.my_file_lv);

		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Bundle args = new Bundle();
				args.putString(FilePreviewFragment.ARGS_STRING_URL, isSearchMode?mSearchResults.get((int) parent.getItemIdAtPosition(position)).getFileUrl():mDatas.get((int) parent.getItemIdAtPosition(position)).getFileUrl());
				ContainerActivity.startActivity(mContext, FilePreviewFragment.class, args, "文件预览");
			}
		});
		mLv.setCommonListViewListener(new CommonListViewListener() {
			@Override
			public void onRefresh() {
				loadPage(1);
			}

			@Override
			public void onLoadMore() {
				loadPage(mCurPage + 1);
			}
		});
		mLv.setPullLoadEnable(true);

		mSearchView = (SearchView) mRootView.findViewById(R.id.my_file_sv);
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				isSearchMode = true;
				mQueryText = query;
				loadPage(1);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (TextUtils.isEmpty(newText)){
					isSearchMode = false;
					refreshListView();
				}
				return false;
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadPage(1);
	}

	private void loadPage(final int page) {
		String url = null;
		Object[] params;
		if (isSearchMode){
			url = URLs.URL_MY_FILE_SEARCH;
			UserInfo user = AppContext.getInstance(mContext).getCurrentUser();
			String encodedQuery = null;
			try {
				encodedQuery = URLEncoder.encode(mQueryText.trim(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			params = new Object[]{encodedQuery, user.getUsername(), page};

		}else{
			url = URLs.URL_MY_FILE_PAGE;
			UserInfo user = AppContext.getInstance(mContext).getCurrentUser();;
			params = new Object[]{page,user.getUsername()};
		}

		mAppContext.getHttpClient().GET(url, params, new WMHRequestListener() {
			@Override
			public void onDone(JSONResult jr, WMHHttpErrorCode errorCode) {
				Log.v("MyFileFragment", jr.result);
				if (errorCode != null) {

					mRootView.post(new Runnable() {
						@Override
						public void run() {
							if (page == 1) {
								mLv.stopRefresh();
							} else {
								mLv.stopLoadMore();
							}
							Toast.makeText(mContext, "网络错误", Toast.LENGTH_LONG).show();
						}
					});
				} else {
					mRootView.post(new Runnable() {
						@Override
						public void run() {
							if (page == 1) {
								mLv.stopRefresh();
							} else {
								mLv.stopLoadMore();
							}
						}
					});

					if (jr.resultCode == 1) {
						List<MyFileModel> result = parseJsonResult(jr);
						if (result == null || result.size() < 1) {
							mRootView.post(new Runnable() {
								@Override
								public void run() {
									mLv.haveLoadAll();
								}
							});
						}
						if (page == 1) {
							if (!isSearchMode){
								mDatas = new ArrayList<MyFileModel>();
							}else{
								mSearchResults = new ArrayList<MyFileModel>();
							}
						}
						if (!isSearchMode){
							mDatas.addAll(result);
						}else{
							mSearchResults.addAll(result);
						}
						mRootView.post(new Runnable() {
							@Override
							public void run() {
								refreshListView();
							}
						});

						mCurPage = page;
					} else {
						Toast.makeText(mContext, "获取数据错误", Toast.LENGTH_LONG).show();
					}
				}
			}
		});

	}

	private List<MyFileModel> parseJsonResult(JSONResult jr) {
		String listStr = null;
		try {
			JSONObject jo = JSONUtils.parseJSONObject(jr.result);
			listStr = jo.getString("appfilelist");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return JSONUtils.parseListFromJson(listStr, MyFileModel.class);
	}

	private void refreshListView() {
		List<MyFileModel> datas = isSearchMode?mSearchResults:mDatas;
		if (mAdapter == null) {
			mAdapter = new CommonAdapter<MyFileModel>(mHostActivity, datas, R.layout.item_my_file) {

				@Override
				protected void fillValues(ViewHolder holder, MyFileModel bean, int position) {
					TextView nameTv = holder.getView(R.id.my_file_name_tv);
					nameTv.setText(bean.getName());
					ImageView moreIv = holder.getView(R.id.my_file_moreIv);
					moreIv.setTag(R.id.temp_id, position);
					moreIv.setOnClickListener(MyFileFragment.this);
					TextView timeTv = holder.getView(R.id.my_file_time_tv);
					timeTv.setText(StringUtils.formatDate(bean.getAddTime(), "yyyy/MM/dd HH:mm"));
					TextView sizeTv = holder.getView(R.id.my_file_size_tv);
					sizeTv.setText(FileUtils.formetFileSize(bean.getBytes()));
					ImageView iv = holder.getView(R.id.my_file_iv);
					if ("docx".equals(bean.getTypeStr())||"doc".equals(bean.getTypeStr())) {
						iv.setImageResource(R.drawable.myfile_file_type_word);
					} else if ("xlsx".equals(bean.getTypeStr())||"xls".equals(bean.getTypeStr())) {
						iv.setImageResource(R.drawable.myfile_file_type_excel);
					} else if ("pptx".equals(bean.getTypeStr())||"ppt".equals(bean.getTypeStr())) {
						iv.setImageResource(R.drawable.myfile_file_type_ppt);
					} else if ("pdf".equals(bean.getTypeStr())) {
						iv.setImageResource(R.drawable.myfile_file_type_pdf);
					} else if("jpeg".equals(bean.getTypeStr())||"jpg".equals(bean.getTypeStr())||"png".equals(bean.getTypeStr())||"gif".equals(bean.getTypeStr())){
						iv.setImageResource(R.drawable.myfile_file_type_img);
					}else {
						iv.setImageResource(R.drawable.myfile_file_type_unknow);
					}
				}
			};
			mLv.setAdapter(mAdapter);
		} else {
			mAdapter.setData(datas);
			mAdapter.notifyDataSetChanged();
		}
	}

	private String getPageUrl(int page) {
		UserInfo user = AppContext.getInstance(mContext).getCurrentUser();
		return String.format(URLs.URL_MY_FILE_PAGE, page, user.getUsername());
	}

	@Override
	public void onClick(final View v) {
		if (v.getTag(R.id.temp_id) == null) {
			return;
		}
		Integer index = (Integer) v.getTag(R.id.temp_id);
		MyFileModel model = mDatas.get(index);
		showMoreActionSheet(model);
	}

	private void showMoreActionSheet(final MyFileModel model) {
		ActionSheetFragment.build(getFragmentManager()).setChoice(ActionSheetFragment.Builder.CHOICE.GRID).setTitle(model.getName()).setTag("MainActivity")
				.setItems(new String[]{"转发", "删除"}).setImages(
				new int[]{R.drawable.myfile_forward, R.drawable.myfile_delete}).setOnItemClickListener(new ActionSheetFragment.OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				Log.v(MyFileFragment.class.getName(), "点击菜单" + position);
				if (position == 1) {
					showDeleteActionSheet(model);
				}
			}
		}).show();
	}

	private void showDeleteActionSheet(final MyFileModel model) {
		String[] menus = {"删除本地及云端文件", "删除本地文件"};
		new MenuDialog(mContext, menus, new MenuDialog.MenuDialogListener() {

			@Override
			public void onItemClicked(int index) {
				Log.v("MyFileFragment", "点击 " + index);
				if (index == 0) {
					ProgressHUD.show(mHostActivity, "请稍候...", true, false, null);
					String[] params = {model.getFileId()};
					mAppContext.getHttpClient().GET(URLs.URL_MY_FILE_DELETE, params, new WMHRequestListener() {
						@Override
						public void onDone(JSONResult jsonResult, @NonNull WMHHttpErrorCode errorCode) {

							if (errorCode==null){
								mAppContext.getCacheManager().removeCache(model.getFileUrl());
								mRootView.post(new Runnable() {
									@Override
									public void run() {
										ProgressHUD.dismissProgressHUDInThisContext(mHostActivity);
										removeFromListView(model);
										Toast.makeText(mHostActivity,"删除成功",Toast.LENGTH_SHORT).show();
									}
								});
							}else{
								mRootView.post(new Runnable() {
									@Override
									public void run() {
										ProgressHUD.dismissProgressHUDInThisContext(mHostActivity);
										Toast.makeText(mHostActivity,"删除失败",Toast.LENGTH_SHORT).show();
									}
								});
							}
						}
					});


				} else if (index == 1) {
					mAppContext.getCacheManager().removeCache(model.getFileUrl());
					Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
				}
			}
		}).show();
	}

	private void removeFromListView(MyFileModel model) {
		mDatas.remove(model);
		mAdapter.notifyDataSetChanged();
	}

}
