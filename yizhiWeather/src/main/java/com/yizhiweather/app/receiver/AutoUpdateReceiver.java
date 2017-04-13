package com.yizhiweather.app.receiver;

import com.yizhiweather.app.service.AutoUpdateService;
import com.yizhiweather.app.util.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i=new Intent(context,AutoUpdateService.class);
		i.putExtra(Constants.SERVICE_MODE, Constants.MODE_UPDATE);
		context.startService(i);
		
	}

}
