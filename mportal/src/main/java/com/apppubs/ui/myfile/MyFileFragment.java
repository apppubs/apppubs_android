package com.apppubs.ui.myfile;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
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

import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.d20.R;
import com.apppubs.model.IAPCallback;
import com.apppubs.bean.MyFileModel;
import com.apppubs.model.message.FilePickerModel;
import com.apppubs.model.message.MyFilePickerHelper;
import com.apppubs.model.cache.CacheListener;
import com.apppubs.model.cache.FileCacheErrorCode;
import com.apppubs.presenter.MyFilePresenter;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.ui.fragment.TitleBarFragment;
import com.apppubs.ui.message.activity.TranspondActivity;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.ProgressHUD;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.apppubs.ui.widget.commonlist.CommonListViewListener;
import com.apppubs.ui.widget.menudialog.MenuDialog;
import com.apppubs.util.FileUtils;
import com.apppubs.util.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MyFileFragment extends TitleBarFragment implements OnClickListener ,IMyFileView{

    public static final String EXTRA_NAME_DISPLAY_MODE = "mode";
    public static final int EXTRA_VALUE_DISPLAY_MODE_NORMAL = 0;
    public static final int EXTRA_VALUE_DISPLAY_MODE_SELECT = 1;

    private SearchView mSearchView;
    private CommonListView mLv;
    private CommonAdapter<MyFileModel> mAdapter;
    private List<MyFileModel> mDatas;
    private int mMode;

    private MyFilePresenter mPresenter;

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initProperties();
        initViews(inflater);

        return mRootView;
    }

    private void initProperties() {
        mMode = getArguments().getInt(EXTRA_NAME_DISPLAY_MODE, EXTRA_VALUE_DISPLAY_MODE_NORMAL);
        mPresenter = new MyFilePresenter(mContext, this);
    }

    private void initViews(LayoutInflater inflater) {
        mRootView = inflater.inflate(R.layout.act_my_file, null);
        mLv = (CommonListView) mRootView.findViewById(R.id.my_file_lv);

        mLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle args = new Bundle();
                args.putString(FilePreviewFragment.ARGS_STRING_URL, mDatas.get((int) parent.getItemIdAtPosition(position)).getFileUrl());
                ContainerActivity.startContainerActivity(mContext, FilePreviewFragment.class, args, "文件预览");
            }
        });
        mLv.setCommonListViewListener(new CommonListViewListener() {
            @Override
            public void onRefresh() {
                mPresenter.onRefreshClicked();
            }

            @Override
            public void onLoadMore() {
                mPresenter.onLoadMoreClicked();
            }
        });
        mLv.setPullLoadEnable(true);

        mSearchView = (SearchView) mRootView.findViewById(R.id.my_file_sv);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    String encodedQuery = URLEncoder.encode(query.trim(), "UTF-8");
                    mPresenter.onSerchMoreClicked(encodedQuery);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    MyFileFragment.this.onError(new APError(APErrorCode.GENERAL_ERROR,"输入格式非法！"));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mPresenter.onStopSearch();
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLv.refresh();
    }

    private void refreshListView() {
        List<MyFileModel> datas =  mDatas;
        if (mAdapter == null) {
            mAdapter = new CommonAdapter<MyFileModel>(mHostActivity, datas, R.layout.item_my_file) {

                @Override
                protected void fillValues(ViewHolder holder, MyFileModel bean, int position) {
                    TextView nameTv = holder.getView(R.id.my_file_name_tv);
                    nameTv.setText(bean.getName());
                    if (mMode == EXTRA_VALUE_DISPLAY_MODE_NORMAL) {
                        ImageView moreIv = holder.getView(R.id.my_file_moreIv);
                        moreIv.setVisibility(View.VISIBLE);
                        moreIv.setTag(R.id.temp_id, position);
                        moreIv.setOnClickListener(MyFileFragment.this);
                    } else if (mMode == EXTRA_VALUE_DISPLAY_MODE_SELECT) {
                        ImageView selectIv = holder.getView(R.id.my_file_selector_iv);
                        selectIv.setVisibility(View.VISIBLE);
                        selectIv.setTag(R.id.temp_id, position);
                        selectIv.setOnClickListener(MyFileFragment.this);
                        FilePickerModel pickerModel = new FilePickerModel();
                        pickerModel.setFilePath(bean.getFileLocalPath());
                        pickerModel.setFileUrl(bean.getFileUrl());
                        pickerModel.setSize(bean.getBytes());
                        checkCheckBtn(selectIv, MyFilePickerHelper.getInstance(getContext()).contains(pickerModel));
                    }

                    TextView timeTv = holder.getView(R.id.my_file_time_tv);
                    timeTv.setText(StringUtils.formatDate(bean.getAddTime(), "yyyy/MM/dd HH:mm"));
                    TextView sizeTv = holder.getView(R.id.my_file_size_tv);
                    sizeTv.setText(FileUtils.formetFileSize(bean.getBytes()));
                    ImageView iv = holder.getView(R.id.my_file_iv);
                    if ("docx".equals(bean.getTypeStr()) || "doc".equals(bean.getTypeStr())) {
                        iv.setImageResource(R.drawable.myfile_file_type_word);
                    } else if ("xlsx".equals(bean.getTypeStr()) || "xls".equals(bean.getTypeStr())) {
                        iv.setImageResource(R.drawable.myfile_file_type_excel);
                    } else if ("pptx".equals(bean.getTypeStr()) || "ppt".equals(bean.getTypeStr())) {
                        iv.setImageResource(R.drawable.myfile_file_type_ppt);
                    } else if ("pdf".equals(bean.getTypeStr())) {
                        iv.setImageResource(R.drawable.myfile_file_type_pdf);
                    } else if ("jpeg".equals(bean.getTypeStr()) || "jpg".equals(bean.getTypeStr()) || "png".equals(bean.getTypeStr()) || "gif".equals(bean.getTypeStr())) {
                        iv.setImageResource(R.drawable.myfile_file_type_img);
                    } else {
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

    private void toggleCheckBtn(MyFileModel bean, ImageView selectIv) {
        FilePickerModel pickerModel = new FilePickerModel();
        pickerModel.setFilePath(bean.getFileLocalPath());
        pickerModel.setFileUrl(bean.getFileUrl());
        pickerModel.setSize(bean.getBytes());
        if (MyFilePickerHelper.getInstance(getContext()).contains(pickerModel)) {
            MyFilePickerHelper.getInstance(getContext()).pop(pickerModel);
            checkCheckBtn(selectIv, false);
        } else {
            MyFilePickerHelper.getInstance(getContext()).put(pickerModel);
            checkCheckBtn(selectIv, true);
        }
    }

    private void checkCheckBtn(ImageView checkBtnIv, boolean check) {
        if (check) {
            checkBtnIv.setSelected(true);
            checkBtnIv.setColorFilter(mHostActivity.getThemeColor(), PorterDuff.Mode.SRC_ATOP);
        } else {
            checkBtnIv.setSelected(false);
            checkBtnIv.clearColorFilter();
        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getTag(R.id.temp_id) == null) {
            return;
        }
        if (mMode == EXTRA_VALUE_DISPLAY_MODE_NORMAL) {
            Integer index = (Integer) v.getTag(R.id.temp_id);
            MyFileModel model = mDatas.get(index);
            showMoreActionSheet(model);
        } else if (mMode == EXTRA_VALUE_DISPLAY_MODE_SELECT) {
            Integer index = (Integer) v.getTag(R.id.temp_id);
            MyFileModel model = mDatas.get(index);
            toggleCheckBtn(model, (ImageView) v);
        }
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
                } else if (position == 0) {
                    if (isDownloaded(model)) {
                        showTranspondActivity(model);
                    } else {
                        showDownloadAlert(model);
                    }
                }
            }
        }).show();
    }

    private void showDownloadAlert(final MyFileModel model) {
        new ConfirmDialog(mContext, new ConfirmDialog.ConfirmListener() {
            ProgressHUD mHUD;

            @Override
            public void onOkClick() {
                mHUD = ProgressHUD.show(mContext);
                mAppContext.getCacheManager().cacheFile(model.getFileUrl(), new CacheListener() {
                    @Override
                    public void onException(FileCacheErrorCode errorCode) {
                        ProgressHUD.dismissProgressHUDInThisContext(mContext);
                        Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDone(String fileUrl) {
                        ProgressHUD.dismissProgressHUDInThisContext(mContext);
                        showTranspondActivity(model);
                    }

                    @Override
                    public void onProgress(float progress, long totalBytesExpectedToRead) {
                        mHUD.setMessage(String.format("%d%%", (int) (progress * 100)));
                    }
                });
            }

            @Override
            public void onCancelClick() {

            }
        }, "是否下载？", "下载到本地后才可发送", "取消", "确定").show();
    }

    private boolean isDownloaded(MyFileModel model) {
        File file = mAppContext.getCacheManager().fetchCache(model.getFileUrl());
        if (file != null && file.exists()) {
            return true;
        }
        return false;
    }

    private void showTranspondActivity(MyFileModel model) {
        Intent i = new Intent(mContext, TranspondActivity.class);
        File file = mAppContext.getCacheManager().fetchCache(model.getFileUrl());
        i.putExtra(TranspondActivity.EXTRA_NAME_FILE_LOCATION, file.getAbsolutePath());
        startActivity(i);
    }

    private void showDeleteActionSheet(final MyFileModel model) {
        String[] menus = {"删除本地及云端文件", "删除本地文件"};
        new MenuDialog(mContext, menus, new MenuDialog.MenuDialogListener() {

            @Override
            public void onItemClicked(int index) {
                Log.v("MyFileFragment", "点击 " + index);
                if (index == 0) {
                    ProgressHUD.show(mHostActivity, "请稍候...", true, false, null);
                    mPresenter.onDeleteRemoteClicked(model.getFileId(), new IAPCallback<String>() {
                        @Override
                        public void onDone(String obj) {
                            ProgressHUD.dismissProgressHUDInThisContext(mHostActivity);
                            removeFromListView(model);
                            Toast.makeText(mHostActivity, "删除成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onException(APError error) {
                            onError(error);
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

    @Override
    public void stopRefresh() {
        mLv.stopRefresh();
    }

    @Override
    public void stopLoadMore() {
        mLv.stopLoadMore();
    }

    @Override
    public void setFileModels(List<MyFileModel> models) {
        mDatas = models;
        refreshListView();
    }

    @Override
    public void setSearchFileModels(List<MyFileModel> models) {
        mDatas = models;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(APError error) {
        super.onError(error);
    }
}
