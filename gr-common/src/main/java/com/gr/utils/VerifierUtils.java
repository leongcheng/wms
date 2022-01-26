package com.gr.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 手机、邮箱格式验证
 */

public class VerifierUtils {

    /**
     * 大陆号码或香港号码均可
     */
    public static boolean isPhoneLegal(String str) throws PatternSyntaxException {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^1(3|4|5|6|7|8|9)\\d{9}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 香港手机号码8位数，5|6|8|9开头+7位任意数
     */
    public static boolean isHKPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^(5|6|8|9)\\d{7}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 座机号码
     */
    public static boolean phone(String str) throws PatternSyntaxException {
        String regExp = "^0\\d{2,3}-\\d{7,8}$"; //座机
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 传真机
     */
    public static boolean fax(String str) throws PatternSyntaxException {
        String regExp = "^(\\d{3,4}-)?\\d{7,8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 用户名校验,以字母开头
     */
    public static boolean checkUserName(String str) throws PatternSyntaxException {
        String regExp = "[\\w_-]{6,28}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 密码校验
     */
    public static boolean checkPassword(String str) throws PatternSyntaxException {
        String regExp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\\\W]{6,40}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 邮箱
     */
    public static boolean checkeEmail(String str) throws PatternSyntaxException {
        String regExp = "^\\w+((\\.\\w+){0,3})@\\w+(\\.\\w{2,3}){1,3}$"; //邮箱
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 车牌号
     */
    public static boolean isCar(String str) throws PatternSyntaxException {
        String regExp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9学警港澳]{1}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 车牌号(挂)
     */
    public static boolean isTrailer(String str) throws PatternSyntaxException {
        String regExp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[挂]{1}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 身份证
     */
    public static boolean isNumber(String str) throws PatternSyntaxException {
        String regExp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }



    /**
     * 非空
     */
    public static String isBlank(String msg) {
        if(StringUtils.isBlank(msg)){
            return "";
        }
        return msg;
    }

}
