package com.apppubs.ui.fragment;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apppubs.bean.TPaperInfo;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.PaperBiz;
import com.apppubs.util.LogM;
import com.artifex.mupdfdemo.MuPDFCore;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.HomeBaseActivity;
import com.apppubs.ui.activity.PaperInfoActivity;
import com.apppubs.bean.TPaper;
import com.apppubs.bean.TPaperCatalog;
import com.apppubs.ui.widget.PdfViewWithHotArea;
import com.apppubs.ui.widget.PdfViewWithHotArea.PdfViewWithHotAreaListener;
import com.orm.SugarRecord;

/**
 * 
 * @author zhangwen
 * 报纸版面 Fragment,用于一期报纸预览界面中的viewpager
 */
public class PaperCatalogFragment extends BaseFragment implements OnClickListener{
	

	public static String INTENT_ACTION_OPEN_NOPERMISSION_DIALOG = ".show_no_permission_dialog";
	
	
	public static String ARG_NAME_CATALOG_ID = "catalog_id";
	protected  final String TAG = this.getClass().getSimpleName();
	private HomeBaseActivity mContext;
	private TPaperCatalog mPaperCatalog;
	private View mRootView;
//	private TPaperIssue mIssue;
	private int mIndex;
	private Thread mGetCatalogHtmlT;
	private PaperBiz mPaperBiz;
	private LinearLayout mWaitLl;
	private LinearLayout mErrLl;
	private ImageView mReloadIv;
	
	private String mCatalogId;
	private TPaperCatalog mCatalog;
	
	private MuPDFCore mCore;
	private ImageView mWindow;
	private Bitmap mWindowBm;
	private boolean mIsHorizontall;//是否为横版
	private Handler mHandler = new Handler();
	private PdfViewWithHotArea mPdfViewWithHotArea;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle args = getArguments();
		mCatalogId = args.getString(ARG_NAME_CATALOG_ID);
		mRootView = inflater.inflate(R.layout.frg_paper_catalog, null);
		mPdfViewWithHotArea = (PdfViewWithHotArea) mRootView.findViewById(R.id.paper_issue_preview_pvwha);
		
		Log.v(TAG,"新建 onCreateView index: "+mIndex);
		init();
		return mRootView;
	}
	@Override
	public void onStart() {
		Log.v(TAG,"PaperFragment onStart index : "+mIndex);
		super.onStart();
		fill();
	}
	private void init(){
		
		mPaperBiz = PaperBiz.getInstance(getContext());
		
		mWaitLl = (LinearLayout) mRootView.findViewById(R.id.catalog_wait_ll);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG,"Fragment onPause "+ mIndex);
	}
	private void fill(){
		
		mWaitLl.setVisibility(View.VISIBLE);
		mPaperBiz.getCatalog(mCatalogId, new IAPCallback<TPaperCatalog>() {
			
			@Override
			public void onException(APError excepCode) {
				Toast.makeText(mHostActivity, "网络错误", Toast.LENGTH_SHORT).show();
				mWaitLl.setVisibility(View.GONE);
			}
			
			@Override
			public void onDone(TPaperCatalog obj) {
				mWaitLl.setVisibility(View.GONE);
				LogM.log(this.getClass(), "完成取回catalog");
				mPaperCatalog = obj;
				
				initPdf(obj);
			}


		});
	}
	
	private void initPdf(TPaperCatalog paperCatalog) {
		
		String sql = "select * from PAPER t1 join PAPER_ISSUE t2 on t1.paper_code = t2.paper_code where t2.id = ?";
		List<TPaper> paperList = SugarRecord.findWithQuery(TPaper.class, sql, paperCatalog.getIssueId());
		mPdfViewWithHotArea.setHotAreaBaseWidth(paperList.get(0).getHotAreaBaseWidth());
		mPdfViewWithHotArea.setHotAreaBaseHeight(paperList.get(0).getHotAreaBaseHeight());
		mPdfViewWithHotArea.setPdfPath(paperCatalog.getPdfPath());
		final List<TPaperInfo> infoList = SugarRecord.find(TPaperInfo.class, "CATALOG_id = ?",paperCatalog.getId() );
		Rect[] hotAreas = new Rect[infoList.size()];
		for(int i=-1;++i<hotAreas.length;){
			TPaperInfo pi = infoList.get(i);
			hotAreas[i] = new Rect(pi.getPosX(), pi.getPosY(), pi.getPosX()+pi.getPosW(), pi.getPosY()+pi.getPosH());
		}
		mPdfViewWithHotArea.setHotAreas(hotAreas);
		
		mPdfViewWithHotArea.setListener(new PdfViewWithHotAreaListener() {
			
			@Override
			public void onHotAreaClicked(int pos) {
				Intent intent=new Intent(getActivity(),PaperInfoActivity.class);
				intent.putExtra(PaperInfoActivity.EXTRA_STRING_ID, infoList.get(pos).getId());
				getActivity().startActivity(intent);
			}

			@Override
			public void onException(int errorCode) {
				
			}
		});
	}
	 
	public void cancel(){
		if(mGetCatalogHtmlT!=null&&!mGetCatalogHtmlT.isInterrupted()){
			mGetCatalogHtmlT.interrupt();
		}
		mPdfViewWithHotArea.clean();
			
	}
	@Override
	public void onStop() {
		Log.v(TAG,"Fragment "+mIndex+" onStop");
		super.onStop();
		if(mWindowBm!=null){
			mWindowBm.recycle();
			mWindowBm = null;
		} 
		if(mWindow!=null) {
			mWindow.setImageBitmap(null);
			mWindow.setVisibility(View.GONE);	
		}
		
		if(mCore!=null){
			mCore.onDestroy();
			mCore = null;
		}
		this.cancel();
	}
	@Override
	public void onDestroy() {
		Log.v(TAG,"销毁 "+mIndex);
		super.onDestroy();
		this.cancel();
		

	}
	@Override
	public void onClick(View v) {
/*		switch(v.getId()){
		case R.id.catalog_reload_iv:
			Log.v(TAG,"重新载入版面");
			fill();
			break;
		}*/
	}
	
	private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
		Bitmap bmpWithBorder =  Bitmap.createBitmap(bmp.getWidth()+2+borderSize * 2,bmp.getHeight()+2+ borderSize * 2,bmp.getConfig());
		Canvas canvas = new Canvas(bmpWithBorder);
		canvas.drawColor(Color.GRAY);
		RectF rect = new RectF();
		rect.left = 5;rect.top = 5;
		rect.bottom = 200;rect.right = 300;
		canvas.drawRect(rect, null);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bmp, borderSize, borderSize, null);
	    return bmpWithBorder;
	}

	
	

}