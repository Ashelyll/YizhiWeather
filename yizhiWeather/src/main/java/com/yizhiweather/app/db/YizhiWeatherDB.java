package com.yizhiweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.yizhiweather.app.model.City;
import com.yizhiweather.app.model.County;
import com.yizhiweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/*YizhiWeatherDB用于封装与数据库相关的操作（查询数据、插入数据等），另外为了保证全局范围中只有
 * 一个YizhiWeatherDB的实例，需要对其构造方法进行私有化。
 */
public class YizhiWeatherDB {
	public static final String DB_NAME="yizhi_weather";//数据库名称
	public static final int DB_VERSION=2;//数据库版本
	public static YizhiWeatherDB yizhiWeatherDB;
	private SQLiteDatabase db;
	
	public static final String SEARCH_NO_EXIST=null;//数据不存在时的返回值
	public static final String SEARCH_NO_INPUT="no input";//没有输入而进行搜索时的返回值
	
	

 
	
	/*构造方法私有化，其构造方法中完成创建数据库和数据表的操作，并得到可对数据库进行操作的
	 * SQLiteDATABASE对象
	 */
	private YizhiWeatherDB(Context context){
		YizhiWeatherOpenHelper helper=new YizhiWeatherOpenHelper(context,DB_NAME,null,DB_VERSION);
		db=helper.getWritableDatabase();
	}
	
	/*
	 * 获取YizhiWeatherDB的实例
	 */
	public synchronized static YizhiWeatherDB getInstance(Context context){
		if(yizhiWeatherDB==null){
			yizhiWeatherDB=new YizhiWeatherDB(context);
		}
		return yizhiWeatherDB;			
	}
	
	/*
	 * 向Province表中存储省份信息
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province",null,values);
		}
	}
	
	
	/*
	 * 从Province表中读取省份数据，并根据每一条省份数据构建一个Province对象，
	 * 然后将这些Province对象存放在provinceList列表中
	 */
	public List<Province> loadProvinces(){
		List<Province> provinceList=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null,null);//将查询到的结果放进Cursor对象中，并按名称的字母顺序排列
		if(cursor.moveToFirst()){
			do{
				Province province=new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				provinceList.add(province);
			}while(cursor.moveToNext());			
		}
		return provinceList;
	}
	
	
	/*
	 * 向City表中存储城市信息
	 */
	public void saveCity(City city){
		if(city!=null){
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City",null,values);
		}
	}
	
	
	/*
	 * 从City表中读取城市数据，并根据每条城市数据创建一个City对象，
	 * 然后将这些City对象存放在cityList列表中。
	 */
	public List<City> loadCities(int provinceId){
		List<City> cityList=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				cityList.add(city);
			}while(cursor.moveToNext());
		}
		return cityList;
	}
	
	/*
	 * 向County表中存储县市信息
	 */
	public void saveCounty(County county){
		if(county!=null){
			ContentValues values=new ContentValues();
			values.put("county_name",county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County",null,values);
		}
	}
	
	/*
	 * 从County表中读取县市信息
	 */
	public List<County> loadCounties(int cityId){
		List<County> countyList=new ArrayList<County>();
		Cursor cursor=db.query("County", null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				countyList.add(county);
			}while(cursor.moveToNext());
		}
		return countyList;
	}
	
	/*
	 * 根据名称从数据库中查找指定数据是否存在（为实现搜索功能而设计）
	 */
	public String searchFromDatabase(String name){	//优先从County表中查询，County表中没找到再从Province表查询
		if(!TextUtils.isEmpty(name)){
			String countyCode=queryFromCounty(name);//从County数据表中查询
			if(countyCode!=null){	
				return countyCode;
			}else{
				Province province=queryFromProvince(name);
				if(province!=null){
					return String.valueOf(province.getId());
				}else{
					return SEARCH_NO_EXIST;
				}
			}
		}else{
			return SEARCH_NO_INPUT;//
		}
	}
	
	/*
	 * 从Province数据表中查询数据
	 */
	public Province queryFromProvince(String name){		//查询Province表时，如果存在则返回Province对象，否则返回null
		Cursor cursor=db.query("Province",null,"province_name=?",new String[]{name},null,null,null);
		if(cursor.moveToFirst()){
			Province province=new Province();
			province.setId(cursor.getInt(cursor.getColumnIndex("id")));
			province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
			province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
			return province;
		}else{
			return null;
		}
	}
	
	/*
	 * 从City数据表中查询数据
	 */
	public City queryFromCity(String name){
		Cursor cursor=db.query("City", null,"city_name=?",new String[]{name},null,null,null);
		if(cursor.moveToFirst()){
			City city=new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
			city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
			city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
			return city;
		}else{
			return null;
		}
	}
	/*
	 * 从County数据表中查询数据
	 */
	public String queryFromCounty(String name){		//查询County表时,如果存在则返回县级代号，否则返回null
		Cursor cursor=db.query("County",new String[]{"county_code"},"county_name=?",new String[]{name},null,null,null);
		if(cursor.moveToFirst()){
			return cursor.getString(cursor.getColumnIndex("county_code"));
		}else{
			return null;
		}
	}
}
