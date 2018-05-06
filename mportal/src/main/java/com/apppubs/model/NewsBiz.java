package com.apppubs.model;

import android.content.Context;
import android.util.Log;

import com.apppubs.bean.NewsPictureInfo;
import com.apppubs.bean.TCollection;
import com.apppubs.bean.THeadPic;
import com.apppubs.bean.TNewsChannel;
import com.apppubs.bean.TNewsInfo;
import com.apppubs.bean.http.ArticlePageResult;
import com.apppubs.bean.http.ArticleResult;
import com.apppubs.bean.http.ChannelsResult;
import com.apppubs.bean.http.IJsonResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.Constants;
import com.apppubs.constant.URLs;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.util.LogM;
import com.apppubs.util.WebUtils;
import com.google.gson.JsonParseException;
import com.orm.SugarRecord;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsBiz extends BaseBiz {

    private SimpleDateFormat mSdf;


    private static volatile NewsBiz mNewsBizImpl;
    private Context mContext;

    private NewsBiz(Context context) {
        super(context);
        mSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);
    }

    public static NewsBiz getInstance(Context context) {
        if (mNewsBizImpl == null) {
            synchronized (NewsBiz.class) {
                if (mNewsBizImpl == null) {
                    mNewsBizImpl = new NewsBiz(context);
                }
            }
        }
        return mNewsBizImpl;
    }

    public void loadChannelGroup(String channelGroupId, final IAPCallback<List<TNewsChannel>>
            callback) {
        Map<String, String> params = new HashMap<>();
        params.put("webAppId", mAppContext.getApp().getWebAppCode());
        params.put("typeId", channelGroupId);
        asyncPOST(Constants.API_NAME_NEWS_CHANNELS, params, ChannelsResult.class, new
                IRQListener<ChannelsResult>() {
                    @Override
                    public void onResponse(ChannelsResult jr, final APError error) {
                        if (error == null) {
                            final List<TNewsChannel> channels = TNewsChannel.createFrom(jr);
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(channels);
                                }
                            });
                        } else {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onException(error);
                                }
                            });
                        }
                    }
                });
    }

    public void loadChannelArticlePage(String channelCode, int pageNum, int pageSize, final
    IAPCallback<ArticlePageResult> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("channelCode", channelCode);
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        asyncPOST(Constants.API_NAME_NEWS_ARTICLE_PAGE, params, ArticlePageResult.class, new
                IRQListener<ArticlePageResult>() {
                    @Override
                    public void onResponse(final ArticlePageResult jr, final APError error) {
                        if (error == null) {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(jr);
                                }
                            });
                        } else {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onException(error);
                                }
                            });
                        }
                    }
                });
    }

    public void loadArticle(String articleId, String channelCode, final IAPCallback<ArticleResult>
            callback) {
        Map<String, String> params = new HashMap<>();
        params.put("id", articleId);
        params.put("channelCode", channelCode);
        asyncPOST(Constants.API_NAME_NEWS_ARTICLE, params, ArticleResult.class, new
                IRQListener<ArticleResult>() {
                    @Override
                    public void onResponse(final ArticleResult jr, final APError error) {
                        if (error == null) {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(jr);
                                }
                            });
                        } else {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onException(error);
                                }
                            });
                        }
                    }
                });
    }

    public void saveList(List<TNewsInfo> list, String channelCode) {
        // 避免重复保存已经是推广信息的信息
        List<TNewsInfo> populationList = SugarRecord.find(TNewsInfo.class, "CHANNEL_CODE = ? and " +
                        "POPULATION_TYPE > 0",
                new String[]{channelCode}, null, null, null);

        String[] ids = new String[populationList.size()];
        for (int i = -1; ++i < ids.length; ) {
            ids[i] = populationList.get(i).getId();
        }

        // 将收藏信息还原
        List<TCollection> cList = SugarRecord.listAll(TCollection.class);
        int csize = cList.size();
        String[] cids = new String[csize];
        for (int i = -1; ++i < csize; ) {
            cids[i] = cList.get(i).getInfoId();
        }
        for (TNewsInfo ni : list) {

            // 判断是否已经存在
            boolean isExist = false;
            boolean isCollected = false;
            for (int i = -1; ++i < ids.length; ) {
                if (ni.getId().equals(ids[i])) {
                    isExist = true;
                    break;
                }
            }

            // 判断是否收藏
            for (int i = -1; ++i < csize; ) {
                if (ni.getId().equals(cids[i])) {
                    isCollected = true;
                    break;
                }
            }

            if (isExist) {
                SugarRecord.updateById(TNewsInfo.class, ni.getId(), new String[]{"PIC_URL",
                                "SUMMARY", "PUB_TIME"},
                        new String[]{ni.getPicURL(), ni.getSummary(), ni.getPubTime().getTime() +
                                ""});
            } else {

                ni.setChannelCode(channelCode);
                // ni.setType(TNewsInfo.NEWS_TYPE_NORAML);
                ni.setCommontNum(ni.getCommentNum());

                ni.save();
            }

            if (isCollected) {
                SugarRecord.updateById(TNewsInfo.class, ni.getId(), "IS_COLLECTED", TNewsInfo
                        .COLLECTED + "");
            }

        }
    }


    public List<THeadPic> getNewsPopulation(String channelCode) {

        return SugarRecord.find(THeadPic.class, "CHANNEL_CODE = ?", new String[]{
                channelCode,}, null, "sort_id", null);
    }

    /**
     * 获取到服务器info json，解析，下载图片，生成html保存到对象并持久化，最后返回
     */

    public TNewsInfo getNewInfo(String newsInfoId, String channelCode) throws IOException,
            InterruptedException, JsonParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA);
        String url = String.format(URLs.URL_NEWS_INFO, URLs.baseURL, URLs.appCode) + "&infoid=" +
                newsInfoId + "&channelcode=" + channelCode;

        TNewsInfo newsInfo = null;

        /*
         * try { newsInfo = WebUtils.request(url, TNewsInfo.class);
         * newsInfo.setContent
         * (newsInfo.getTitle()+"<br/>"+mSdf.format(newsInfo.getPubTime
         * ())+"\n"+newsInfo.getContent()); } catch (IOException e) {
         * e.printStackTrace(); } catch (InterruptedException e) {
         * e.printStackTrace(); }
         */

        // 如果数据库中不存在此信息，例如推送，搜索，需要先将本期多有信息均缓存然后做其他操作
        newsInfo = SugarRecord.findById(TNewsInfo.class, newsInfoId);
        // 如果数据库中已经有content则直接返回info对象
        // if (newsInfo != null && newsInfo.getContent() != null
        // &&
        // !newsInfo.getContent().equals("")&&!newsInfo.getContent().equals("null"))
        // {
        // return newsInfo;//直接返回
        // }

        try {

            newsInfo = WebUtils.request(url, TNewsInfo.class);
        } catch (JsonParseException e) {
            throw e;
        }

        Log.v("NewsBiz", "拼接html字符串");

        String fontCssLink = "";
        switch (newsInfo.getFontFamilyFlag()) {
            case 1:
                fontCssLink = "<link rel=\"stylesheet\" type=\"text/css\" " +
                        "href=\"file:///android_asset/css/font_fangsong.css\" />";
                break;
            case 2:
                fontCssLink = "<link rel=\"stylesheet\" type=\"text/css\" " +
                        "href=\"file:///android_asset/css/font_heiti.css\" />";
                break;
            case 3:
                fontCssLink = "<link rel=\"stylesheet\" type=\"text/css\" " +
                        "href=\"file:///android_asset/css/font_kaiti.css\" />";
                break;
            default:
                break;
        }

        String fontSizeLink = "";
        switch (newsInfo.getFontSizeFlag()) {
            case 0:
                fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" " +
                        "href=\"file:///android_asset/css/font_size_normal.css\" />";
                break;
            case 1:
                fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" " +
                        "href=\"file:////css/font_size_xsmall.css\" />";
                break;
            case 2:
                fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" " +
                        "href=\"file:///android_asset/css/font_size_small.css\" />";
                break;
            case 3:
                fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" " +
                        "href=\"file:///android_asset/css/font_size_normal.css\" />";
                break;
            case 4:
                fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" " +
                        "href=\"file:///android_asset/css/font_size_large.css\" />";
                break;
            case 5:
                fontSizeLink = "<link rel=\"stylesheet\" type=\"text/css\" " +
                        "href=\"file:///android_asset/css/font_size_xlarge.css\" />";
                break;

            default:
                break;
        }

        // 拼接html字符串
        StringBuilder htmlSb = new StringBuilder(1200);

        htmlSb.append("<body margin=0>");
        htmlSb.append(fontCssLink);
        htmlSb.append(fontSizeLink);
        // htmlSb.append("<link rel=\"stylesheet\" type=\"text/css\"
        // href=\"file:///android_asset/font_kaiti.css\" />");//
        // 写入csslink
        htmlSb.append("<link rel=\"stylesheet\" type=\"text/css\" " +
                "href=\"file:///android_asset/css/news.css\" />");// 写入csslink
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

        Log.v("NewsBiz", "html构造完毕 更新Info 数据库");
        SugarRecord.updateById(TNewsInfo.class, newsInfoId, new String[]{"CONTENT", "SIZE"}, new
                String[]{htmlStr,
                htmlStr.length() + ""});

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
                                 final IAPCallback<TNewsInfo> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    TNewsInfo newsInfo = getNewInfo(newsInfoId, channelCode);
                    sHandler.post(new OnDoneRun<TNewsInfo>(callback, newsInfo));
                } catch (IOException e) {
                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<TNewsInfo>(callback));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<TNewsInfo>(callback));
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<TNewsInfo>(callback));
                }
            }
        });

        return f;

    }

    public Future<?> getPicInfoPage(final String infoId, final int page,
                                    final IAPCallback<List<NewsPictureInfo>> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    String url = String.format(URLs.URL_PIC_INFO_LIST, URLs.baseURL) + "&infoid="
                            + infoId + "&pno=" + page;

                    List<NewsPictureInfo> pList = WebUtils.requestList(url, NewsPictureInfo
                            .class, "data");
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
        List<TNewsChannel> list = SugarRecord.find(TNewsChannel.class, "TYPE_ID= ? and " +
                        "DISPLAY_ORDER !=0",
                new String[]{channelType}, null, "DISPLAY_ORDER", null);
        /*
         * 如果是向后移动则需要先将oldIndex+1~newIndex处的元素整体向前移动一个单位
         * 如果是向前移动则需要先将newIndex~oldIndex-1出的元素向后移动一个单位
         */
        TNewsChannel dragged = list.get(oldIndex);
        if (newIndex > oldIndex) {

            for (int i = oldIndex; i++ < newIndex; ) {
                TNewsChannel temp = list.get(i);

                LogM.log(this.getClass(), "名称：" + temp.getName() + "从：+" + temp.getDisplayOrder()
                        + "移动到" + i);
                SugarRecord.update(TNewsChannel.class, "DISPLAY_ORDER", i + "", "CODE = ?",
                        new String[]{temp.getCode()});
            }
        } else if (oldIndex > newIndex) {
            for (int i = oldIndex; i-- > newIndex; ) {
                TNewsChannel temp = list.get(i);

                LogM.log(this.getClass(), "名称：" + temp.getName() + "从：+" + temp.getDisplayOrder()
                        + "移动到" + i);
                SugarRecord.update(TNewsChannel.class, "DISPLAY_ORDER", (i + 2) + "", "CODE = ?",
                        new String[]{temp.getCode()});
            }
        }
        // 将拖动的栏目最后移动到目的地，
        SugarRecord.update(TNewsChannel.class, "DISPLAY_ORDER", newIndex + 1 + "", "CODE = ?",
                new String[]{dragged.getCode()});
    }

    public void removeChannel(String channelType, String channelCode) {
        // 数据库中display_order设为0标识从已选中剔除，将之后的元素向前移位1
        List<TNewsChannel> listSelected = SugarRecord.find(TNewsChannel.class, "TYPE_ID= ? and " +
                        "DISPLAY_ORDER !=0",
                new String[]{channelType}, null, "DISPLAY_ORDER", null);
        List<TNewsChannel> list = SugarRecord.find(TNewsChannel.class, "TYPE_ID= ? and CODE = ?",
                new String[]{
                        channelType, channelCode}, null, "DISPLAY_ORDER", null);
        TNewsChannel nc = list.get(0);
        int order = nc.getDisplayOrder();
        int size = listSelected.size();
        // 将选择的排序数设置为0
        SugarRecord.update(TNewsChannel.class, "DISPLAY_ORDER", "0", "CODE = ?", new String[]{nc
                .getCode()});
        // 将当前节点后的所有所有节点前移
        for (int i = order; i < size; i++) {
            TNewsChannel temp = listSelected.get(i);
            SugarRecord.update(TNewsChannel.class, "DISPLAY_ORDER", i + "", "CODE = ?", new
                    String[]{temp.getCode()});
        }
        LogM.log(this.getClass(), "删除：" + nc.getName());
        // 将拖动的栏目先移动到目的地，
    }

    public void addChannel(String channelType, String channelCode) {
        // 直接将节点增加至末尾,即将其排序数变为当前已选择的栏目数+1
        List<TNewsChannel> list = SugarRecord.find(TNewsChannel.class, "TYPE_ID= ? and CODE = ?",
                new String[]{
                        channelType, channelCode}, null, null, null);
        long count = SugarRecord.count(TNewsChannel.class, "TYPE_ID= ? and DISPLAY_ORDER != 0",
                new String[]{channelType});
        TNewsChannel nc = list.get(0);
        LogM.log(this.getClass(), "增加：" + nc.getName() + "到以选择列表：" + (count + 1));
        SugarRecord.update(TNewsChannel.class, "DISPLAY_ORDER", (count + 1) + "", "CODE = ?",
                new String[]{nc.getCode()});
    }

    public Future<?> refreshNewsInfoCommontNum(String newsInfoId, String channelCode,
                                               IAPCallback<Integer> callback) {

        return null;
    }

}
