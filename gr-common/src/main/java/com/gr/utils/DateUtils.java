package com.gr.utils;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 封装java.util.Date
 * @author xiaoleilu
 *
 */
@Slf4j
public class DateUtils extends Date{
	private static final long serialVersionUID = -5395712593979185936L;

	public final static Long MSEC = 1L;
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	public final static String DATE_PATTERN2 = "yyyy年MM月dd日";
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_TIME_PATTERN2 = "yyyy/MM/dd HH:mm:ss";

	/**
	 * 转换JDK date为 DateTime
	 * @param date JDK Date
	 * @return DateTime
	 */
	public static DateUtils parse(Date date) {
		return new DateUtils(date);
	}

	/**
	 * 当前时间
	 */
	public DateUtils() {
		super();
	}

	/**
	 * 给定日期的构造
	 * @param date 日期
	 */
	public DateUtils(Date date) {
		this(date.getTime());
	}

	/**
	 * 给定日期毫秒数的构造
	 * @param timeMillis 日期毫秒数
	 */
	public DateUtils(long timeMillis) {
		super(timeMillis);
	}


	/**
	 * @return java.util.Date
	 */
	public Date toDate() {
		return new Date(this.getTime());
	}

	/**
	 * String日期转换localDateTime时间日期
	 *
	 * @param pattern
	 * @return
	 */
	public static String getLocalTime(String pattern) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

		return LocalDateTime.parse(pattern, df).toString();
	}

	/**
	 * 将字符串格式yyyy-MM-dd HH:mm:ss的字符串转为日期，格式"yyyy-MM-dd"
	 *
	 * @param date 日期字符串
	 * @return 返回格式化的日期
	 */
	public static String getDateFormat(Date date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN2);

			return formatter.format(date);

		} catch (Exception e) {
			log.error("日期格式转换失败{}", e.getMessage());
		}
		return null;
	}

	/**
	 * 将字符串格式yyyy-MM-dd HH:mm:ss的字符串转为日期，格式"yyyy-MM-dd"
	 *
	 * @param date 日期字符串
	 * @return 返回格式化的日期
	 */
	public static String getDateFormat2(String date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
			formatter.setLenient(false);
			Date newDate = formatter.parse(date);
			formatter = new SimpleDateFormat(DATE_PATTERN);

			return formatter.format(newDate);

		} catch (Exception e) {
			log.error("日期格式转换失败{}", e.getMessage());
		}
		return null;
	}

	/**
	 * 将字符串格式yyyy-MM-dd HH:mm:ss的字符串转为日期，格式"yyyy年MM月dd日"
	 *
	 * @param date 日期字符串
	 * @return 返回格式化的日期
	 */
	public static String getDateFormat(String date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
			formatter.setLenient(false);
			Date newDate = formatter.parse(date);
			formatter = new SimpleDateFormat(DATE_PATTERN2);

			return formatter.format(newDate);

		} catch (ParseException e) {
			log.error("日期格式转换失败{}", e.getMessage());
		}
		return date;
	}

	/**
	 * 获取服务器启动时间
	 */
	public static Date getServerStartDate() {
		long time = ManagementFactory.getRuntimeMXBean().getStartTime();
		return new Date(time);
	}

	public static final String parseDateToStr(final String format, final Date date) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 计算两个时间差
	 */
	public static String getDatePoor(Date endDate, Date nowDate) {
		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		return day + "天" + hour + "小时" + min + "分钟";
	}

	/**
	 * 计算两个时间差
	 */
	public static long getMinutes(Date nowDate) {
		long nm = 1000 * 60;
		Date endDate = new Date();
		// 获得两个时间的毫秒时间差异
		long dateTime = endDate.getTime() - nowDate.getTime();
		// 计算差多少分钟
		long minutes = dateTime / nm;

		return minutes;
	}

	/**
	 * 相隔天数 yyyy-mm-dd
	 *
	 * @param begin 日期
	 * @return
	 */
	public static long betweens(String begin) {
		if("长期".equals(begin)){
			return MSEC;
		}
		org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_PATTERN);
		Date end = new Date();
		long msec = end.getTime() - formatter.parseLocalDateTime(begin).toDate().getTime();

		return msec / (1000 * 60 * 60 * 24);
	}
}
