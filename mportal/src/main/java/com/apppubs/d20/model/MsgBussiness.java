package com.apppubs.d20.model;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.json.JSONException;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.Msg;
import com.apppubs.d20.bean.MsgRecord;
import com.apppubs.d20.bean.ServiceNOInfo;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.http.AjaxCallBack;
import com.google.gson.reflect.TypeToken;
import com.apppubs.d20.bean.ServiceNo;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.WebUtils;
import com.apppubs.d20.util.http.AjaxParams;
import com.apppubs.d20.util.http.FinalHttp;
import com.orm.SugarRecord;

/**
 * 消息业务类
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年3月16日 by zhangwen create
 * 
 */
public class MsgBussiness extends BaseBussiness {

	private static MsgBussiness sMessageBussiness;
	private Context mContext;
	private MsgBussiness(Context context) {
		mContext = context;
	}

	public static MsgBussiness getInstance(Context context) {

		if (sMessageBussiness == null) {
			synchronized (MsgBussiness.class) {
				if(sMessageBussiness==null){
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
			final BussinessCallbackCommon<List<Msg>> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				String url = String.format(URLs.URL_CHAT_LIST, receiverUsername,senderUsername,"","1","30");
				try {
					
					String responseString = WebUtils.requestWithGet(url);
					JSONResult jr = JSONResult.compile(responseString);
					if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
						
						Type msgListType = new TypeToken<List<Msg>>() {}.getType();
						List<Msg> list = WebUtils.gson.fromJson(jr.result, msgListType);
						Comparator<Object> comparator = new Comparator<Object>() {

							@Override
							public int compare(Object lhs, Object rhs) {
								Msg lMsg = (Msg) lhs;
								Msg rMsg = (Msg) rhs;
								return lMsg.getSendTime().compareTo(rMsg.getSendTime());
							}
						};
						Collections.sort(list, comparator);
						sHandler.post(new OnDoneRun<List<Msg>>(callback, list));
					}else{
						sHandler.post(new OnExceptionRun<List<Msg>>(callback));
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
	public Future<?> getChatGroupChatList(final String receiverUsername, final String groupid,final String startTimeStr,
			final BussinessCallbackCommon<List<Msg>> callback) {
		
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
				String url = String.format(URLs.URL_CHAT_LIST, receiverUsername,"",groupid,"1","30",encodedTimeStr);
				try {
					
					String responseString = WebUtils.requestWithGet(url);
					JSONResult jr = JSONResult.compile(responseString);
					if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
						
						Type msgListType = new TypeToken<List<Msg>>() {}.getType();
						List<Msg> list = WebUtils.gson.fromJson(jr.result, msgListType);
						Comparator<Object> comparator = new Comparator<Object>() {
							
							@Override
							public int compare(Object lhs, Object rhs) {
								Msg lMsg = (Msg) lhs;
								Msg rMsg = (Msg) rhs;
								return lMsg.getSendTime().compareTo(rMsg.getSendTime());
							}
						};
						Collections.sort(list, comparator);
						sHandler.post(new OnDoneRun<List<Msg>>(callback, list));
					}else{
						sHandler.post(new OnExceptionRun<List<Msg>>(callback));
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
	 * @param senderUsername
	 *            发送者用户名
	 * @param receiverUsername
	 *            接受者用户名
	 * @param content
	 *            内容
	 * @param callback
	 * @return
	 * 
	 */
	public Future<?> sendTextMsg(final String senderUsername, final String receiverUsername, final String groupId,final String content,
			final BussinessCallbackCommon<Object> callback) {

		Future<?> f = sDefaultExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					String encodeContent = URLEncoder.encode(content, "UTF-8");
					String url = String.format(URLs.URL_CHAT_SEND_MSG,groupId,senderUsername,receiverUsername,encodeContent,Msg.TYPE_CONTENT_TEXT+"","",""); 
					String result = WebUtils.requestWithGet(url);
					JSONResult jr = JSONResult.compile(result);
					if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
						sHandler.post(new OnDoneRun<Object>(callback, null));
					}else{
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

	public void makeRecord(int type, final String targetUsernameOrId, final String content, boolean addUnreadNum) {
		if (type == MsgRecord.TYPE_CHAT) {
			User receiver = SugarRecord.findByProperty(User.class, "USERNAME", targetUsernameOrId);
			MsgRecord msgRecord = SugarRecord.findByProperty(MsgRecord.class, "SOURCE_USERNAME_OR_ID",
					receiver.getUsername());
			if (msgRecord == null) {
				msgRecord = new MsgRecord();
				msgRecord.setSourceUsernameOrId(targetUsernameOrId);
				msgRecord.setTitle(receiver.getTrueName());
				msgRecord.setSubTitle(content);
				msgRecord.setUpdateTime(new Date());
				msgRecord.setUnreadNum(addUnreadNum ? 1 : 0);
				msgRecord.setType(MsgRecord.TYPE_CHAT);
				msgRecord.save();
			} else if (addUnreadNum) {

				SugarRecord.updateById(
						MsgRecord.class,
						msgRecord.getId(),
						new String[] { "TITLE", "SUB_TITLE", "UPDATE_TIME", "UNREAD_NUM" },
						new String[] { receiver.getTrueName(), content, new Date().getTime() + "",
								(msgRecord.getUnreadNum() + 1) + "" });

			} else {
				SugarRecord.updateById(MsgRecord.class, msgRecord.getId(), new String[] { "TITLE", "SUB_TITLE",
						"UPDATE_TIME" }, new String[] { receiver.getTrueName(), content, new Date().getTime() + "" });
			}

		} else if (type == MsgRecord.TYPE_SERVICE) {
			MsgRecord msgRecord = SugarRecord.findByProperty(MsgRecord.class, "SOURCE_USERNAME_OR_ID",
					targetUsernameOrId);
			ServiceNo sn = SugarRecord.findById(ServiceNo.class, targetUsernameOrId);

			if (sn == null) {
				LogM.log(this.getClass(), "makeRecord ServiceNo为空");
				return;
			}
			if (msgRecord == null) {
				msgRecord = new MsgRecord();
				msgRecord.setSourceUsernameOrId(targetUsernameOrId);
				msgRecord.setTitle(sn.getName());
				msgRecord.setSubTitle(content);
				msgRecord.setUpdateTime(new Date());
				msgRecord.setIcon(sn.getPicURL());
				msgRecord.setUnreadNum(addUnreadNum?1:0);
				msgRecord.setType(MsgRecord.TYPE_SERVICE);
				msgRecord.save();
			} else if (addUnreadNum) {

				SugarRecord.updateById(MsgRecord.class, msgRecord.getId(), new String[] { "TITLE", "SUB_TITLE",
						"UPDATE_TIME", "UNREAD_NUM" }, new String[] { sn.getName(), content, new Date().getTime() + "",
						(msgRecord.getUnreadNum() + 1) + "" });

			} else {
				SugarRecord.updateById(MsgRecord.class, msgRecord.getId(), new String[] { "TITLE", "SUB_TITLE",
						"UPDATE_TIME" }, new String[] { sn.getName(), content, new Date().getTime() + "" });
			}
		}
	}

	public void makeRecord(String targetUsername, Msg msg, boolean addUnreadNum) {
		int msgType = msg.getType();
		if (msgType == Msg.TYPE_CHAT) {

			if (msg.getContentType() == Msg.TYPE_CONTENT_TEXT) {

				makeRecord(MsgRecord.TYPE_CHAT, msg.getSenderId(), msg.getContent(), addUnreadNum);
			} else if (msg.getContentType() == Msg.TYPE_CONTENT_IMAGE) {
				makeRecord(MsgRecord.TYPE_CHAT, msg.getSenderId(), "[图片]", addUnreadNum);
			} else if (msg.getContentType() == Msg.TYPE_CONTENT_SOUND) {
				makeRecord(MsgRecord.TYPE_CHAT, msg.getSenderId(), "[语音]", addUnreadNum);
			}
		} else if (msgType == Msg.TYPE_SYSTEM||msgType == Msg.TYPE_THIRD_PARTY||msgType==Msg.TYPE_CMS) {
			makeRecord(MsgRecord.TYPE_SERVICE, msg.getSenderId(), msg.getContent(), addUnreadNum);
		}
	}

	public void makeRecord(String targetUsername, Msg msg) {
		makeRecord(targetUsername, msg, false);
	}

	/**
	 * 清除某记录的未阅读数
	 */
	public void cleanUnread(String targetUsernameOrId) {
		SugarRecord.update(MsgRecord.class, "UNREAD_NUM", "0", "SOURCE_USERNAME_OR_ID = ?",
				new String[] { targetUsernameOrId });
	}

	public Future<?> sendPicMsg(final String senderUsername, final String receiverUsername, final String src) {
		makeRecord(MsgRecord.TYPE_CHAT, receiverUsername, "[图片]");
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				String uploadURL = URLs.URL_UPLOAD;
				String picURL = WebUtils.uploadFile(new File(src), "zipfile", uploadURL);
				LogM.log(this.getClass(), "上传成功结果是：" + picURL);

				try {

//					String url = URLs.URL_CHAT_SEND + "&receiver=" + receiverUsername + "&sender=" + senderUsername
//							+ "&content=" + "&contentType=" + Msg.TYPE_CONTENT_IMAGE + "&fileName=" + picURL + "";
					String url = String.format(URLs.URL_CHAT_SEND_MSG,"",senderUsername,receiverUsername,URLEncoder.encode("图片", "utf-8"),Msg.TYPE_CONTENT_IMAGE+"",URLEncoder.encode(picURL.trim(),"UTF-8"),"");
				
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
	public Future<?> sendGroupPicMsg(final String senderUsername, final String groupid, final String src) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {
			
			@Override
			public void run() {
				
				String uploadURL = URLs.URL_UPLOAD;
				String picURL = WebUtils.uploadFile(new File(src), "zipfile", uploadURL);
				LogM.log(this.getClass(), "上传成功结果是：" + picURL);
				
				try {
					
//					String url = URLs.URL_CHAT_SEND + "&receiver=" + receiverUsername + "&sender=" + senderUsername
//							+ "&content=" + "&contentType=" + Msg.TYPE_CONTENT_IMAGE + "&fileName=" + picURL + "";
					String url = String.format(URLs.URL_CHAT_SEND_MSG,groupid,senderUsername,"",URLEncoder.encode("图片", "utf-8"),Msg.TYPE_CONTENT_IMAGE+"",URLEncoder.encode(picURL.trim(),"UTF-8"),"");
					
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
	public Future<?> sendVideoMsg(final String senderUsername, final String receiverUsername, final String src,final BussinessCallbackCommon<Object> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					String uploadURL = URLs.URL_UPLOAD;
//					String picURL = WebUtils.uploadFile(new File(src), "zipfile", uploadURL);
					
					  AjaxParams params = new AjaxParams();
					  params.put("zipfile", new File(src)); // 上传文件

					  FinalHttp fh = new FinalHttp();
					  fh.post(uploadURL, params, new AjaxCallBack<String>() {
						  public void onSuccess(String t) {
							  FinalHttp fh = new FinalHttp();

								try {
//									url = URLs.URL_CHAT_SEND + "&receiver=" + receiverUsername + "&sender=" + senderUsername
//											+ "&content=" + "&contentType=" + Msg.TYPE_CONTENT_VIDEO + "&fileName=" + URLEncoder.encode(t, "utf-8") + "";
									String url = String.format(URLs.URL_CHAT_SEND_MSG,"",senderUsername,receiverUsername,URLEncoder.encode("[视频]","utf-8"),Msg.TYPE_CONTENT_VIDEO+"",URLEncoder.encode(t.trim(), "utf-8"),"");
									fh.get(url, new AjaxCallBack<String>(){
										public void onSuccess(String t) {
											sHandler.post(new OnDoneRun<Object>(callback, null));
										};
									});
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							  
								
						  };
					});
//					LogM.log(this.getClass(), "上传成功结果是：" + picURL);
					
		
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Object>(callback));
				}
			}
			
		});
		
		return f;
	}
	public Future<?> sendGroupVideoMsg(final String senderUsername, final String groupid, final String src,final BussinessCallbackCommon<Object> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					String uploadURL = URLs.URL_UPLOAD;
//					String picURL = WebUtils.uploadFile(new File(src), "zipfile", uploadURL);
					
					AjaxParams params = new AjaxParams();
					params.put("zipfile", new File(src)); // 上传文件
					
					FinalHttp fh = new FinalHttp();
					fh.post(uploadURL, params, new AjaxCallBack<String>() {
						public void onSuccess(String t) {
							FinalHttp fh = new FinalHttp();
							
							try {
//									url = URLs.URL_CHAT_SEND + "&receiver=" + receiverUsername + "&sender=" + senderUsername
//											+ "&content=" + "&contentType=" + Msg.TYPE_CONTENT_VIDEO + "&fileName=" + URLEncoder.encode(t, "utf-8") + "";
								String url = String.format(URLs.URL_CHAT_SEND_MSG,groupid,senderUsername,"",URLEncoder.encode("[视频]","utf-8"),Msg.TYPE_CONTENT_VIDEO+"",URLEncoder.encode(t.trim(), "utf-8"),"");
								fh.get(url, new AjaxCallBack<String>(){
									public void onSuccess(String t) {
										sHandler.post(new OnDoneRun<Object>(callback, null));
									};
								});
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							
							
						};
					});
//					LogM.log(this.getClass(), "上传成功结果是：" + picURL);
					
					
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Object>(callback));
				}
			}
			
		});
		
		return f;
	}

	public Future<?> sendSoundMsg(final String senderUsername, final String receiverUsername, final String src,
			final int length) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				String uploadURL = URLs.URL_UPLOAD;
				String picURL = WebUtils.uploadFile(new File(src), "zipfile", uploadURL);
				LogM.log(this.getClass(), "上传成功结果是：" + picURL);

				try {

//					String url = URLs.URL_CHAT_SEND + "&receiver=" + receiverUsername + "&sender=" + senderUsername
//							+ "&content=" + "&contentType=" + Msg.TYPE_CONTENT_SOUND + "&length=" + length + ""
//							+ "&fileName=" + picURL;
					String url = String.format(URLs.URL_CHAT_SEND_MSG,"",senderUsername,receiverUsername,URLEncoder.encode("[语音]", "utf-8"),Msg.TYPE_CONTENT_SOUND+"",URLEncoder.encode(picURL.trim(),"UTF-8"),""+length);
					
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
	public Future<?> sendGroupSoundMsg(final String senderUsername, final String groupId, final String src,
			final int length) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {
			
			@Override
			public void run() {
				
				String uploadURL = URLs.URL_UPLOAD;
				String picURL = WebUtils.uploadFile(new File(src), "zipfile", uploadURL);
				LogM.log(this.getClass(), "上传成功结果是：" + picURL);
				
				try {
					
//					String url = URLs.URL_CHAT_SEND + "&receiver=" + receiverUsername + "&sender=" + senderUsername
//							+ "&content=" + "&contentType=" + Msg.TYPE_CONTENT_SOUND + "&length=" + length + ""
//							+ "&fileName=" + picURL;
					String url = String.format(URLs.URL_CHAT_SEND_MSG,groupId,senderUsername,"",URLEncoder.encode("[语音]", "utf-8"),Msg.TYPE_CONTENT_SOUND+"",URLEncoder.encode(picURL.trim(),"UTF-8"),""+length);
					
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

	public List<MsgRecord> listMsgRecord() {
		return SugarRecord.find(MsgRecord.class, null, null, null, "update_time desc", null);
	}

	public Future<?> getServiceNoList(final BussinessCallbackCommon<List<ServiceNo>> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					List<ServiceNo> snl = WebUtils.requestList(URLs.URL_SERVICE_LIST, ServiceNo.class);
					SugarRecord.deleteAll(ServiceNo.class);
					for (ServiceNo s : snl) {
						s.save();
					}
					sHandler.post(new OnDoneRun<List<ServiceNo>>(callback, snl));
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

	public ServiceNo getServiceNoById(String id) {
		return SugarRecord.findById(ServiceNo.class, id);
	}

	public Future<?> getAloneServiceList(final String serviceid, final BussinessCallbackCommon<List<ServiceNOInfo>> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					// http://www.wmh360.com/wmh360/json/msg/getserviceinfolist.jsp?appcode=U1433417616429&service_id=1433580152045&username=ceshi6&userid=&curp=1&perp=10&clientkey=bb7c1386d85044ba7a7ae53f3362d634
					String url = URLs.URL_SERVICE_MESSAGE_INFO_LIST + "&service_id="+serviceid+"&username="+ AppContext.getInstance(mContext).getCurrentUser().getUsername()+"&userid="+ AppContext.getInstance(mContext).getCurrentUser().getUserId()
					+"&curp=1&perp=10";
					List<ServiceNOInfo> snl = WebUtils.requestList(url, ServiceNOInfo.class);
					sHandler.post(new OnDoneRun<List<ServiceNOInfo>>(callback, snl));
				} catch (Exception e) {

					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<ServiceNOInfo>>(callback));
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
			final BussinessCallbackCommon<String> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					String str = WebUtils.requestAloneRequest(URLs.URL_SERVICE_ATTENTION + "&service_id=" + serviceid
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
			final BussinessCallbackCommon<String> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					String str = WebUtils.requestAloneRequest(URLs.URL_UNATTENTION_SERVICE + "&service_id=" + serviceid
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
	public Future<?> getUserServiceNoList(final String username, final BussinessCallbackCommon<List<ServiceNo>> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					List<ServiceNo> snl = WebUtils.requestList(URLs.URL_USERSERVICELIST + "&username=" + username,
							ServiceNo.class);
					sHandler.post(new OnDoneRun<List<ServiceNo>>(callback, snl));
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
	 * @return
	 */
	public Future<?> getSubcribableServiceNoList(final BussinessCallbackCommon<List<ServiceNo>> callback){
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					List<ServiceNo> snl = WebUtils.requestList(URLs.URL_SERVICE_NO_SUBSCRIBEABLE,ServiceNo.class);
					sHandler.post(new OnDoneRun<List<ServiceNo>>(callback, snl));
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
	public Future<?> writePicUrlSD(final String pinurl, final File file, final BussinessCallbackCommon<String> callback) {
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
	
	
	public void initializeMsgRecordList(){
		List<ServiceNo> serviceNoList = SugarRecord.listAll(ServiceNo.class);
		
		for(ServiceNo sn:serviceNoList){
			if(!sn.isAllowSubscribe()){
				
				makeRecord(MsgRecord.TYPE_SERVICE, sn.getId(), "",false);
			}
		}
	}
	

}
