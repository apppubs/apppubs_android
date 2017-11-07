package com.apppubs.d20.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.Collection;
import com.apppubs.d20.bean.HeadPic;
import com.apppubs.d20.bean.NewsInfo;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.WebUtils;
import com.google.gson.JsonParseException;
import com.apppubs.d20.bean.NewsChannel;
import com.apppubs.d20.bean.NewsPictureInfo;
import com.apppubs.d20.constant.URLs;
import com.orm.SugarRecord;

public class NewsBussiness extends BaseBussiness {

	private SimpleDateFormat mSdf;


	private static NewsBussiness mNewsBussinessImpl;
	private Context mContext;

	private NewsBussiness(Context context){
		mContext = context;
		mSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.CHINA);
	}

	public static synchronized  NewsBussiness getInstance(Context context) {
		if (mNewsBussinessImpl == null)
			mNewsBussinessImpl = new NewsBussiness(context);
		return mNewsBussinessImpl;

	}

	/**
	 * 获取某个频道下的某一页信息列表，如果获取的是第一页，则刷新数据库,并给此频道的刷新时间赋值 如果取第0页说明是要刷新
	 */
	public Future<?> getNewsInfoPage(final String type, final String channelCode, final int page, final int pageSize,
			final APResultCallback<List<NewsInfo>> callback) {

		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					// 先从数据库获取数据
					List<NewsInfo> infoList = null;
					if (page == 0) {
						if (type == NewsInfo.NEWS_TYPE_NORAML) {

							// 如果page==0，刷新数据库信息
							String url = String.format(URLs.URL_NEWS_LIST_OF_CHANNEL,URLs.baseURL) + "&channelcode=" + channelCode
									+ "&pno=1&pernum=" + pageSize;

							infoList = WebUtils.requestList(url, NewsInfo.class, "info");

							SugarRecord.deleteAll(NewsInfo.class, "CHANNEL_CODE=?", channelCode);
							Date curDate = new Date();
							SugarRecord.update(NewsChannel.class, "LOCAL_LAST_UPDATE_TIME", curDate.getTime() + "",
									"CODE = ?", new String[] { channelCode });
							saveList(infoList, channelCode);
						} else if (type == NewsInfo.NEWS_TYPE_PICTURE) {
							// 如果page==0，刷新数据库信息
							String url = String.format(URLs.URL_PIC_LIST,URLs.baseURL) + "&channelcode=" + channelCode + "&pno=1&pernum="
									+ pageSize;
							List<NewsPictureInfo> picList = WebUtils.requestList(url, NewsPictureInfo.class, "data");

							SugarRecord.deleteAll(NewsInfo.class, "CHANNEL_CODE=?", channelCode);
							Date curDate = new Date();
							SugarRecord.update(NewsChannel.class, "LOCAL_LAST_UPDATE_TIME", curDate.getTime() + "",
									"CODE = ?", new String[] { channelCode });
							infoList = savePicInfoList(picList);
						}
					} else if (page == 1) {
						infoList = SugarRecord.find(NewsInfo.class, "CHANNEL_CODE = ?", new String[] { channelCode },
								null, "PUB_TIME desc", (page - 1) * pageSize + "," + pageSize);
						LogM.log(NewsBussiness.class, "从数据库中查询出的数据条数："+infoList.size());
					} else {

						infoList = SugarRecord.find(NewsInfo.class, "CHANNEL_CODE = ? ", new String[] { channelCode },
								null, "PUB_TIME desc", (page - 1) * pageSize + "," + pageSize);
						if (infoList.size() != pageSize && type == NewsInfo.NEWS_TYPE_NORAML) {

							String url = String.format(URLs.URL_NEWS_LIST_OF_CHANNEL,URLs.baseURL) + "&channelcode=" + channelCode + "&pno=" + page
									+ "&pernum=" + pageSize;
							infoList = WebUtils.requestList(url, NewsInfo.class, "info");
							saveList(infoList, channelCode);
						} else if (infoList.size() == 0 && type == NewsInfo.NEWS_TYPE_PICTURE) {
							String url = String.format(URLs.URL_PIC_LIST,URLs.baseURL) + "&channelcode=" + channelCode + "&pno=" + page
									+ "&pernum=" + pageSize;
							;
							List<NewsPictureInfo> picList = WebUtils.requestList(url, NewsPictureInfo.class, "data");
							infoList = savePicInfoList(picList);
						}
					}
					LogM.log(NewsBussiness.class, "准备返回到界面："+infoList.size());
					sHandler.post(new OnDoneRun<List<NewsInfo>>(callback, infoList));

				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<NewsInfo>>(callback));
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<NewsInfo>>(callback));
				} catch (JSONException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<NewsInfo>>(callback));
				}
			}

			/**
			 * 将图片列表转换为newinfo列表，并保存
			 * 
			 * @param picList
			 * @return
			 */
			private List<NewsInfo> savePicInfoList(List<NewsPictureInfo> picList) {
				List<NewsInfo> desList = new ArrayList<NewsInfo>();
				for (NewsPictureInfo npi : picList) {
					NewsInfo ni = new NewsInfo();
					ni.setType(NewsInfo.NEWS_TYPE_PICTURE);
					ni.setTitle(npi.getTitle());
					ni.setId(npi.getInfoId());
					ni.setChannelCode(npi.getChannelCode());
					ni.setPicURL(npi.getUrl());
					ni.setCommontNum(Integer.parseInt(npi.getCommontNum()));
					ni.setPubTime(npi.getPubTime());
					desList.add(ni);
					ni.save();
				}
				return desList;
			}
		});

		return f;
	}

	public Future<?> getNewsChannelUpdateTime(final String channelCode, final APResultCallback<Date> callback) {

		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					String url = String.format(URLs.URL_CHANNEL_UPDATE_TIME, URLs.baseURL) + "&channelcode=" + channelCode;
					JSONObject jo = new JSONObject(WebUtils.requestWithGet(url));
					Date date = mSdf.parse(jo.getString("lastupdatetime"));
					sHandler.post(new OnDoneRun<Date>(callback, date));
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Date>(callback));
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Date>(callback));
				} catch (ParseException e) {

					e.printStackTrace();
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
		});

		return f;
	}

	public Future<?> refreshChannelUpdateTime(final String channelCode, final APResultCallback<Date> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					String url = String.format(URLs.URL_CHANNEL_UPDATE_TIME, URLs.baseURL) + "&channelcode=" + channelCode;
					JSONObject jo = new JSONObject(WebUtils.requestWithGet(url));
					Date date = mSdf.parse(jo.getString("lastupdatetime"));
					SugarRecord.update(NewsChannel.class, "LAST_UPDATE_TIME", date.getTime() + "", "CODE = ?",
							new String[] { channelCode });
					sHandler.post(new OnDoneRun<Date>(callback, date));
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Date>(callback));
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Date>(callback));
				} catch (ParseException e) {

					e.printStackTrace();
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
		});

		return f;
	}

	/**
	 * 更新某频道，如果有推广图，需要更新推广图
	 */
	public Future<?> refreshChannel(final String channelCode, final APResultCallback<List<NewsInfo>> callback) {

		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				SugarRecord.deleteAll(NewsInfo.class, "CHANNEL_CODE=?", channelCode);
				SugarRecord.deleteAll(HeadPic.class,"CHANNEL_CODE=?",channelCode);
				// 刷新推广信息
				NewsChannel newsChannel = SugarRecord.findByProperty(NewsChannel.class, "CODE", channelCode);
				int showType = newsChannel.getShowType();
				try {
					if (showType == NewsChannel.SHOWTYPE_HEADLINE) {

						String url = String.format(URLs.URL_HEAD_PIC,URLs.baseURL) + "&channelcode=" + channelCode + "&webappcode="
								+ AppContext.getInstance(mContext).getApp().getWebAppCode();
						List<HeadPic> hList = WebUtils.requestList(url, HeadPic.class, "tgpic");

						for (HeadPic hp : hList) {
							hp.setChannelCode(channelCode);
							hp.save();
						}

					}
					String url = String.format(URLs.URL_NEWS_LIST_OF_CHANNEL,URLs.baseURL) + "&channelcode=" + channelCode + "&pno=1&pernum="
							+ URLs.PAGE_SIZE;

					List<NewsInfo> infoList = WebUtils.requestList(url, NewsInfo.class, "info");

					saveList(infoList, channelCode);
					if (newsChannel.getLastUpdateTime() != null) {

						SugarRecord.update(NewsChannel.class, "LOCAL_LAST_UPDATE_TIME", newsChannel.getLastUpdateTime()
								.getTime() + "", "CODE = ?", new String[] { channelCode });
					}

					sHandler.post(new OnDoneRun<List<NewsInfo>>(callback, infoList));
				} catch(Exception e){
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<NewsInfo>>(callback));
				}

			}
		});
		return f;
	}

	public void saveList(List<NewsInfo> list, String channelCode) {
		// 避免重复保存已经是推广信息的信息
		List<NewsInfo> populationList = SugarRecord.find(NewsInfo.class, "CHANNEL_CODE = ? and POPULATION_TYPE > 0",
				new String[] { channelCode }, null, null, null);

		String[] ids = new String[populationList.size()];
		for (int i = -1; ++i < ids.length;) {
			ids[i] = populationList.get(i).getId();
		}

		// 将收藏信息还原
		List<Collection> cList = SugarRecord.listAll(Collection.class);
		int csize = cList.size();
		String[] cids = new String[csize];
		for (int i = -1; ++i < csize;) {
			cids[i] = cList.get(i).getInfoId();
		}
		for (NewsInfo ni : list) {

			// 判断是否已经存在
			boolean isExist = false;
			boolean isCollected = false;
			for (int i = -1; ++i < ids.length;) {
				if (ni.getId().equals(ids[i])) {
					isExist = true;
					break;
				}
			}

			// 判断是否收藏
			for (int i = -1; ++i < csize;) {
				if (ni.getId().equals(cids[i])) {
					isCollected = true;
					break;
				}
			}

			if (isExist) {
				SugarRecord.updateById(NewsInfo.class, ni.getId(), new String[] { "PIC_URL", "SUMMARY", "PUB_TIME" },
						new String[] { ni.getPicURL(), ni.getSummary(), ni.getPubTime().getTime() + "" });
			} else {

				ni.setChannelCode(channelCode);
				// ni.setType(NewsInfo.NEWS_TYPE_NORAML);
				ni.setCommontNum(ni.getCommentNum());

				ni.save();
			}

			if (isCollected) {
				SugarRecord.updateById(NewsInfo.class, ni.getId(), "IS_COLLECTED", NewsInfo.COLLECTED + "");
			}

		}
	}


	public List<HeadPic> getNewsPopulation(String channelCode) {

		return SugarRecord.find(HeadPic.class, "CHANNEL_CODE = ?", new String[] {
				channelCode, }, null, "sort_id", null);
	}

	/** 获取到服务器info json，解析，下载图片，生成html保存到对象并持久化，最后返回 */

	public NewsInfo getNewInfo(String newsInfoId, String channelCode) throws IOException, InterruptedException,JsonParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm",Locale.CHINA);
		String url = String.format(URLs.URL_NEWS_INFO, URLs.baseURL) + "&infoid=" + newsInfoId + "&channelcode=" + channelCode;

		NewsInfo newsInfo = null;

		/*
		 * try { newsInfo = WebUtils.request(url, NewsInfo.class);
		 * newsInfo.setContent
		 * (newsInfo.getTitle()+"<br/>"+mSdf.format(newsInfo.getPubTime
		 * ())+"\n"+newsInfo.getContent()); } catch (IOException e) {
		 * e.printStackTrace(); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */

		// 如果数据库中不存在此信息，例如推送，搜索，需要先将本期多有信息均缓存然后做其他操作
		newsInfo = SugarRecord.findById(NewsInfo.class, newsInfoId);
		// 如果数据库中已经有content则直接返回info对象
		// if (newsInfo != null && newsInfo.getContent() != null
		// &&
		// !newsInfo.getContent().equals("")&&!newsInfo.getContent().equals("null"))
		// {
		// return newsInfo;//直接返回
		// }

		try{
			
			newsInfo = WebUtils.request(url, NewsInfo.class);
		}catch(JsonParseException e){
			throw e;
		}

		Log.v("NewsBussiness", "拼接html字符串");

		String fontCssLink = "";
		switch (newsInfo.getFontFamilyFlag()) {
		case 1:
			fontCssLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/font_fangsong.css\" />";
			break;
		case 2:
			fontCssLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/font_heiti.css\" />";
			break;
		case 3:
			fontCssLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/font_kaiti.css\" />";
			break;
		default:
			break;
		}

		String fontSizeLink = "";
		switch (newsInfo.getFontSizeFlag()) {
		case 0:
			fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/font_size_normal.css\" />";
			break;
		case 1:
			fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:////css/font_size_xsmall.css\" />";
			break;
		case 2:
			fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/font_size_small.css\" />";
			break;
		case 3:
			fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/font_size_normal.css\" />";
			break;
		case 4:
			fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/font_size_large.css\" />";
			break;
		case 5:
			fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/font_size_xlarge.css\" />";
			break;

		default:
			break;
		}

		// 拼接html字符串
		StringBuilder htmlSb = new StringBuilder(1200);

		htmlSb.append("<body margin=0>");
		htmlSb.append(fontCssLink);
		htmlSb.append(fontSizeLink);
		// htmlSb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/font_kaiti.css\" />");//
		// 写入csslink
		htmlSb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/news.css\" />");// 写入csslink
		htmlSb.append("<div class=infotitle>");
		htmlSb.append("<div class=titleshow>" + newsInfo.getTitle() + "</div>");
		htmlSb.append("<div class=pubtimeshow>" + sdf.format(newsInfo.getPubTime()) + "</div>");
		htmlSb.append("</div>");
		htmlSb.append("<DIV class=arcinfo>");
		// 下载图片
		htmlSb.append("<div class=contetshow>");

		// 处理content
		String content = newsInfo.getContent();
		// content = content.replaceAll("href=[\'\"][^\'\"]*[\"\']", "");
//		content = content.replaceAll("styledis=[\'\"][^\']*[\'\"]", "");
//		content = content.replaceAll("style=[\'\"][^\']*[\'\"]", "");
//		content = content.replaceAll("color:[^)]*[)][;]?", "");
		// content = content.replaceAll("<a[^>]*>", "");
		// content = content.replaceAll("</a[^>]*>", "");

		// 为img添加 href

		String img = "";
		Pattern p_image;
		Matcher m_image;
		List<String> pics = new ArrayList<String>();

		// String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址

		String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
		p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
		m_image = p_image.matcher(content);
		while (m_image.find()) {
			img = img + "," + m_image.group();

			Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);

			while (m.find()) {

				pics.add(m.group(1));
			}
		}

		htmlSb.append(content);
		
		htmlSb.append("</div>");
		htmlSb.append("</div>");
		htmlSb.append("<div style=\"height:60px\"></div>");
		htmlSb.append("</body>");
		// 将html保存为info的content属性
		String htmlStr = htmlSb.toString();
//		htmlStr = htmlStr.replaceAll("width=", "widthstyle=");
		newsInfo.setContent(htmlStr);

		Log.v("NewsBussiness", "html构造完毕 更新Info 数据库");
		SugarRecord.updateById(NewsInfo.class, newsInfoId, new String[] { "CONTENT", "SIZE" }, new String[] { htmlStr,
				htmlStr.length() + "" });

		return newsInfo;
	}


	public static List<String> getImgStr(String htmlStr) {
		String img = "";
		Pattern p_image;
		Matcher m_image;
		List<String> pics = new ArrayList<String>();

		// String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址

		String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
		p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
		m_image = p_image.matcher(htmlStr);
		while (m_image.find()) {
			img = img + "," + m_image.group();
			// Matcher m =
			// Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src

			Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);

			while (m.find()) {
				pics.add(m.group(1));
			}
		}
		return pics;
	}

	public Future<?> getNewsInfo(final String newsInfoId, final String channelCode,
			final APResultCallback<NewsInfo> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					NewsInfo newsInfo = getNewInfo(newsInfoId, channelCode);
					sHandler.post(new OnDoneRun<NewsInfo>(callback, newsInfo));
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<NewsInfo>(callback));
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<NewsInfo>(callback));
				}catch(JsonParseException e){
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<NewsInfo>(callback));
				}
			}
		});

		return f;

	}

	public Future<?> getPicInfoPage(final String infoId, final int page,
			final APResultCallback<List<NewsPictureInfo>> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					String url = String.format(URLs.URL_PIC_INFO_LIST,URLs.baseURL) + "&infoid=" + infoId +"&pno=" + page;

					List<NewsPictureInfo> pList = WebUtils.requestList(url, NewsPictureInfo.class, "data");
					if (page == 0) {

					}
					sHandler.post(new OnDoneRun<List<NewsPictureInfo>>(callback, pList));

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

	public void rerangeChannelIndex(String channelType, int oldIndex, int newIndex) {
		LogM.log(this.getClass(), "排序 名称：之前index" + oldIndex + "当前index：" + newIndex);
		List<NewsChannel> list = SugarRecord.find(NewsChannel.class, "TYPE_ID= ? and DISPLAY_ORDER !=0",
				new String[] { channelType }, null, "DISPLAY_ORDER", null);
		/*
		 * 如果是向后移动则需要先将oldIndex+1~newIndex处的元素整体向前移动一个单位
		 * 如果是向前移动则需要先将newIndex~oldIndex-1出的元素向后移动一个单位
		 */
		NewsChannel dragged = list.get(oldIndex);
		if (newIndex > oldIndex) {

			for (int i = oldIndex; i++ < newIndex;) {
				NewsChannel temp = list.get(i);

				LogM.log(this.getClass(), "名称：" + temp.getName() + "从：+" + temp.getDisplayOrder() + "移动到" + i);
				SugarRecord.update(NewsChannel.class, "DISPLAY_ORDER", i + "", "CODE = ?",
						new String[] { temp.getCode() });
			}
		} else if (oldIndex > newIndex) {
			for (int i = oldIndex; i-- > newIndex;) {
				NewsChannel temp = list.get(i);

				LogM.log(this.getClass(), "名称：" + temp.getName() + "从：+" + temp.getDisplayOrder() + "移动到" + i);
				SugarRecord.update(NewsChannel.class, "DISPLAY_ORDER", (i + 2) + "", "CODE = ?",
						new String[] { temp.getCode() });
			}
		}
		// 将拖动的栏目最后移动到目的地，
		SugarRecord.update(NewsChannel.class, "DISPLAY_ORDER", newIndex + 1 + "", "CODE = ?",
				new String[] { dragged.getCode() });
	}

	public void removeChannel(String channelType, String channelCode) {
		// 数据库中display_order设为0标识从已选中剔除，将之后的元素向前移位1
		List<NewsChannel> listSelected = SugarRecord.find(NewsChannel.class, "TYPE_ID= ? and DISPLAY_ORDER !=0",
				new String[] { channelType }, null, "DISPLAY_ORDER", null);
		List<NewsChannel> list = SugarRecord.find(NewsChannel.class, "TYPE_ID= ? and CODE = ?", new String[] {
				channelType, channelCode }, null, "DISPLAY_ORDER", null);
		NewsChannel nc = list.get(0);
		int order = nc.getDisplayOrder();
		int size = listSelected.size();
		// 将选择的排序数设置为0
		SugarRecord.update(NewsChannel.class, "DISPLAY_ORDER", "0", "CODE = ?", new String[] { nc.getCode() });
		// 将当前节点后的所有所有节点前移
		for (int i = order; i < size; i++) {
			NewsChannel temp = listSelected.get(i);
			SugarRecord.update(NewsChannel.class, "DISPLAY_ORDER", i + "", "CODE = ?", new String[] { temp.getCode() });
		}
		LogM.log(this.getClass(), "删除：" + nc.getName());
		// 将拖动的栏目先移动到目的地，
	}

	public void addChannel(String channelType, String channelCode) {
		// 直接将节点增加至末尾,即将其排序数变为当前已选择的栏目数+1
		List<NewsChannel> list = SugarRecord.find(NewsChannel.class, "TYPE_ID= ? and CODE = ?", new String[] {
				channelType, channelCode }, null, null, null);
		long count = SugarRecord.count(NewsChannel.class, "TYPE_ID= ? and DISPLAY_ORDER != 0",
				new String[] { channelType });
		NewsChannel nc = list.get(0);
		LogM.log(this.getClass(), "增加：" + nc.getName() + "到以选择列表：" + (count + 1));
		SugarRecord.update(NewsChannel.class, "DISPLAY_ORDER", (count + 1) + "", "CODE = ?",
				new String[] { nc.getCode() });
	}

	public Future<?> refreshNewsInfoCommontNum(String newsInfoId, String channelCode,
			APResultCallback<Integer> callback) {

		return null;
	}

}
