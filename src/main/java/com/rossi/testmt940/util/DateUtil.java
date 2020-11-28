package com.rossi.testmt940.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class DateUtil {
    final Calendar cal = Calendar.getInstance();

    public Date stringToDate(String value, String pattern){
        try{
            return new SimpleDateFormat(pattern).parse(value);
        } catch (Exception e){
            log.error("Cannot parse String: {}, Pattern: {}", value, pattern);
            log.error("Error while parsing date DateUtil.stringToDate: ", e.getMessage());
        }
        return null;
    }

    public String dateToString(Date value, String pattern){
        return new SimpleDateFormat(pattern).format(value);
    }

    public Date startDaily(Date date) {
        return getDate(date,0,0,0,0);
    }

    public Date endDaily(Date date) {
        return getDate(date,23,59,59,999);
    }

    private Date getDate(Date date, Integer  hour, Integer minute, Integer second, Integer millisecond){
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    public Date getBeginningOfMonth(Date date){
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return startDaily(cal.getTime());
    }

    public Date getEndOfMonth(Date date){
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return endDaily(cal.getTime());
    }

    public String convertToOtherFormat(String date, String firstFormat, String finalFormat){
        return dateToString(stringToDate(date, firstFormat), finalFormat);
    }

    public Date addDateTime (Date date, Integer year, Integer month, Integer day, Integer hour, Integer minute){
        cal.setTime(date);
        Optional.ofNullable(year).ifPresent(y -> cal.add(Calendar.YEAR, y));
        Optional.ofNullable(month).ifPresent(mo -> cal.add(Calendar.MONTH, mo));
        Optional.ofNullable(day).ifPresent(d -> cal.add(Calendar.DATE, d));
        Optional.ofNullable(hour).ifPresent(h -> cal.add(Calendar.HOUR, h));
        Optional.ofNullable(minute).ifPresent(mi -> cal.add(Calendar.MINUTE, mi));

        return cal.getTime();
    }

}
