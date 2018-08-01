package com.apppubs.model.message;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.apppubs.AppContext;
import com.apppubs.bean.Settings;
import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TUser;
import com.apppubs.bean.UserInfo;
import com.apppubs.constant.URLs;
import com.apppubs.model.BaseBiz;
import com.apppubs.model.IAPCallback;
import com.apppubs.util.ACache;
import com.apppubs.util.JSONResult;
import com.apppubs.util.StringUtils;
import com.apppubs.util.WebUtils;
import com.orm.SugarRecord;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
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

//	/**
//	 * 列出某个department下的用户
//	 * 当前系统需要限制通讯录权限且有部门权限则显示全部信息，否则只查询出userid和truename
//	 * @param departmentId
//	 * @return
//	 */
//	public List<TUser> listUser(String departmentId) {
//		String sql = "";
//		if ( hasReadPermissionOfDept(departmentId)){
//			sql = "select * from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID = t2.USER_ID where t2.DEPT_ID = ? order by t2.sort_id";
//		} else {
//			sql = "select t1.USER_ID,t1.TRUE_NAME from USER t1 join USER_DEPT_LINK t2 on t1.USER_ID = t2.USER_ID where t2.DEPT_ID = ? order by t2.sort_id";
//		}
//
//		return SugarRecord.findWithQuery(TUser.class, sql, departmentId);
//	}

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

}
