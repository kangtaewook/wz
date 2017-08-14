package com.vinetech.util;

import android.content.Context;
import android.util.Log;

import com.vinetech.wezone.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Created by galuster3 on 2017-02-10.
 */

public class LibDateUtil {

    public static String getAmPm(String inputTime) {

        // inputTime ="20131210010";
        // inputTime ="20131";
        // inputTime = "20140108155646";

        if (inputTime == null)
            return null;

        String splitTime[] = inputTime.split(".");

        if (inputTime != null && splitTime.length > 1)
            inputTime = splitTime[0];

        Formatter formatter = new Formatter();

        Date date = getStringToDate(inputTime, "yyyy-MM-dd HH:mm:ss");

        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String am_pm = "";
            if (calendar.get(Calendar.AM_PM) == 0) {
                am_pm = "오전 ";
            } else if (calendar.get(Calendar.AM_PM) == 1) {
                am_pm = "오후 ";
            }

            int tempHour = calendar.get(Calendar.HOUR);

            if (tempHour == 0) {
                tempHour = 12;
            }

            // TODO: String.format으로 대체할것(왜 formatter로했는지 확인필요)
            formatter.format("%s %02d:%02d", am_pm, tempHour,
                    calendar.get(Calendar.MINUTE));

        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * 지정한 날짜를 원하는 날짜로 변환하여 반환함
     *
     * @param inputTime  날짜및 시간 , 예) 20140114121200
     * @param inputType  input Time format , 예) yyyyMMddHHmmssSS (년 월 일 시간 분 초 밀리초)
     * @param outputType 반환 원하는 format 셋팅 , 예) yyyy년 MM월 dd일 HH시 mm분 ss초 (년 월 일 시간 분 초
     *                   밀리초)
     * @return String
     */
    public static String getConvertDate(String inputTime, String inputType,
                                        String outputType) {

        if (inputTime == null || inputType == null || outputType == null) {
            return "";
        }
        Date date = new Date();

        // input 형식으로 셋팅
        SimpleDateFormat dateFormat = new SimpleDateFormat(inputType);

        // 가져올 시간 입력
        try {
            date = dateFormat.parse(inputTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            // SDSLog.e(e);
            return "";
        }

        // output 형식에 맞게 셋팅
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(outputType,
                Locale.getDefault());

        return dateFormat2.format(date);
    }

    /**
     * DateFormat.LONG 타입으로 반환
     *
     * @param inputTime 날짜및 시간 , 예) 20140114121200
     * @param inputType input Time format , 예) yyyyMMddHHmmssSS (년 월 일 시간 분 초 밀리초)
     * @return String
     */
    public static String getConvertDate(String inputTime, String inputType) {

        if (inputTime == null || inputType == null) {
            return "";
        }
        Date date = new Date();

        // input 형식으로 셋팅
        SimpleDateFormat dateFormat = new SimpleDateFormat(inputType);

        // 가져올 시간 입력
        try {
            date = dateFormat.parse(inputTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            // SDSLog.e(e);
            return "";
        }

        DateFormat format2 = DateFormat.getDateInstance(DateFormat.LONG);

        return format2.format(date);
    }

    public static String getConvertDateWithShortFormat(String inputTime,
                                                       String inputType) {

        if (inputTime == null || inputType == null) {
            return "";
        }
        Date date = new Date();

        // input 형식으로 셋팅
        SimpleDateFormat dateFormat = new SimpleDateFormat(inputType);

        // 가져올 시간 입력
        try {
            date = dateFormat.parse(inputTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            // SDSLog.e(e);
            return "";
        }

        DateFormat format2 = DateFormat.getDateInstance(DateFormat.SHORT);

        return format2.format(date);
    }

    /**
     * 지정한 날짜에 대한 요일 추출
     *
     * @param inputTime 날짜및 시간 , 예) 20140114121200
     * @param inputType input Time format , 예) yyyyMMddHHmmssSS (년 월 일 시간 분 초 밀리초)
     * @return String
     */
    public static String getDayOfWeek(String inputTime, String inputType) {

        if (inputTime == null || inputType == null) {
            return "";
        }

        Calendar cal = null;
        Date date = new Date();
        String strDayOfWeek = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat(inputType);

        try {
            date = dateFormat.parse(inputTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            // SDSLog.e(e);
            return "";
        }

        if (date != null) {
            cal = Calendar.getInstance();
            cal.setTime(date);
        }

        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                strDayOfWeek = "일요일";
                break;
            case Calendar.MONDAY:
                strDayOfWeek = "월요일";
                break;
            case Calendar.TUESDAY:
                strDayOfWeek = "화요일";
                break;
            case Calendar.WEDNESDAY:
                strDayOfWeek = "수요일";
                break;
            case Calendar.THURSDAY:
                strDayOfWeek = "목요일";
                break;
            case Calendar.FRIDAY:
                strDayOfWeek = "금요일";
                break;
            case Calendar.SATURDAY:
                strDayOfWeek = "토요일";
                break;
        }
        return strDayOfWeek;
    }

    public static String getDayOfWeek(Calendar cal) {

        String strDayOfWeek = "";
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                strDayOfWeek = "일";
                break;
            case Calendar.MONDAY:
                strDayOfWeek = "월";
                break;
            case Calendar.TUESDAY:
                strDayOfWeek = "화";
                break;
            case Calendar.WEDNESDAY:
                strDayOfWeek = "수";
                break;
            case Calendar.THURSDAY:
                strDayOfWeek = "목";
                break;
            case Calendar.FRIDAY:
                strDayOfWeek = "금";
                break;
            case Calendar.SATURDAY:
                strDayOfWeek = "토";
                break;
        }
        return strDayOfWeek;
    }

    public static Calendar getCalendarFirstDayinWeek(){

        Calendar cal =  Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK);
        //주의 첫쨋날을 찾는다.
        cal.add(Calendar.DAY_OF_MONTH, (week * -1) + 1);
        return cal;
    }

    public static String getCalendarDayinWeek(int pos) {

        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK);
        //주의 첫쨋날을 찾는다.
        //calendar의 일요일 index 1 부터
        //2를 더함으로써 월요일부터 가져온다.
        cal.add(Calendar.DAY_OF_MONTH, (week * -1) + (pos + 1));

        return String.format("%d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    }

    public static String getCalendarDayinWeekForBook(int pos) {

        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK);
        //주의 첫쨋날을 찾는다.
        //calendar의 일요일 index 1 부터
        //2를 더함으로써 월요일부터 가져온다.
        cal.add(Calendar.DAY_OF_MONTH, (week * -1) + (pos + 1));

        SimpleDateFormat df = new SimpleDateFormat("MM/dd EEE");
        return df.format(cal.getTime());
    }

    public static String getCalendarDayinWeekFromMonday(int pos) {

        Calendar cal = Calendar.getInstance();
        int week = cal.get(Calendar.DAY_OF_WEEK);
        //주의 첫쨋날을 찾는다.
        //calendar의 일요일 index 1 부터
        //2를 더함으로써 월요일부터 가져온다.
        cal.add(Calendar.DAY_OF_MONTH, (week * -1) + (pos + 1));

        SimpleDateFormat df = new SimpleDateFormat("E");
        return df.format(cal.getTime());
    }

    public static String getDataInStrFullDate(String strDate) {

        if(strDate == null)
            return strDate;

        int length = strDate.length();

        return strDate.substring(0, length- 4);
    }

    public static String getFullTimeInStrDate(String strDate) {

        if(strDate == null)
            return strDate;

        int length = strDate.length();

        return strDate.substring(length - 4, length);
    }

    public static String getTimeInStrFullTime(String strDate) {

        if(strDate == null)
            return strDate;

        int length = strDate.length();

        return strDate.substring(0,2);
    }

    public static String getMinInStrFullTime(String strDate) {

        if(strDate == null)
            return strDate;

        int length = strDate.length();

        return strDate.substring(length - 2, length);
    }

    public static String getDayInStrDate(String strDate) {

        if(strDate == null)
            return strDate;

        int length = strDate.length();

        return strDate.substring(length - 4, length - 2);
    }

    public static String getCurrentTime(String dateType) {
        return getCurrentTime(dateType, new Date());
    }

    public static String getCurrentTime(String dateType, Date currentTime) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(dateType,
                Locale.KOREA);
        return mSimpleDateFormat.format(currentTime);
    }

    public static Date getStringToDate(String inputTime, String dateType) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType,
                Locale.KOREA);
        Date date = null;

        if (inputTime != null) {
            try {
                date = dateFormat.parse(inputTime);

            } catch (ParseException e) {
                date = null;
            }
        }

        return date;
    }

    public static String moveDateAll(String yyyyMMddHHmm, int diffYear, int diffMonth, int diffDay) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmm");
        Date tempDate = null;
        try {
            tempDate = dateFormatter.parse(yyyyMMddHHmm);

        } catch (ParseException e) {
            tempDate = null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(tempDate);

        cal.add(Calendar.YEAR, diffYear);     // 1년 전
        cal.add(Calendar.MONTH, diffMonth);    // 한달 전
        cal.add(Calendar.DATE, diffDay);     // 하루 전

        String r = dateFormatter.format(cal.getTime());

        return r;
    }

    public static String moveMinuteWithFormat(String date, String dateType, int moveMins) {

        SimpleDateFormat format = new SimpleDateFormat(dateType);

        Date tempDate = null;

        try {
            tempDate = format.parse(date);
        } catch (ParseException e) {
        }

        long time = tempDate.getTime();

        time = time + (60 * 1000) * moveMins;

        Date returnDate = new Date(time);

        return format.format(returnDate);
    }

    public static String moveMinute(String dateType, String date, int moveMins) {

        SimpleDateFormat format = new SimpleDateFormat(dateType);

        Date tempDate = null;

        try {
            tempDate = format.parse(date);
        } catch (ParseException e) {
        }

        long time = tempDate.getTime();

        time = time + (60 * 1000) * moveMins;

        Date returnDate = new Date(time);

        return format.format(returnDate);
    }

    public static String moveMinute(String date, int moveMins) {

        SimpleDateFormat format = new SimpleDateFormat("HHmm");

        Date tempDate = null;

        try {
            tempDate = format.parse(date);
        } catch (ParseException e) {
        }

        long time = tempDate.getTime();

        time = time + (60 * 1000) * moveMins;

        Date returnDate = new Date(time);

        return format.format(returnDate);
    }

    public static Date moveDate(Date date, int movedays) {

        long time = date.getTime();

        time = time + (24 * 60 * 60 * 1000) * movedays;

        Date tempDate = new Date(time);

        return tempDate;
    }

    public static String moveDateWithString(String strDate, String dateType, int movedays){

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType, Locale.getDefault());

        Date tempDate = null;

        try {
            tempDate = dateFormat.parse(strDate);
        } catch (ParseException e) {
        }

        Date outDate = moveDate(tempDate, movedays);
        return dateFormat.format(outDate);
    }

    public static String moveDateWithFormat(Date date, String dateType, int movedays){
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType, Locale.getDefault());
        Date outDate = moveDate(date, movedays);
        return dateFormat.format(outDate);
    }

    public static int moveDateWithDayOfWeek(Date date, int movedays){
        Date outDate = moveDate(date, movedays);
        Calendar inputCalendar = Calendar.getInstance();
        inputCalendar.setTime(outDate);

        return inputCalendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static long diffOfDate(String dateType, String begin, String end)
            throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(dateType);

        Date beginDate = formatter.parse(begin);
        Date endDate = formatter.parse(end);

        long diff = endDate.getTime() - beginDate.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return diffDays;
    }

    public static long diffOfTime(String dateType, String begin, String end)
            throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(dateType);

        Date beginDate = formatter.parse(begin);
        Date endDate = formatter.parse(end);

        long diff = endDate.getTime() - beginDate.getTime();
        long diffTimes = diff / (60 * 60 * 1000);

        return diffTimes;
    }

    public static long diffOfMin(String dateType, String begin, String end)
            throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(dateType);

        Date beginDate = formatter.parse(begin);
        Date endDate = formatter.parse(end);

        long diff = endDate.getTime() - beginDate.getTime();
        long diffMins = diff / (60 * 1000);

        return diffMins;
    }

    public static String getChangeFormat(String inputTime, String inputType,
                                         String outputType) {

        if (inputTime == null)
            return null;

        SimpleDateFormat format = new SimpleDateFormat(inputType);

        SimpleDateFormat outformat = new SimpleDateFormat(outputType);

        Date inputDate = null;
        try {
            inputDate = format.parse(inputTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (inputDate == null)
            return null;

        return outformat.format(inputDate);

    }

    public static String getCurruntDayWithFormat(String dateType) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType, Locale.KOREA);
        Date today = new Date();

        return dateFormat.format(today);
    }

    public static String getCurruntDayWithFormatAsLocale(String dateType){
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType, Locale.getDefault());
        Date today = new Date();
        return dateFormat.format(today);
    }

    public static String getPassTime(Context c, String inputTime, String dateType) {

        // 테스트 용
        // inputTime ="2013-10-23 07:15";
        // inputTime ="2013-12-19 12:02:03.35";
        // dateType = "yyyy-MM-dd HH:mm:ss";
        // inputTime ="2013121001010000";

        Date begineDate = null;
        Date endDate = null;

        SimpleDateFormat format = new SimpleDateFormat(dateType);
        Calendar calendar = Calendar.getInstance();
        String today = format.format(calendar.getTime());

        long todaytMillis = calendar.getTimeInMillis();

        if (inputTime != null) {
            try {
                endDate = format.parse(inputTime);
            } catch (ParseException e) {
                return inputTime;
            }
        }

        if (endDate == null)
            return inputTime;

        long serverTimeMillis = endDate.getTime();

        long diffTime = todaytMillis - serverTimeMillis;

        String returnDate = formatTimeString(c,diffTime / 1000);

        if (returnDate != null) {
            Formatter formatter = new Formatter();

            if (returnDate.equals("dummy")) {
                Calendar inputCalendar = Calendar.getInstance();
                inputCalendar.setTime(endDate);
                // String am_pm = "";

                // if(inputCalendar.get(inputCalendar.AM_PM) == 0){
                // am_pm = "오전 ";
                // }else if(inputCalendar.get(inputCalendar.AM_PM) == 1){
                // am_pm = "오후 ";
                // }
                // TODO: String.format으로 대체할것(왜 formatter로했는지 확인필요)
                // formatter.format("%d년 %02d월 %02d일 %s %02d:%02d",
                // inputCalendar.get(inputCalendar.YEAR),
                // inputCalendar.get(((Integer)inputCalendar.MONTH))+1,
                // inputCalendar.get(inputCalendar.DATE), am_pm,
                // inputCalendar.get(inputCalendar.HOUR_OF_DAY),inputCalendar.get(inputCalendar.MINUTE));
                formatter.format("%d-%02d-%02d",
                        inputCalendar.get(Calendar.YEAR),
                        inputCalendar.get(Calendar.MONTH) + 1,
                        inputCalendar.get(Calendar.DATE));

                String result = formatter.toString();
                formatter.close();
                return result;

            } else {
                return returnDate;
            }
        }
        return returnDate;
    }

    public static String getPassTime(Context c,long inputMillis) {
        return formatTimeString(c,inputMillis);
    }

    private static String formatTimeString(Context c,long diffTime) {

        final int SEC = 60;
        final int MIN = 60;
        final int HOUR = 24;
        final int DAY = 30;
        final int MONTH = 12;

        String msg = null;
        if (diffTime < SEC) {
            msg = String.format(c.getResources().getString(R.string.minutes_ago),0);
        } else if ((diffTime /= SEC) < MIN) {
            msg = String.format(c.getResources().getString(R.string.minutes_ago),diffTime);
        } else if ((diffTime /= MIN) < HOUR) {
            msg = String.format(c.getResources().getString(R.string.hours_ago),diffTime);
        } else if ((diffTime /= HOUR) < DAY) {
            msg = String.format(c.getResources().getString(R.string.days_ago),diffTime);
        } else if ((diffTime /= DAY) < MONTH) {
            msg = String.format(c.getResources().getString(R.string.months_ago),diffTime);
        }else{
            msg = "dummy";
        }
        return msg;
    }

    public static long convertLocalTimeToGMT(long localDateTime) {
        long gmtTime = localDateTime;

        TimeZone z = TimeZone.getDefault();

        int offset = z.getOffset(localDateTime);

        gmtTime = localDateTime - offset;

        return gmtTime;
    }

    public static long convertGMTToLocalTime(long gmtDateTime) {
        long localDateTime = gmtDateTime;

        TimeZone z = TimeZone.getDefault();

        int offset = z.getOffset(gmtDateTime);

        localDateTime = gmtDateTime + offset;

        return localDateTime;
    }

    public static String getCurrentGMT() {

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("z");
        // TimeZone tz = TimeZone.getDefault();

        return df.format(date);
    }

    public static String getCurrentGMTTime(String dateType, int gmt) {

        Date date = new Date();

        SimpleDateFormat readDate = new SimpleDateFormat(dateType);

        SimpleTimeZone stz = new SimpleTimeZone(gmt, "GMT");
        readDate.setTimeZone(stz);

        return readDate.format(date);
    }

    public static String getCurrentGMTwithNumberOnly() {

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("z");

        String str = df.format(date);

        String[] strTemp = str.split("\\:");

        String strTime = strTemp[0];

        int timeInt = 0;
        int minInt = 0;
        try{
            if(strTime.contains("+")){
                timeInt =  Integer.valueOf(strTime.substring(strTime.length()-2, strTime.length()));
            }else if(strTime.contains("-")){
                timeInt =  Integer.valueOf(strTime.substring(strTime.length()-3, strTime.length()));
            }else{
                timeInt =  Integer.valueOf(strTime.substring(strTime.length()-2, strTime.length()));
            }
            minInt = Integer.valueOf(strTemp[1]);
        }catch (Exception e) {
            // TODO: handle exception

            String contry = Locale.getDefault().getCountry();
            String lang = Locale.getDefault().getLanguage();

            return  "error ["+contry+"-"+lang+"]" + str;
        }


        StringBuffer sb = new StringBuffer();

        sb.append(timeInt);

        if(minInt != 0){
            sb.append(".");
            sb.append(String.format("%02d", minInt));
        }

        return sb.toString();

    }


    public static String getCurrentGMT(String gmt) {

        if (gmt == null || "".equals(gmt))
            return null;

        String returnStr = null;


        if(gmt.length() > 1){

            String[] strTemp = gmt.split("\\.");


            StringBuffer sb = new StringBuffer();
            sb.append("GMT");

            if(strTemp.length > 1){

                if(strTemp[0].contains("-")){

                    int tempInt = Integer.valueOf(strTemp[0]);
                    if(tempInt == 0){
                        sb.append("-");
                    }
                    sb.append(tempInt);
                    sb.append(":");
                }else{

                    String strTime = strTemp[0];

                    if(strTime.contains("+")){
                        strTime = strTime.replaceAll("\\+", "");
                    }

                    sb.append("+");
                    sb.append(Integer.valueOf(strTime));
                    sb.append(":");
                }

                //25, 50, 75로 떨어짐.  0.6을 곱해서 시간 단위로 변경
                //5, 4,  분단위인데, 한자리인 경우 0을 붙여 두자리로 계산.
                if(strTemp[1].length() == 1){
                    strTemp[1] += "0";
                }

                float temp = Float.valueOf(strTemp[1]);
                temp *= 0.6;

                //소수점 제거
                int tempInt = (int)temp;

                if(tempInt == 0){
                    sb.append("00");
                }else{
                    sb.append(tempInt);
                }

            }else{
                if(strTemp[0].contains("-")){
                    sb.append(Integer.valueOf(strTemp[0]));
                    sb.append(":");
                    sb.append("00");
                }else{

                    String strTime = strTemp[0];

                    if(strTime.contains("+")){
                        strTime = strTime.replaceAll("\\+", "");
                    }
                    sb.append("+");
                    sb.append(Integer.valueOf(strTime));
                    sb.append(":");
                    sb.append("00");
                }
            }

            returnStr = sb.toString();
        }else{
            //그냥 양수 한자리..
            int gmtInt = Integer.valueOf(gmt);
            if(gmtInt == 0){
                returnStr = String.format("GMT+%d:00",gmtInt);
            }else{
                returnStr = String.format("GMT+0%d:00", gmtInt);
            }
        }

        return returnStr;
    }

    public static String getCurrentTimeWithGMT(String gmtTime) {

        return getCurrentTimeWithGMT("HH:mm:ss", gmtTime);

    }

    public static String getCurrentTimeWithGMT(String dateType, String gmtTime) {

        TimeZone tz = TimeZone.getTimeZone(gmtTime);

        Calendar c = Calendar.getInstance();
        c.setTimeZone(tz);

        SimpleDateFormat writeDate = new SimpleDateFormat(dateType);
        writeDate.setTimeZone(tz);

        return writeDate.format(c.getTime());

    }

    public static String getCalTimeWithGMT(String dateType, String gmtTime) {

        String localTime = "";

        SimpleDateFormat writeDate = new SimpleDateFormat(dateType);

        try {
            Date date = writeDate.parse(gmtTime);

            long longTime = date.getTime();

            int offset = TimeZone.getDefault().getOffset(longTime);
            long longLocalTime = longTime + offset;
            Date localDate = new Date();
            localDate.setTime(longLocalTime);

            localTime = writeDate.format(localDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return localTime;

    }

    public static String getCalTimeWithGMT(String dateType, String gmtTime, int gmt) {

        String localTime = "";

        SimpleDateFormat writeDate = new SimpleDateFormat(dateType);
        SimpleTimeZone stz = new SimpleTimeZone(gmt, "GMT");

        Calendar c = Calendar.getInstance();
        try {
            Date date = writeDate.parse(gmtTime);

            c.setTimeZone(stz);
            c.setTime(date);

            writeDate.setTimeZone(stz);

            localTime = writeDate.format(c.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return localTime;
    }


    public static String getChangeDateType(String inputType, String outputTime, String strDate){

        SimpleDateFormat input = new SimpleDateFormat(inputType);

        SimpleDateFormat output = new SimpleDateFormat(outputTime);
        Date date = null;
        try {
            date = input.parse(strDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return output.format(date);
    }

    public static int getDayOfWeekWithGMT(String dateType, String gmtTime, int gmt) {

        String localTime = "";

        SimpleDateFormat readDate = new SimpleDateFormat(dateType);

        Calendar c = Calendar.getInstance();

        SimpleTimeZone stz = new SimpleTimeZone(gmt, "GMT");
        try {
            Date date = readDate.parse(gmtTime);

            c.setTime(date);
            c.setTimeZone(stz);


        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static String getCalTimeWithGMT(String dateType, String gmtTime, String gmt) {

        String localTime = "";

        SimpleDateFormat readDate = new SimpleDateFormat(dateType);

        SimpleTimeZone stz = new SimpleTimeZone(0, "GMT");
        readDate.setTimeZone(stz);

        SimpleDateFormat writeDate = new SimpleDateFormat(dateType);

        Calendar c = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone(gmt);
        writeDate.setTimeZone(tz);

        try {
            Date date = readDate.parse(gmtTime);
            c.setTimeZone(tz);
            c.setTime(date);

            localTime = writeDate.format(c.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return localTime;
    }

    public static int getDayOfWeekWithGMT(String dateType, String gmtTime, String gmt) {

        String localTime = "";

        SimpleDateFormat writeDate = new SimpleDateFormat(dateType);

        SimpleTimeZone stz = new SimpleTimeZone(0, "GMT");
        writeDate.setTimeZone(stz);

        Calendar c = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone(gmt);

        try {
            Date date = writeDate.parse(gmtTime);

            c.setTimeZone(tz);
            c.setTime(date);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getDayOfWeekWithOutGMT(String dateType, String strDate) {

        String localTime = "";

        SimpleDateFormat writeDate = new SimpleDateFormat(dateType);

        Calendar c = Calendar.getInstance();

        try {
            Date date = writeDate.parse(strDate);

            c.setTime(date);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }
}
