package com.apppubs.d20.business;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.apppubs.d20.bean.PaperCatalog;
import com.apppubs.d20.bean.PaperInfo;
import com.apppubs.d20.bean.PaperIssue;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.WebUtils;
import com.google.gson.Gson;
import com.apppubs.d20.bean.Paper;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.FileUtils;
import com.orm.SugarRecord;


/**
 * 
 * @author zhangwen 2014-10-30
 * The service about the paper
 */
public class PaperBussiness extends BaseBussiness{
	
	private static PaperBussiness sPaperBussiness;

	private PaperBussiness() {
		
	};

	public static PaperBussiness getInstance() {

		if (sPaperBussiness == null)
			sPaperBussiness = new PaperBussiness();

		return sPaperBussiness;
	}

	public List<Paper> getPaperList() {
		return SugarRecord.listAll(Paper.class);
	}

	public Future<?> getPaperIssueList(final String paperCode, int page, final BussinessCallbackCommon<List<PaperIssue>> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				List<PaperIssue> piList = null;
				try {
					String url = URLs.URL_ISSUE_LIST + "?papercode=" + paperCode;
					piList = WebUtils.requestList(url, PaperIssue.class, "qi");
					for (PaperIssue pi : piList) {
						pi.setPaperCode(paperCode);
						//替换报纸中的名称中的“-”为“/"
						if(!TextUtils.isEmpty(pi.getName())){
							pi.setName(pi.getName().replaceAll("-", "/"));
						}
						pi.save();
					}
					sHandler.post(new OnDoneRun<List<PaperIssue>>(callback, piList));

				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		return f;

	}

	public Future<?> getPaperIssueInfo(final String issueId, final BussinessCallbackCommon<PaperIssue> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					PaperIssue pi = getIssue(issueId);
					sHandler.post(new OnDoneRun<PaperIssue>(callback, pi));

				} catch (Exception e) {
					sHandler.post(new OnExceptionRun<PaperIssue>(callback));
				}
			}
		});

		return f;
	}

	// 通过id获取某一期对象,并存库
	private PaperIssue getIssue(String issueId) throws IOException, JSONException, InterruptedException {
		String remoteStr = WebUtils.requestWithGet(URLs.URL_ISSUE_INFO + "?qiid=" + issueId);
		// 获取此期的版面信息
		JSONObject jsonO = new JSONObject(remoteStr);
		JSONArray catalogsArr = jsonO.getJSONArray("qi");
		List<PaperCatalog> catalogList = new ArrayList<PaperCatalog>();
		Gson gson = new Gson();
		for (int i = -1; ++i < catalogsArr.length();) {
			List<PaperInfo> infoList = new ArrayList<PaperInfo>();
			JSONObject jo = catalogsArr.getJSONObject(i);
			PaperCatalog ca = new PaperCatalog(jo.getString("id"), jo.getString("name"), jo.getString("mulupic"),
					jo.getInt("order"), issueId, jo.getString("mulupdf"), jo.getInt("mulutype") == 1 ? true : false);
			
			// 保存版面
			ca.save();
			JSONArray coordinateArr = jo.getJSONArray("coordinate");
			
			for (int j = -1; ++j < coordinateArr.length();) {
				PaperInfo info = gson.fromJson(coordinateArr.getString(j), PaperInfo.class);
				info.setIssueId(issueId);
				info.setCatalogId(ca.getId());
				info.save();
				infoList.add(info);
			}
			ca.setInfoList(infoList);
			catalogList.add(ca);

		}
		PaperIssue issue = new PaperIssue();
		issue.setId(issueId);
		issue.setCatalogList(catalogList);
		return issue;

	}

	public Future<?> getCatalog(final String catalogId, final BussinessCallbackCommon<PaperCatalog> callback) {

		Future<?> f = sDefaultExecutor.submit(new Runnable() {
			@Override
			public void run() {
				LogM.log(this.getClass(), "getCatalog catalogId:"+catalogId);
				final PaperCatalog catalog = SugarRecord.findById(PaperCatalog.class, catalogId);
				if (catalog.getPdfPath() != null && !catalog.getPdfPath().equals("") && new File(catalog.getPdfPath()).exists()) {// 说明图片存已经缓存过，但有可能文件已经被删除
					// 在ui线程handler中执行回调方法，回调方法中做ui操作
					sHandler.post(new OnDoneRun<PaperCatalog>(callback, catalog));
					return;
				}

				PaperIssue issue = SugarRecord.findById(PaperIssue.class, catalog.getIssueId());
				File dir = null;
				try {
					String htmlPathDir = FileUtils.getPaperStorageFile().getAbsolutePath() + "/" + issue.getPaperCode() + "/" + issue.getName()
							+ "/";
					dir = new File(htmlPathDir);
					if (!dir.isDirectory()) {
						dir.mkdirs();
					}

				} catch (Exception e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<PaperCatalog>(callback));
					return;
				}
				String desPath = dir.getAbsolutePath() + "/" + catalogId + ".pdf";
				try {
					FileUtils.download(catalog.getPdf(), desPath);
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<PaperCatalog>(callback));
					return;
				}
				catalog.setPdfPath(desPath);
				SugarRecord.updateById(PaperCatalog.class, catalogId, "PDF_PATH", desPath);
				sHandler.post(new OnDoneRun<PaperCatalog>(callback, catalog));

			};
		});
		return f;

	}

	
}
