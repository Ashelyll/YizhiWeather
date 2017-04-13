package com.yizhiweather.app.util;

public class Constants {
	public static final String WEATHER_PREF="weather_data";//保存天气数据的SharedPreferences文件名
	public static final String SETTINGS_PREF="settings_pref";//保存设置偏好的SharedPreferences文件名
	public static final String UPDFREQ_PREF="upd_freq";
	public static final String NOTIF_PREF="allow_notif";
	public static final boolean DEFAULT_NOTIF=false;
	public static final int DEFAULT_UPDFREQ=2;
	public static final String SERVICE_MODE="service_mode";
	public static final int MODE_UPDATE=1;
	public static final int MODE_CHANGE_FREQ=2;
	public static final int MODE_CHANGE_NOTIF=3;

}
