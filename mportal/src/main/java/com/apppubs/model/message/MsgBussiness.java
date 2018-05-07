package com.apppubs.model.message;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.apppubs.AppContext;
import com.apppubs.bean.TMsg;
import com.apppubs.bean.TMsgRecord;
import com.apppubs.bean.TServiceNOInfo;
import com.apppubs.bean.TServiceNo;
import com.apppubs.bean.TUser;
import com.apppubs.bean.UserInfo;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.constant.URLs;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.BaseBiz;
import com.apppubs.net.APHttpClient;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.ui.message.MyFilePlugin;
import com.apppubs.util.JSONResult;
import com.apppubs.util.LogM;
import com.apppubs.util.WebUtils;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imkit.widget.provider.LocationPlugin;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 消息业务类
 * <p>
 * Copyright (c) heaven Inc.
 * <p>
 * Original Author: zhangwen
 * <p>
 * ChangeLog: 2015年3月16日 by zhangwen create
 */
public class MsgBussiness extends BaseBiz {

    private static MsgBussiness sMessageBussiness;
    private Context mContext;

    private MsgBussiness(Context context) {
        super(context);
        mContext = context;
    }

    public static MsgBussiness getInstance(Context context) {

        if (sMessageBussiness == null) {
            synchronized (MsgBussiness.class) {
                if (sMessageBussiness == null) {
                    sMessageBussiness = new MsgBussiness(context);
                }
            }

        }

        return sMessageBussiness;
    }

    /**
     * 获取聊天消息列表
     *
     * @param callback
     * @return
     */
    public Future<?> getChatList(final String receiverUsername, final String senderUsername,
                                 final IAPCallback<List<TMsg>> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                String url = String.format(URLs.URL_CHAT_LIST, URLs.baseURL, URLs.appCode,
						receiverUsername, senderUsername, "", "1", "30");
                try {

                    String responseString = WebUtils.requestWithGet(url);
                    JSONResult jr = JSONResult.compile(responseString);
                    if (jr.code == JSONResult.RESULT_CODE_SUCCESS) {

                        Type msgListType = new TypeToken<List<TMsg>>() {
                        }.getType();
                        List<TMsg> list = WebUtils.gson.fromJson(jr.result, msgListType);
                        Comparator<Object> comparator = new Comparator<Object>() {

                            @Override
                            public int compare(Object lhs, Object rhs) {
                                TMsg lMsg = (TMsg) lhs;
                                TMsg rMsg = (TMsg) rhs;
                                return lMsg.getSendTime().compareTo(rMsg.getSendTime());
                            }
                        };
                        Collections.sort(list, comparator);
                        sHandler.post(new OnDoneRun<List<TMsg>>(callback, list));
                    } else {
                        sHandler.post(new OnExceptionRun<List<TMsg>>(callback));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        return f;
    }

    public Future<?> getChatGroupChatList(final String receiverUsername, final String groupid,
										  final String startTimeStr,
                                          final IAPCallback<List<TMsg>> callback) {

        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                String encodedTimeStr = "";
                try {
                    encodedTimeStr = URLEncoder.encode(startTimeStr, "utf-8");
                } catch (UnsupportedEncodingException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                String url = String.format(URLs.URL_CHAT_LIST, URLs.baseURL, URLs.appCode,
						receiverUsername, "", groupid, "1", "30", encodedTimeStr);
                try {

                    String responseString = WebUtils.requestWithGet(url);
                    JSONResult jr = JSONResult.compile(responseString);
                    if (jr.code == JSONResult.RESULT_CODE_SUCCESS) {

                        Type msgListType = new TypeToken<List<TMsg>>() {
                        }.getType();
                        List<TMsg> list = WebUtils.gson.fromJson(jr.result, msgListType);
                        Comparator<Object> comparator = new Comparator<Object>() {

                            @Override
                            public int compare(Object lhs, Object rhs) {
                                TMsg lMsg = (TMsg) lhs;
                                TMsg rMsg = (TMsg) rhs;
                                return lMsg.getSendTime().compareTo(rMsg.getSendTime());
                            }
                        };
                        Collections.sort(list, comparator);
                        sHandler.post(new OnDoneRun<List<TMsg>>(callback, list));
                    } else {
                        sHandler.post(new OnExceptionRun<List<TMsg>>(callback));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        return f;
    }

    /**
     * 发送消息
     *
     * @param senderUsername   发送者用户名
     * @param receiverUsername 接受者用户名
     * @param content          内容
     * @param callback
     * @return
     */
    public Future<?> sendTextMsg(final String senderUsername, final String receiverUsername,
								 final String groupId, final String content,
                                 final IAPCallback<Object> callback) {

        Future<?> f = sDefaultExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String encodeContent = URLEncoder.encode(content, "UTF-8");
                    String url = String.format(URLs.URL_CHAT_SEND_MSG, URLs.baseURL, URLs
							.appCode, groupId, senderUsername, receiverUsername, encodeContent,
							TMsg.TYPE_CONTENT_TEXT + "", "", "");
                    String result = WebUtils.requestWithGet(url);
                    JSONResult jr = JSONResult.compile(result);
                    if (jr.code == JSONResult.RESULT_CODE_SUCCESS) {
                        sHandler.post(new OnDoneRun<Object>(callback, null));
                    } else {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        return f;
    }

    private void makeRecord(int type, final String targetUsernameOrId, final String content) {
        makeRecord(type, targetUsernameOrId, content, false);
    }

    public void makeRecord(int type, final String targetUsernameOrId, final String content,
						   boolean addUnreadNum) {
        if (type == TMsgRecord.TYPE_CHAT) {
            TUser receiver = SugarRecord.findByProperty(TUser.class, "USERNAME",
					targetUsernameOrId);
            TMsgRecord msgRecord = SugarRecord.findByProperty(TMsgRecord.class,
					"SOURCE_USERNAME_OR_ID",
                    receiver.getUsername());
            if (msgRecord == null) {
                msgRecord = new TMsgRecord();
                msgRecord.setSourceUsernameOrId(targetUsernameOrId);
                msgRecord.setTitle(receiver.getTrueName());
                msgRecord.setSubTitle(content);
                msgRecord.setUpdateTime(new Date());
                msgRecord.setUnreadNum(addUnreadNum ? 1 : 0);
                msgRecord.setType(TMsgRecord.TYPE_CHAT);
                msgRecord.save();
            } else if (addUnreadNum) {

                SugarRecord.updateById(
                        TMsgRecord.class,
                        msgRecord.getId(),
                        new String[]{"TITLE", "SUB_TITLE", "UPDATE_TIME", "UNREAD_NUM"},
                        new String[]{receiver.getTrueName(), content, new Date().getTime() + "",
                                (msgRecord.getUnreadNum() + 1) + ""});

            } else {
                SugarRecord.updateById(TMsgRecord.class, msgRecord.getId(), new String[]{"TITLE",
						"SUB_TITLE",
                        "UPDATE_TIME"}, new String[]{receiver.getTrueName(), content, new Date()
						.getTime() + ""});
            }

        } else if (type == TMsgRecord.TYPE_SERVICE) {
            TMsgRecord msgRecord = SugarRecord.findByProperty(TMsgRecord.class,
					"SOURCE_USERNAME_OR_ID",
                    targetUsernameOrId);
            TServiceNo sn = SugarRecord.findById(TServiceNo.class, targetUsernameOrId);

            if (sn == null) {
                LogM.log(this.getClass(), "makeRecord ServiceNo为空");
                return;
            }
            if (msgRecord == null) {
                msgRecord = new TMsgRecord();
                msgRecord.setSourceUsernameOrId(targetUsernameOrId);
                msgRecord.setTitle(sn.getName());
                msgRecord.setSubTitle(content);
                msgRecord.setUpdateTime(new Date());
                msgRecord.setIcon(sn.getPicURL());
                msgRecord.setUnreadNum(addUnreadNum ? 1 : 0);
                msgRecord.setType(TMsgRecord.TYPE_SERVICE);
                msgRecord.save();
            } else if (addUnreadNum) {

                SugarRecord.updateById(TMsgRecord.class, msgRecord.getId(), new String[]{"TITLE",
						"SUB_TITLE",
                        "UPDATE_TIME", "UNREAD_NUM"}, new String[]{sn.getName(), content, new
						Date().getTime() + "",
                        (msgRecord.getUnreadNum() + 1) + ""});

            } else {
                SugarRecord.updateById(TMsgRecord.class, msgRecord.getId(), new String[]{"TITLE",
						"SUB_TITLE",
                        "UPDATE_TIME"}, new String[]{sn.getName(), content, new Date().getTime()
						+ ""});
            }
        }
    }

    public void makeRecord(String targetUsername, TMsg msg, boolean addUnreadNum) {
        int msgType = msg.getType();
        if (msgType == TMsg.TYPE_CHAT) {

            if (msg.getContentType() == TMsg.TYPE_CONTENT_TEXT) {

                makeRecord(TMsgRecord.TYPE_CHAT, msg.getSenderId(), msg.getContent(), addUnreadNum);
            } else if (msg.getContentType() == TMsg.TYPE_CONTENT_IMAGE) {
                makeRecord(TMsgRecord.TYPE_CHAT, msg.getSenderId(), "[图片]", addUnreadNum);
            } else if (msg.getContentType() == TMsg.TYPE_CONTENT_SOUND) {
                makeRecord(TMsgRecord.TYPE_CHAT, msg.getSenderId(), "[语音]", addUnreadNum);
            }
        } else if (msgType == TMsg.TYPE_SYSTEM || msgType == TMsg.TYPE_THIRD_PARTY || msgType ==
				TMsg.TYPE_CMS) {
            makeRecord(TMsgRecord.TYPE_SERVICE, msg.getSenderId(), msg.getContent(), addUnreadNum);
        }
    }

    public void makeRecord(String targetUsername, TMsg msg) {
        makeRecord(targetUsername, msg, false);
    }

    /**
     * 清除某记录的未阅读数
     */
    public void cleanUnread(String targetUsernameOrId) {
        SugarRecord.update(TMsgRecord.class, "UNREAD_NUM", "0", "SOURCE_USERNAME_OR_ID = ?",
                new String[]{targetUsernameOrId});
    }

    public Future<?> sendPicMsg(final String senderUsername, final String receiverUsername, final
	String src) {
        makeRecord(TMsgRecord.TYPE_CHAT, receiverUsername, "[图片]");
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {

                String uploadURL = URLs.baseURL + URLs.URL_UPLOAD;
                String picURL = WebUtils.uploadFile(new File(src), "zipfile", uploadURL);
                LogM.log(this.getClass(), "上传成功结果是：" + picURL);

                try {

//					String url = URLs.URL_CHAT_SEND + "&receiver=" + receiverUsername + "&sender="
// + senderUsername
//							+ "&content=" + "&contentType=" + TMsg.TYPE_CONTENT_IMAGE +
// "&fileName=" + picURL + "";
                    String url = String.format(URLs.URL_CHAT_SEND_MSG, URLs.baseURL, URLs
							.appCode, "", senderUsername, receiverUsername, URLEncoder.encode
							("图片", "utf-8"), TMsg.TYPE_CONTENT_IMAGE + "", URLEncoder.encode
							(picURL.trim(), "UTF-8"), "");

                    String result = WebUtils.requestWithGet(url);

                    sHandler.post(null);
                    LogM.log(this.getClass(), "发送完成 发送结果：" + result);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        return f;
    }

    public Future<?> sendGroupPicMsg(final String senderUsername, final String groupid, final
	String src) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {

                String uploadURL = URLs.baseURL + URLs.URL_UPLOAD;
                String picURL = WebUtils.uploadFile(new File(src), "zipfile", uploadURL);
                LogM.log(this.getClass(), "上传成功结果是：" + picURL);

                try {

//					String url = URLs.URL_CHAT_SEND + "&receiver=" + receiverUsername + "&sender="
// + senderUsername
//							+ "&content=" + "&contentType=" + TMsg.TYPE_CONTENT_IMAGE +
// "&fileName=" + picURL + "";
                    String url = String.format(URLs.URL_CHAT_SEND_MSG, URLs.baseURL, URLs
							.appCode, groupid, senderUsername, "", URLEncoder.encode("图片",
							"utf-8"), TMsg.TYPE_CONTENT_IMAGE + "", URLEncoder.encode(picURL.trim
							(), "UTF-8"), "");

                    String result = WebUtils.requestWithGet(url);

                    sHandler.post(null);
                    LogM.log(this.getClass(), "发送完成 发送结果：" + result);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        return f;
    }

    public Future<?> sendVideoMsg(final String senderUsername, final String receiverUsername,
								  final String src, final IAPCallback<Object> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {

            }

        });

        return f;
    }

    public Future<?> sendGroupVideoMsg(final String senderUsername, final String groupid, final
	String src, final IAPCallback<Object> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
            }

        });

        return f;
    }

    public Future<?> sendGroupSoundMsg(final String senderUsername, final String groupId, final
	String src,
                                       final int length) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {

                String uploadURL = URLs.baseURL + URLs.URL_UPLOAD;
                String picURL = WebUtils.uploadFile(new File(src), "zipfile", uploadURL);
                LogM.log(this.getClass(), "上传成功结果是：" + picURL);

                try {

//					String url = URLs.URL_CHAT_SEND + "&receiver=" + receiverUsername + "&sender="
// + senderUsername
//							+ "&content=" + "&contentType=" + TMsg.TYPE_CONTENT_SOUND + "&length="
// + length + ""
//							+ "&fileName=" + picURL;
                    String url = String.format(URLs.URL_CHAT_SEND_MSG, URLs.baseURL, URLs
							.appCode, groupId, senderUsername, "", URLEncoder.encode("[语音]",
							"utf-8"), TMsg.TYPE_CONTENT_SOUND + "", URLEncoder.encode(picURL.trim
							(), "UTF-8"), "" + length);

                    String result = WebUtils.requestWithGet(url);

                    sHandler.post(null);
                    LogM.log(this.getClass(), "发送完成 发送结果：" + result);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        return f;
    }

    public List<TMsgRecord> listMsgRecord() {
        return SugarRecord.find(TMsgRecord.class, null, null, null, "update_time desc", null);
    }

    public TServiceNo getServiceNoById(String id) {
        return SugarRecord.findById(TServiceNo.class, id);
    }

    public Future<?> getAloneServiceList(final String serviceid, final
	IAPCallback<List<TServiceNOInfo>> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    // http://www.wmh360.com/wmh360/json/msg/getserviceinfolist
					// .jsp?appcode=U1433417616429&service_id=1433580152045&username=ceshi6&userid
					// =&curp=1&perp=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634
                    String url = String.format(URLs.URL_SERVICE_MESSAGE_INFO_LIST, URLs.baseURL,
							URLs.appCode) + "&service_id=" + serviceid + "&username=" + AppContext
							.getInstance(mContext).getCurrentUser().getUsername() + "&userid=" +
							AppContext.getInstance(mContext).getCurrentUser().getUserId()
                            + "&curp=1&perp=10";
                    List<TServiceNOInfo> snl = WebUtils.requestList(url, TServiceNOInfo.class);
                    sHandler.post(new OnDoneRun<List<TServiceNOInfo>>(callback, snl));
                } catch (Exception e) {

                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<List<TServiceNOInfo>>(callback));
                }
            }

        });

        return f;
    }

    /**
     * 服务号加关注
     *
     * @param serviceid
     * @param username
     * @param callback
     * @return
     */
    public Future<?> getServiceAttention(final String serviceid, final String username,
                                         final IAPCallback<String> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    String str = WebUtils.requestAloneRequest(String.format(URLs
							.URL_SERVICE_ATTENTION, URLs.baseURL, URLs.appCode) + "&service_id=" +
							serviceid
                            + "&username=" + username);

                    sHandler.post(new OnDoneRun<String>(callback, str));
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

    /**
     * 取消关注订阅号
     */
    public Future<?> getServiceUnAttention(final String serviceid, final String username,
                                           final IAPCallback<String> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    String str = WebUtils.requestAloneRequest(URLs.URL_UNATTENTION_SERVICE +
							"&service_id=" + serviceid
                            + "&username=" + username);

                    sHandler.post(new OnDoneRun<String>(callback, str));
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

    /**
     * 获得某个用户所有的服务号
     *
     * @param callback
     * @return
     */
    public Future<?> getUserServiceNoList(final String username, final
	IAPCallback<List<TServiceNo>> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    List<TServiceNo> snl = WebUtils.requestList(String.format(URLs
									.URL_USERSERVICELIST, URLs.baseURL, URLs.appCode) +
									"&username=" + username,
                            TServiceNo.class);
                    sHandler.post(new OnDoneRun<List<TServiceNo>>(callback, snl));
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


    /**
     * 获得可订阅的服务号列表
     *
     * @return
     */
    public Future<?> getSubcribableServiceNoList(final IAPCallback<List<TServiceNo>> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    List<TServiceNo> snl = WebUtils.requestList(String.format(URLs
							.URL_SERVICE_NO_SUBSCRIBEABLE, URLs.baseURL, URLs.appCode), TServiceNo
							.class);
                    sHandler.post(new OnDoneRun<List<TServiceNo>>(callback, snl));
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

    /**
     * 把一个url的网络图片保存在本地
     */
    public Future<?> writePicUrlSD(final String pinurl, final File file, final IAPCallback<String>
            callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    WebUtils.saveRemoteFile(pinurl, file);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sHandler.post(new OnDoneRun<String>(callback, ""));
            }

        });

        return f;
    }


    public void initializeMsgRecordList() {
        List<TServiceNo> serviceNoList = SugarRecord.listAll(TServiceNo.class);

        for (TServiceNo sn : serviceNoList) {
            if (!sn.isAllowSubscribe()) {

                makeRecord(TMsgRecord.TYPE_SERVICE, sn.getId(), "", false);
            }
        }
    }


    /**
     * 登录融云
     */
    public void loginRC(final IAPCallback callback) {
        post(new Runnable() {
            @Override
            public void run() {
                String token = mAppContext.getCurrentUser().getRongToken();
                if (token != null) {
                    RongIM.connect(token, new RongIMClient.ConnectCallback() {

                        /**
                         * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                         *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                         */
                        @Override
                        public void onTokenIncorrect() {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onException(new APError(APErrorCode.ONG_TOKEN_ERROR,
                                            "融云token错误"));
                                }
                            });
                        }

                        /**
                         * 连接融云成功
                         * @param userid 当前 token 对应的用户 id
                         */
                        @Override
                        public void onSuccess(String userid) {
                            Log.d("LoginActivity", "--onSuccess" + userid);
                            setMyExtensionModule();
                            UserInfo user = AppContext.getInstance(mContext).getCurrentUser();
                            if (user != null) {
                                Uri uri = user.getAvatarUrl() == null ? null : Uri.parse(user.getAvatarUrl());
                                io.rong.imlib.model.UserInfo userinfo = new io.rong.imlib.model.UserInfo(userid, user.getTrueName(), uri);
                                RongIM.getInstance().setCurrentUserInfo(userinfo);
                                RongIM.getInstance().setMessageAttachedUserInfo(true);
                            }

                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(null);
                                }
                            });

                        }

                        /**
                         * 连接融云失败
                         * @param errorCode 错误码，可到官网 查看错误码对应的注释
                         */
                        @Override
                        public void onError(final RongIMClient.ErrorCode errorCode) {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onException(new APError(APErrorCode.RONG_LOGIN_ERROR, "融云登录错误！code:" + errorCode.getValue()));
                                }
                            });
                        }
                    });
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(new APError(APErrorCode.RONG_TOKEN_EMPTY, "融云token不存在！"));
                        }
                    });
                }
            }
        });

    }


    public void setMyExtensionModule() {
        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new MyExtensionModule());
            }
        }
    }


    public class MyExtensionModule extends DefaultExtensionModule {
        private FilePlugin filePlugin;
        private ImagePlugin imagePlugin;
        private LocationPlugin locationPlugin;
        private MyFilePlugin myFilePlugin;
        List<IPluginModule> pluginModules;

        public MyExtensionModule() {
            pluginModules = new ArrayList<IPluginModule>();
            filePlugin = new FilePlugin();
            imagePlugin = new ImagePlugin();
            locationPlugin = new LocationPlugin();
            myFilePlugin = new MyFilePlugin();
            pluginModules.add(imagePlugin);
//			pluginModules.add(locationPlugin);
            pluginModules.add(myFilePlugin);
        }

        @Override
        public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {

            return pluginModules;
        }

    }

}
