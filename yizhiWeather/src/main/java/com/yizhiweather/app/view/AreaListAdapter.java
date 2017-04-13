package com.yizhiweather.app.view;

import java.util.List;

import com.yizhiweather.app.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*自定义ListViewAdapter继承自ArrayAdapter,用作设置ListView的适配器*/
public class AreaListAdapter extends ArrayAdapter<String> {
	private int listItemId;//子项布局id
	
	/*构造方法*/
	public AreaListAdapter(Context context,int resourceId,List<String> dataList){
		super(context,resourceId,dataList);
		listItemId=resourceId;
	}
	
	/*重写getView()方法,当每个子项被滚动到屏幕内时调用*/
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		String str=getItem(position);//得到指定位置的数据
		View view;
		ViewHolder viewHolder;
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(listItemId,null);//使用LayoutInflater的inflate()方法为子项加载布局,并得到子项
			viewHolder=new ViewHolder();
			viewHolder.textView=(TextView)view.findViewById(R.id.data_name);
			view.setTag(viewHolder);
		}else{
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}
		viewHolder.textView.setText(str);
		return view;
	}
		
		/*定义内部类ViewHolder用于存储每个Item对象，提升代码性能*/
		class ViewHolder{		
			TextView textView;
		}
}

	
	
	