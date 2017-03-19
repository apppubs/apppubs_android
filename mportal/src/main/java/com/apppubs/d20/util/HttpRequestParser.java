package com.apppubs.d20.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
   
/**   
 * HttpRequestParser������   
 */   
public class HttpRequestParser {    
  /**   
   * ����url�ַ���,����utf-8����   
   * @param urlString   
   * @return   
   */   
  public static Request parse(String urlString) {    
    return parse(urlString, "utf-8");    
  }    
  
  public static String getBaseUrl(String url)
  {
  String baseurl = "";
  final Pattern pa = Pattern.compile("http://(.*?)/", Pattern.DOTALL);

	   final Matcher ma = pa.matcher(url);

	   while (ma.find())
	   {
		   baseurl =ma.group(1);

	   }
  return baseurl;
  }
  
   
  /**   
   * ����url�ַ���,ָ���ַ������н���   
   * @param urlString   
   * @param enc   
   * @return   
   */   
  public static Request parse(String urlString, String enc) {    
    if ((urlString == null) || (urlString.length() == 0)) {    
      return new Request();    
    }    
    int questIndex = urlString.indexOf('?');    
    if (questIndex == -1) { 
      return new Request(urlString);    
    }    
    String url = urlString.substring(0, questIndex);    
    String queryString = urlString.substring(questIndex + 1, urlString.length());    
    return new Request(url, getParamsMap(queryString, enc));    
  }    
  
  private static Map<String, String[]> getParamsMap(String queryString, String enc) {    
	    Map<String, String[]> paramsMap = new HashMap<String, String[]>();    
	    if ((queryString != null) && (queryString.length() > 0)) {    
	      int ampersandIndex, lastAmpersandIndex = 0;    
	      String subStr, param, value;    
	      String[] paramPair, values, newValues;    
	      do {    
	        ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;    
	        if (ampersandIndex > 0) {    
	          subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);    
	          lastAmpersandIndex = ampersandIndex;    
	        } else {    
	          subStr = queryString.substring(lastAmpersandIndex);    
	        }    
	        paramPair = subStr.split("=");    
	        param = paramPair[0];    
	        value = paramPair.length == 1 ? "" : paramPair[1];    
	        try {    
	          value = URLEncoder.encode(value,enc);
              //System.out.println(value);
	        } catch (UnsupportedEncodingException ignored) {    
	        }    
	        if (paramsMap.containsKey(param)) {    
	          values = paramsMap.get(param);    
	          int len = values.length;    
	          newValues = new String[len + 1];    
	          System.arraycopy(values, 0, newValues, 0, len);    
	          newValues[len] = value;    
	        } else {    
	          newValues = new String[] { value };    
	        }    
	        paramsMap.put(param, newValues);    
	      } while (ampersandIndex > 0);    
	    }    
	    return paramsMap;    
	  }    
   
  private static Map<String, String[]> getParamsMap1(String queryString, String enc) {    
    Map<String, String[]> paramsMap = new HashMap<String, String[]>();    
    if ((queryString != null) && (queryString.length() > 0)) {    
      int ampersandIndex, lastAmpersandIndex = 0;    
      String subStr, param, value;    
      String[] paramPair, values, newValues;    
      do {    
        ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;    
        if (ampersandIndex > 0) {    
          subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);    
          lastAmpersandIndex = ampersandIndex;    
        } else {    
          subStr = queryString.substring(lastAmpersandIndex);    
        }    
        paramPair = subStr.split("=");    
        param = paramPair[0];    
        value = paramPair.length == 1 ? "" : paramPair[1];    
        try {    
          value = URLDecoder.decode(value, enc);    
        } catch (UnsupportedEncodingException ignored) {    
        }    
        if (paramsMap.containsKey(param)) {    
          values = paramsMap.get(param);    
          int len = values.length;    
          newValues = new String[len + 1];    
          System.arraycopy(values, 0, newValues, 0, len);    
          newValues[len] = value;    
        } else {    
          newValues = new String[] { value };    
        }    
        paramsMap.put(param, newValues);    
      } while (ampersandIndex > 0);    
    }    
    return paramsMap;    
  }    
   
  /**   
   * �������   
   * @author yy   
   * @date Jun 21, 2009 2:17:31 PM   
   */   
  public static class Request {    
    private String requestURL;    
    private Map<String, String[]> parameterMap;    
   
    Request() {    
      this("");    
    }    
   
    Request(String requestURL) {    
      this.requestURL = requestURL;    
      parameterMap = new HashMap<String, String[]>();    
    }    
   
    Request(String requestURL, Map<String, String[]> parameterMap) {    
      this.requestURL = requestURL;    
      this.parameterMap = parameterMap;    
    }    
   
    /**   
     * ���ָ�����ƵĲ���   
     * @param name   
     * @return   
     */   
    public String getParameter(String name) {    
      String[] values = parameterMap.get(name);    
      if ((values != null) && (values.length > 0)) {    
        return values[0];    
      }    
      return null;    
    }    
   
    /**   
     * ������еĲ�������   
     * @return   
     */   
    public Enumeration<String> getParameterNames() {    
      return Collections.enumeration(parameterMap.keySet());    
    }    
   
    /**   
     * ���ָ�����ƵĲ���ֵ(���)   
     * @param name   
     * @return   
     */   
    public String[] getParameterValues(String name) {    
      return parameterMap.get(name);    
    }    
   
    /**   
     * ��������url��ַ   
     * @return   
     */   
    public String getRequestURL() {    
      return requestURL;    
    }   
    
    
    //��������baseurl
    public String getBaseRequestURL() {    
        return "http://"+getBaseUrl(requestURL);    
      } 
    
    /**   
     * ��� ����-ֵMap   
     * @return   
     */   
    public Map<String, String[]> getParameterMap() {    
      return parameterMap;    
    }    
   
    @Override   
    public String toString() {    
      StringBuilder buf = new StringBuilder();    
  
      buf.append(this.requestURL+"?");      
      if (this.parameterMap.size() > 0) {    
        for (Map.Entry<String, String[]> e : this.parameterMap.entrySet()) {    
          buf.append(e.getKey()).append("=").append(Arrays.toString(e.getValue()).replace("[", "").replace("]", "")).append("&");    
        }    
        buf.deleteCharAt(buf.length() - 1);    
      }    
  
      return buf.toString();    
    }    
  }    
}  

