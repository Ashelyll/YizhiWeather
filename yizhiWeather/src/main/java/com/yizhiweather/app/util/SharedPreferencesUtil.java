package com.yizhiweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesUtil {
	public static SharedPreferences getWeatherPref(Context context){
		return context.getSharedPreferences(Constants.WEATHER_PREF,android.content.Context.MODE_PRIVATE);
	}
	
	public static SharedPreferences getDefaultSettingsPref(Context context){
		SharedPreferences sp=context.getSharedPreferences(Constants.SETTINGS_PREF,android.content.Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putBoolean(Constants.NOTIF_PREF,Constants.DEFAULT_NOTIF);
		editor.putInt(Constants.UPDFREQ_PREF, Constants.DEFAULT_UPDFREQ);
		editor.commit();
		Log.d(context.getClass().getSimpleName(),"def_upd_freq=" + sp.getInt(Constants.UPDFREQ_PREF,2) +
				"def_allow_notif=" +sp.getBoolean(Constants.NOTIF_PREF,false));
		return sp;
		
	}
	
	public static SharedPreferences getSettingsPref(Context context){
		return context.getSharedPreferences(Constants.SETTINGS_PREF,android.content.Context.MODE_PRIVATE);
	}
	
}
