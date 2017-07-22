package com.apppubs.d20.constant;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.Settings;
import com.apppubs.d20.util.SystemUtils;
import com.apppubs.d20.MportalApplication;

public class URLs {
	public static final String PROTOCOL_URL_CLIENTKEY = SystemUtils.md5("CmsClient");
	public static final String CLIENTKEY = "bb7c1386d85044ba7a7ae53f3362d634";
	public static final int PAGE_SIZE = 20;// 普通信息分页大小
	public static final int PAGE_PIC_SIZE = 10;// 图片分页大小
	public static String baseURL;
	public static String appCode;

	static{
		Settings settings = AppContext.getInstance(MportalApplication.getContext()).getSettings();
		baseURL = settings.getBaseURL();
		appCode = settings.getAppCode();
	}
	
	/**
	 * 应用基本信息（用于切换应用时检测应用是否存在）
	 * http://123.56.46.218/wmh360/json/getwebappinfo.jsp
	 * ?appcode=D20&imei=imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 * ?appcode=D20&
	 */
	public static final String URL_APP_BASIC_INFO = "wmh360/json/getwebappinfo.jsp?imei=imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 应用信息
	 * http://124.205.71.106:8080//wmh360/json/getappinfo.jsp?appcode=D01&imei
	 * =imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 * orientation 0纵向 1横向
	 * device 1:iphone  2IPAD 3Android手机 4 android pad
	 */
	public static final String URL_APPINFO = baseURL + "wmh360/json/getappinfo.jsp?appcode=" + appCode
			+ "&corpcode=%s&orientation=%s&device=%s&screen=%s&imei=imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	// public static final String URL_APPINFO =
	// URl_BASE+"wmh360/json/getappinfo.jsp?appcode="+APP_CODE+"&imei=imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	/**
	 * 启动图 http://123.56.46.218/wmh360/json/getadpiclist.jsp?appcode=D01&
	 * clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	// public static final String URL_STARTUP_PIC =
	// URl_BASE+"wmh360/json/getadpiclist.jsp?appcode="+APP_CODE+"&clientkey="+CLIENTKEY;
	public static final String URL_STARTUP_PIC = baseURL + "wmh360/json/getadpiclist.jsp?appcode=" + appCode
			+ "&clientkey=" + CLIENTKEY;

	/**
	 * http://www.wmh360.com/wmh360/json/msg/getappconfig.jsp?appcode=D20&
	 * clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_APP_CONFIG = baseURL + "wmh360/json/msg/getappconfig.jsp?appcode=" + appCode
			+ "&params=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 菜单 http://123.56.46.218/wmh360/json/getapp.jsp?appcode=D58&imei=imeitest&
	 * clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	// public static final String URL_MENU=
	// URl_BASE+"wmh360/json/getapp.jsp?appcode="+APP_CODE+"&clientkey="+CLIENTKEY;
	public static final String URL_MENU = baseURL + "wmh360/json/getapp.jsp?appcode=" + appCode + "&clientkey="
			+ CLIENTKEY;
	/*
	 * 通过superid来获取菜单
	 * http://192.168.1.103/wmh360/json/getmenu.jsp?appcode=U1431761314224&imei=imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_MENUS = baseURL+"wmh360/json/getmenu.jsp?appcode="+appCode+"&superid=%s&imei=imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634"; 
	/**
	 * 推广图 http://123.56.46.218/wmh360/json/gettgpiclist.jsp?webappcode=A09
	 * &clientkey=bb7c1386d85044ba7a7ae53f3362d634&channelcode=A090102
	 * &channelcode=A090102 &webappcode = A09
	 */
	public static final String URL_HEAD_PIC = baseURL + "wmh360/json/gettgpiclist.jsp?clientkey=" + CLIENTKEY;
	
	/**
	 * http://120.27.5.117/wmh360/json/getchannelbycode.jsp?channelcode=A080402&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_CHANNEL = baseURL+"wmh360/json/getchannelbycode.jsp?channelcode=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	
	// public static final String URL_HEAD_PIC =
	// URl_BASE+"wmh360/json/gettgpiclist.jsp?clientkey="+CLIENTKEY+"";
	/**
	 * 类型频道列表 http://123.56.46.218/wmh360/json/getchannellist.jsp?webappcode
	 * =A09&typeidID=1366524543362&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 * typeidID=1366524543362 webappcode = A09
	 */
	public static final String URL_CHANNEL_LIST = baseURL
			+ "wmh360/json/getchannellist.jsp?clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	// public static final String URL_CHANNEL_LIST =
	// URl_BASE+"/wmh360/json/getchannellist.jsp?clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	/**
	 * 版本更新 http://123.56.46.218/wmh360/json/getversion.jsp?appcode=D03&type=
	 * iphone&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_UPDATE = baseURL + "wmh360/json/getversion.jsp?";

	/**
	 * 某一个频道的信息列表 http://123.56.46.218/wmh360/json/getchannelinfolist.jsp?
	 * channelcode
	 * =A090102&pernum=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634&pno=1
	 * &pno=1 &channelcode=A090102
	 */
	public static final String URL_NEWS_LIST_OF_CHANNEL = baseURL
			+ "wmh360/json/getchannelinfolist.jsp?clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	// public static final String URL_NEWS_LIST_OF_CHANNEL =
	// URl_BASE+"wmh360/json/getchannelinfolist.jsp?pernum="+PAGE_SIZE+"&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 信息详情 http://123.56.46.218/wmh360/json/getchannelinfodetail.jsp?infoid
	 * =1414611497793770
	 * &clientkey=bb7c1386d85044ba7a7ae53f3362d634&channelcode=A090104
	 * infoid=1414611497793770 channelcode=A090104
	 */
	public static final String URL_NEWS_INFO = baseURL + "wmh360/json/getchannelinfodetail.jsp?appcode=" + appCode+"&device=android"
			+ "&clientkey=" + CLIENTKEY + "";
	// public static final String URL_NEWS_INFO =
	// URl_BASE+"wmh360/json/getchannelinfodetail.jsp?clientkey="+CLIENTKEY+"";
	/**
	 * 用户反馈 http://123.56.46.218/wmh360/json/feedback.jsp?webappcode=A09&fdtype=
	 * 1&fdcontract=test@com.cn&fdcontent=testcontent&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634
	 * wmh360/json/feedback.jsp?webappcode=%s&userid
	 * =%s&fdtype=%s&fdcontract=%s&fdcontent
	 * =%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_FEEDBACK = baseURL + "wmh360/json/feedback.jsp?appcode=" + appCode
			+ "&userid=%s&fdtype=%s&fdcontract=%s&fdcontent=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	/**
	 * 视频列表 http://123.56.46.218/wmh360/json/getchannelinfomedialist.jsp?
	 * channelcode
	 * =A070107&pno=1&pernum=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_VIDEO_LIST = baseURL + "wmh360/json/getvideolist.jsp?channelcode=%s&pno=%d&pernum=10&clientkey=" + CLIENTKEY;
	
	/**
	 * 视频详情
	 * http://123.57.13.138:8088/wmh360/json/getvideodetail.jsp?infoid=1465023705987501&channelcode=A8502&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_VIDEO = baseURL+"wmh360/json/getvideodetail.jsp?infoid=%s&channelcode=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	
	/**
	 * 音频列表
	 *  http://123.57.13.138:8088/wmh360/json/getaudiolist.jsp?channelcode=A8501&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_AUDIO_LIST = baseURL+"wmh360/json/getaudiolist.jsp?channelcode=%s&pno=%d&pernum=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634"; 
	
	/**
	 * 专题列表
	 * http://123.57.13.138:8088/wmh360/json/getsubjectlist.jsp?channelcode=A8504&pno=1&pernum=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_SPECIALS_LIST = baseURL+"wmh360/json/getsubjectlist.jsp?channelcode=%s&pno=%d&pernum=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	/**
	 * 获得微博列表
	 * http://202.85.221.113/wmh360/json/getweiboinfo.jsp?appcode=D01&imei
	 * =imeitest
	 */
	public static final String URL_WEIBO = baseURL + "wmh360/json/getweiboinfo.jsp?imei=imeitest&appcode=" + appCode;
	/**
	 * 搜索
	 * http://124.205.71.106:8080/wmh360/json/getsearchlist.jsp?webappcode=A09
	 * &keyword=交通&pno=1&pernum=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 * http
	 * ://202.85.221.113/wmh360/json/getsearchlist.jsp?webappcode=A09&keyword
	 * =交通&pno=1&pernum=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_SEARCH = baseURL + "wmh360/json/getsearchlist.jsp?";
	/**
	 * 用户评论结果提交 http://123.56.46.218/wmh360/json/updatecomment.jsp?infoid=
	 * 1226239645973833&imei=imeitest&content=contenttest&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_COMMENT_SUMMIT = baseURL + "wmh360/json/updatecomment.jsp?";
	/**
	 * // * 历史消息 // *
	 * http://123.56.46.218/wmh360/push/json/getmsglist.jsp?pno=1&pernum=10& //
	 * * clientid=D35&&clientkey=bb7c1386d85044ba7a7ae53f3362d634 //
	 */
	// public static final String URL_HISTORY = baseURL +
	// "wmh360/push/json/getmsglist.jsp?&pernum=10&clientkey=" + CLIENTKEY
	// + "&clientid=" + appCode;
	/**
	 * 图片列表
	 * http://123.56.46.218/wmh360/json/getpiclist.jsp?channelcode=A48&pno
	 * =1&pernum=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 * pernum="+PAGE_PIC_SIZE+"&
	 */
	public static final String URL_PIC_LIST = baseURL
			+ "wmh360/json/getpiclist.jsp?clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 图片详情 http://123.56.46.218/wmh360/json/getpiclistbyid.jsp?channelcode=
	 * A4807&infoid=1422535398644904&pno=1&pernum=10&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634 &infoid=1422535398644904 &pno=1
	 */
	public static final String URL_PIC_INFO_LIST = baseURL
			+ "wmh360/json/getpiclistbyid.jsp?pernum=20&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	public static final int IO_BUFFER_SIZE = 2 * 1024;
	/**
	 * 报料
	 * http://www.sxxynews.com:8080/wmh360/epaper/json/readernews.jsp?&userid=
	 * 123&title=123&name=1233&content=123&contract=123&picurl=123&appcode=D01
	 */
	public static final String URL_BAOLIAO = baseURL + "wmh360/epaper/json/readernews.jsp?";

	/**
	 * 评论列表 http://123.56.46.218/wmh360/json/getchannelcomment.jsp?
	 * infoid=1411214958719254
	 * &pno=1&pernum=20&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_COMMENTLIST = baseURL + "wmh360/json/getchannelcomment.jsp";
	/**
	 * 登陆 http://123.56.46.218/wmh360/wmhadmin/ucenter/dologin.jsp?username=
	 * admin&password=123 login/userlogin
	 * {"result":"0","username":"","userid":"","cnname":"","password":"11111"}
	 */
	public static final String URL_LOGIN = baseURL + "wmh360/json/login/userlogin.jsp?" + "appcode=" + appCode;

	/**
	 * http://www.apppubs.com/wmh360/json/login/mobilesmslogin.jsp?mobile=
	 * 13811708941&deviceid=erewrer3433243242&appcode=D20
	 */
	public static final String URL_LOGIN_WITH_PHONE = baseURL
			+ "wmh360/json/login/mobilesmslogin.jsp?mobile=%s&deviceid=%s&appcode=" + appCode;

	/**
	 * 通过用户名获取验证码
	 * //http://www.apppubs.com/wmh360/json/login/usersmslogin.jsp?username
	 * =xiwang&deviceid=erewrer3433243242&appcode=D20&os=ostest&dev=devtest&app=
	 * apptest&fr=1
	 */
	public static final String URL_LOGIN_WITH_USERNAME = baseURL
			+ "/wmh360/json/login/usersmslogin.jsp?username=%s&deviceid=%s&token=%s&os=%s&dev=%s&app=%s&appcodeversion=%d&fr=4&appcode="
			+ appCode;
	
	/**
	 * 通过组织机构登录
	 * http://www.apppubs.com/wmh360/json/login/usercroplogin.jsp?username=xiwang&deviceid=erewrer3433243242&appcode=D20&os=ostest&dev=devtest&app=apptest&fr=1
	 * wmh360/json/login/usercroplogin.jsp?username=%s&password=%s&corpcode=%s&deviceid=%s&os=%s&dev=%s&app=%s&fr=4&appcode="+appCode+"
	 */
	public static final String URL_LOGIN_WITH_ORG = baseURL+"wmh360/json/login/usercroplogin.jsp?username=%s&password=%s&corpcode=%s&deviceid=%s&os=%s&token=%s&dev=%s&app=%s&fr=4&appcode="+appCode+"";
	
	/**
	 * 无需登陆时注册设备信息
	 * 
	 * os 硬件操作系统版本 dev 硬件版本 app 安装的客户端版本 fr 1.IPHONE客户端 3.IPad客户端 4.Android客户端
	 * 
	 * http://123.56.46.218/wmh360/json/login/nologin.jsp?token=tokentest&
	 * deviceid
	 * =deviceidtest&os=ostest&dev=devtest&app=1&fr=1&appcode=U1435426278267
	 */
	public static final String URL_REGISTER_DEVICE = baseURL
			+ "wmh360/json/login/nologin.jsp?token=%s&deviceid=%s&os=%s&dev=%s&app=%s&fr=4&appcode=%s";

	/**
	 * 注册 http://123.56.46.218/wmh360/wmhadmin/ucenter/doreg.jsp?usernamestr=
	 * sdls
	 * &emailstr=234235325@qq.com&passwordstr=1234&clientidstr=D35&mobilestr=
	 * 156728933398&nicknamestr=dfssf usernamestr emailstr passwordstr
	 * clientidstr mobilestr nicknamestr
	 */
	public static final String URL_ZHUCE = baseURL + "wmh360/wmhadmin/ucenter/doreg.jsp?";
	/**
	 * 关于我们 http://123.56.46.218/wmh360/json/getappwebinfo.jsp?clientid=D58&what
	 * =appdesc
	 */
	public static final String URL_ABOUT = baseURL + "wmh360/json/getappwebinfo.jsp?clientid=" + appCode
			+ "&what=appdesc";
	/**
	 * 修改用户信息 http://123.56.46.218/wmh360/wmhadmin/ucenter/domodify.jsp
	 * http://123.56.46.218/wmh360/json/login/domodify.jsp useridstr 用户的id
	 * emailstr passwordstr clientidstr mobilestr nicknamestr
	 */
	public static final String URL_DOMODIFY = baseURL + "wmh360/json/login/domodify.jsp";

	/**
	 * http://123.56.46.218/wmh360/json/getappextrainfo.jsp?clientid=D58&what=
	 * reginfo
	 */
	public static final String URL_REGMIANZE = baseURL + "wmh360/json/getappextrainfo.jsp?clientid=" + appCode
			+ "&what=reginfo";

	/**
	 * 根据infoID获得评论数 http://123.56.46.218/wmh360/json/getinfocomment.jsp?infoid=
	 * 1411143367293075&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_INFOIDCOMMENTSIZE = baseURL + "wmh360/json/getinfocomment.jsp";

	/**
	 * 获得一区地址 http://123.56.46.218/wmh360/json/getweathercode.jsp?clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_ONEPLEASE = baseURL + "wmh360/json/getweathercode.jsp?clientkey=" + CLIENTKEY;
	/**
	 * 获得二区地址
	 * http://123.56.46.218/wmh360/json/getweathercodesub.jsp?code=101010100
	 * &clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_SECONDPLEASE = baseURL + "wmh360/json/getweathercodesub.jsp?";

	/**
	 * 报纸
	 */
	public static final String URL_PAPER_LIST = baseURL + "wmh360/epaper/json/getpaperinfo.jsp?appcode=" + appCode
			+ "&imei=imeitest";

	// 报纸期列表
	// ?papercode=xxrb"
	public static final String URL_ISSUE_LIST = baseURL + "wmh360/epaper/json/getqilist_all.jsp";

	// 某期报纸内容列表
	// ?qiid=1411815505567
	public static final String URL_ISSUE_INFO = baseURL + "wmh360/epaper/json/getqiinfo.jsp";

	/**
	 * 取得某一期报纸详情 http://123.56.46.218/wmh360/epaper/json/getnewspage.jsp?id=
	 * 1411817974211
	 */
	public static final String URL_PAPER_INFO = baseURL + "wmh360/epaper/json/getnewspage.jsp";
	// 往期报纸
	public static final String URL_LAST_ISSUE_ = baseURL + "wmh360/epaper/json/getqilist_old.jsp?papercode=xxrb";

	/**
	 * 获取频道更新时间
	 * http://123.56.46.218/wmh360/json/getchannelupdate.jsp?channelcode
	 * =A4801&clientkey=bb7c1386d85044ba7a7ae53f3362d634 &channelcode=A4801
	 */
	public static final String URL_CHANNEL_UPDATE_TIME = baseURL + "wmh360/json/getchannelupdate.jsp?clientkey="
			+ CLIENTKEY + "";

	/**
	 * 子菜单布局 http://123.56.46.218/wmh360/json/getmenugroup.jsp?id=1426613042915&
	 * clientkey=bb7c1386d85044ba7a7ae53f3362d634 id=1426613042915&
	 */

	public static final String URL_SUBMENU_GROUP = baseURL + "wmh360/json/getmenugroup.jsp?clientkey=" + CLIENTKEY
			+ "&appcode=" + appCode + "";

	/**
	 * 布局刷新时间 http://123.56.46.218/wmh360/json/getappinfo.jsp?appcode=D63&imei=
	 * imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_SUBMENU_GROUP_UPDATETIME = baseURL + "wmh360/json/getappinfo.jsp?appcode=" + appCode
			+ "&imei=imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 根据聊天查找聊天id
	 * http://www.wmh360.com/wmh360/json/msg/getchatgroupid.jsp?appcode
	 * =D20&username
	 * =dadcs&receiver=xiwang&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_CHAT_GET_CHAT_GROUP_ID = baseURL + "wmh360/json/msg/getchatgroupid.jsp?appcode="
			+ appCode + "&username=%s&receiver=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 新建聊天
	 * 
	 * http://www.wmh360.com/wmh360/json/msg/addgchat.jsp?appcode=D20&username=
	 * dacs&groupname=&groupuserall=xiwang,jiangrui&grouptype=2&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634 username 创建者用户名 groupname 群组名称
	 * groupuserall 群组成员 多个以逗号分隔 grouptype 1单聊 2群聊 返回： groupid 创建的聊天群组ID
	 */
	public static final String URL_CHAT_CREATE_CHAT = baseURL + "wmh360/json/msg/addgchat.jsp?appcode=" + appCode
			+ "&username=%s&groupname=&groupuserall=%s&grouptype=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 增加群聊人员 http://www.wmh360.com/wmh360/json/msg/addchatuser.jsp?appcode=D20&
	 * groupid=1447914062284&groupuserall=xiwang,jiangrui,wubin&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_CHAT_ADD_CHAT_USER = baseURL + "wmh360/json/msg/addchatuser.jsp?appcode=" + appCode
			+ "&groupid=%s&groupuserall=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * http://www.wmh360.com/wmh360/json/msg/delchatuser.jsp?appcode=
	 * U1446231090246
	 * &groupid=1448846887462&username=wangwu&groupuserall=meng&clientkey
	 * =bb7c1386d85044ba7a7ae53f3362d634 groupid 群组ID username 群主用户名
	 * groupuserall 要删除的群组成员， 多个以逗号分隔
	 */
	public static final String URL_CHAT_DELETE_CHAT_USER = baseURL
			+ "wmh360/json/msg/delchatuser.jsp?appcode="+appCode+"&groupid=%s&username=%s&groupuserall=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * .聊天列表显示接口 只返回未读信息 其他已经读过的应该缓存到客户端本地
	 * http://www.wmh360.com/wmh360/msg/json/
	 * getchatlist.jsp?receiver=fanyi&sender=ly receiver sender
	 * 
	 * http://www.wmh360.com/wmh360/json/msg/getchatlist.jsp?groupid=
	 * 1448008312259
	 * &appcode=D20&sender=dacs&receiver=xiwang&curp=1&perp=10&clientkey
	 * =bb7c1386d85044ba7a7ae53f3362d634 groupid 群组ID 可以为空 sender 发送者 receiver
	 * 接收者 可以为空 curp 当前第几页 perp 每页多少条
	 */

	public static final String URL_CHAT_LIST = baseURL + "wmh360/json/msg/getchatlist.jsp?appcode=" + appCode
			+ "&receiver=%s&sender=%s&groupid=%s&curp=%s&perp=%s&starttime=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * http://www.wmh360.com/wmh360/msg/json/insertchatinfo.jsp?sender=ly&
	 * receiver
	 * =fanyi&contentType=&content=testtestfdgdfgdfgfdg&fileName=&fromtype
	 * =5&flag=1&totype=4 聊天发送消息接口
	 * &receiver=fanyi&sender=ly&contentType=&content
	 * =testtestfdgdfgdfgfdg&fileName=
	 * length=1000如果是图片则此处为图片或文件此处单位为byte，如果为声音此处单位为millisecond
	 * 
	 * 
	 * http://www.wmh360.com/wmh360/json/msg/insertchatinfo.jsp?sender=xiwang&
	 * receiver
	 * =dacs&contentType=1&content=hello&fileName=&fromtype=1&flag=4&totype
	 * =&appcode=D20&length=&clientkey=bb7c1386d85044ba7a7ae53f3362d634&msginfo=
	 * nder 发送者 receiver 接收者 //contentType 附件类别 1 文字,2 图片,3 音频, 4视频 5链接
	 * //content 下发内容 UTF-8编码的 //fileName 有附件时的附件文件名称 //fromtype 来源 1 iphone
	 * 3系统下发4 第三方下发 5 Android下发 //totype 信息接收客户端类型 2为其他，空为手机，一般该参数可不穿值
	 * 
	 * //flag 0 系统服务号;1 CMS服务号;2 第三方服务号;3 报纸服务号；4 聊天---最新启用 //length
	 * 如果是图片则此处为图片或文件此处单位为byte，如果为声音此处单位为millisecond //msginfo 推送的信息ID
	 * 也可以是一个链接地址 //appcode 客户ID 我们系统会为每个客户分配一个ID
	 * 
	 */
	public static final String URL_CHAT_SEND = baseURL
			+ "wmh360/json/msg/insertchatinfo.jsp?clientkey=bb7c1386d85044ba7a7ae53f3362d634&appcode=" + appCode
			+ "&fromtype=5&flag=4";

	// public static final String URL_CHAT_SEND_MSG =
	// baseURL+"wmh360/json/msg/insertchatinfo.jsp?appcode="+appCode+"&sender=%s&receiver=%s&contentType=%s&content=%s&fileName=%s&fromtype=5&flag=4&totype=&length=&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 聊天信息发送 http://www.wmh360.com/wmh360/json/msg/addchatinfo.jsp?appcode=D20&
	 * groupid
	 * =1447914062284&username=dacs&receiver=xiwang&content=hello&contenttype
	 * =1&fromtype
	 * =1&filename=&length=&clientkey=bb7c1386d85044ba7a7ae53f3362d634 groupid
	 * 群组ID 可以为空 username 聊天发起人 receiver 聊天接收人 可以为空 content 聊天内容 contenttype
	 * 内容类别 1 文字,2 图片,3 音频, 4视频 5链接 fromtype 来源 1 iphone 5 Android下发 filename
	 * 有附件时的附件文件名称 length 如果是图片则此处为图片或文件此处单位为byte，如果为声音此处单位为millisecond
	 * wmh360/json
	 * /msg/addchatinfo.jsp?appcode=&username=%s&receiver=%s&content=%
	 * s&contenttype=%s&fromtype=5&filename=%s&length=%s&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634
	 */

	public static final String URL_CHAT_SEND_MSG = baseURL
			+ "wmh360/json/msg/addchatinfo.jsp?appcode="
			+ appCode
			+ "&groupid=%s&username=%s&receiver=%s&content=%s&contenttype=%s&fromtype=5&filename=%s&length=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * http://www.wmh360.com/wmh360/json/msg/getchatgroupinfo.jsp?appcode=
	 * U1446231090246&groupid=1448260027442&username=lisi&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634 获得聊天群组信息
	 * http://www.wmh360.com/wmh360/json
	 * /msg/getchatgroupinfo.jsp?appcode=D20&groupid
	 * =1448008312259&username=dacs&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 * groupid 群组ID username 当前用户名 grouptype 1单聊 2群聊
	 * 
	 * 返回： {"resultcode":"1", "resultreason":"获得聊天群组信息成功", "resultinfo":{
	 * "group_name": "", "group_type": "2", "group_createtime":
	 * "2015-11-20 16:34:42", "group_creator": "dacs", "group_userinfo": [
	 * {"username":"zhangsan","truename":"张三","receiveflag":"1"},
	 * {"username":"lisi","truename":"李四","receiveflag":"1"},
	 * {"username":"dacs","truename":"档案测试用户","receiveflag":"1"} ] } }
	 * group_name 群组名 group_type 1单聊 2群聊 group_createtime 创建时间 group_creator
	 * 聊天群组创建人 group_userinfo 聊天群组成员 username 用户名 truename 真实姓名 receiveflag
	 * 是否接收消息 1接收 0不接收
	 */

	public static final String URL_CHAT_GROUD_INFO = baseURL + "wmh360/json/msg/getchatgroupinfo.jsp?appcode="
			+ appCode + "&groupid=%s&username=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 修改群聊天名称
	 * http://www.wmh360.com/wmh360/json/msg/setchatgroupname.jsp?appcode
	 * =D20&groupid=1448008312259&groupname=hello&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_CHAT_CHANGE_GROUP_NAME = baseURL + "wmh360/json/msg/setchatgroupname.jsp?appcode="
			+ appCode + "&groupid=%s&groupname=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 删除群成员，退出群聊
	 * http://www.wmh360.com/wmh360/json/msg/delchatgroupbyuser.jsp?appcode=D20&
	 * groupid
	 * =1448008312259&username=dacs&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_CHAT_EXIT_CHAT_GROUP = baseURL + "wmh360/json/msg/delchatgroupbyuser.jsp?appcode="
			+ appCode + "&groupid=%s&username=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 服务号列表
	 * http://www.wmh360.com/wmh360/json/msg/getservicelistbyall.jsp?appcode=D20
	 */
	public static final String URL_SERVICE_LIST = baseURL + "wmh360/json/msg/getservicelistbyall.jsp?appcode="
			+ appCode + "&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	/*
	 * http://www.wmh360.com/wmh360/json/msg/getallservicelistbyuser.jsp?appcode=
	 * D20&username=dacs&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_SERVICE_NO_FOR_USER = baseURL
			+ "wmh360/json/msg/getallservicelistbyuser.jsp?appcode=" + appCode
			+ "&username=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 服务号属性
	 * http://202.99.19.140:8080/wmh360/json/msg/getservicebyid.jsp?appcode
	 * =D20&service_id
	 * =1445543142727&username=dacs&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_SERVICE_NO = baseURL + "wmh360/json/msg/getservicebyid.jsp?appcode=" + appCode
			+ "&service_id=%s&username=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * .获得某一服务下历史消息列表的接口
	 * 
	 * http://www.wmh360.com/wmh360/json/msg/getserviceinfolist.jsp?appcode=D20&
	 * service_id=1427461856233&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_SERVICE_MESSAGE_INFO_LIST = baseURL
			+ "wmh360/json/msg/getserviceinfolist.jsp?appcode=" + appCode
			+ "&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 所有需要订阅的服务号
	 * 
	 * 可以订阅的列表
	 * http://www.wmh360.com/wmh360/json/msg/getservicelistbyorder.jsp?appcode
	 * =U1433417616429&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 * 
	 */
	public static final String URL_SERVICE_NO_SUBSCRIBEABLE = baseURL
			+ "wmh360/json/msg/getservicelistbyorder.jsp?appcode=" + appCode
			+ "&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 添加公众号关注 e.添加关注 用户订阅服务
	 * http://www.wmh360.com/wmh360/json/msg/addservicebyuser
	 * .jsp?appcode=D20&service_id=1427461856233&username=xiwang&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634 {"result": "1"} result:0失败，1成功，2已经关注
	 */
	public static final String URL_SERVICE_ATTENTION = baseURL + "wmh360/json/msg/addservicebyuser.jsp?appcode="
			+ appCode + "&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	/**
	 * 获得某一用户订阅的服务号列表
	 * http://www.wmh360.com/wmh360/json/msg/getservicelistbyuser.
	 * jsp?appcode=D20
	 * &username=xiwang&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_USERSERVICELIST = baseURL + "wmh360/json/msg/getservicelistbyuser.jsp?appcode="
			+ appCode + "&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * g.取消关注 用户订阅服务
	 * http://www.wmh360.com/wmh360/json/msg/delservicebyuser.jsp?appcode
	 * =D20&service_id=1427461856233&username=xiwang&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634
	 * 
	 * {"result": "1"} result:0失败，1成功
	 */
	public static final String URL_UNATTENTION_SERVICE = baseURL + "wmh360/json/msg/delservicebyuser.jsp?appcode="
			+ appCode + "&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 显示某一条信息的详细信息 http://www.wmh360.com/wmh360/json/msg/getserviceinfodetail.
	 * jsp?appcode=D20 &serviceinfo_id=1428549646637&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634
	 * 
	 * serviceinfo_id 消息ID serviceinfo_titl 消息标题 serviceinfo_picurl 消息图片
	 * serviceinfo_summary 消息摘要 serviceinfo_content 消息内容 serviceinfo_thedate
	 * 消息时间
	 */
	public static final String URL_SERVICEINFO = baseURL + "wmh360/json/msg/serviceinfo.jsp?appcode=" + appCode
			+ "&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 清空某服务号/聊天的未读信息
	 * http://www.wmh360.com/wmh360/json/msg/setreadinfo.jsp?appcode=
	 * D20&service_id
	 * =1428155175898&username=dacs&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_CLEAR_UNREAD_NUM_FOR_SERVICE_NO_AND_CHAT = baseURL
			+ "wmh360/json/msg/setreadinfo.jsp?appcode=" + appCode
			+ "&service_id=%s&username=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 清空单独信息的阅读
	 * http://www.wmh360.com/wmh360/json/msg/setreadinfoone.jsp?appcode
	 * =D20&id=1428155175898&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_CLEAR_UNREAD_NUM_FOR_SINGLE_SERVICE_NO = baseURL
			+ "wmh360/json/msg/setreadinfoone.jsp?appcode=" + appCode
			+ "&id=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * //推广图(应用)单独的推广图，自己是一个应用在menugroup中显示
	 * http://www.apppubs.com/wmh360/json/getsliding
	 * .jsp?appcode=U1435426278267&imei
	 * =imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634&slidingid=1001
	 * slidingid：channeltypeid，一个推广图菜单对应一个推广图的频道，通过channeltypeid来得到此推广图频道的信息
	 */
	public static final String URL_PROMOTION_PIC_LIST = baseURL
			+ "wmh360/json/getsliding.jsp?imei=imeitest&clientkey=bb7c1386d85044ba7a7ae53f3362d634&appcode=" + appCode
			+ "&slidingid=%s";

	/**
	 * http://www.apppubs.com/wmh360/json/login/smsresend.jsp?mobile=13811708941
	 * &deviceid=erewrer3433243242&appcode=D20
	 */
	public static final String URL_SEND_SMS = baseURL
			+ "wmh360/json/login/smsresend.jsp?username=%s&mobile=%s&deviceid=%s&appcode=" + appCode
			+ "&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	public static final int RESULT_CODE_SEND_SMS_ERROR = -1;

	/**
	 * wmh360/json/login/smsconfirm.jsp?mobile=13811708941&deviceid=1671e9f7
	 * c4e83393a6cddcca401d9072
	 * &smscode=786312&appcode=D20&username=xiwang&token=
	 * 5465465656567&os=ostest&
	 * dev=devtest&app=apptest&fr=1&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_CONFIRM_VERIFICATION_CODE = baseURL
			+ "wmh360/json/login/smsconfirm.jsp?mobile=%s&deviceid=%s&smscode=%s&username=%s&token=%s&os=%s&dev=%s&app=%s&appcodeversion=%d&fr=4&clientkey=bb7c1386d85044ba7a7ae53f3362d634&appcode="
			+ appCode;
	public static final int RESULT_CODE_CONFIRM_VERIFICATION_CODE_ERROR = -1;

	/**
	 * 标题左右的菜单 http://123.56.46.218/wmh360/json/gettitlemenu.jsp?appcode=
	 * U1443595553587
	 * &supermenuid=1444337659819&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_TITLE_MENU = baseURL + "wmh360/json/gettitlemenu.jsp?appcode=" + appCode
			+ "&supermenuid=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	/**
	 * 修改密码
	 */
	public static final String URL_MODIFY_PASSWORD = baseURL + "wmh360/json/modifypwd.jsp?appcode=" + appCode
			+ "&userid=%s&oldpassword=%s&newpassword=%s";

	/**
	 * 用户通讯录权限
	 * http://www.wmh360.com/wmh360/json/msg/getdept_authsetl.jsp?appcode
	 * =D20&userid=8a80c6533629df2f01362e0db94e01c8&clientkey=
	 * bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_ADDRESS_PERMISSION = baseURL + "wmh360/json/msg/getdept_authsetl.jsp?appcode="
			+ appCode + "&userid=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	/**
	 * http://60.205.140.176:8088/wmh360/json/msg/getdept_imauthset.jsp?appcode=D20&userid=8a80c6533629df2f01362e0db94e01c8&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_USER_PERMISSION = baseURL+"wmh360/json/msg/getdept_imauthset.jsp?appcode="+appCode+"&userid=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * http://123.57.13.138:8088/wmh360/json/getaddressicon.jsp?username=xiwang&appcode=D20&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_ADDRESS_USER_ICON = baseURL+"wmh360/json/getaddressicon.jsp?appcode="+ appCode+"&username=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	/**
	 * String uploadURL = "http://www.wmh360.com/wmh360/json/msg/uploadfile.jsp";
	 * 上传接口
	 */
	public static final String URL_UPLOAD = baseURL+"wmh360/json/msg/uploadfile.jsp";
	
	/**
	 * http://123.56.46.218/wmh360/json/msg/getsysconfig.jsp?clientkey=bb7c1386d85044ba7a7ae53f3362d634&params=min_android_code_version
	 */
	public static final String URL_SYSTEM_CONFIG = baseURL+"wmh360/json/msg/getsysconfig.jsp?clientkey=bb7c1386d85044ba7a7ae53f3362d634&params=%s";
	
	public static final String URL_PAGE = baseURL+"wmh360/json/getpagejson.jsp?pageid=%s&appid="+appCode+"&userid=%s";

	/**
	 * 获取融云token
	 */
	public static final String URL_RC_TOKEN = baseURL+"wmh360/json/getrongToken.jsp?userid=%s&username=%s&appid="+appCode+"&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 获取用户详情
	 */
	public static final String URL_USER_INFO =  baseURL+"wmh360/json/user/getuser.jsp?userid=%s&appcode="+appCode+"&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 获取用户基本信息，userid，username，truename，photourl，appclientcode
	 */
	public static final String URL_USER_BASIC_INFO = baseURL+"wmh360/json/user/getbasicinfolist.jsp?userids=%s&appcode="+appCode+"&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 发送邀请短信
	 */
	public static final String URL_SEND_INVITE_SMS = baseURL+"wmh360/json/msg/sendinvitesms.jsp?userids=%s&appcode="+appCode+"&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
	 * 上传头像
	 * "%@wmh360/json/user/uploadavatar.jsp?userid=%@&appcode=%@&clientkey=bb7c1386d85044ba7a7ae53f3362d634
	 */
	public static final String URL_UPLOAD_AVATAR = baseURL+"wmh360/json/user/uploadavatar.jsp?userid=%s&appcode="+appCode+"&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

//	NSString * const kURLMyFilePage = @"%@wmh360/json/user/getfilelist.jsp?appcode=%@&curp=%d&perp=20&username=%@&clientkey=bb7c1386d85044ba7a7ae53f3362d634";
	public static final String URL_MY_FILE_PAGE = baseURL+"wmh360/json/user/getfilelist.jsp?appcode="+appCode+"&curp=%d&perp=20&username=%s&clientkey=bb7c1386d85044ba7a7ae53f3362d634";

	/**
		删除文件
		http://60.205.140.176:8088/wmh360/json/user/delfile.jsp?appcode=D20&clientkey=bb7c1386d85044ba7a7ae53f3362d634&fileid=1
	 */
	public static final String URL_MY_FILE_DELETE = baseURL+"wmh360/json/user/delfile.jsp?appcode="+appCode+"&clientkey=bb7c1386d85044ba7a7ae53f3362d634&fileid=%s";

	/**
	 * 搜索文件
	 * http://60.205.140.176:8088/wmh360/json/user/searchfile.jsp?filename=国&username=xdhryce&curp=1&perp=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634&appcode=D20
	 */
	public static final String URL_MY_FILE_SEARCH = baseURL+"wmh360/json/user/searchfile.jsp?filename=%s&username=%s&curp=%d&perp=20&clientkey=bb7c1386d85044ba7a7ae53f3362d634&appcode="+appCode;
}
