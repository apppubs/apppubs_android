package com.apppubs.d20.message.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.d20.message.model.FilePickerModel;
import com.apppubs.d20.util.FileUtils;
import com.apppubs.d20.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2017/8/11.
 */

public class FileSelectionBar extends FrameLayout {

	private LinearLayout mContainerLL;
	private TextView mHintTV;
	private Button mOkButton;
	private View mTopLine;
	private String mHintText = "已选0B";
	private List<FilePickerModel> mDatas;
	private FileSelectionBar.FileSelectionBarListener mListener;


	public FileSelectionBar(Context context) {
		super(context);
		init();
	}

	public FileSelectionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FileSelectionBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initData();
		init();
	}

	private void initData() {
		mDatas = new ArrayList<FilePickerModel>();
	}

	private void init(){
		mContainerLL = new LinearLayout(getContext());
		mContainerLL.setOrientation(LinearLayout.HORIZONTAL);
		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mContainerLL,lp);

		//提示文字
		mHintTV = new TextView(getContext());
		mHintTV.setText(mHintText);
		mHintTV.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
		lp2.leftMargin = Utils.dip2px(getContext(),10);
		lp2.weight = 1;
		mContainerLL.addView(mHintTV,lp2);

		mOkButton = new Button(getContext());
		mOkButton.setText("确定");
		mOkButton.setTextSize(12);
		int pxFor8dp = Utils.dip2px(getContext(),8);
		mOkButton.setPadding(pxFor8dp,0,pxFor8dp,0);
		mOkButton.setEnabled(false);
		mOkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener!=null){
					mListener.onOkClick();
				}
			}
		});

		LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		okLp.setMargins(pxFor8dp,pxFor8dp,pxFor8dp,pxFor8dp);
		mContainerLL.addView(mOkButton,okLp);

		mTopLine = new View(getContext());
		mTopLine.setBackgroundResource(R.color.common_divider);
		ViewGroup.LayoutParams topLineLP = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
		addView(mTopLine,topLineLP);
	}


	public void setListener(FileSelectionBar.FileSelectionBarListener onItemClickListener){
		mListener = onItemClickListener;
	}

	public void setDatas(List<FilePickerModel> datas){
		mDatas = new ArrayList<FilePickerModel>();
		mDatas.addAll(datas);
		updateHintAndOkBtn();
	}

	public void put(FilePickerModel model){
		mDatas.add(model);
		updateHintAndOkBtn();
	}

	private void updateHintText(){
		mHintTV.setText("已选择 "+getTotalSize());
	}

	public FilePickerModel pop(FilePickerModel model){
		FilePickerModel targetModel = null;
		for (FilePickerModel m:mDatas){
			if (model.equals(m)){
				targetModel = m;
			}
		}
		if (targetModel!=null){
			mDatas.remove(targetModel);
			updateHintAndOkBtn();
		}
		return model;
	}

	private void updateHintAndOkBtn() {
		updateHintText();
		updateOkBtn();
	}

	private String getTotalSize(){
		long size = 0;
		for (FilePickerModel m:mDatas){
			size += m.getSize();
		}
		return FileUtils.formetFileSize(size);
	}

	/**
	 * 刷新ok按钮
	 */
	private void updateOkBtn() {
		if (mDatas.size()>0){
			mOkButton.setEnabled(true);
			mOkButton.setText("确定("+mDatas.size()+")");
		}else{
			mOkButton.setEnabled(false);
			mOkButton.setText("确定");
		}
	}

	public interface FileSelectionBarListener{
		void onOkClick();
	}

}
