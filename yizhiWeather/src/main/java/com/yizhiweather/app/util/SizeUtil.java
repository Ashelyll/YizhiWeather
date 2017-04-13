package com.yizhiweather.app.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class SizeUtil {
	public static int getScreenWidth(Context context) {				//获取屏幕宽度
        return context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {			//获取屏幕高度
        return context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
    }
    public static float getScreenDensity(Context context){			//获取屏幕密度
    	return context.getApplicationContext().getResources().getDisplayMetrics().density;
    }
    public static int dp2px(Context context, float dpValue) {       //将dp值转换成像素值
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (dpValue * dm.density + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {       //将像素值转换成dp值
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (pxValue / dm.density + 0.5f);
    }

    public static int sp2px(Context context, float spValue)
    {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (spValue * dm.scaledDensity + 0.5f);
    }

    public static int px2sp(Context context, float pxValue)
    {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return (int) (pxValue / dm.scaledDensity + 0.5f);
    }
	

	public static int getStatusBarHeight(Context context){		//获取状态栏高度
		int statusBarHeight = -1;  
		//获取status_bar_height资源的ID  
		int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");  
		if (resourceId > 0) {  
		    //根据资源ID获取响应的尺寸值  
		    statusBarHeight = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);  
		}  
		return px2dp(context,statusBarHeight);
	}
	

}
