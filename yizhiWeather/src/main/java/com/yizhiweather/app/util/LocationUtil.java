package com.yizhiweather.app.util;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;

import android.content.Context;
import android.util.Log;

public class LocationUtil{
	private static AMapLocationClient mLocationClient;
	private static AMapLocationClientOption mLocationOption;
	private static boolean locationResult;//定位是否成功
	private static String locationLL;//经纬度
	private static String locationCountry;//国家信息
	private static String locationProvince;//省份信息
	private static String locationCity;//城市信息
	private static String locationDistrict;//城区信息
	private static AMapLocationListener listener=new AMapLocationListener(){
		@Override
		public void onLocationChanged(AMapLocation amapLocation){
			if(amapLocation!=null){
				if(amapLocation.getErrorCode()==0){	//错误码为0时表示定位成功
					locationResult=true;
					locationLL=amapLocation.getLatitude()+":" +amapLocation.getLongitude();//获取经纬度	
					locationCountry=amapLocation.getCountry();
					locationProvince=amapLocation.getProvince();
					locationCity=amapLocation.getCity();
					locationDistrict=amapLocation.getDistrict();					
					
				}else{
					Log.d("AmapError:","location error,ErrorCode:" + amapLocation.getErrorCode()
						  +",ErrorInfo:" + amapLocation.getErrorInfo());
					locationResult=false;
				}
			}
			Log.d("LocationUtil",String.valueOf(locationResult));
		}
	};

	
	public static boolean getLocationResult(){
		return locationResult;
	}

	public static String getLocationLL(){
		return locationLL;
	}
	
	public static String getLocationCountry(){
		return locationCountry;
	}
	
	public static String getLocationProvince(){
		return locationProvince;
	}
	
	public static String getLocationCity(){
		return locationCity;
	}
	
	public static String getLocationDistrict(){
		return locationDistrict;
	}
	
	public static AMapLocationClient setLocationClient(Context context){
		mLocationClient=new AMapLocationClient(context.getApplicationContext());
		mLocationOption=new AMapLocationClientOption();
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		mLocationOption.setOnceLocation(true);
		mLocationOption.setOnceLocationLatest(true);//获取最近3秒内精度最高的一次定位结果
		mLocationOption.setHttpTimeOut(10000);
		mLocationOption.setNeedAddress(true);
		mLocationClient.setLocationOption(mLocationOption);
		mLocationClient.setLocationListener(listener);
		mLocationClient.startLocation();
		return mLocationClient;
	}

	public static void destroyLocationClient(){
		if(mLocationClient!=null){
			mLocationClient.onDestroy();
		}
	}

}
