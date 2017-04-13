package com.yizhiweather.app.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xyz.matteobattilana.library.WeatherView;

import com.yizhiweather.app.R;
import com.yizhiweather.app.model.DailyForecast;
import com.yizhiweather.app.service.AutoUpdateService;
import com.yizhiweather.app.util.Constants;
import com.yizhiweather.app.util.TimeUtil;
import com.yizhiweather.app.util.HttpCallbackListener;
import com.yizhiweather.app.util.HttpUtil;
import com.yizhiweather.app.util.UIUtil;
import com.yizhiweather.app.util.LocationUtil;
import com.yizhiweather.app.util.SharedPreferencesUtil;
import com.yizhiweather.app.util.SizeUtil;
import com.yizhiweather.app.util.Utility;
import com.yizhiweather.app.view.ForecastListAdapter;
import com.yizhiweather.app.view.ForecastListView;
import com.yizhiweather.app.view.TrendView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class WeatherActivity extends CheckPermissionsActivity implements OnClickListener,OnRefreshListener{
	private WeatherView weatherView;
	
	private LinearLayout weatherLayout;
	
	private View divider1;
	
	private View divider2;
	
	private View divider3;
	
	private View divider4;
	
	private View divider5;
	
	private View divider6;
	
	private RelativeLayout weatherTopBar;
	
	private SwipeRefreshLayout swipeRefreshLayout;
	
	private TextView statusText;
	
	private LinearLayout basicInfoLayout;
	
	private TextView areaNameText;
	
	private ImageButton locate;
	
	private TextView weatherText;
	
	private TextView temperatureText;
	
	private TrendView tempTrend;
	
	private ForecastListView weatherForecastList;
	
	private List<DailyForecast> forecastList=new ArrayList<DailyForecast>();
	
	private ForecastListAdapter adapter;
	
	private LinearLayout weatherNowDescription;
	
	private TextView description;
	
	private LinearLayout otherInfoLayout;
	
	private TextView humidityText;
	
	private TextView windDirectionText;
	
	private TextView windSpeedText;
	
	private TextView windScaleText;
	
	private TextView aqiText;
	
	private TextView qualityText;
	
	private RelativeLayout weatherBottomBar;
	
	private LinearLayout dotsGroup;
	
	private ImageButton settings;
	
	private ImageButton selectCity;
	
	private static final int REQUEST_NEWQUERY=1;
	
	private static final int REQUEST_REFRESH=2;
	
	private ProgressDialog progressDialog;
	
	private boolean isFromSettingsActivity;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);			
		initViews();			
		isFromSettingsActivity=getIntent().getBooleanExtra("from_settings_activity", false);
		String countyCode=getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){	//如果有县市代号则向服务器查询天气			
			divider1.setVisibility(View.INVISIBLE);
			divider2.setVisibility(View.INVISIBLE);
			divider3.setVisibility(View.INVISIBLE);
			divider4.setVisibility(View.INVISIBLE);
			divider5.setVisibility(View.INVISIBLE);
			divider6.setVisibility(View.INVISIBLE);
			basicInfoLayout.setVisibility(View.INVISIBLE);
			tempTrend.setVisibility(View.INVISIBLE);
			weatherForecastList.setVisibility(View.INVISIBLE);
			weatherNowDescription.setVisibility(View.INVISIBLE);
			otherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeatherInfo(countyCode,REQUEST_NEWQUERY);//根据县市代号查询天气			
		}else{
			showWeather();
		}
	}
	
	private void initViews(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.yizhiweather.app.R.layout.ll_weather_activity);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏		
		weatherLayout=(LinearLayout)findViewById(R.id.ll_weather_layout);
		weatherLayout.setBackgroundResource(UIUtil.getBgId());
		weatherView=(WeatherView)findViewById(R.id.weather_view);
		weatherView.cancelAnimation();
		weatherTopBar=(RelativeLayout)findViewById(R.id.rl_weather_topbar);
		int statusBarHeight=SizeUtil.getStatusBarHeight(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0,statusBarHeight*2,0,0);//4个参数按顺序分别是左上右下
		weatherTopBar.setLayoutParams(layoutParams);
		divider1=(View)findViewById(R.id.weather_divider_1);
		divider2=(View)findViewById(R.id.weather_divider_2);
		divider3=(View)findViewById(R.id.weather_divider_3);
		divider4=(View)findViewById(R.id.weather_divider_4);
		divider5=(View)findViewById(R.id.weather_divider_5);
		divider6=(View)findViewById(R.id.weather_divider_6);
		swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);		
		swipeRefreshLayout.setColorSchemeResources(R.color.swipeRefreshIconColor);
		swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.swipeRefreshBgColor);
		swipeRefreshLayout.setOnRefreshListener(this);		
		statusText=(TextView)findViewById(R.id.tv_weather_status);
		basicInfoLayout=(LinearLayout)findViewById(R.id.ll_weather_basic_info);
		areaNameText=(TextView)findViewById(R.id.tv_weather_area_name);
		locate=(ImageButton)findViewById(R.id.ib_weather_locate);
		locate.setOnClickListener(this);
		weatherText=(TextView)findViewById(R.id.tv_weather_now);
		temperatureText=(TextView)findViewById(R.id.tv_weather_temperature_now);
		tempTrend=(TrendView)findViewById(R.id.temp_trend);
		weatherForecastList=(ForecastListView)findViewById(R.id.lv_weather_forecast_list);
		weatherForecastList.setFocusable(false);
		adapter=new ForecastListAdapter(WeatherActivity.this,R.layout.forecast_item,forecastList);
		weatherForecastList.setAdapter(adapter);
		weatherNowDescription=(LinearLayout)findViewById(R.id.ll_weather_now_description);
		description=(TextView)findViewById(R.id.tv_weather_description);
		otherInfoLayout=(LinearLayout)findViewById(R.id.ll_weather_other_info);
		humidityText=(TextView)findViewById(R.id.tv_weather_humidity);
		windDirectionText=(TextView)findViewById(R.id.tv_weather_wind_direction);
		windSpeedText=(TextView)findViewById(R.id.tv_weather_wind_speed);
		windScaleText=(TextView)findViewById(R.id.tv_weather_wind_scale);
		aqiText=(TextView)findViewById(R.id.tv_weather_aqi);
		qualityText=(TextView)findViewById(R.id.tv_weather_quality);
		weatherBottomBar=(RelativeLayout)findViewById(R.id.rl_weather_bottombar);		
		dotsGroup=(LinearLayout)findViewById(R.id.ll_weather_bottombar_dots_group);
		settings=(ImageButton)findViewById(com.yizhiweather.app.R.id.ib_weather_bottombar_settings);
		settings.setImageResource(R.drawable.settings);
		selectCity=(ImageButton)findViewById(com.yizhiweather.app.R.id.ib_weather_bottombar_select_city);		
		selectCity.setImageResource(R.drawable.category);
		settings.setOnClickListener(this);
		selectCity.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.ib_weather_bottombar_settings:		
			Intent settingsIntent=new Intent(WeatherActivity.this,SettingsActivity.class);
			startActivity(settingsIntent);
			finish();
			break;
		case R.id.ib_weather_bottombar_select_city:
			Intent chooseAreaIntent=new Intent(WeatherActivity.this,ChooseAreaActivity.class);
			chooseAreaIntent.putExtra("from_weather_activity", true);
			startActivity(chooseAreaIntent);
			finish();
			break;
		case R.id.ib_weather_locate:
			locate();
			break;
		}
	}

	private void locate(){
		statusText.setVisibility(View.VISIBLE);
		statusText.setText("定位中...");
		LocationUtil.setLocationClient(WeatherActivity.this).startLocation();//启动定位
		new Thread() {
			@Override
			public void run() {
				try {
					sleep(1500);//休眠
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if(!LocationUtil.getLocationResult()){
							statusText.setText("定位失败");
							hide(statusText,1500);
						}else {
							String country = LocationUtil.getLocationCountry();
							String city = LocationUtil.getLocationCity();
							if (!country.equals("中国")) {
								statusText.setText("暂不支持获取中国以外地区天气");
								hide(statusText, 1500);
							} else {
								statusText.setText("当前城市：" + city);
								hide(statusText, 1500);
								String ll = LocationUtil.getLocationLL();//获取经纬度
								queryWeatherInfo(ll, REQUEST_NEWQUERY);//根据经纬度查询天气
							}
						}
					}

				});
			}
		}.start();

	}
	
	@Override
	public void onRefresh() {
		SharedPreferences sp = SharedPreferencesUtil.getWeatherPref(this);
		String area_id = sp.getString("area_id", "");
		if (!TextUtils.isEmpty(area_id)) {
			statusText.setVisibility(View.VISIBLE);
			swipeRefreshLayout.setRefreshing(true);
			statusText.setText("更新中...");
			queryWeatherInfo(area_id, REQUEST_REFRESH);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] paramArrayOfInt){
		super.onRequestPermissionsResult(requestCode,permissions,paramArrayOfInt);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		LocationUtil.destroyLocationClient();
	}
	/*
	 * 查询天气(location可以是县级代号或"纬度:经度")
	 */
	private void queryWeatherInfo(String location,int requestCode){
		String address="http://www.thinkpage.cn/weather/api.svc/getWeather?city=" +
				location +"&language=zh-CHS&provider=CMA&unit=C&aqi=city";
		queryFromServer(address,requestCode);
	}
	
	/*
	 * 从服务器查询天气
	 */
	private void queryFromServer(final String address,final int requestCode){
		if(requestCode==REQUEST_NEWQUERY){
			showProgressDialog();			
		}
		HttpUtil.sendHttpRequest(address,new HttpCallbackListener(){
			@Override
			public void onFinish(String response){
				if(!TextUtils.isEmpty(response)){
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					//返回主线程更新界面
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							if(requestCode==REQUEST_REFRESH){								
								swipeRefreshLayout.setRefreshing(false);
								statusText.setText("天气已更新");
								hide(statusText,1500);
							}
							if(requestCode==REQUEST_NEWQUERY){
								closeProgressDialog();
							}
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						if(requestCode==REQUEST_REFRESH){
							swipeRefreshLayout.setRefreshing(false);
							statusText.setText("更新失败，网络异常");
							hide(statusText,1500);
						}
						if(requestCode==REQUEST_NEWQUERY){
							closeProgressDialog();
							Toast.makeText(WeatherActivity.this,"加载失败,网络异常",Toast.LENGTH_SHORT).show();
							showWeather();							
						}						
					}
				});
			}
		});		
	}
	
	/*
	 * 显示天气信息
	 */
	private void showWeather(){
		weatherLayout.setBackgroundResource(UIUtil.getBgId());
		SharedPreferences sp=SharedPreferencesUtil.getWeatherPref(this);
		String areaName=sp.getString("area_name","");
		String weather=sp.getString("weather", "");
		String temperature=sp.getString("temperature","");
		String humidity=sp.getString("humidity","");
		String windDirection=sp.getString("wind_direction","");
		String windSpeed=sp.getString("wind_speed","");
		String windScale=sp.getString("wind_scale","");
		String aqi=sp.getString("aqi","");
		String quality=sp.getString("quality", "");
		String weatherCode=sp.getString("weather_code","");
		areaNameText.setText(areaName);
		weatherText.setText(weather);
		temperatureText.setText(temperature+"°");
		humidityText.setText(humidity);
		windDirectionText.setText(windDirection);
		windSpeedText.setText(windSpeed);
		windScaleText.setText(windScale);
		aqiText.setText(aqi);
		qualityText.setText(quality);
		setTempTrend(sp);//设置温度趋势
		setForecastList(sp);//设置天气预报列表
		setDescription(sp);//设置描述信息
		weatherView.cancelAnimation();
		UIUtil.getWeatherView(weatherView,weatherCode)
		         .startAnimation();////设置天气背景
		Log.d("WeatherActivity","weather code is " + weatherCode);
		divider1.setVisibility(View.VISIBLE);
		divider2.setVisibility(View.VISIBLE);
		divider3.setVisibility(View.VISIBLE);
		divider4.setVisibility(View.VISIBLE);
		divider5.setVisibility(View.VISIBLE);
		divider6.setVisibility(View.VISIBLE);
		basicInfoLayout.setVisibility(View.VISIBLE);
		tempTrend.setVisibility(View.VISIBLE);
		weatherForecastList.setVisibility(View.VISIBLE);
		weatherNowDescription.setVisibility(View.VISIBLE);
		otherInfoLayout.setVisibility(View.VISIBLE);
		
		
		//激活后台定时更新天气的服务（如果是从SettingsActivity跳转过来则无需重新启动服务）
		if(!isFromSettingsActivity){
			Intent intent=new Intent(this,AutoUpdateService.class);
			intent.putExtra(Constants.SERVICE_MODE, Constants.MODE_UPDATE);
			startService(intent);
		}		
	}
	
	/*
	 * 设置温度趋势
	 */
	private void setTempTrend(SharedPreferences sp){

		String dates[]=new String[5];
		int tempHigh[]=new int[5];
		int tempLow[]=new int[5];
		for(int i=0;i<5;i++){
			String mark="_"+i;
			Date date=TimeUtil.stringToDate("yyyy-MM-dd",sp.getString("date"+mark,""));
			String dayOfWeek=TimeUtil.dateToDayOfWeek(date);
			dates[i]=dayOfWeek;
			tempHigh[i]=Integer.valueOf(sp.getString("high"+mark, ""));
			tempLow[i]=Integer.valueOf(sp.getString("low"+mark,""));
		}
		tempTrend.setDates(dates);
		tempTrend.setTempHigh(tempHigh);
		tempTrend.setTempLow(tempLow);
		tempTrend.requestLayout();//控件刷新，重新绘制

		
	}
	
	/*
	 * 设置天气预报列表
	 */
	private void setForecastList(SharedPreferences sp){
		forecastList.clear();
		for(int i=0;i<5;i++){
			DailyForecast dailyForecast=new DailyForecast();
			String mark="_" + i;
			Date date=TimeUtil.stringToDate("yyyy-MM-dd", sp.getString("date" + mark,""));//将解析得到的日期字符串转换成日期
			String dayOfWeek=TimeUtil.dateToDayOfWeek(date);//将日期转换成星期
			dailyForecast.setDate(dayOfWeek);
			dailyForecast.setHigh(sp.getString("high" + mark,""));
			dailyForecast.setLow(sp.getString("low" + mark, ""));
			dailyForecast.setPicId(UIUtil.getWeatherIconId(sp.getString("code_day" + mark,"")));
			forecastList.add(dailyForecast);			
		}
		adapter.notifyDataSetChanged();		
	}
	
	/*
	 * 设置描述信息（如果有警报信息则显示警报，否则显示当前天气描述）
	 */
	private void setDescription(SharedPreferences sp){
		description.setText("当前：" + sp.getString("weather", "") + "，气温" +
                    sp.getString("temperature", "") + "°。今日最高气温" +
		            sp.getString("high_0", "") + "°，最低气温" + sp.getString("low_0", "")+"°。");
	}
	

	/*
	 * 显示progressDialog
	 */
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/*
	 * 关闭progressDialog
	 */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	/*
	 * 延迟隐藏控件
	 */
	private void hide(final View v,long delayMillis){
		v.postDelayed(new Runnable(){
			@Override
			public void run(){
				v.setVisibility(View.INVISIBLE);
			}
		}, delayMillis);
	}
	

}
