package com.mportal.client.message.model;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.mportal.client.MportalApplication;
import com.mportal.client.bean.App;
import com.mportal.client.bean.AppConfig;
import com.mportal.client.bean.Department;
import com.mportal.client.bean.User;
import com.mportal.client.bean.UserDeptLink;
import com.mportal.client.business.AbstractBussinessCallback;
import com.mportal.client.business.BaseBussiness;
import com.mportal.client.business.BussinessCallbackCommon;
import com.mportal.client.business.SystemBussiness;
import com.mportal.client.constant.Actions;
import com.mportal.client.constant.URLs;
import com.mportal.client.util.Des3;
import com.mportal.client.util.JSONResult;
import com.mportal.client.util.LogM;
import com.mportal.client.util.Utils;
import com.mportal.client.util.WebUtils;
import com.orm.SugarRecord;

import org.json.JSONObject;

import java.io.IOException;
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
	private Context mContext;

	private UserBussiness(Context context) {

		mContext = context;
	}

	public static UserBussiness getInstance(Context context) {

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
//		Collections.sort(list, new SortByInitialsAndTruename());
		return listAllUser(null);
	}
	public List<User> listAllUser(String permissionString){
		List<User> result = null;
		if(permissionString!=null){
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
		SugarRecord.count(User.class);
		return SugarRecord.count(User.class);
	}

	/**
	 * 列出某个department下的用户
	 * 
	 * @param departmentId
	 * @return
	 */
	public List<User> listUser(String departmentId) {
		String sql = "select * from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID = t2.USER_ID where t2.DEPT_ID = ? order by sort_id";
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
		List<Department> result = null;
		int needPermsion = MportalApplication.app.getAddressbookNeedPermission();
		if (needPermsion==App.NEED){
			String departnentStr = MportalApplication.user.getAddressbookPermissionString();
			result = listSubDepartment(superDepId,departnentStr);
		}else{
			result = listSubDepartment(superDepId,null);
		}
		return result;
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

	/*
	 * 列出第一层组织
	 * 
	 */
	public List<Department> listRootDepartment() {
		AppConfig appConfig = SystemBussiness.getInstance(mContext).getAppConfig();
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

	public List<Department> getDepartmentByUserId(String userId) {
		String sql = "select * from DEPARTMENT t1 join USER_DEPT_LINK t2 on t1.DEPT_ID = t2.DEPT_ID where t2.USER_ID = ?";
		return SugarRecord.findWithQuery(Department.class, sql, userId);
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
	private void getDepartmentStringByDeptId(String deptId,String deptRootId,StringBuilder resultSb){
		Department dept = SugarRecord.findByProperty(Department.class, "dept_id", deptId);
		if(!deptId.equals(deptRootId)){
			if(TextUtils.isEmpty(resultSb.toString())){
				resultSb.append(dept.getName());
			}else{
				resultSb.insert(0, dept.getName()+"-");
			}
			getDepartmentStringByDeptId(dept.getSuperId(),deptRootId,resultSb);
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
	public Future<?> sycnAddressBook(final AbstractBussinessCallback<Object> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				SQLiteDatabase db = SugarRecord.getDatabase();
				try {
					db.beginTransaction();
					SugarRecord.deleteAll(User.class);
					SugarRecord.deleteAll(Department.class);
					SugarRecord.deleteAll(UserDeptLink.class);

					// 用户

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
					String urlUser = MportalApplication.app.getAddressbookUserUrl();
					// String urlUser = URLs.URL_ADDRESS_BOOK +
					// "&dowhat=getuser";
					String userString = WebUtils.requestWithGet(urlUser);
					if (MportalApplication.app.getAddressbookNeedDecryption() == App.NEED) {
						userString = Des3.decode(userString);
					}
					JSONObject userJson = new JSONObject(userString);
					List<User> userL = gson.fromJson(userJson.getString("users"), new TypeToken<List<User>>() {
					}.getType());
					userJson = null;
					userString = null;
					for (User u : userL) {
						u.save();
					}
					userL.clear();
					userL = null;

					// 部门
					String urlDep = MportalApplication.app.getAddressbookDetpUrl();
					String deptString = WebUtils.requestWithGet(urlDep);
					if (MportalApplication.app.getAddressbookNeedDecryption() == App.NEED) {
						deptString = Des3.decode(deptString);
					}
					JSONObject deptJson = new JSONObject(deptString);
					List<Department> deptL = gson.fromJson(deptJson.getString("depts"),
							new TypeToken<List<Department>>() {
							}.getType());
					deptJson = null;
					deptString = null;
					for (Department d : deptL) {
						d.save();
					}
					deptL.clear();
					deptL = null;

					// 关联
					String urlLink = MportalApplication.app.getAddressbookDeptUserUrl();
					// String urlLink = URLs.URL_ADDRESS_BOOK +
					// "&dowhat=getdeptuser";
					String linkString = WebUtils.requestWithGet(urlLink);
					if (MportalApplication.app.getAddressbookNeedDecryption() == App.NEED) {
						linkString = Des3.decode(linkString);
					}
					JSONObject linkJson = new JSONObject(linkString);
					List<UserDeptLink> linkL = gson.fromJson(linkJson.getString("deptuser"),
							new TypeToken<List<UserDeptLink>>() {
							}.getType());
					linkJson = null;
					linkString = null;
					for (UserDeptLink l : linkL) {
						l.save();
					}
					linkL.clear();
					linkL = null;
					db.setTransactionSuccessful();
					sHandler.post(new OnDoneRun<Object>(callback, new Object()));

				} catch (Exception e) {
					sHandler.post(new OnExceptionRun<Object>(callback));
					e.printStackTrace();
				} finally {

					db.endTransaction();
				}

			}
		});

		return f;
	}

	/**
	 * 登陆msg/json/userlogin.jsp? username=lixiaowei&password=111111&deviceid=9
	 * c6d7094c422ddce4307e34bac10014b54fabb09&dev=1&os=2&app=3&fr=4 //fr
	 * 1.IPHONE客户端 3.IPad客户端 4.Android客户端 孙姐 13:11:28 //username 用户名 //password
	 * 密码 //deviceid 硬件设备号 //dev 硬件版本 //os 操作系统版本 //app 当前应用版本 //fr 1.IPHONE客户
	 */
	public int login(final String username, final String password, final String deviceid, final String dev,
			final String systemVresion, final String currentVersionName, final boolean allowAutoLogin) {

		int result = 0;
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
			result = jo.getInt("result");

			// 登录成功才会修改本地的用户信息

			User user = new User(jo.getString("userid"), jo.getString("username"), jo.getString("cnname"), password,
					jo.getString("email"), jo.getString("mobile"));
			user.setMenuPower(jo.getString("menupower"));
			// 保存user对象，并保存是否自动登录的配置
			MportalApplication.saveAndRefreshUser(mContext, user);
			MportalApplication.systemSettings.setIsAllowAutoLogin(allowAutoLogin);
			MportalApplication.commitAndRefreshSystemSettings(MportalApplication.systemSettings, mContext);

			// sHandler.post(new OnDoneRun<Integer>(callback, result));//
			// 与主线程的通信

		} catch (Exception e) {
			e.printStackTrace();
		}

		// };
		// Future<?> f = sDefaultExecutor.submit(r);
		//
		// return f;
		return result;
	}
	public User login(final String username, final String password, final String deviceid, final String dev,
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

				String token = MportalApplication.app.getPushVendorType() == App.PUSH_VENDOR_TYPE_BAIDU ? MportalApplication.app
						.getBaiduPushUserId() : MportalApplication.app.getJpushRegistrationID();// 百度硬件设备号
				String deviceId = SystemBussiness.getInstance(mContext).getMachineId();
				String systemVresion = Utils.getAndroidSDKVersion();// 操作系统号
				String currentVersionCode = Utils.getVersionName(mContext);// app版本号
				try {
					String url = String.format(URLs.URL_REGISTER_DEVICE, token, deviceId, systemVresion,
							URLEncoder.encode(Build.MODEL, "utf-8"), currentVersionCode,
							MportalApplication.app.getCode());
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
		if(MportalApplication.app.getLoginFlag()==App.LOGIN_INAPP){
			
			User user = new User();
			MportalApplication.saveAndRefreshUser(context, user);
			
		}
		context.sendBroadcast(new Intent(Actions.ACTION_LOGOUT));
	}

}
