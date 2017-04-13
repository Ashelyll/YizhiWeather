package com.yizhiweather.app.service;

import com.yizhiweather.app.R;
import com.yizhiweather.app.activity.WeatherActivity;
import com.yizhiweather.app.receiver.AutoUpdateReceiver;
import com.yizhiweather.app.util.Constants;
import com.yizhiweather.app.util.HttpCallbackListener;
import com.yizhiweather.app.util.HttpUtil;
import com.yizhiweather.app.util.SharedPreferencesUtil;
import com.yizhiweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;


public class AutoUpdateService extends Service {
	private SharedPreferences settingsPref;//保存设置偏好的SharedPreferences文件
	private SharedPreferences weatherData;//保存天气数据的SharedPreferences文件
	private int mode;
	private Notification notification;
	                        



	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		
	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){		//每次服务启动时调用
		mode=intent.getIntExtra(Constants.SERVICE_MODE,Constants.MODE_UPDATE);
			if(mode==1){
				//任务1：在子线程中更新天气
				new Thread(new Runnable(){
					@Override
					public void run(){
						updateWeather();
					}
				}).start();
				
				//任务2：设置定时器
				setAlarmManager();
				
				//任务3：设置前台服务				
				setNotification();
			}
			
			if(mode==2){
				//任务1：在子线程中更新天气
				new Thread(new Runnable(){
					@Override
					public void run(){
						updateWeather();
					}
				}).start();
				
				//任务2：设置定时器				
				setAlarmManager();
				
			}
			
			if(mode==3){
				//设置前台服务
				setNotification();
			}

		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/*
	 * 设置定时器
	 */
	private void setAlarmManager(){
		settingsPref=SharedPreferencesUtil.getSettingsPref(this);
		int updFreq=settingsPref.getInt(Constants.UPDFREQ_PREF, Constants.DEFAULT_UPDFREQ);
		Log.d("AutoUpdateService","upd_freq=" + updFreq);
		AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
		int anHour=60*60*1000;
		long triggerAtTime=SystemClock.elapsedRealtime()+anHour*updFreq;
		Intent broadcastIntent=new Intent(this,AutoUpdateReceiver.class);
		PendingIntent broadcastPi=PendingIntent.getBroadcast(this,0,broadcastIntent,0);
		if(updFreq==0){
			manager.cancel(broadcastPi);
			broadcastPi.cancel();
		}else{
			manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, broadcastPi);
		}
	}
	
	/*
	 * 设置前台服务
	 */
	private void setNotification(){	
		weatherData=SharedPreferencesUtil.getWeatherPref(this);			
		String notifTitle=weatherData.getString("area_name","");
		String notifText=weatherData.getString("weather","") + "  " + weatherData.getString("temperature","")+"°";
		Intent notifIntent=new Intent(this,WeatherActivity.class);
		PendingIntent notifPi=PendingIntent.getActivity(this,0,notifIntent,0);
		Notification.Builder builder=new Notification.Builder(this);
		builder.setSmallIcon(R.drawable.ios_weather_icon)
			   .setContentTitle(notifTitle)
			   .setContentText(notifText)
			   .setContentIntent(notifPi);
		notification=builder.build();
		settingsPref=SharedPreferencesUtil.getSettingsPref(this);
		boolean notifFlag=settingsPref.getBoolean(Constants.NOTIF_PREF,Constants.DEFAULT_NOTIF);
		Log.d("AutoUpdateService","allow_notif=" + notifFlag);
		if(notifFlag){
			startForeground(1,notification);			
		}else{
			stopForeground(true);
		}
	}
	
	/*
	 * 更新天气信息
	 */
	private void updateWeather(){
		weatherData=SharedPreferencesUtil.getWeatherPref(this);
		String area_id=weatherData.getString("area_id", "");
		String address="http://www.thinkpage.cn/weather/api.svc/getWeather?city=" +
						area_id +"&language=zh-CHS&provider=CMA&unit=C&aqi=city";
		HttpUtil.sendHttpRequest(address,new HttpCallbackListener(){
			@Override
			public void onFinish(String response){
				Utility.handleWeatherResponse(AutoUpdateService.this,response);
			}
			
			@Override
			public void onError(Exception e){
				e.printStackTrace();
			}
		});
	}

	
}
