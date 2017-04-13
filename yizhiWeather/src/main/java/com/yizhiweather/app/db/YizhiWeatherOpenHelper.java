package com.yizhiweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/*YizhiWeatherOpenHelper用于完成创建数据库和数据表以及日后的数据库升级操作*/
public class YizhiWeatherOpenHelper extends SQLiteOpenHelper {
	/*建表语句：建立Province表*/
	public static final String CREATE_PROVINCE="create table Province(" +
			"id integer primary key autoincrement," +
			"province_name text," +
			"province_code text)";
	
	/*建表语句：建立City表*/
	public static final String CREATE_CITY="create table City(" +
			"id integer primary key autoincrement," +
			"city_name text," +
			"city_code text," +
			"province_id integer)";//province_id作为与Province表联系的外键	
	
	/*建表语句：建立County表*/
	public static final String CREATE_COUNTY="create table County(" +
			"id integer primary key autoincrement," +
			"county_name text," +
			"county_code text," +
			"city_id integer)";	//city_id作为与County表联系的外键
	
	
	/*覆写SQLiteOpenHelper的构造方法*/
	public YizhiWeatherOpenHelper(Context context,String name,CursorFactory cursor,int version){
		super(context,name,cursor,version);
	}
	
	
	@Override
	/*执行建表语句，即创建对应的数据表*/
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		switch(oldVersion){
		case 1:
			db.execSQL(CREATE_COUNTY);
		default:
		}

	}

}
