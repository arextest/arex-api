package com.arextest.web.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class LocalDateTimeFormatUtils {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    private LocalDateTimeFormatUtils() {
    }

    
    public static String formatYMDHMS(LocalDateTime dateTime) {
        return format(dateTime, YYYY_MM_DD_HH_MM_SS);
    }

    
    public static String formatYMD(LocalDateTime dateTime) {
        return format(dateTime, YYYY_MM_DD);
    }

    
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime == null ? null : getStringFormat(dateTime, pattern);
    }

    public static LocalDateTime format(String dateTime, String pattern) {
        return dateTime == null ? null : getDateFormat(dateTime, pattern);
    }

    
    public static String getStringFormat(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    
    public static LocalDateTime getDateFormat(String dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTime, formatter);
    }
}
