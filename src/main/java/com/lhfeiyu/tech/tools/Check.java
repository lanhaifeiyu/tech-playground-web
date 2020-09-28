package com.lhfeiyu.tech.tools;

import java.util.List;

/**
 * @author yuronghua-airson
 * @description 工具包：检查工具类
 * @template 2019.08.02 v11.0
 * @organization Zero One More, Inc. http://www.01more.com
 * @remark 工具包：检查工具类
 * @time 2020-02-13 10:08:09
 */
public class Check {

    /**
     * 判断两个Integer是否相等，相等返回true,否则返回false
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    public static boolean integerEqual(Integer num1, Integer num2) {
        if (null != num1 && null != num2 && num1.intValue() == num2.intValue()) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个Integer是否不相等，不相等返回true,否则返回false
     *
     * @param num1
     * @param num2
     * @return boolean
     */
    public static boolean integerNotEqual(Integer num1, Integer num2) {
        if (null != num1 && null != num2 && num1.intValue() == num2.intValue()) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否不为null或不为空字符串（去掉空格后判断），不为空返回true,否则返回false
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNotNull(String str) {
        if (null == str || "".equals(str.trim())) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否为null或空字符串（去掉空格后判断），为空返回true,否则返回false
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNull(String str) {
        if (null == str || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 判断Integer是否不为null，不为空返回true,否则返回false
     *
     * @param integer
     * @return boolean
     */
    public static boolean isNotNull(Integer integer) {
        if (null == integer) {
            return false;
        }
        return true;
    }

    /**
     * 判断Integer是否为null，为空返回true,否则返回false
     *
     * @param integer
     * @return boolean
     */
    public static boolean isNull(Integer integer) {
        if (null == integer) {
            return true;
        }
        return false;
    }

    /**
     * 判断List不为null并且长度大于0，通过返回true,否则返回false
     *
     * @param list 泛型list
     * @return boolean
     */
    public static boolean isNotNull(List<?> list) {
        if (null == list || list.size() <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 判断List为null或者长度小于等于0，通过返回true,否则返回false
     *
     * @param list 泛型list
     * @return boolean
     */
    public static boolean isNull(List<?> list) {
        if (null == list || list.size() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断Integer不为空并且大于0，通过返回true,否则返回false
     *
     * @param integer
     * @return boolean
     */
    public static boolean isGtZero(Integer integer) {
        if (null != integer && integer.intValue() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断Integer不为空并且大于等于0，通过返回true,否则返回false
     *
     * @param integer
     * @return boolean
     */
    public static boolean isGtEqlZero(Integer integer) {
        if (null != integer && integer.intValue() >= 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断Integer为空或者小于0，通过返回true,否则返回false
     *
     * @param integer
     * @return boolean
     */
    public static boolean isLtZero(Integer integer) {
        if (null == integer || integer.intValue() < 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断Integer为空或者小于等于0，通过返回true,否则返回false
     *
     * @param integer
     * @return boolean
     */
    public static boolean isLtEqlZero(Integer integer) {
        if (null == integer || integer.intValue() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断Integer为空或者等于0，通过返回true,否则返回false
     *
     * @param integer
     * @return boolean
     */
    public static boolean isNullZero(Integer integer) {
        if (null == integer || integer.intValue() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串非空且相等，通过返回true,否则返回false
     *
     * @param baseStr
     * @param compareStr
     * @return boolean
     */
    public static boolean strEqual(String baseStr, String compareStr) {
        if (null == baseStr || baseStr.length() <= 0 || null == compareStr || compareStr.length() <= 0) {
            return false;
        }
        if (baseStr.equals(compareStr)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否包含特殊字符，不包含则返回true,否则返回false
     *
     * @param str
     * @return boolean
     */
    public static boolean haveSpecialChar(String str) {
        if (!Check.isNotNull(str))
            return false;
        if (str.matches(RegexUtil.special_char_regexp)) {
            return true;
        }
        return false;
    }

}
