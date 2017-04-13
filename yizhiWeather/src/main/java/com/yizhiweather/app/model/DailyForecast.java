package com.yizhiweather.app.model;

public class DailyForecast {
	private String date;
	private int picId;
	private String high;
	private String low;
	public String getDate(){
		return this.date;
	}
	public void setDate(String date){
		this.date=date;
	}
	public int getPicId(){
		return this.picId;
	}
	public void setPicId(int picId){
		this.picId=picId;
	}
	public String getHigh(){
		return this.high;
	}
	public void setHigh(String high){
		this.high=high;
	}
	public String getLow(){
		return this.low;
	}
	public void setLow(String low){
		this.low=low;
	}
	

}
