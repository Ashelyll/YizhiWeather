package com.yizhiweather.app.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;



public class TimeUtil {
	
	/*
	 * 获取系统时间制式
	 */
	public static String getTimeFormat(Context context){
		ContentResolver cr = context.getApplicationContext().getContentResolver(); // 获取当前系统设置 
		String timeFormat = android.provider.Settings.System.getString(cr, android.provider.Settings.System.TIME_12_24);//获取系统时间制式
		if(timeFormat.equals("24")) { 
			Log.d("TimeUtil","当前是24制式");
		}
		if(timeFormat.equals("12")) { 
			Log.d("TimeUtil","当前是12制式");
		}
		return timeFormat;
	}
	
	
	/*
	 * 将表示日期的字符串转换成日期
	 */
	public static Date stringToDate(String pattern,String in){
		SimpleDateFormat sdf=(SimpleDateFormat)SimpleDateFormat.getDateInstance();
		sdf.applyPattern(pattern);
		Date date=null;
		try{
			date=sdf.parse(in);//将字符串解析成指定日期格式的日期
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	/*
	 * 以当前日期为基准，根据传入的日期推算出星期几
	 */
	public static String dateToDayOfWeek(Date compDate){
		GregorianCalendar nowCalendar=new GregorianCalendar();//获取当前公历日期
		GregorianCalendar compCalendar=new GregorianCalendar();
		compCalendar.setTime(compDate);//获取传入的公历日期
		int now=nowCalendar.get(GregorianCalendar.DAY_OF_YEAR);
		int comp=compCalendar.get(GregorianCalendar.DAY_OF_YEAR);
		int compDayOfWeek=compCalendar.get(GregorianCalendar.DAY_OF_WEEK);
		String compDayOfWeekString="---";
		switch(compDayOfWeek){
		case 1:
			compDayOfWeekString="星期天";
			break;
		case 2:
			compDayOfWeekString="星期一";
			break;
		case 3:
			compDayOfWeekString="星期二";
			break;
		case 4:
			compDayOfWeekString="星期三";
			break;
		case 5:
			compDayOfWeekString="星期四";
			break;
		case 6:
			compDayOfWeekString="星期五";
			break;
		case 7:
			compDayOfWeekString="星期六";
			break;
		}
		if(comp-now==0){
			return "今天";
		}else{
			return compDayOfWeekString;
		}
		
	}
}
