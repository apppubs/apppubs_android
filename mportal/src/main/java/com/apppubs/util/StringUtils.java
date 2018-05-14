package com.apppubs.util;

import android.text.TextUtils;

import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    // 温度处理
    public static String getTemp(String str) {
        if (str.length() > 0 && str.indexOf("~") != -1) {
            return str.substring(0, str.indexOf("~")) + "°/" + str.substring(str.indexOf("~") +
                    1, str.indexOf("℃")) + "°";
        } else {
            return str;
        }

    }

    // 日期处理
    public static String getNowDateString(String format) {
        Date curDate = new Date(System.currentTimeMillis());
        return getDateString(curDate, format);
    }

    public static String getDateString(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String getDate(String str) {
        return str.substring(3, 9);

    }

    public static String getDateString(Date date, long localAndServiceTimeInterval) {
        Date localDate = new Date();
        return "";
    }

    // 比较date类型的大小
    public static String getCommmentDate(String str, Date stardardDateTime) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(str.substring(0, 19));
            int time = (int) (stardardDateTime.getTime() - date.getTime()); // 以毫秒为单位
            int miao = time / (60 * 60 * 1000);// 判断几天前
            if (miao < 24) {
                return str.substring(str.indexOf(" "), str.length() - 3);
            } else if (miao < 48) {
                return "昨天" + str.substring(str.indexOf(" "), str.length() - 3);
            } else if (miao < 72) {
                return "前天" + str.substring(str.indexOf(" "), str.length() - 3);
            } else {
                return str.substring(0, str.indexOf(" "));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 比较date类型的大小
    public static String getCommmentDate1(Date date, Date stardardDateTime) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = formatter.format(date);
        int time = Math.abs((int) (stardardDateTime.getTime() - date.getTime())); // 以毫秒为单位
        int miao = time / 1000;
        if (miao < 60) {
            return miao + "秒前";
        } else if (miao / 60 < 60) {
            return miao / 60 + "分钟前";
        } else if (miao / (60 * 60) < 24) {
            return miao / (60 * 60) + "小时前";
        } else {
            return dateStr.substring(0, dateStr.indexOf(" "));
        }
    }

    /**
     * 格式化时间，当天的时间只显示"时","分"，其他时间只显示"年","月","日"。
     *
     * @param date
     * @param standardTime
     * @return
     */
    public static String getFormattedTime(Date date, Date standardTime) {
        String result = "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar calendarStandard = Calendar.getInstance();
        calendarStandard.setTime(standardTime);
        if (calendar.get(Calendar.DAY_OF_YEAR) < calendarStandard.get(Calendar.DAY_OF_YEAR)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd", Locale.CHINESE);
            result = sdf.format(date);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINESE);
            result = sdf.format(date);
        }

        return result;
    }

    // 星期处理
    public static String getWeek(String str) {
        if (str.length() > 2) {
            str = str.substring(0, 2);
        }
        if (str.equals("周一")) {
            return "星期一";
        } else if (str.equals("周二")) {
            return "星期二";
        } else if (str.equals("周三")) {
            return "星期三";
        } else if (str.equals("周四")) {
            return "星期四";
        } else if (str.equals("周五")) {
            return "星期五";
        } else if (str.equals("周六")) {
            return "星期六";
        } else {
            return "星期日";
        }
    }

    public static void main(String[] args) throws Exception {
        URL url = new URL("http://www.bjtime.cn");// 取得资源对象
        URLConnection uc = url.openConnection();// 生成连接对象
        uc.connect(); // 发出连接
        long ld = uc.getDate(); // 取得网站日期时间
        Date date = new Date(ld); // 转换为标准时间对象
        // 分别取得时间中的小时，分钟和秒，并输出

    }

    // 实时温度处理
    public static String getNowTemp(String str) {
        if (str.contains("(")) {
            return str.substring(str.indexOf("("), str.indexOf(")") + 1);
        } else {
            return str;
        }
    }

    // 天气记录
    public static String saveInitWeather(String str) {
        if (str.contains("暴雪")) {
            return "暴雪";
        } else if (str.contains("暴雨")) {
            return "暴雨";
        } else if (str.contains("大雪")) {
            return "大雪";
        } else if (str.contains("大雨")) {
            return "大雨";
        } else if (str.contains("多云")) {
            return "多云";
        } else if (str.contains("浮尘")) {
            return "浮尘";
        } else if (str.contains("雷阵雨")) {
            return "雷阵雨";
        } else if (str.contains("雷阵雨冰雹")) {
            return "雷阵雨冰雹";
        } else if (str.contains("晴")) {
            return "晴";
        } else if (str.contains("沙尘暴")) {
            return "沙尘暴";
        } else if (str.contains("雾")) {
            return "雾";
        } else if (str.contains("霾")) {
            return "霾";
        } else if (str.contains("小雪")) {
            return "小雪";
        } else if (str.contains("小雨")) {
            return "小雨";
        } else if (str.contains("扬沙")) {
            return "扬沙";
        } else if (str.contains("阴")) {
            return "阴";
        } else if (str.contains("雨夹雪")) {
            return "雨夹雪";
        } else if (str.contains("阵雪")) {
            return "阵雪";
        } else if (str.contains("阵雨")) {
            return "阵雨";
        } else if (str.contains("中雪")) {
            return "中雪";
        } else if (str.contains("中雨")) {
            return "中雨";
        } else {
            return "晴";
        }
    }

    private static String[] yues = {"January", "February", "Marcy", "April", "May", "June",
            "July", "August",
            "September", "October", "November", "December"};

    // private static int[] myues = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

    public static int getDateInt(String str) {
        int i = 0;
        if (str.equals(yues[0])) {
            i = 1;
        } else if (str.equals(yues[1])) {
            i = 2;
        } else if (str.equals(yues[2])) {
            i = 3;
        } else if (str.equals(yues[3])) {
            i = 4;
        } else if (str.equals(yues[4])) {
            i = 5;
        } else if (str.equals(yues[5])) {
            i = 6;
        } else if (str.equals(yues[6])) {
            i = 7;
        } else if (str.equals(yues[7])) {
            i = 8;
        } else if (str.equals(yues[8])) {
            i = 9;
        } else if (str.equals(yues[9])) {
            i = 10;
        } else if (str.equals(yues[10])) {
            i = 11;
        } else if (str.equals(yues[11])) {
            i = 12;
        }
        return i;
    }

    public static String getDateEnglish(int yue) {

        String str = null;
        switch (yue) {
            case 1:
                str = yues[0];
                break;
            case 2:
                str = yues[1];
                break;
            case 3:
                str = yues[2];
                break;
            case 4:
                str = yues[3];
                break;
            case 5:
                str = yues[4];
                break;

            case 6:
                str = yues[5];
                break;
            case 7:
                str = yues[6];
                break;
            case 8:
                str = yues[7];
                break;
            case 9:
                str = yues[8];
                break;
            case 10:
                str = yues[9];
                break;
            case 11:
                str = yues[10];
                break;
            case 12:
                str = yues[11];
                break;
        }
        return str;

    }

    /**
     * 从一个由 分隔符隔开的字符串得到一个ArrayList对象
     *
     * @param str
     * @return
     */
    public static ArrayList<String> str2ArrayList(String str, String regularExpression) {

        ArrayList<String> list = new ArrayList<String>();
        if (!TextUtils.isEmpty(str)) {
            String[] arr = str.split(regularExpression);
            for (int i = -1; ++i < arr.length; ) {
                list.add(arr[i]);
            }
        }

        return list;
    }

    /**
     * 由数组生成一个由分隔符分隔的字符串
     */
    public static String array2Str(List<String> list, String regularExpression) {
        String result = "";
        if (list != null && list.size() > 0) {
            for (int i = -1; ++i < list.size(); ) {
                if (i != 0) {
                    result += regularExpression;
                }
                result += list.get(i);
            }
        }
        return result;
    }

    /**
     * 从url中查询路径参数
     *
     * @param url
     * @return
     */
    public static String[] getPathParams(String url) {
        String[] result = null;
        if (url != null && !url.equals("") && url.indexOf("://") > 0) {
            url = url.substring(url.indexOf("://") + "://".length());
            int indexOfQuerySymbol = -1;
            if ((indexOfQuerySymbol = url.indexOf("?")) > 0) {
                url = url.substring(0, indexOfQuerySymbol);
            }
            String[] params = url.split("/");
            result = params;
        }
        return result;
    }

    /**
     * 从url中查找查询参数,如果有查询参数则返回没有则返回null
     *
     * @param url
     * @param paramName
     * @return
     */
    public static String getQueryParameter(String url, String paramName) {
        String result = null;
        int indexOfQuerySymbol = -1;
        //如果搜不到“？”或者这个字符串只有一个？那就算了
        if ((indexOfQuerySymbol = url.indexOf("?")) > -1 && (indexOfQuerySymbol < url.length())) {
            url = url.substring(indexOfQuerySymbol + 1);
            String[] params = url.split("&");
            for (String param : params) {
                if (param.startsWith(paramName)) {
                    String[] keyValueArr = param.split("=");
                    if (keyValueArr.length > 1) {
                        result = keyValueArr[1];
                    } else {
                        result = "";
                    }
                }
            }
        }
        return result;
    }

    public static Map<String, String> getQueryParameters(String url) {
        Map<String, String> map = new HashMap<>();
        int indexOfQuerySymbol = -1;
        //如果搜不到“？”或者这个字符串只有一个？那就算了
        if ((indexOfQuerySymbol = url.indexOf("?")) > -1 && (indexOfQuerySymbol < url.length())) {
            url = url.substring(indexOfQuerySymbol + 1);
            String[] params = url.split("&");
            for (String param : params) {
                String[] keyValueArr = param.split("=");
                if (keyValueArr.length > 1) {
                    map.put(keyValueArr[0], keyValueArr[1]);
                }
            }
        }
        return map;
    }

    public static String inputURL(String url, String paramName, String paramValue) {
        //参数和参数名为空的话就返回原来的URL
        if (TextUtils.isEmpty(paramValue) || TextUtils.isEmpty(paramName)) {
            return url;
        }
        //先很据# ? 将URL拆分成一个String数组
        String a = "";
        String b = "";
        String c = "";

        String[] abcArray = url.split("[?]");
        a = abcArray[0];
        if (abcArray.length > 1) {
            String bc = abcArray[1];
            String[] bcArray = bc.split("#");
            b = bcArray[0];
            if (bcArray.length > 0) {
                c = bcArray[1];
            }
        }
        if (TextUtils.isEmpty(b)) {
            return url;
        }

        // 用&拆p, p1=1&p2=2 ，{p1=1,p2=2}
        String[] bArray = b.split("&");
        String newb = "";
        boolean found = false;
        for (int i = 0; i < bArray.length; i++) {
            String bi = bArray[i];
            if (TextUtils.isEmpty(bi))
                continue;
            String key = "";
            String value = "";

            String[] biArray = bi.split("="); // {p1,1}
            key = biArray[0];
            if (biArray.length > 1)
                value = biArray[1];

            if (key.equals(paramName)) {
                found = true;
                if (TextUtils.isEmpty(paramValue)) {
                    newb = newb + "&" + key + "=" + paramValue;
                } else {
                    continue;
                }
            } else {
                newb = newb + "&" + key + "=" + value;
            }
        }
        // 如果没找到，加上
        if (!found && TextUtils.isEmpty(paramValue)) {
            newb = newb + "&" + paramName + "=" + paramValue;
        }
        if (TextUtils.isEmpty(newb))
            a = a + "?" + newb;
        if (TextUtils.isEmpty(c))
            a = a + "#" + c;
        return a;
    }

    public static String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(str);
        }
        return sb.toString();
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = TextUtils.isEmpty(pattern) ? new SimpleDateFormat("yyyy-MM-dd " +
                "HH:mm:ss") : new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static boolean isDigist(String str) {
        if (!TextUtils.isEmpty(str)) {
            Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
            Matcher isNum = pattern.matcher(str);
            if (!isNum.matches()) {
                return true;
            }
        }
        return false;
    }

    public static boolean equals(String res, String des) {
        if (res == null || des == null) {
            return res == des;
        } else {
            return res.equals(des);
        }
    }

    public static int compareVersion(String version1, String version2) {
        if (TextUtils.isEmpty(version1) || TextUtils.isEmpty(version2)) {
            return 0;
        }
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        //获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        //循环判断每位的大小
        while (index < minLen && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt
                (version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            //如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }


    }

}
