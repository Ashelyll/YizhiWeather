package com.yizhiweather.app.view;

import java.util.List;

import com.yizhiweather.app.R;
import com.yizhiweather.app.model.DailyForecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastListAdapter extends ArrayAdapter<DailyForecast> {
	private int listItemId;
	public ForecastListAdapter(Context context,int resourceId,List<DailyForecast> dataList){
		super(context,resourceId,dataList);
		listItemId=resourceId;
	}
	
	class ViewHolder{
		private TextView date;
		private ImageView pic;
		private TextView high;
		private TextView low;
	}
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		DailyForecast dailyForecast=getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(listItemId, null);
			viewHolder=new ViewHolder();
			viewHolder.date=(TextView)view.findViewById(R.id.date);
			viewHolder.pic=(ImageView)view.findViewById(R.id.pic);
			viewHolder.high=(TextView)view.findViewById(R.id.high);
			viewHolder.low=(TextView)view.findViewById(R.id.low);
			view.setTag(viewHolder);
		}else{
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}
		viewHolder.date.setText(dailyForecast.getDate());
		viewHolder.pic.setImageResource(dailyForecast.getPicId());
		viewHolder.high.setText(dailyForecast.getHigh());
		viewHolder.low.setText(dailyForecast.getLow());
		return view;
	}
	
}
