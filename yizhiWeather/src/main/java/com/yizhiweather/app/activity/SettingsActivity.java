package com.yizhiweather.app.activity;

import com.yizhiweather.app.R;
import com.yizhiweather.app.service.AutoUpdateService;
import com.yizhiweather.app.util.Constants;
import com.yizhiweather.app.util.SharedPreferencesUtil;
import com.yizhiweather.app.util.SizeUtil;
import com.yizhiweather.app.util.UIUtil;
import com.yizhiweather.app.view.UpdateFrequencyDialogFragment;
import com.yizhiweather.app.view.UpdateFrequencyDialogFragment.UpdateFrequencyDialogListener;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsActivity extends FragmentActivity implements View.OnClickListener,UpdateFrequencyDialogListener{	
	private LinearLayout settingsLayout;
	private RelativeLayout settingsTopBar;
	private ImageButton topBarBack;
	private LinearLayout setUpdateFrequency;
	private TextView currentFreq;
	private RelativeLayout allowNotification;
	private ImageButton notifSwitch;	

	public static final int FREQ_NO=0;
	public static final int FREQ_ONE=1;
	public static final int FREQ_TWO=2;
	public static final int FREQ_THREE=3;
	public static final int FREQ_FOUR=4;
	private boolean notifFlag=false;

	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		sp=SharedPreferencesUtil.getSettingsPref(this);
		editor=sp.edit();
		initViews();		
	}

	
	private void initViews(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.ll_settings_activity);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏，android 4.4(API 19)以上才支持 
		settingsLayout=(LinearLayout)findViewById(R.id.ll_settings_layout);
		settingsLayout.setBackgroundResource(UIUtil.getBgId());
		settingsTopBar=(RelativeLayout)findViewById(R.id.rl_settings_topbar);
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		int statusBarHeight=SizeUtil.getStatusBarHeight(this);
		layoutParams.setMargins(0,statusBarHeight*2,0,0);
		settingsTopBar.setLayoutParams(layoutParams);
		topBarBack=(ImageButton)findViewById(R.id.ib_settings_topbar_back);
		topBarBack.setImageResource(R.drawable.arrow_left);
		topBarBack.setOnClickListener(this);
		setUpdateFrequency=(LinearLayout)findViewById(R.id.ll_settings_set_updfreq);
		setUpdateFrequency.setOnClickListener(this);
		currentFreq=(TextView)findViewById(R.id.tv_settings_current_updfreq);
		switch(sp.getInt(Constants.UPDFREQ_PREF, Constants.DEFAULT_UPDFREQ)){
		case 0:
			currentFreq.setText("不自动更新");
			break;
		case 1:
			currentFreq.setText("1小时");
			break;
		case 2:
			currentFreq.setText("2小时");
			break;
		case 3:
			currentFreq.setText("3小时");
			break;
		case 4:
			currentFreq.setText("4小时");
			break;
		}
		allowNotification=(RelativeLayout)findViewById(R.id.rl_settings_allow_notif);
		allowNotification.setOnClickListener(this);
		notifSwitch=(ImageButton)findViewById(R.id.ib_settings_notif_switch);
		if(sp.getBoolean(Constants.NOTIF_PREF, Constants.DEFAULT_NOTIF)){
			notifSwitch.setImageResource(R.drawable.switch_on);
		}else{
			notifSwitch.setImageResource(R.drawable.switch_off);
		}
		notifSwitch.setOnClickListener(this);
		
	}
	
	
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.ib_settings_topbar_back:
			Intent weatherIntent=new Intent(SettingsActivity.this,WeatherActivity.class);
			weatherIntent.putExtra("from_settings_activity",true);
			startActivity(weatherIntent);
			finish();
			break;
		
		case R.id.ll_settings_set_updfreq:
			showUpdateFreqDialog();
			break;

		case R.id.rl_settings_allow_notif:
			switchNotifState();			
			break;
		case R.id.ib_settings_notif_switch:
			switchNotifState();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onDialogItemClick(int which){
		switch(which){
			case 0:
				editor.putInt(Constants.UPDFREQ_PREF,FREQ_NO);
				editor.commit();
				currentFreq.setText("不自动更新");
				startAutoUpdateService(Constants.MODE_CHANGE_FREQ);
				break;
			case 1:
				editor.putInt(Constants.UPDFREQ_PREF,FREQ_ONE);
				editor.commit();
				currentFreq.setText("1小时");
				startAutoUpdateService(Constants.MODE_CHANGE_FREQ);
				break;
			case 2:
				editor.putInt(Constants.UPDFREQ_PREF,FREQ_TWO);
				editor.commit();
				currentFreq.setText("2小时");
				startAutoUpdateService(Constants.MODE_CHANGE_FREQ);
				break;
			case 3:
				editor.putInt(Constants.UPDFREQ_PREF,FREQ_THREE);
				editor.commit();
				currentFreq.setText("3小时");
				startAutoUpdateService(Constants.MODE_CHANGE_FREQ);
				break;
			case 4:
				editor.putInt(Constants.UPDFREQ_PREF,FREQ_FOUR);
				editor.commit();
				currentFreq.setText("4小时");
				startAutoUpdateService(Constants.MODE_CHANGE_FREQ);
				break;
		}
	}
	
	private void showUpdateFreqDialog(){
		DialogFragment dialogFragment = new UpdateFrequencyDialogFragment();		
        dialogFragment.show(getSupportFragmentManager(),"upd_freq_dialog");
	}

	
	private void switchNotifState(){
		if(notifFlag){
			notifFlag=false;
			notifSwitch.setImageResource(R.drawable.switch_off);			
		}else{
			notifFlag=true;
			notifSwitch.setImageResource(R.drawable.switch_on);
		}
		editor.putBoolean(Constants.NOTIF_PREF,notifFlag);
		editor.commit();	
		startAutoUpdateService(Constants.MODE_CHANGE_NOTIF);
		
	}
	
	private void startAutoUpdateService(int mode){
		switch(mode){
		case Constants.MODE_CHANGE_FREQ:
			Intent intent1=new Intent(this,AutoUpdateService.class);
			intent1.putExtra(Constants.SERVICE_MODE, mode);
			startService(intent1);
			break;
		case Constants.MODE_CHANGE_NOTIF:
			Intent intent2=new Intent(this,AutoUpdateService.class);
			intent2.putExtra(Constants.SERVICE_MODE, mode);
			startService(intent2);
		}
		
	}
	
	
	/*
	 * 捕获Back按键，在设置界面按Back键返回天气界面
	 */
	@Override
	public void onBackPressed(){
		Intent weatherIntent=new Intent(this,WeatherActivity.class);
		weatherIntent.putExtra("from_settings_activity",true);
		startActivity(weatherIntent);
		finish();
	}


}
