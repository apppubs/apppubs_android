package com.apppubs.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class DateBean {

    Calendar calendar = null;

    public DateBean() {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
    }
   
    public int getyear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getmonth() {
        return 1 + calendar.get(Calendar.MONTH);
    }

    public int getday() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int gethour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getminute() {
        return calendar.get(Calendar.MINUTE);
    }

    public int getsecond() {
        return calendar.get(Calendar.SECOND);
    }

    public String getdate() {
        return getmonth() + "/" + getday() + "/" + getyear();
    }

    public String getdate2() {
        String themonth = "" + getmonth();
        if (getmonth() < 10) {
            themonth = "0" + getmonth();
        }
        String theday = "" + getday();
        if (getday() < 10) {
            theday = "0" + getday();
        }

        return getyear() + "/" + themonth + "/" + theday;
    }

    public String getdate1() {
        String themonth = "" + getmonth();
        if (getmonth() < 10) {
            themonth = "0" + getmonth();
        }
        String theday = "" + getday();
        if (getday() < 10) {
            theday = "0" + getday();
        }

        return getyear() + "-" + themonth + "-" + theday;
    }
    
    public static void main1(String args[]){
    	DateBean dt =  new DateBean();
    	String sToday=dt.getdate1();
    	String sToday_s = sToday.substring(5,sToday.length());
    	System.out.println(sToday_s);
    	 
        
    }
    
    
    
    

    public String getNowday() {
        String themonth = "" + getmonth();
        if (getmonth() < 10) {
            themonth = "0" + getmonth();
        }
        String theday = "" + getday();
        if (getday() < 10) {
            theday = "0" + getday();
        }
        String thehour = "" + gethour();
        if (gethour() < 10) {
            thehour = "0" + gethour();
        }
        return getyear() + themonth + theday + thehour;

    }

    public String gettime() {
        return gethour() + ":" + getminute() + ":" + getsecond();
    }

    public String getyearmonthday() {
        String yyyy = "0000", mm = "00", dd = "00", yy = "00";
        yyyy = yyyy + getyear();
        mm = mm + getmonth();
        dd = dd + getday();
        yy = yyyy.substring(yyyy.length() - 2);
        mm = mm.substring(mm.length() - 2);
        dd = dd.substring(dd.length() - 2);
        return yy + "/" + mm + "/" + dd;
    }

    public String gethourminutesecond() {
        String hh = "00", mm = "00", ss = "00";
        hh = hh + gethour();
        mm = mm + getminute();
        ss = ss + getsecond();
        hh = hh.substring(hh.length() - 2, hh.length());
        mm = mm.substring(mm.length() - 2, hh.length());
        ss = ss.substring(ss.length() - 2, ss.length());
        return hh + ":" + mm + ":" + ss;
    }

    public String getAllDateStr() {
        String yyyy = "", mm = "00", dd = "00", yy = "00", hh = "00", nn = "00", ss = "00";
        yyyy = yyyy + getyear();
        mm = mm + getmonth();
        dd = dd + getday();
        hh = hh + gethour();
        nn = nn + getminute();
        ss = ss + getsecond();
        yy = yyyy.substring(yyyy.length() - 2);
        mm = mm.substring(mm.length() - 2);
        dd = dd.substring(dd.length() - 2);
        hh = hh.substring(hh.length() - 2, hh.length());
        nn = nn.substring(nn.length() - 2, nn.length());
        ss = ss.substring(ss.length() - 2, ss.length());
        return yyyy + "-" + mm + "-" + dd + " " + hh + ":" + nn + ":" + ss;
    }

    public String getalldate() {
        String yyyy = "", mm = "00", dd = "00", yy = "00", hh = "00", nn = "00", ss = "00";
        yyyy = yyyy + getyear();
        mm = mm + getmonth();
        dd = dd + getday();
        hh = hh + gethour();
        nn = nn + getminute();
        ss = ss + getsecond();
        yy = yyyy.substring(yyyy.length() - 2);
        mm = mm.substring(mm.length() - 2);
        dd = dd.substring(dd.length() - 2);
        hh = hh.substring(hh.length() - 2, hh.length());
        nn = nn.substring(nn.length() - 2, nn.length());
        ss = ss.substring(ss.length() - 2, ss.length());
        return yyyy + "/" + mm + "/" + dd + " " + hh + ":" + nn + ":" + ss;
    }

    public String getalldate2() {
        String yyyy = "", mm = "00", dd = "00", yy = "00", hh = "00", nn = "00", ss = "00";
        yyyy = yyyy + getyear();
        mm = mm + getmonth();
        dd = dd + getday();
        hh = hh + gethour();
        nn = nn + getminute();
        ss = ss + getsecond();
        yy = yyyy.substring(yyyy.length() - 2);
        mm = mm.substring(mm.length() - 2);
        dd = dd.substring(dd.length() - 2);
        hh = hh.substring(hh.length() - 2, hh.length());
        nn = nn.substring(nn.length() - 2, nn.length());
        ss = ss.substring(ss.length() - 2, ss.length());
        return yyyy + mm + dd + hh + nn + ss;
    }

    public String getDateOnly() {
        String yyyy = "", mm = "00", dd = "00", yy = "00";
        yyyy = yyyy + getyear();
        mm = mm + getmonth();
        dd = dd + getday();
        yy = yyyy.substring(yyyy.length() - 2);
        mm = mm.substring(mm.length() - 2);
        dd = dd.substring(dd.length() - 2);
        return yyyy + mm + dd;
    }

    /**
     *���Date����һ����/��/�յ��ַ��ָ��ſ����Լ��趨
     * @param date Date
     * @param split String ����ʱ���ַ�ķָ��
     */
    static public String getDateString(Date date, String split) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        int year = cl.get(cl.YEAR);
        int month = cl.get(cl.MONTH) + 1;
        int day = cl.get(cl.DAY_OF_MONTH);
        return year + split + month + split + day;
    }

    static public String getAllDateString(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        int year = cl.get(cl.YEAR);
        int month = cl.get(cl.MONTH) + 1;
        int day = cl.get(cl.DAY_OF_MONTH);
        int hour = cl.get(cl.HOUR_OF_DAY);
        int min = cl.get(cl.MINUTE);
        int sec = cl.get(cl.SECOND);
        return year + "��" + month + "��" + day + "��" + hour + ":" + min + ":" + sec;
    }

    /**
     *���Date����һ����/��/�յ��ַ�Ĭ�Ϸָ���"-"
     * @param date Date
     */
    static public String getDateString(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        int year = cl.get(cl.YEAR);
        int month = cl.get(cl.MONTH) + 1;
        String firstSplit = "-";
        if (month < 10) {
            firstSplit += "0";
        }
        int day = cl.get(cl.DAY_OF_MONTH);
        String secondSplit = "-";
        if (day < 10) {
            secondSplit += "0";
        }
        return year + firstSplit + month + secondSplit + day;
    }

    /**
     * ����������ַ�����Date����
     * @param date String �磺2002-12-10
     * @param split String
     */
    //date������2002/11/22,*�������κηָ���
    static public Date getDateFromString(String date, String split) {
        if (date == null || date.equals(""))
            return null;
        int year = 0;
        int month = 0;
        int day = 0;
        String[] ls = new String[3];
        StringTokenizer st = new StringTokenizer(date, split);
        int i = 0;
        while (st.hasMoreTokens()) {
            ls[i] = st.nextToken();
            i++;
        }
        year = (new Integer(ls[0])).intValue();
        month = (new Integer(ls[1])).intValue();
        day = (new Integer(ls[2])).intValue();
        Calendar cl = Calendar.getInstance();
        cl.set(year, month, day);
        Date rDate = cl.getTime();
        return rDate;
    }

    static public Date getDateFromString(String date) {
        int year = 0;
        int month = 0;
        int day = 0;
        String[] ls = new String[3];
        StringTokenizer st = new StringTokenizer(date, "-");
        int i = 0;
        while (st.hasMoreTokens()) {
            ls[i] = st.nextToken();
            i++;
        }
        year = (new Integer(ls[0])).intValue();
        month = (new Integer(ls[1])).intValue();
        day = (new Integer(ls[2])).intValue();
        Calendar cl = Calendar.getInstance();
        cl.set(year, month, day);
        Date rDate = cl.getTime();
        return rDate;
    }

    /**
     * �õ��ļ�����ʱ���ʽ
     * @param date  ����ʱ��
     * @return  �ļ�����ʱ���ʽ�ַ�
     */
    static public String getFileDateString(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        int year = cl.get(cl.YEAR);
        int month = cl.get(cl.MONTH) + 1;
        int day = cl.get(cl.DAY_OF_MONTH);
        int hour = cl.get(cl.HOUR_OF_DAY);
        int min = cl.get(cl.MINUTE);
        int sec = cl.get(cl.SECOND);
        return year + "-" + month + "-" + day + "-" + " " + hour + ":" + min + ":" + sec;
    }

  



    

  

    public String getnowdate() {
        String yyyy = "", mm = "00", dd = "00", yy = "00", hh = "00", nn = "00", ss = "00";
        yyyy = yyyy + getyear();
        mm = mm + getmonth();
        dd = dd + getday();
        hh = hh + gethour();
        nn = nn + getminute();
        ss = ss + getsecond();
        yy = yyyy.substring(yyyy.length() - 2);
        mm = mm.substring(mm.length() - 2);
        dd = dd.substring(dd.length() - 2);
        hh = hh.substring(hh.length() - 2, hh.length());
        nn = nn.substring(nn.length() - 2, nn.length());
        ss = ss.substring(ss.length() - 2, ss.length());
        return yyyy + ":" + mm + ":" + dd + " " + hh + ":" + nn + ":" + ss;
    }

    public static String getLastdate() {
        Date   today   =   new   Date();
        Date   yestoday   =   new   Date(today.getTime()-24*3600*1000);
        return String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(yestoday));
    }

    static public String getHourStr(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        String hour = String.valueOf(cl.get(cl.HOUR_OF_DAY));
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        return hour;
    }

    static public String getMinuteStr(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        String min = String.valueOf(cl.get(cl.MINUTE));
        if (min.length() == 1) {
            min = "0" + min;
        }
        return min;
    }

    static public String getSecondStr(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        String sec = String.valueOf(cl.get(cl.SECOND));
        if (sec.length() == 1) {
            sec = "0" + sec;
        }
        return sec;
    }

    /**
     * ���DATE�������+ʱ�䴮
     * @param date  ����ʱ��
     * @return  �ļ�����ʱ���ʽ�ַ�
     */
    static public String getStrFromDate(Date date) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        int year = cl.get(cl.YEAR);
        String month = String.valueOf(cl.get(cl.MONTH) + 1);
        if (month.length() == 1) {
            month = "0" + month;
        }
        String day = String.valueOf(cl.get(cl.DAY_OF_MONTH));
        if (day.length() == 1) {
            day = "0" + day;
        }
        String hour = String.valueOf(cl.get(cl.HOUR_OF_DAY));
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String min = String.valueOf(cl.get(cl.MINUTE));
        if (min.length() == 1) {
            min = "0" + min;
        }
        String sec = String.valueOf(cl.get(cl.SECOND));
        if (sec.length() == 1) {
            sec = "0" + sec;
        }
        return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
    }

}