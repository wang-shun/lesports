package com.lesports.sms.cooperation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static int parseInt(String src, int def) {
        if (StringUtils.isEmpty(src)) {
            return def;
        }
        int res = def;
        try {
            res = Integer.parseInt(src);
        } catch (Exception e) {
            logger.warn(src + "can not be parse to int", e);
        }
        return res;
    }

    public static long parseLong(String src, Long def) {
        if (StringUtils.isEmpty(src)) {
            return def;
        }
        long res = def;
        try {
            res = Long.parseLong(src);
        } catch (Exception e) {
            logger.warn(src + "can not be parse to long", e);
        }
        return res;
    }

    public static double parseDouble(String src, double def) {
        if (StringUtils.isEmpty(src)) {
            return def;
        }
        double res = def;
        try {
            if (src.startsWith(".")) {
                src = "0" + src;
            }
            res = Double.parseDouble(src);
        } catch (Exception e) {
            logger.warn(src + "can not be parse to double", e);
        }
        return res;
    }

    public static Map<String, Object> convertBeanToMap(Object bean) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        if (null != bean) {
            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    data.put(field.getName(), field.get(bean));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static boolean compare(Object object, Object object1) {
        if (null != object && null != object1) {
            try {
                int objectInt = Integer.parseInt(object.toString());
                int object1Int = Integer.parseInt(object1.toString());
                return objectInt >= object1Int;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static String getPercentFormat(double d) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumIntegerDigits(3);//小数点前保留几位
        nf.setMinimumFractionDigits(1);// 小数点后保留几位
        String str = nf.format(d);
        return str;
    }

    public static String getDataYYYYMMDDHHMMSS(String year, String month, String date, String hour, String minute, String utc) {
        try {
            if (parseInt(month, 0) < 10) {
                month = "0" + month;
            }
            if (parseInt(date, 0) < 10) {
                date = "0" + date;
            }
            if (parseInt(hour, 0) < 10) {
                hour = "0" + hour;
            }
            String resultTime = year + month + date + hour + minute + "00";
            DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
            Date dt = format2.parse(resultTime);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.HOUR_OF_DAY, 8 - parseInt(utc, 0));//shijian加10天
            Date dt1 = rightNow.getTime();
            return format2.format(dt1);
        } catch (Exception e) {
            logger.warn("time  is not digitial");
            return "99991230000000";
        }
    }

    public static String getDataYYMMDD(String year, String month, String date, String hour, String minute, String utc) {
        try {
            if (parseInt(month, 0) < 10) {
                month = "0" + month;
            }
            if (parseInt(date, 0) < 10) {
                date = "0" + date;
            }
            if (parseInt(hour, 0) < 10) {
                hour = "0" + hour;
            }
            String resultTime = year + month + date + hour + minute + "00";
            DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
            Date dt = format2.parse(resultTime);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.HOUR_OF_DAY, 8 - parseInt(utc, 0));//shijian加10天
            Date dt1 = rightNow.getTime();
            return format1.format(dt1);
        } catch (Exception e) {
            logger.warn("time  is not digitial");
            return "99991230";
        }
    }

    public static Date getDataYYMMDD(String curentDate) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = sd.parse(curentDate);
            return date;
        } catch (Exception e) {
            logger.warn("can not change the current date,{}", curentDate);
            return null;
        }

    }

    public static Date getNextDate(Date curentDate) {
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(curentDate);
        rightNow.add(Calendar.DAY_OF_MONTH, 1);//shijian加10天
        Date dt1 = rightNow.getTime();
        return dt1;

    }

    public static Date getBeforeDate(Date curentDate) {
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(curentDate);
        rightNow.add(Calendar.DAY_OF_MONTH, -1);
        Date dt1 = rightNow.getTime();
        return dt1;

    }

    public static String getYYYYMMDDDate(Date curentDate) {
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        return format1.format(curentDate);

    }

    public static String getWeekday(String date) {//必须yyyyMMdd
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdw = new SimpleDateFormat("E");
        Date d = null;
        try {
            d = sd.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdw.format(d);
    }


}
