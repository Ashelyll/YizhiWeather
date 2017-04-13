package com.yizhiweather.app.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.yizhiweather.app.db.YizhiWeatherDB;
import com.yizhiweather.app.model.City;
import com.yizhiweather.app.model.County;
import com.yizhiweather.app.model.Province;

/*Utility类用于对服务器返回的数据进行解析*/
public class Utility {
	private static SharedPreferences sp;
	private static SharedPreferences.Editor editor;
	/*
	 * 解析和处理服务器返回的省份数据：对返回的JSON数据解析后，
	 * 根据每条JSON数据创建一个Province对象，并将其存入本地数据库
	 */
	public synchronized static boolean handleProvincesResponse(
							YizhiWeatherDB yizhiWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			try{
				JSONArray jsonArray=new JSONArray(response);//返回数据中包含多条JSON格式数据，所以创建JSONArray数组
				for(int i=0;i<jsonArray.length();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					Province province=new Province();
					province.setProvinceName(jsonObject.getString("value"));
					province.setProvinceCode(jsonObject.getString("key"));
					yizhiWeatherDB.saveProvince(province);
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	
	/*
	 * 解析和处理服务器返回的城市数据：对返回的JSON数据解析后，
	 * 根据每条JSON数据创建一个City对象，并将其存入本地数据库
	 */
	public synchronized static boolean handleCitiesResponse(YizhiWeatherDB yizhiWeatherDB,
										String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			try{
				JSONArray jsonArray=new JSONArray(response);
				for(int i=0;i<jsonArray.length();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					City city=new City();
					city.setCityName(jsonObject.getString("value"));
					city.setCityCode(jsonObject.getString("key"));
					city.setProvinceId(provinceId);
					yizhiWeatherDB.saveCity(city);
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return true;
		}
		return false;		
	}
	
	
	/*
	 * 解析和处理服务器返回的县市数据
	 */
	public synchronized static boolean handleCountiesResponse(YizhiWeatherDB yizhiWeatherDB,
														String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			try{
				JSONArray jsonArray=new JSONArray(response);
				for(int i=0;i<jsonArray.length();i++){
					JSONObject jsonObject=jsonArray.getJSONObject(i);
					County county=new County();
					county.setCountyName(jsonObject.getString("value"));
					county.setCountyCode(jsonObject.getString("key"));
					county.setCityId(cityId);
					yizhiWeatherDB.saveCounty(county);
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	
	/*
	 * 解析和处理服务器返回的天气信息
	 */
	public static void handleWeatherResponse(Context context,String response){
		try{
			JSONObject jsonObject=new JSONObject(response);
			JSONArray resultsArray=jsonObject.getJSONArray("results");//数组长度为1
			JSONObject info=resultsArray.getJSONObject(0);//获取数组元素（一个JSONObject对象）
			
			/*获取location信息*/	
			JSONObject location=info.getJSONObject("location");
			String areaId=location.getString("id");
			String areaName=location.getString("name");
			String country=location.getString("country");
			String path=location.getString("path");
			String timezone=location.getString("timezone");
			String timezone_offset=location.getString("timezone_offset");
			saveLocation(context,areaId,areaName,country,path,timezone,timezone_offset);
			
			/*获取实时天气信息*/
			JSONObject now=info.getJSONObject("now");
			String weather=now.getString("text");
			String weather_code=now.getString("code");
			String temperature=now.getString("temperature");
			String humidity=now.getString("humidity");
			String wind_direction=now.getString("wind_direction");
			String wind_speed=now.getString("wind_speed");
			String wind_scale=now.getString("wind_scale");
			saveWeatherNow(weather,weather_code,temperature,humidity,
					wind_direction,wind_speed,wind_scale);
			
			/*获取今天和未来4天的天气预报信息*/
			JSONArray dailyArray=info.getJSONArray("daily");
			for(int i=0;i<dailyArray.length();i++){
				int dateId=i;
				JSONObject daily=dailyArray.getJSONObject(i);
				String date=daily.getString("date");
				String weather_day=daily.getString("text_day");
				String code_day=daily.getString("code_day");
				String weather_night=daily.getString("text_night");
				String code_night=daily.getString("code_night");
				String high=daily.getString("high");
				String low=daily.getString("low");
				saveWeatherForecast(dateId,date,weather_day,code_day,weather_night,
						code_night,high,low);
			}
			
			/*获取空气质量*/
			JSONObject air=info.getJSONObject("air");
			JSONObject city=air.getJSONObject("city");
			String aqi=city.getString("aqi");
			String quality=city.getString("quality");
			
			/*获取警报信息*/
			JSONArray alarmsArray=info.getJSONArray("alarms");
			String alarmType="";
			String alarmLevel = "";
			String alarmDescription = "";
			String alarmPubTime = "";
			if(alarmsArray.length()>0) {
				JSONObject alarmsInfo = alarmsArray.getJSONObject(0);
				 alarmType = alarmsInfo.getString("type");
				 alarmLevel = alarmsInfo.getString("level");
				 alarmDescription = alarmsInfo.getString("description");
				 alarmPubTime = alarmsInfo.getString("pub_date");

			}
			
			/*获取更新时间*/
			String update_time=info.getString("last_update");			
			saveOtherInfo(aqi,quality,alarmType,alarmLevel,alarmDescription,alarmPubTime,update_time);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * 保存location信息
	 */
	private static void saveLocation(Context context,String areaId,String areaName,String country,
								String path,String timezone,String timezone_offset){
		sp=SharedPreferencesUtil.getWeatherPref(context);
		editor=sp.edit();
		editor.putBoolean("area_selected",true);
		editor.putString("area_id", areaId);
		editor.putString("area_name", areaName);
		editor.putString("country", country);
		editor.putString("path", path);
		editor.putString("timezone",timezone);
		editor.putString("timezone_offset",timezone_offset);
		editor.commit();
	}
	
	/*
	 * 保存实时天气信息
	 */
	private static void saveWeatherNow(String weather,String weather_code,String temperature,
			String humidity,String wind_direction,String wind_speed,String wind_scale){
		editor.putString("weather", weather);
		editor.putString("weather_code", weather_code);
		editor.putString("temperature", temperature);
		editor.putString("humidity",humidity);
		editor.putString("wind_direction", wind_direction);
		editor.putString("wind_speed", wind_speed);
		editor.putString("wind_scale", wind_scale);
		editor.commit();
	}
	
	/*
	 * 保存未来5天的天气预报信息
	 */
	private static void saveWeatherForecast(int dateId,String date,String weather_day,
		String code_day,String weather_night,String code_night,String high,String low){
		String mark="_" + String.valueOf(dateId);
		editor.putString("date" + mark, date);
		editor.putString("weather_day" + mark, weather_day);
		editor.putString("code_day" + mark, code_day);
		editor.putString("weather_night" + mark, weather_night);
		editor.putString("code_night" + mark, code_night);
		editor.putString("high" + mark,high);
		editor.putString("low" + mark,low);
		editor.commit();
	}
	
	/*
	 * 保存空气质量、警报信息和更新时间
	 */
	private static void saveOtherInfo(String aqi,String quality,String alarmType,
			                          String alarmLevel,String alarmDescription,
			                          String alarmPubTime,String update_time){
		SharedPreferences.Editor editor=sp.edit();
		editor.putString("aqi", aqi);
		editor.putString("quality", quality);
		editor.putString("alarmType", alarmType);
		editor.putString("alarmLevel", alarmLevel);
		editor.putString("alarmDescription", alarmDescription);
		editor.putString("alarmPubTime",alarmPubTime);
		editor.putString("update_time", update_time);
		editor.commit();
	}

	
}
