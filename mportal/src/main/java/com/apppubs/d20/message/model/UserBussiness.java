package com.apppubs.d20.message.model;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.FirstLoginActity;
import com.apppubs.d20.bean.App;
import com.apppubs.d20.bean.AppConfig;
import com.apppubs.d20.bean.Department;
import com.apppubs.d20.bean.Settings;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.bean.UserDeptLink;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.constant.Actions;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.model.AbstractBussinessCallback;
import com.apppubs.d20.model.BaseBussiness;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.model.SystemBussiness;
import com.apppubs.d20.util.ACache;
import com.apppubs.d20.util.Des3;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.StringUtils;
import com.apppubs.d20.util.Utils;
import com.apppubs.d20.util.WebUtils;
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
public class UserBussiness extends BaseBussiness {

	private static UserBussiness sUserBussiness;
	private static final String CACHE_NAME = "com.client.message.model.UserBussiness";
	private Context mContext;
	private AppContext mAppContext;
	private boolean isSynchronizingAdbook;

	private UserBussiness(Context context) {
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
	public List<User> listAllUser() {
		return listAllUser(null);
	}
	public List<User> listAllUser(String permissionString){
		List<User> result = null;
		if(!TextUtils.isEmpty(permissionString)){
			String permissionStr = resovePermissionString(permissionString);
			String sql =  "select * from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID  = t2.USER_ID where t2.DEPT_ID in("+permissionStr+")  order by t1.INITIALS ";
			result = SugarRecord.findWithQuery(User.class, sql, new String[]{});
		}else{
			result = SugarRecord.listAll(User.class);
		}
		return result;
	}


	// 根据首字母和真实名称排序
	private class SortByInitialsAndTruename implements Comparator<User> {
		Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

		@Override
		public int compare(User o1, User o2) {
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
		return SugarRecord.count(User.class);
	}

	public long countUserOfCertainDepartment(String deptId){
		int count = 0;

		List<User> user = new ArrayList<User>();
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
		List<Department> depts = SugarRecord.find(Department.class,"super_id=?",deptId);
		if (depts==null||depts.size()<1){
			return;
		}
		for (Department dept:depts){
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
	public List<User> listUser(String departmentId) {
		String sql = "";
		if (this.mAppContext.getAppConfig().getAdbookAuthFlag() < 1 || (this.mAppContext.getAppConfig().getAdbookAuthFlag() > 0 && mAppContext.getCurrentUser().getAddressbookPermissionString().contains(departmentId))
				) {
			sql = "select * from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID = t2.USER_ID where t2.DEPT_ID = ? order by t2.sort_id";
		} else {
			sql = "select t1.USER_ID,t1.TRUE_NAME from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID = t2.USER_ID where t2.DEPT_ID = ? order by t2.sort_id";
		}

		return SugarRecord.findWithQuery(User.class, sql, departmentId);
	}

	/**
	 * 列出子部门
	 * 
	 * @param superDepId
	 *            部门的父id
	 * @return0 superDepId 0
	 */
	public List<Department> listSubDepartment(String superDepId) {
		return listSubDepartment(superDepId,null);
	}

	public List<Department> listSubDepartment(String superDepId, String permissionString) {
		List<Department> result = null;
		if (permissionString != null) {
			String sb = resovePermissionString(permissionString);
			String sql = "select * from department where super_id = '"+superDepId+"' and dept_id in ("+sb+") order by sort_id";
			result = SugarRecord.findWithQuery(Department.class, sql, new String[]{});
		} else {
			result = SugarRecord.find(Department.class, "super_id = ?", new String[] { superDepId }, null, "SORT_ID",
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

		long count = SugarRecord.count(Department.class, "SUPER_ID = ?", new String[] { departmentId });
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
	public List<Department> listRootDepartment() {
		AppConfig appConfig = AppContext.getInstance(mContext).getAppConfig();
		return listSubDepartment(appConfig.getAdbookRootId());
	}

	/**
	 * 获取某一个user
	 * 
	 * @param userId
	 * @return
	 */
	public User getUserByUserId(String userId) {
		return SugarRecord.findByProperty(User.class, "USER_ID", userId);
	}

	public User getUserByUsername(String username) {
		return SugarRecord.findByProperty(User.class, "username", username);
	}

	public List<User> getUsersByUserIds(List<String> userIds){
		StringBuilder sb = new StringBuilder();
		for (String userId : userIds){
			if (sb.length()>0){
				sb.append(",");
			}
			sb.append("'"+userId+"'");
		}
		String sql = "select * from USER where USER_ID in ("+sb.toString()+")";
		return SugarRecord.findWithQuery(User.class,sql);
	}

	public Future cacheUserBasicInfoList(final List<String> userIds, final BussinessCallbackCommon<List<UserBasicInfo>> callback){
		Future future = post(new Runnable() {
			@Override
			public void run() {
				String userIdsStr = StringUtils.join(userIds);
				String url = String.format(URLs.URL_USER_BASIC_INFO,userIdsStr);
				try {
					String response = WebUtils.requestWithGet(url);
					JSONResult jo = JSONResult.compile(response);
					if (jo.resultCode==1){
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

	public List<Department> getDepartmentByUserId(String userId) {
		String sql = "select * from DEPARTMENT t1 join USER_DEPT_LINK t2 on t1.DEPT_ID = t2.DEPT_ID where t2.USER_ID = ?";
		return SugarRecord.findWithQuery(Department.class, sql, userId);
	}

	public Department getDepartmentById(String deptId){
		List<Department> deptList = SugarRecord.find(Department.class,"dept_id=?",deptId);
		if (deptList==null||deptList.size()<1){
			return null;
		}else{
			return deptList.get(0);
		}
	}
	
	public List<String> getDepartmentStringListByUserId(String userId,String deptRootId){
		List<Department> deptList = getDepartmentByUserId(userId);
		List<String> strList = new ArrayList<String>(deptList.size());
		for(Department dept:deptList){
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
		Department dept = SugarRecord.findByProperty(Department.class, "dept_id", deptId);
		if(!deptId.equals(deptRootId)){
			if(TextUtils.isEmpty(resultSb.toString())){
				resultSb.append(dept.getName());
			}else{
				resultSb.insert(0, dept.getName()+"-");
			}
			if (!TextUtils.isEmpty(dept.getSuperId())&&!dept.getSuperId().equals(deptRootId)){
				Department superDept = SugarRecord.findByProperty(Department.class, "dept_id", dept.getSuperId());
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
	public List<User> searchUser(String str) {

		String dimStr = "%"+str+"%";
		return SugarRecord
				.find(User.class, "TRUE_NAME like ? or mobile like ? or work_tel like ? or office_no like ? or email like ?", new String[] { dimStr,dimStr,dimStr,dimStr,dimStr }, null, "sort_id", null);
	}

	/**
	 * 记录用户使用记录
	 * 
	 * @param userId
	 */
	public void recordUser(String userId) {

		SugarRecord.update(User.class, "LAST_USED_TIME", new Date().getTime() + "", "USER_ID = ?",
				new String[] { userId });
	}

	/*
	 * 列出常用用户
	 */
	public List<User> listRectent() {

		return SugarRecord.find(User.class, "LAST_USED_TIME IS NOT NULL", null, null, "LAST_USED_TIME desc", "0,20");
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
					String urlUser = app.getAddressbookUserUrl();
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
					String urlDep = app.getAddressbookDetpUrl();
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
							callback.onException(0);
							sHandler.post(new OnExceptionRun<Object>(callback));
							isSynchronizingAdbook = false;
						}
					});


					// 关联
					String urlLink = mAppContext.getApp().getAddressbookDeptUserUrl();
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
			SugarRecord.deleteAll(User.class);
			SugarRecord.deleteAll(Department.class);
			SugarRecord.deleteAll(UserDeptLink.class);

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
				List<User> userL = gson.fromJson(userJson.getString("users"), new TypeToken<List<User>>() {
				}.getType());
				mUserResponse = null;
				for (User u : userL) {
					u.save();
				}
				userL.clear();

				//部门
				if (app.getAddressbookNeedDecryption() == App.NEED) {
					mDeptResponse = Des3.decode(mDeptResponse);
				}
				JSONObject deptJson = new JSONObject(mDeptResponse);
				List<Department> deptL = gson.fromJson(deptJson.getString("depts"),
						new TypeToken<List<Department>>() {
						}.getType());
				mDeptResponse = null;
				for (Department d : deptL) {
					d.save();
				}
				deptL.clear();

				//关联表

				if (mAppContext.getApp().getAddressbookNeedDecryption() == App.NEED) {
					mUserDeptResponse = Des3.decode(mUserDeptResponse);
				}
				JSONObject linkJson = new JSONObject(mUserDeptResponse);
				List<UserDeptLink> linkL = gson.fromJson(linkJson.getString("deptuser"),
						new TypeToken<List<UserDeptLink>>() {
						}.getType());
				mUserDeptResponse = null;
				for (UserDeptLink l : linkL) {
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

			String data = WebUtils.requestWithPost(URLs.URL_LOGIN, requestParamsMap);
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
	public User login1(final String username, final String password, final String deviceid, final String dev,
			final String systemVresion, final String currentVersionName) {
		
		User result = null;
		try {
			Map<String, Object> requestParamsMap = new HashMap<String, Object>();
			requestParamsMap.put("username", username);
			requestParamsMap.put("password", password);
			requestParamsMap.put("deviceid", deviceid);
			requestParamsMap.put("dev", URLEncoder.encode(dev, "utf-8"));
			requestParamsMap.put("os", systemVresion);
			requestParamsMap.put("app", currentVersionName);
			requestParamsMap.put("fr", "4");
			
			String data = WebUtils.requestWithPost(URLs.URL_LOGIN, requestParamsMap);
			JSONObject jo = new JSONObject(data);
			/**
			 * //0、用户名或密码错误 //1、还未注册 //2、已经注册并且信息一致 //3、已经注册但信息不一致，该帐户被其他人注册
			 * //参数4 用户中文名字
			 */
			if(jo.getInt("result")==2){
				
				result = new User(jo.getString("userid"), jo.getString("username"), jo.getString("cnname"), password,
						jo.getString("email"), jo.getString("mobile"));
				result.setMenuPower(jo.getString("menupower"));
			}
			
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
	 * 客户端不需要登陆时，需要将设备的信息注册到服务端
	 */
	public void registerDevice(final BussinessCallbackCommon<Integer> callback) {
		sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				String token = mAppContext.getApp().getPushVendorType() == App.PUSH_VENDOR_TYPE_BAIDU ? mAppContext.getApp()
						.getBaiduPushUserId() : JPushInterface.getRegistrationID(mContext);// 百度硬件设备号
				String deviceId = SystemBussiness.getInstance(mContext).getMachineId();
				String systemVresion = Utils.getAndroidSDKVersion();// 操作系统号
				String currentVersionCode = Utils.getVersionName(mContext);// app版本号
				try {
					String url = String.format(URLs.URL_REGISTER_DEVICE, token, deviceId, systemVresion,
							URLEncoder.encode(Build.MODEL, "utf-8"), currentVersionCode,
							mAppContext.getApp().getCode());
					String result = WebUtils.requestWithGet(url);
					JSONResult jr = JSONResult.compile(result);
					if (jr.resultCode == 1) {
//						JSONObject jo = new JSONObject(jr.result);
//						String username = jo.getString("username");
//						MportalApplication.user.setUsername(username);
//						MportalApplication.saveAndRefreshUser(mContext, MportalApplication.user);
					} else {
						LogM.log(this.getClass(), "注册设备失败" + jr.reason);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
		});

	}
	
	public void logout(Context context) {
		AppContext.getInstance(mContext).clearCurrentUser();
		RongIM.getInstance().logout();
		context.sendBroadcast(new Intent(Actions.ACTION_LOGOUT));
	}

	/**
	 * 判断在是否有某用户的读取权限
	 * @param userid
	 * @return
	 */
	public boolean hasReadPermissionOfUser(String userid){
		List<Department> dl = getDepartmentByUserId(userid);
		for (Department d: dl){
			String permissionStr = AppContext.getInstance(mContext).getCurrentUser().getAddressbookPermissionString();
			if (!TextUtils.isEmpty(permissionStr)&&permissionStr.contains(d.getDeptId())){
				return true;
			}
		}
		return false;
	}

	public boolean hasChatPermissionOfUser(String userId){
		List<Department> dl = getDepartmentByUserId(userId);
		for (Department d: dl){
			String permissionStr = AppContext.getInstance(mContext).getCurrentUser().getChatPermissionString();
			if (!TextUtils.isEmpty(permissionStr)&&permissionStr.contains(d.getDeptId())){
				return true;
			}
		}
		return false;
	}


	public void updateUserInfo(final Context context, final BussinessCallbackCommon<UserInfo> callback){

		sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				if (mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME) {

					String osVersion = Utils.getAndroidSDKVersion();// 操作系统号
					String currentVersionName = Utils.getVersionName(context);// app版本号
					int appCode = Utils.getVersionCode(context);
					String machineId = SystemBussiness.getInstance(context).getMachineId();
					String url = null;
					try {
						// /wmh360/json/login/usersmslogin.jsp?username=%s&deviceid=%s&token=%s&os=%s&dev=%s&app=%s&fr=4&appcode="+appCode;
						UserInfo currentUser = AppContext.getInstance(mContext).getCurrentUser();
						url = String.format(URLs.URL_LOGIN_WITH_USERNAME, currentUser.getUsername(),
								machineId, JPushInterface.getRegistrationID(mContext), osVersion,
								URLEncoder.encode(Build.MODEL, "utf-8"), currentVersionName,appCode);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					LogM.log(this.getClass(), "请求url:" + url);
					try {
						WebUtils.requestWithGet(url, new WebUtils.DownloadLisener() {
							@Override
							public void onUpdate(double progress) {

							}

							@Override
							public void onSuccess(String response) {

								try {
									JSONObject jo = new JSONObject(response);
									int result = jo.getInt("result");
									UserInfo user = AppContext.getInstance(mContext).getCurrentUser();
									user.setUserId(jo.getString("userid"));
									user.setUsername(jo.getString("username"));
									user.setTrueName(jo.getString("cnname"));
									user.setEmail(jo.getString("email"));
									user.setMobile(jo.getString("mobile"));
									if(jo.has("menupower")){
										user.setMenuPower(jo.getString("menupower"));
									}

									if (jo.has("photourl")){
										user.setAvatarUrl(jo.getString("photourl"));
									}
									if (result != 2) {

										user = new UserInfo();
										AppContext.getInstance(context).setCurrentUser(user);
									} else {
										AppContext.getInstance(context).setCurrentUser(user);
									}
									sHandler.post(new OnDoneRun<UserInfo>(callback,user));
								} catch (JSONException e) {
									e.printStackTrace();
									sHandler.post(new OnExceptionRun<UserInfo>(callback));
								}
							}

							@Override
							public void onExceptioin(Exception e) {
								sHandler.post(new OnExceptionRun<UserInfo>(callback));
							}
						});
					} catch (IOException|InterruptedException e) {
						e.printStackTrace();
						sHandler.post(new OnExceptionRun<UserInfo>(callback));
					}
				}else if(mAppContext.getApp().getLoginFlag() == App.LOGIN_ONSTART_USE_USERNAME_PASSWORD){
					String token = JPushInterface.getRegistrationID(mContext);;
					String osVersion = Utils.getAndroidSDKVersion();// 操作系统号
					String currentVersionName = Utils.getVersionName(mContext);// app版本号


					int result =0;
					try {
						Map<String, Object> requestParamsMap = new HashMap<String, Object>();

						UserInfo currentUser = mAppContext.getCurrentUser();
						requestParamsMap.put("username", currentUser.getUsername());
						requestParamsMap.put("password", currentUser.getPassword());
						requestParamsMap.put("deviceid",  SystemBussiness.getInstance(mContext).getMachineId());
						requestParamsMap.put("token",  token);
						requestParamsMap.put("dev", URLEncoder.encode(Build.MODEL, "utf-8"));
						requestParamsMap.put("os", osVersion);
						requestParamsMap.put("app", currentVersionName);
						requestParamsMap.put("appcodeversion",Utils.getVersionCode(mContext)+"");
						requestParamsMap.put("fr", "4");

						String data = WebUtils.requestWithPost(URLs.URL_LOGIN, requestParamsMap);
						JSONObject jo = new JSONObject(data);
						/**
						 * //0、用户名或密码错误 //1、还未注册 //2、已经注册并且信息一致
						 * //3、已经注册但信息不一致，该帐户被其他人注册 //参数4 用户中文名字
						 */
						result = jo.getInt("result");

						// 登录成功才会修改本地的用户信息
						UserInfo user = null;
						if (result==2){
							user = new UserInfo(jo.getString("userid"), jo.getString("username"), jo.getString("cnname"),
									currentUser.getPassword(), jo.getString("email"), jo.getString("mobile"));
							user.setMenuPower(jo.getString("menupower"));

						}else{
							user = new UserInfo();
						}
						AppContext.getInstance(mContext).setCurrentUser(user);
						sHandler.post(new OnDoneRun<UserInfo>(callback,user));
					} catch (JSONException e) {
						e.printStackTrace();
						sHandler.post(new OnExceptionRun<UserInfo>(callback));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						sHandler.post(new OnExceptionRun<UserInfo>(callback));
					}
				}else{
					sHandler.post(new OnExceptionRun<UserInfo>(callback));
				}
			}
		});

	}
}
