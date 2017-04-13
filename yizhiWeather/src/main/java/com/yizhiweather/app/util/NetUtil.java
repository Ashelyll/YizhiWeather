package com.yizhiweather.app.util;

import com.yizhiweather.app.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetUtil{
	public static final int SET_NETWORK=1;
	public static boolean checkNetworkState(Context context){
		boolean flag;
		ConnectivityManager manager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(manager.getActiveNetworkInfo()!=null){
			flag=manager.getActiveNetworkInfo().isAvailable();
		}else{
			flag=false;
		}
		return flag;
	}
	
	public static void setNetwork(final Activity activity){
		if(!checkNetworkState(activity)){
			AlertDialog.Builder builder=new AlertDialog.Builder(activity);
			builder.setIcon(R.drawable.ios_weather_icon)
		       .setTitle(R.string.app_name)
		       .setCancelable(false)
		       .setMessage("易知天气请求访问网络，当前无网络连接，是否前往设置？")
		       .setPositiveButton(R.string.settings,new OnClickListener(){	
		    	   @Override
		    	   public void onClick(DialogInterface dialog,int which){
		    		   Intent intent=null;
		    		   /*
		    		    * 判断手机系统版本！3.0（API 10）以上的版本与3.0以下的版本调用设置的方法不同 
		    		    */
		    		   if(android.os.Build.VERSION.SDK_INT>10){
		    			   intent=new Intent(android.provider.Settings.ACTION_SETTINGS);
		    		   }else{
		    			   intent=new Intent();
		    			   ComponentName component=new ComponentName(activity,"com.android.settings");
		    			   intent.setComponent(component);
		    			   intent.setAction("android.intent.action.VIEW");	    			   
		    		   }
		    		   activity.startActivityForResult(intent,SET_NETWORK);	
		    	   }
		       })
		       .setNegativeButton(R.string.cancel,new OnClickListener(){
		    	   @Override
		    	   public void onClick(DialogInterface dialog,int which){
					   Toast.makeText(activity,"网络未连接",Toast.LENGTH_SHORT).show();
					  
		    	   }
		       });
		builder.create();
		builder.show();
		}
		
		
	}
		
}
