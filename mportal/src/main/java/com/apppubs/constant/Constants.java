package com.apppubs.constant;

public class Constants {


	public static final String REAL_PACKAGE_NAME = "com.apppubs.d20";

	public static final String APP_CONFIG_PARAM_REG_URL = "reg_url";
	public static final String APP_CONFIG_PARAM_FORGET_PWD_URL = "forget_password_url";
	public static final String APP_CONFIG_PARAM_ADBOOK_USER_ICON_FLAGS = "adbook_user_icon_flags";
	public static final String APP_CONFIG_PARAM_USER_ACCOUNT_PWD_FLAGS = "user_account_pwd_flags";
	public static final String APP_CONFIG_PARAM_ADBOOK_ORG_COUNT_FLAG = "adbook_org_count_flag";
	public static final String APP_CONFIG_PARAM_CHAT_FLAG = "chat_flag";
	public static final String APP_CONFIG_ABOUT_PROPERTIES = "about_properties";
	public static final String APP_CONFIG_PDF_DEFAULT_READ_MODE = "pdf_default_read_mode";

	public static final String APP_CONFIG_ADBOOK_USER_URL = "address_userurl";
	public static final String APP_CONFIG_ADBOOK_DEPT_URL = "address_depturl";
	public static final String APP_CONFIG_ADBOOK_USER_DETP_LINK_URL = "address_deptuserurl";


	public static final String CUSTOM_SCHEMA_APPPUBS_NEWS = "apppubsnews";
	public static final String CUSTOM_SCHEMA_APPPUBS = "apppubs";
	
	
	public static final String DEFAULT_SHARED_PREFERENCE_NAME = "mportal_shared_preference";
	public static final String SHAREDPREFERENCE_KEY_DElETED_CHAT_IDS = "delete_chat_ids";
	public static final String SHAREDPREFRERENCE_KEY_DOWNLOAD_REFERENCE = "download_reference";
	public static final String SHAREDPREFRERENCE_KEY_ADDRESS_NAME = "address_name";
	public static final String SHAREDPREFRERENCE_KEY_ADDRESS_CODE = "address_code";
	
	public static final String PAGE_COMPONENT_NAV_DEFAULT = "1001";//默认导航组件
	public static final String PAGE_COMPONENT_TAG = "1003";//标签
	public static final String PAGE_COMPONENT_TAB_WITH_ANCHOR = "1004";//带有锚标记的标签
	public static final String PAGE_COMPONENT_SINGLE_PIC_DEFAULT = "2001";//默认单图组件
	public static final String PAGE_COMPONENT_PIC = "2002";//简单图片组件（只显示一张图）
	public static final String PAGE_COMPONENT_SLIDE_PIC_DEFAULT = "3001";//默认滚动图组件
	public static final String PAGE_COMPONENT_SLIDE_PIC_WITH_PAGE_CONTROL_ONLY = "3002";
	public static final String PAGE_COMPONENT_PIC_TEXT_LIST_DEFAULT = "4001";//默认图文列表组件
	public static final String PAGE_COMPONENT_PIC_TEXT_LIST = "4002";
	public static final String PAGE_COMPONENT_ICON_LIST_DEFAULT = "8001";//默认图标列表组件(四列)
	public static final String PAGE_COMPONENT_ICON_LIST_3_COLUMN = "8002";//默认图标列表组件(三列)
	public static final String PAGE_COMPONENT_ICON_PURE_TEXT_LIST = "8003";//流式圆角关键词
	public static final String PAGE_COMPONENT_ICON_LIST = "8004";
	public static final String PAGE_COMPONENT_PIC_ICON_LIST = "8005";
	public static final String PAGE_COMPONENT_ICON_LIST_VERTICAL = "8010";//默认纵向图标列表
	public static final String PAGE_COMPONENT_HOT_AREA_DEFAULT = "9001";//默认热区组件
	public static final String PAGE_COMPONENT_HOT_AREA_SINGLE_PAGE = "9002";//单页热区组件
	public static final String PAGE_COMPONENT_BLANK_ROW = "11001";//空白行
	public static final String PAGE_COMPONENT_HORIZONTALL_LINE = "11002";//横线
	public static final String PAGE_COMPONENT_DEFAULT_USER_INFO ="11020";//默认用户信息组件
	
	public static final String FILE_NAME_APP_CONFIG = "appconfig";
	public static final String FILE_NAME_SELECTED_NAV_TGABS = "selected_tabs_%s";//已订阅的标签，保存时文件名用page id拼接于末尾

	public static final String APP_FOLDER_NAME = "apppubs";
	public static String APK_FILE_NAME = "mportal.apk";
	
	public static final int MESSAGE_EVENT_INIT_APP = 0;

	public static final boolean IS_DEBUG = true;

	public static final String META_KEY_SERVER_CONTEXT = "SERVER_CONTEXT";

	/**
	 * 接口
	 */
	//其他
	public static final String API_ENTRY = "meap/entry.jsp";
	public static final String API_NAME_APP_INFO = "app_info";
	public static final String API_NAME_MENUS = "menus";
	public static final String API_NAME_PAGE = "page";
	public static final String API_NAME_UPLOAD_FILE = "upload_file";
	public static final String API_NAME_CHECK_VERSION = "check_version";
	public static final String API_NAME_ADBOOK_INFO = "adbook_info";

	//文件管理
	public static final String API_NAME_MYFILE_PAGE = "my_file_page";
	public static final String API_NAME_DEL_MY_FILE = "del_my_file";
	public static final String API_NAME_UPLOAD_MY_FILE = "upload_my_file";
	public static final String API_NAME_SEARCH_MY_FILE = "search_my_file";

	//服务号
	public static final String API_NAME_MY_SERVICENOS = "my_servicenos";
	public static final String API_NAME_SERVICENOS = "servicenos";
	public static final String API_NAME_SERVICENO = "serviceno";
	public static final String API_NAME_SERVICENO_ARTICLE_PAGE = "serviceno_article_page";
	public static final String API_NAME_ALERT_MESSAGES = "alert_messages";
	public static final String API_NAME_MAKR_ALERT_MESSAGE = "mark_alert_message";

	//用户
	public static final String API_NAME_LOGIN_WITH_USERNAME_AND_PWD = "login_with_username_and_pwd";
	public static final String API_NAME_LOGIN_WITH_PHONE = "login_with_phone";
	public static final String API_NAME_LOGIN_WITH_USERNAME = "login_with_username";
	public static final String API_NAME_LOGIN_WITH_ORG = "login_with_org";
	public static final String API_NAME_REQUEST_SENT_VERIFY_CODE = "request_send_verify_code";
	public static final String API_NAME_CONFIRM_VERIFY_CODE = "confirm_verify_code";
	public static final String API_NAME_MODIFY_PWD = "modify_pwd";
	public static final String API_NAME_UPLOAD_AVATAR = "upload_avatar";
	public static final String API_NAME_REQUEST_SENT_INVITE_SMS = "request_send_invite_sms";
	public static final String API_NAME_COMMIT_PUSH_REGISTER_ID = "commit_push_register_id";
	public static final String API_NAME_LOGOUT = "logout";
	public static final String API_NAME_HTTP = "http";
	public static final String API_NAME_USER_BASIC_INFO = "user_basic_infos";

	//cms
	public static final String API_NAME_NEWS_CHANNELS = "channels";
	public static final String API_NAME_NEWS_CHANNEL = "channel";
	public static final String API_NAME_NEWS_ARTICLE_PAGE = "article_page";
	public static final String API_NAME_NEWS_ARTICLE = "article";
	public static final String API_NAME_NEWS_VIDEO_PAGE = "video_page";
	public static final String API_NAME_NEWS_VIDEO = "video";
	public static final String API_NAME_NEWS_AUDIO_PAGE = "audio_page";
	public static final String API_NAME_NEWS_AUDIO = "audio";
	public static final String API_NAME_NEWS_SPECIAL_ARTICLE_PAGE = "special_article_page";
	public static final String API_NAME_NEWS_SPECIAL_ARTICLE = "special_article";
	public static final String API_NAME_NEWS_MAKE_COMMENT = "make_comment";
	public static final String API_NAME_NEWS_PIC_PAGE = "pic_page";
	public static final String API_NAME_NEWS_PIC = "pic";
	public static final String API_NAME_NEWS_COMMENT_PAGE = "comment_page";

	public static final String APPPUBS_PROTOCOL_TYPE_CHANNEL_GROUP = "channelGroup";
	public static final String APPPUBS_PROTOCOL_TYPE_CHANNEL = "channel";
	public static final String APPPUBS_PROTOCOL_TYPE_NEWS_INFO = "newsInfo";
	public static final String APPPUBS_PROTOCOL_TYPE_PAGE = "page";
	public static final String APPPUBS_PROTOCOL_TYPE_ANCHOR_POINTER = "anchorPointer";
	public static final String APPPUBS_PROTOCOL_TYPE_ADDRESS_BOOK = "addressBook";
	public static final String APPPUBS_PROTOCOL_TYPE_SETTING = "setting";
	public static final String APPPUBS_PROTOCOL_TYPE_FAVORITE = "favorite";
	public static final String APPPUBS_PROTOCOL_TYPE_MESSAGE = "message";
	public static final String APPPUBS_PROTOCOL_TYPE_USER_ACCOUNT = "userAccount";
	public static final String APPPUBS_PROTOCOL_TYPE_CLOSE_WINDOW = "closeWindow";
	public static final String APPPUBS_PROTOCOL_TYPE_TEL = "tel";
	public static final String APPPUBS_PROTOCOL_TYPE_HINT= "hint";
	public static final String APPPUBS_PROTOCOL_TYPE_SERVICENO = "serviceno";
	public static final String APPPUBS_PROTOCOL_TYPE_QRCODE = "QRCode";
	public static final String APPPUBS_PROTOCOL_TYPE_EMAIL = "email";
	public static final String APPPUBS_PROTOCOL_TYPE_MY_FILE = "myFile";
	public static final String APPPUBS_PROTOCOL_TYPE_NEWSPAPER = "newspaper";
	public static final String APPPUBS_PROTOCOL_TYPE_CHECK_VERSION = "checkVersion";
	public static final String APPPUBS_PROTOCOL_TYPE_OPEN_SLIDE_MENU = "openSlideMenu";
	public static final String APPPUBS_PROTOCOL_TYPE_LOGOUT = "logout";
}
