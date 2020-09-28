package com.lhfeiyu.tech.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DateUtils {

    private static final ThreadLocal<DateFormat> ym = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMM");
        }
    };

    private static final ThreadLocal<DateFormat> ymd = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> ymdh = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHH");
        }
    };

    /**
     *  计算两个日期之间的时间,比如日期开始时间为20200201，结束时间为20200203，返回的list为20200201,20200202,20200203
     * @param startTime
     * @param endTime
     * @param level         ym,ymd
     * @return
     */
    public static List<String> getDate (String startTime, String endTime, String level) {
        if (startTime == null) {
            throw new RuntimeException("参数开始时间不能为空");
        }
        if (endTime == null) {
            throw new RuntimeException("参数结束时间不能为空");
        }
        if (level == null) {
            throw new RuntimeException("日期格式不能为空");
        }
        List<String> date = new ArrayList<>();
        if ("ym".equalsIgnoreCase(level)) {
            LocalDate stDate = LocalDate.of(Integer.parseInt(startTime.substring(0,4)),
                    Integer.parseInt(startTime.substring(4, 6)),
                    Integer.parseInt("01"));

            LocalDate etDate = LocalDate.of(Integer.parseInt(endTime.substring(0,4)),
                    Integer.parseInt(endTime.substring(4, 6)),
                    Integer.parseInt("01"));
            Period period = Period.between(stDate, etDate);
            int y = period.getYears();
            int f = period.getMonths() + 1;
            for (int i = 0; i < (y * 12) + f; i++) {
                String month = timeAddMonth(ym.get(), startTime, i);
                date.add(month);
            }
        } else if ("ymd".equalsIgnoreCase(level)) {
            LocalDate stDate = LocalDate.of(Integer.parseInt(startTime.substring(0,4)),
                    Integer.parseInt(startTime.substring(4, 6)),
                    Integer.parseInt(startTime.substring(6, 8)));

            LocalDate etDate = LocalDate.of(Integer.parseInt(endTime.substring(0,4)),
                    Integer.parseInt(endTime.substring(4, 6)),
                    Integer.parseInt(endTime.substring(6, 8)));
            long f = etDate.toEpochDay() - stDate.toEpochDay() + 1;
            for (int i = 0; i < f; i++) {
                try {
                    date.add(timeAddDate(ymd.get(), Integer.parseInt(startTime), i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else if ("ymdh".equalsIgnoreCase(level)) {
            // 暂时不支持
        }
        date.stream().sorted().collect(Collectors.toList());
        return date;
    }

    /**
     * 对yyyyMMdd格式的日期进行加多天操作
     * @param simpleDateFormat
     * @param value
     * @param day
     * @return
     */
    private static String timeAddDate (DateFormat simpleDateFormat, Integer value, int day) throws ParseException {
        Calendar c = Calendar.getInstance();
        Date date = simpleDateFormat.parse(value.toString());
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        return simpleDateFormat.format(c.getTime());
    }


    /**
     * 对yyyyMM格式进行月份加一操作
     * @param simpleDateFormat
     * @param value
     * @param month
     * @return
     */
    private static String timeAddMonth (DateFormat simpleDateFormat, String value, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(4, 6)) - 1, 01);
        calendar.add(Calendar.MONTH, month);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 通过月份，生成该月的最后一天
     * @param month
     * @return
     */
    public static String getLastDayByMonth (String month) {
        Date parse = null;
        try {
            parse = ym.get().parse(month);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        return ymd.get().format(calendar.getTime());
    }

}
