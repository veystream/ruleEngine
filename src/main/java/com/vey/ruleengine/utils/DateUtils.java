package com.vey.ruleengine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther vey
 * @Date 2018/11/7
 */
public class DateUtils {

    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    public static Date format(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(yyyyMMddHHmmss);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 计算两个时间差
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public static long calculateSeconds(Date fromDate, Date toDate) {
        long from;
        if (fromDate == null) {
            from = new Date().getTime();
        } else {
            from = fromDate.getTime();
        }
        long to = toDate.getTime();
        return (to - from) / 1000;
    }

    /**
     * 日期转时间戳
     *
     * @param date
     * @return
     */
    public static Long translate(Date date) {
        return date.getTime();
    }
}
