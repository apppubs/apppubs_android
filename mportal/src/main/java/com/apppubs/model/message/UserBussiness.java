package com.apppubs.model.message;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TUser;
import com.apppubs.bean.UserInfo;
import com.apppubs.bean.TUserDeptLink;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.constant.Actions;
import com.apppubs.AppContext;
import com.apppubs.bean.App;
import com.apppubs.bean.AppConfig;
import com.apppubs.bean.Settings;
import com.apppubs.constant.URLs;
import com.apppubs.model.AbstractBussinessCallback;
import com.apppubs.model.BaseBiz;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.SystemBiz;
import com.apppubs.util.ACache;
import com.apppubs.util.Des3;
import com.apppubs.util.JSONResult;
import com.apppubs.util.LogM;
import com.apppubs.util.StringUtils;
import com.apppubs.util.Utils;
import com.apppubs.util.WebUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;

/**
 * 用户相关业务
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年3月10日 by zhangwen create
 * 
 */
public class UserBussiness extends BaseBiz {

	private static UserBussiness sUserBussiness;
	private static final String CACHE_NAME = "com.client.message.model.UserBussiness";
	private Context mContext;
	private AppContext mAppContext;
	private boolean isSynchronizingAdbook;

	private UserBussiness(Context context) {
		super(context);
		mContext = context;
		mAppContext = AppContext.getInstance(context);
	}

	public static synchronized UserBussiness getInstance(Context context) {

		if (sUserBussiness == null) {
			sUserBussiness = new UserBussiness(context);
		}
		return sUserBussiness;
	}

	/**
	 * 列出所有用户
	 * 
	 * @return
	 */
	public List<TUser> listAllUser() {
		return listAllUser(null);
	}
	public List<TUser> listAllUser(String permissionString){
		List<TUser> result = null;
		if(!TextUtils.isEmpty(permissionString)){
			String permissionStr = resovePermissionString(permissionString);
			String sql =  "select * from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID  = t2.USER_ID where t2.DEPT_ID in("+permissionStr+")  order by t1.INITIALS ";
			result = SugarRecord.findWithQuery(TUser.class, sql, new String[]{});
		}else{
			result = SugarRecord.listAll(TUser.class);
		}
		return result;
	}


	// 根据首字母和真实名称排序
	private class SortByInitialsAndTruename implements Comparator<TUser> {
		Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

		@Override
		public int compare(TUser o1, TUser o2) {
			if (cmp.compare(o1.getInitials(), o2.getInitials()) > 0) {
				return 1;
			} else if (cmp.compare(o1.getInitials(), o2.getInitials()) < 0) {
				return -1;
			} else if (cmp.compare(o1.getTrueName(), o2.getTrueName()) > 0) {
				return 1;
			} else if (cmp.compare(o1.getTrueName(), o2.getTrueName()) < 0) {
				return -1;
			}
			return 0;
		}
	}

	/**
	 * 所有用户数
	 * 
	 * @return
	 */
	public long countAllUser() {
		return SugarRecord.count(TUser.class);
	}

	public long countUserOfCertainDepartment(String deptId){
		int count = 0;

		List<TUser> user = new ArrayList<TUser>();
		List<String> deptIds = new ArrayList<String>();
		deptIds.add(deptId);
		recurseGet(deptId,deptIds);

		StringBuilder sb = new StringBuilder();
		for (String id:deptIds){
			if (sb.length()>0){
				sb.append(",");
			}
			sb.append("'");
			sb.append(id);
			sb.append("'");
		}
		String sql = String.format("select count(user_id) as usercount from user_dept_link where dept_id in(%s)",sb.toString());

		Cursor cursor = SugarRecord.getDatabase().rawQuery(sql,null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		cursor.close();
		return result;
	}

	/**
	 * 某部门下的所有用户，包含子部门的用户
	 * @param deptId
	 * @return
     */
	public List<String> getUserIdsOfCertainDepartment(String deptId){
		return getUserIdsOfCertainDepartment(deptId,false);
	}

	public List<String> getUserIdsOfCertainDepartment(String deptId,boolean needChatPermission){

		List<String> userIdList = new ArrayList<String>();
		List<String> deptIds = new ArrayList<String>();
		deptIds.add(deptId);
		recurseGet(deptId,deptIds);

		StringBuilder sb = new StringBuilder();
		if (needChatPermission){
			String permissionStr = AppContext.getInstance(mContext).getCurrentUser().getChatPermissionString();
			for (String id:deptIds){
				if (!TextUtils.isEmpty(permissionStr)&&permissionStr.contains(id)){
					if (sb.length()>0){
						sb.append(",");
					}
					sb.append("'");
					sb.append(id);
					sb.append("'");
				}
			}
		}else{
			for (String id:deptIds){
				if (sb.length()>0){
					sb.append(",");
				}
				sb.append("'");
				sb.append(id);
				sb.append("'");
			}
		}



		String sql = String.format("select distinct user_id from user_dept_link where dept_id in(%s)",sb.toString());

		Cursor cursor = SugarRecord.getDatabase().rawQuery(sql,null);
		while (cursor.moveToNext()){
			String userid = cursor.getString(0);
			userIdList.add(userid);
		}
		cursor.close();
		return userIdList;
	}

	private void recurseGet(String deptId, List<String> deptIds) {
		List<TDepartment> depts = SugarRecord.find(TDepartment.class,"super_id=?",deptId);
		if (depts==null||depts.size()<1){
			return;
		}
		for (TDepartment dept:depts){
			deptIds.add(dept.getDeptId());
			recurseGet(dept.getDeptId(),deptIds);
		}
	}

	/**
	 * 列出某个department下的用户
	 * 当前系统需要限制通讯录权限且有部门权限则显示全部信息，否则只查询出userid和truename
	 * @param departmentId
	 * @return
	 */
	public List<TUser> listUser(String departmentId) {
		String sql = "";
		if (this.mAppContext.getAppConfig().getAdbookAuthFlag() < 1 || (this.mAppContext.getAppConfig().getAdbookAuthFlag() > 0 && hasReadPermissionOfDept(departmentId))
				) {
			sql = "select * from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID = t2.USER_ID where t2.DEPT_ID = ? order by t2.sort_id";
		} else {
			sql = "select t1.USER_ID,t1.TRUE_NAME from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID = t2.USER_ID where t2.DEPT_ID = ? order by t2.sort_id";
		}

		return SugarRecord.findWithQuery(TUser.class, sql, departmentId);
	}

	/**
	 * 列出子部门
	 * 
	 * @param superDepId
	 *            部门的父id
	 * @return0 superDepId 0
	 */
	public List<TDepartment> listSubDepartment(String superDepId) {
		return listSubDepartment(superDepId,null);
	}

	public List<TDepartment> listSubDepartment(String superDepId, String permissionString) {
		List<TDepartment> result = null;
		if (permissionString != null) {
			String sb = resovePermissionString(permissionString);
			String sql = "select * from department where super_id = '"+superDepId+"' and dept_id in ("+sb+") order by sort_id";
			result = SugarRecord.findWithQuery(TDepartment.class, sql, new String[]{});
		} else {
			result = SugarRecord.find(TDepartment.class, "super_id = ?", new String[] { superDepId }, null, "SORT_ID",
					null);
		}
		return result;
	}

	private String resovePermissionString(String permissionString) {
		String[] permissionArr = permissionString.split(",");
		StringBuilder sb = new StringBuilder();
		for(int i=-1;++i<permissionArr.length;){
			if(i!=0){
				sb.append(",");
			}
			sb.append("'"+permissionArr[i]+"'");
		}
		return sb.toString();
	}

	/**
	 * 判断某个department是否为叶子节点
	 * 
	 * @param departmentId
	 * @return
	 */
	public boolean isLeaf(String departmentId) {

		long count = SugarRecord.count(TDepartment.class, "SUPER_ID = ?", new String[] { departmentId });
		return count == 0;

	}

	public String getRootSuperId(){
		AppConfig appConfig = AppContext.getInstance(mContext).getAppConfig();
		return appConfig.getAdbookRootId();
	}
	/*
	 * 列出第一层组织
	 * 
	 */
	public List<TDepartment> listRootDepartment() {
		AppConfig appConfig = AppContext.getInstance(mContext).getAppConfig();
		return listSubDepartment(appConfig.getAdbookRootId());
	}

	/**
	 * 获取某一个user
	 * 
	 * @param userId
	 * @return
	 */
	public TUser getUserByUserId(String userId) {
		return SugarRecord.findByProperty(TUser.class, "USER_ID", userId);
	}

	public TUser getUserByUsername(String username) {
		return SugarRecord.findByProperty(TUser.class, "username", username);
	}

	public List<TUser> getUsersByUserIds(List<String> userIds){
		StringBuilder sb = new StringBuilder();
		for (String userId : userIds){
			if (sb.length()>0){
				sb.append(",");
			}
			sb.append("'"+userId+"'");
		}
		String sql = "select * from USER where USER_ID in ("+sb.toString()+")";
		return SugarRecord.findWithQuery(TUser.class,sql);
	}

	public Future cacheUserBasicInfoList(final List<String> userIds, final IAPCallback<List<UserBasicInfo>> callback){
		Future future = post(new Runnable() {
			@Override
			public void run() {
				String userIdsStr = StringUtils.join(userIds);
				String url = String.format(URLs.URL_USER_BASIC_INFO,URLs.baseURL,URLs.appCode,userIdsStr);
				try {
					String response = WebUtils.requestWithGet(url);
					JSONResult jo = JSONResult.compile(response);
					if (jo.code ==1){
						List<UserBasicInfo> list = jo.getResultList(UserBasicInfo.class);
						Log.i("userbussiness","解析结果："+list+"list长度："+list.size());
						ACache cache =  ACache.get(mContext,CACHE_NAME);
						for (UserBasicInfo userBasicInfo: list){
							cache.put(userBasicInfo.getUserId(),userBasicInfo);
						}
						onDone(callback,list);
					}
				} catch (Exception e) {
					e.printStackTrace();
					onException(callback);
				}
			}
		});
		return future;
	}

	public UserBasicInfo getCachedUserBasicInfo(String userId){
		ACache cache =  ACache.get(mContext,CACHE_NAME);
		UserBasicInfo userInfo = (UserBasicInfo) cache.getAsObject(userId);
		return userInfo;
	}

	public void addOrUpdateUserBasicInfo(UserBasicInfo userBasicInfo){
		ACache cache =  ACache.get(mContext,CACHE_NAME);
		cache.put(userBasicInfo.getUserId(),userBasicInfo);
	}

	public List<TDepartment> getDepartmentByUserId(String userId) {
		String sql = "select * from DEPARTMENT t1 join USER_DEPT_LINK t2 on t1.DEPT_ID = t2.DEPT_ID where t2.USER_ID = ?";
		return SugarRecord.findWithQuery(TDepartment.class, sql, userId);
	}

	public TDepartment getDepartmentById(String deptId){
		List<TDepartment> deptList = SugarRecord.find(TDepartment.class,"dept_id=?",deptId);
		if (deptList==null||deptList.size()<1){
			return null;
		}else{
			return deptList.get(0);
		}
	}
	
	public List<String> getDepartmentStringListByUserId(String userId,String deptRootId){
		List<TDepartment> deptList = getDepartmentByUserId(userId);
		List<String> strList = new ArrayList<String>(deptList.size());
		for(TDepartment dept:deptList){
			StringBuilder sb = new StringBuilder();
			getDepartmentStringByDeptId(dept.getDeptId(), deptRootId,sb);
			strList.add(sb.toString());
		}
		return strList;
	}

	/**
	 * 获取除顶级部门外两级部门名称
	 * @param deptId
	 * @param deptRootId
	 * @param resultSb
	 */
	private void getDepartmentStringByDeptId(String deptId,String deptRootId,StringBuilder resultSb){
		TDepartment dept = SugarRecord.findByProperty(TDepartment.class, "dept_id", deptId);
		if(!deptId.equals(deptRootId)){
			if(TextUtils.isEmpty(resultSb.toString())){
				resultSb.append(dept.getName());
			}else{
				resultSb.insert(0, dept.getName()+"-");
			}
			if (!TextUtils.isEmpty(dept.getSuperId())&&!dept.getSuperId().equals(deptRootId)){
				TDepartment superDept = SugarRecord.findByProperty(TDepartment.class, "dept_id", dept.getSuperId());
				resultSb.insert(0,superDept.getName()+"-");
			}
		}
	}

	/**
	 * 搜索用户
	 * 
	 * @param str
	 * @return
	 */
	public List<TUser> searchUser(String str) {

		String dimStr = "%"+str+"%";
		return SugarRecord
				.find(TUser.class, "TRUE_NAME like ? or mobile like ? or work_tel like ? or office_no like ? or email like ?", new String[] { dimStr,dimStr,dimStr,dimStr,dimStr }, null, "sort_id", null);
	}

	/**
	 * 记录用户使用记录
	 * 
	 * @param userId
	 */
	public void recordUser(String userId) {

		SugarRecord.update(TUser.class, "LAST_USED_TIME", new Date().getTime() + "", "USER_ID = ?",
				new String[] { userId });
	}

	/*
	 * 列出常用用户
	 */
	public List<TUser> listRectent() {

		return SugarRecord.find(TUser.class, "LAST_USED_TIME IS NOT NULL", null, null, "LAST_USED_TIME desc", "0,20");
	}

	/**
	 * 同步通讯录
	 * 
	 * @param callback
	 */

	private double mUserDownloadProgress;//用户信息下载进度
	private double mDeptDownloadProgress;//部门信息下载进度
	private double mUserDeptDownloadProgress;//用户，部门信息关联表下载进度

	private String mUserResponse;
	private String mDeptResponse;
	private String mUserDeptResponse;

	public Future<?> sycnAddressBook(final AbstractBussinessCallback<Object> callback) {
		if (isSynchronizingAdbook){
			sHandler.post(new OnExceptionRun<Object>(callback));
			return null;
		}
		isSynchronizingAdbook = true;
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					// 用户
					App app = mAppContext.getApp();
					String urlUser = app.getAppConfig().getAdbookUserURL();
					WebUtils.requestWithGet(urlUser, new WebUtils.DownloadLisener() {
						@Override
						public void onUpdate(double progress) {
							mUserDownloadProgress = progress;
							sHandler.post(new OnUpdateRun(callback, (mUserDownloadProgress+mDeptDownloadProgress+mUserDeptDownloadProgress)/3.0f));
						}

						@Override
						public void onSuccess(String response) {
							mUserResponse = response;
							try {
								if(writeDbIfPossible()){
									sHandler.post(new OnDoneRun<Object>(callback, new Object()));
								}
							} catch (Exception e) {
								e.printStackTrace();
								sHandler.post(new OnExceptionRun<Object>(callback));
							}
						}

						@Override
						public void onExceptioin(Exception e) {
							e.printStackTrace();
							sHandler.post(new OnExceptionRun<Object>(callback));
							isSynchronizingAdbook = false;
						}
					});

					// 部门
					String urlDep = app.getAppConfig().getAdbookDeptURL();
					WebUtils.requestWithGet(urlDep, new WebUtils.DownloadLisener() {
						@Override
						public void onUpdate(double progress) {
							mDeptDownloadProgress = progress;
							sHandler.post(new OnUpdateRun(callback, (mUserDownloadProgress+mDeptDownloadProgress+mUserDeptDownloadProgress)/3.0f));
						}

						@Override
						public void onSuccess(String response) {
							mDeptResponse = response;
							try {
								if(writeDbIfPossible()){
									sHandler.post(new OnDoneRun<Object>(callback, new Object()));
								}
							} catch (Exception e) {
								e.printStackTrace();
								sHandler.post(new OnExceptionRun<Object>(callback));
							}
						}

						@Override
						public void onExceptioin(Exception e) {
							e.printStackTrace();
							callback.onException(new APError(APErrorCode.GENERAL_ERROR,"系统异常！"));
							sHandler.post(new OnExceptionRun<Object>(callback));
							isSynchronizingAdbook = false;
						}
					});


					// 关联
					String urlLink = mAppContext.getApp().getAppConfig().getAdbookLinkURL();
					WebUtils.requestWithGet(urlLink, new WebUtils.DownloadLisener() {
						@Override
						public void onUpdate(double progress) {
							mUserDeptDownloadProgress = progress;
							sHandler.post(new OnUpdateRun(callback, (mUserDownloadProgress+mDeptDownloadProgress+mUserDeptDownloadProgress)/3.0f));
						}

						@Override
						public void onSuccess(String response) {
							mUserDeptResponse = response;
							try {
								if(writeDbIfPossible()){
									sHandler.post(new OnDoneRun<Object>(callback, new Object()));
								}
							} catch (Exception e) {
								e.printStackTrace();
								sHandler.post(new OnExceptionRun<Object>(callback));
							}
						}

						@Override
						public void onExceptioin(Exception e) {
							sHandler.post(new OnExceptionRun<Object>(callback));
							isSynchronizingAdbook = false;
						}
					});


				} catch (Exception e) {
					sHandler.post(new OnExceptionRun<Object>(callback));
					e.printStackTrace();
					isSynchronizingAdbook = false;
				} finally {

				}

			}
		});

		return f;
	}

	protected class OnUpdateRun implements Runnable{
		private AbstractBussinessCallback mCallback;
		private double mProgress;
		public OnUpdateRun(AbstractBussinessCallback callback,double progress){
			mCallback = callback;
			mProgress = progress;
		}
		@Override
		public void run() {
			mCallback.onProgressUpdate((float)mProgress);
		}

	}

	private boolean writeDbIfPossible() throws Exception {
		if (shouldWriteDb()){

			SQLiteDatabase db = SugarRecord.getDatabase();
			db.beginTransaction();
			SugarRecord.deleteAll(TUser.class);
			SugarRecord.deleteAll(TDepartment.class);
			SugarRecord.deleteAll(TUserDeptLink.class);

			GsonBuilder gb = new GsonBuilder();
			gb.registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>() {

				@Override
				public Integer deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2)
						throws JsonParseException {
					Integer result = 0;
					String jsonStr = json.getAsString();
					try {
						result = Integer.parseInt(jsonStr);
					} catch (Exception e) {
						result = 0;
					}
					return result;
				}
			});
			Gson gson = gb.create();
			App app = AppContext.getInstance(mContext).getApp();

			try {

				if (app.getAddressbookNeedDecryption() == App.NEED) {
					mUserResponse = Des3.decode(mUserResponse);
				}
				JSONObject userJson = new JSONObject(mUserResponse);
				List<TUser> userL = gson.fromJson(userJson.getString("users"), new TypeToken<List<TUser>>() {
				}.getType());
				mUserResponse = null;
				for (TUser u : userL) {
					u.save();
				}
				userL.clear();

				//部门
				if (app.getAddressbookNeedDecryption() == App.NEED) {
					mDeptResponse = Des3.decode(mDeptResponse);
				}
				JSONObject deptJson = new JSONObject(mDeptResponse);
				List<TDepartment> deptL = gson.fromJson(deptJson.getString("depts"),
						new TypeToken<List<TDepartment>>() {
						}.getType());
				mDeptResponse = null;
				for (TDepartment d : deptL) {
					d.save();
				}
				deptL.clear();

				//关联表

				if (mAppContext.getApp().getAddressbookNeedDecryption() == App.NEED) {
					mUserDeptResponse = Des3.decode(mUserDeptResponse);
				}
				JSONObject linkJson = new JSONObject(mUserDeptResponse);
				List<TUserDeptLink> linkL = gson.fromJson(linkJson.getString("deptuser"),
						new TypeToken<List<TUserDeptLink>>() {
						}.getType());
				mUserDeptResponse = null;
				for (TUserDeptLink l : linkL) {
					l.save();
				}
				linkL.clear();
				db.setTransactionSuccessful();
				return true;
			}catch (Exception e){
				throw e;
			}finally {
				db.endTransaction();
				isSynchronizingAdbook = false;
				mUserDeptDownloadProgress = 0;
				mDeptDownloadProgress = 0;
				mUserDeptDownloadProgress = 0;
			}
		}

		return false;
	}

	/**
	 * 判断是否通讯录信息是否下载完毕,是否可以写数据库
	 * @return
	 */
	private boolean shouldWriteDb(){
		if((mUserDownloadProgress==mUserDeptDownloadProgress)&&
				(mUserDeptDownloadProgress==mDeptDownloadProgress)&&
				(mDeptDownloadProgress==1.0f)){
			return true;
		}
		return false;
	}

	/**
	 * 登陆msg/json/userlogin.jsp? username=lixiaowei&password=111111&deviceid=9
	 * c6d7094c422ddce4307e34bac10014b54fabb09&dev=1&os=2&app=3&fr=4 //fr
	 * 1.IPHONE客户端 3.IPad客户端 4.Android客户端 孙姐 13:11:28 //username 用户名 //password
	 * 密码 //deviceid 硬件设备号 //dev 硬件版本 //os 操作系统版本 //app 当前应用版本 //fr 1.IPHONE客户
	 */
	public int login(final String username, final String password, String deviceid,String token, final String dev,
			final String systemVresion, final String currentVersionName, final boolean allowAutoLogin) {

		int result = 0;
		try {
			Map<String, Object> requestParamsMap = new HashMap<String, Object>();
			requestParamsMap.put("username", username);
			requestParamsMap.put("password", password);
			requestParamsMap.put("deviceid", deviceid);
			requestParamsMap.put("token",token);
			requestParamsMap.put("dev", URLEncoder.encode(dev, "utf-8"));
			requestParamsMap.put("os", systemVresion);
			requestParamsMap.put("app", currentVersionName);
			requestParamsMap.put("fr", "4");

			String data = WebUtils.requestWithPost(String.format(URLs.URL_LOGIN, URLs.baseURL,URLs.appCode), requestParamsMap);
			JSONObject jo = new JSONObject(data);
			/**
			 * //0、用户名或密码错误 //1、还未注册 //2、已经注册并且信息一致 //3、已经注册但信息不一致，该帐户被其他人注册
			 * //参数4 用户中文名字
			 */
			result = jo.getInt("result");

			// 登录成功才会修改本地的用户信息

			UserInfo user = new UserInfo(jo.getString("userid"), jo.getString("username"), jo.getString("cnname"), password,
					jo.getString("email"), jo.getString("mobile"));
			user.setMenuPower(jo.getString("menupower"));
			// 保存user对象，并保存是否自动登录的配置
			AppContext.getInstance(mContext).setCurrentUser(user);
			Settings settings = mAppContext.getSettings();
			settings.setIsAllowAutoLogin(allowAutoLogin);
			mAppContext.setSettings(settings);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// };
		// Future<?> f = sDefaultExecutor.submit(r);
		//
		// return f;
		return result;
	}

	/**
	 * 判断在是否有某用户的读取权限
	 * @param userid
	 * @return
	 */
	public boolean hasReadPermissionOfUser(String userid){
		List<TDepartment> dl = getDepartmentByUserId(userid);
		for (TDepartment d: dl){
			if (hasReadPermissionOfDept(d.getDeptId())){
				return true;
			}
		}
		return false;
	}

	private boolean hasReadPermissionOfDept(String deptId){
		if (TextUtils.isEmpty(deptId)){
			return false;
		}
		String permissionStr = AppContext.getInstance(mContext).getCurrentUser().getAddressbookPermissionString();
		String [] deptIds = permissionStr.split(",");
		for (String curDeptId : deptIds){
			if (deptId.equals(curDeptId)){
				return true;
			}
		}
		return false;
	}

	public boolean hasChatPermissionOfUser(String userId){
		List<TDepartment> dl = getDepartmentByUserId(userId);
		String permissionStr = AppContext.getInstance(mContext).getCurrentUser().getChatPermissionString();
		for (TDepartment d: dl){
			if (!TextUtils.isEmpty(permissionStr)&&hasChatPermissionOfDept(d.getDeptId())){
				return true;
			}
		}
		return false;
	}

	private boolean hasChatPermissionOfDept(String deptId){
		if (TextUtils.isEmpty(deptId)){
			return false;
		}
		String permissionStr = AppContext.getInstance(mContext).getCurrentUser().getChatPermissionString();
		String[] deptIds = permissionStr.split(",");
		for (String curDeptId : deptIds){
			if (curDeptId.equals(deptId)){
				return true;
			}
		}

		return false;
	}
}
