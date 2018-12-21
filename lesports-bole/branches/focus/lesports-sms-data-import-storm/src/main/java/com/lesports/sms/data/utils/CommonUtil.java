package com.lesports.sms.data.utils;

import org.dom4j.Attribute;
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

    public static boolean isContainLetter(String str) {
        if (org.apache.commons.lang3.StringUtils.isBlank(str)) return false;
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) { //循环遍历字符串
            if (Character.isLetter(str.charAt(i))) {     //用char包装类中的判断数字的方法判断每一个字符
                isLetter = true;
                break;
            }
        }
        return isLetter;
    }

    public static Object getValue(Object element, String type) {
        try {

            Attribute currentAttribute = (Attribute) element;
            if (type.equals("Integer")) {
                return Integer.valueOf(currentAttribute.getStringValue());
            } else if (type.equalsIgnoreCase("Double")) {
                return Double.valueOf(currentAttribute.getStringValue());
            } else {
                return currentAttribute.getStringValue();
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static String getStringValue(Object element) {
        try {
            Attribute currentAttribute = (Attribute) element;
            return currentAttribute.getStringValue();
        } catch (Exception e) {
            return "";
        }
    }

    public static Integer getIntegerValue(Object element) {
        try {
            Attribute currentAttribute = (Attribute) element;
            return Integer.valueOf(currentAttribute.getStringValue());
        } catch (Exception e) {
            return 0;
        }
    }

    public static Double getStatDoubleValue(Object element) {
        try {
            Attribute currentAttribute = (Attribute) element;
            String value = currentAttribute.getStringValue();
            return Double.valueOf("0" + value);
        } catch (Exception e) {
            return 0.0;
        }
    }


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

    public static String getDataYYMMDD(Date d) {
        DateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        if (d == null) d = new Date();
        return format2.format(d);
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


}
