package com.yizhiweather.app.util;


import java.util.GregorianCalendar;
import java.util.TimeZone;

import xyz.matteobattilana.library.WeatherView;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.yizhiweather.app.R;

/**
 * Created by Administrator on 2017/3/22.
 */

public class UIUtil {
    /*
     *根据天气现象代码获取对应的天气现象图片id
     */
    public static int getWeatherIconId(String weatherCode){
        if(!TextUtils.isEmpty(weatherCode)){
            int code=Integer.valueOf(weatherCode);
            switch(code){
                case 0:return R.drawable.wea_0;
                case 1:return R.drawable.wea_1;
                case 2:return R.drawable.wea_2;
                case 3:return R.drawable.wea_3;
                case 4:return R.drawable.wea_4;
                case 5:return R.drawable.wea_5;
                case 6:return R.drawable.wea_6;
                case 7:return R.drawable.wea_7;
                case 8:return R.drawable.wea_8;
                case 9:return R.drawable.wea_9;
                case 10:return R.drawable.wea_10;
                case 11:return R.drawable.wea_11;
                case 12:return R.drawable.wea_12;
                case 13:return R.drawable.wea_13;
                case 14:return R.drawable.wea_14;
                case 15:return R.drawable.wea_15;
                case 16:return R.drawable.wea_16;
                case 17:return R.drawable.wea_17;
                case 18:return R.drawable.wea_18;
                case 19:return R.drawable.wea_19;
                case 20:return R.drawable.wea_20;
                case 21:return R.drawable.wea_21;
                case 22:return R.drawable.wea_22;
                case 23:return R.drawable.wea_23;
                case 24:return R.drawable.wea_24;
                case 25:return R.drawable.wea_25;
                case 26:return R.drawable.wea_26;
                case 27:return R.drawable.wea_27;
                case 28:return R.drawable.wea_28;
                case 29:return R.drawable.wea_29;
                case 30:return R.drawable.wea_30;
                case 31:return R.drawable.wea_31;
                case 32:return R.drawable.wea_32;
                case 33:return R.drawable.wea_33;
                case 34:return R.drawable.wea_34;
                case 35:return R.drawable.wea_35;
                case 36:return R.drawable.wea_36;
                case 37:return R.drawable.wea_37;
                case 38:return R.drawable.wea_38;
                case 99:return R.drawable.wea_99;
                default:return -1;
            }
        }
        return -1;
    }
    
    /*
     * 设置背景为白天/夜间
     */
    public static int getBgId(){
    	//获取系统当前小时
    	GregorianCalendar calendar=new GregorianCalendar();
    	calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    	int curHour=calendar.get(GregorianCalendar.HOUR_OF_DAY);
    	Log.d("UIUtil",String.valueOf(curHour));
    	if((curHour>=19 && curHour<24) || (curHour>=0 && curHour<=6)){	//夜间段
    		return R.drawable.bg_night;    		
    	}else															//日间段
    		return R.drawable.bg_day;
    	
    }
    
    
    /*
     * 根据天气代码设置天气背景
     */
    public static WeatherView getWeatherView(WeatherView weatherView,String weatherCode){
    	if(!TextUtils.isEmpty(weatherCode) && weatherView!=null){
    		int code=Integer.valueOf(weatherCode);
    		weatherView.setFPS(50);
    		weatherView.setLifeTime(1200);
    		weatherView.setFadeOutTime(1000);
    		switch(code){
    		case 10:	//阵雨
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
    			           .setParticles(30)
    			           .setAngle(-3);
    			break;
    		case 11:	//雷阵雨
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
		                   .setParticles(30)
		                   .setAngle(-3);
    			break;
    			
    		case 12:	//雷阵雨伴有冰雹
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
    			           .setParticles(30)
    			           .setAngle(-3);
    			break;
    			
    		case 13:	//小雨
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
		                   .setParticles(5)
		                   .setAngle(-3);
    			break;
    			
    		case 14:	//中雨
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
		                   .setParticles(25)
		                   .setAngle(-3);
    			break;
    			
    		case 15:	//大雨
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
		                   .setParticles(45)
		                   .setAngle(-3);
    			break;
    			
    		case 16:	//暴雨
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
		                   .setParticles(50)
		                   .setAngle(-3);
    			break;
    			
    		case 17:	//大暴雨
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
		                   .setParticles(60)
		                   .setAngle(-3);
 			break;
 			
    		case 18:	//特大暴雨
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
		                   .setParticles(70)
		                   .setAngle(-3);
    			break;
    			
    		case 19:	//冻雨
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
		                   .setParticles(30)
		                   .setAngle(-3);
    			break;
    			
    		case 20:	//雨夹雪
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[0])
                .setParticles(30)
                .setAngle(-3);
    			break;
    			
    		case 21:	//阵雪
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[1])
		                   .setParticles(15)
		                   .setAngle(0);
    			break;
    			
    		case 22:	//小雪
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[1])
		                   .setParticles(5)
		                   .setAngle(0);
    			break;
    			
    		case 23:	//中雪
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[1])
		                   .setParticles(15)
		                   .setAngle(0);
    			break;
    			
    		case 24:	//大雪
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[1])
		                   .setParticles(30)
		                   .setAngle(0);
    			break;
    			
    		case 25:	//暴雪
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[1])
		                   .setParticles(50)
		                   .setAngle(0);
    			break;
    			
    		default:
    			weatherView.setWeather(xyz.matteobattilana.library.Common.Constants.weatherStatus.values()[2]);    		
    			break;
    		}
    	}
    	return weatherView;
    }

}
