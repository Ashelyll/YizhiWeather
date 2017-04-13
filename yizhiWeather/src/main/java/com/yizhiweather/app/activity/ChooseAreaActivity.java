package com.yizhiweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.yizhiweather.app.R;
import com.yizhiweather.app.db.YizhiWeatherDB;
import com.yizhiweather.app.model.City;
import com.yizhiweather.app.model.County;
import com.yizhiweather.app.model.Province;
import com.yizhiweather.app.util.HttpCallbackListener;
import com.yizhiweather.app.util.HttpUtil;
import com.yizhiweather.app.util.NetUtil;
import com.yizhiweather.app.util.SharedPreferencesUtil;
import com.yizhiweather.app.util.SizeUtil;
import com.yizhiweather.app.util.UIUtil;
import com.yizhiweather.app.util.Utility;
import com.yizhiweather.app.view.AreaListAdapter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity implements View.OnClickListener,ListView.OnItemClickListener{
	
	/*
	 * 定义地区列表的选中级别 
	 */
	public static final int LEVEL_PROVINCE=1;
	public static final int LEVEL_CITY=2;
	public static final int LEVEL_COUNTY=3;
	
	private LinearLayout chooseAreaLayout;
	private ProgressDialog progressDialog;
	private RelativeLayout chooseAreaTopBar;
	private TextView titleText;
	private ImageButton topBarBack;
	private ListView areaList;
	private AreaListAdapter adapter;
	private List<String> dataList=new ArrayList<String>();
	private YizhiWeatherDB yizhiWeatherDB;
	
	private List<Province> provinceList;//省列表
	private List<City> cityList;//市列表
	private List<County> countyList;//县列表
	
	private int currentLevel=0;//当前的选中等级
	private Province selectedProvince;//选中的省份
	private City selectedCity;//选中的城市
	private String selectedCountyCode;//手动选择的要查询天气的地区的县级代号
	
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		Log.d("ChooseAreaActivity","onCreate executed");
		super.onCreate(savedInstanceState);
		isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);
		SharedPreferences sp=SharedPreferencesUtil.getWeatherPref(this);
		//如果已经成功查询过某地的天气信息并且没有收到切换城市的请求，则直接显示之前地区的天气信息
		if(sp.getBoolean("area_selected",false)&& !isFromWeatherActivity){	
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		initViews();		
		yizhiWeatherDB=YizhiWeatherDB.getInstance(this);
		queryProvinces();
		
	}
	
	/*
	 * 初始化各控件
	 */
	private void initViews(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.yizhiweather.app.R.layout.ll_choosearea_activity);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏，android 4.4(API 19)以上才支持 
		chooseAreaLayout=(LinearLayout)findViewById(R.id.ll_choosearea_layout);
		chooseAreaLayout.setBackgroundResource(UIUtil.getBgId());
		chooseAreaTopBar=(RelativeLayout)findViewById(com.yizhiweather.app.R.id.rl_choosearea_topbar);
		int statusBarHeight=SizeUtil.getStatusBarHeight(this);
		Log.d("StatusBarHeight:",String.valueOf(statusBarHeight));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0,statusBarHeight*2,0,0);//4个参数按顺序分别是左上右下
		chooseAreaTopBar.setLayoutParams(layoutParams);
		titleText=(TextView)findViewById(com.yizhiweather.app.R.id.tv_choosearea_topbar_title);
		topBarBack=(ImageButton)findViewById(com.yizhiweather.app.R.id.ib_choosearea_topbar_back);
		topBarBack.setImageResource(R.drawable.arrow_left);
		topBarBack.setOnClickListener(this);
		areaList=(ListView)findViewById(com.yizhiweather.app.R.id.lv_choosearea_area_list);
		adapter=new AreaListAdapter(ChooseAreaActivity.this,R.layout.area_item,dataList);//构建adapter
		areaList.setAdapter(adapter);
		areaList.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume(){
		super.onRestart();
		Log.d("ChooseAreaActivity","onResume executed");
		boolean flag=NetUtil.checkNetworkState(ChooseAreaActivity.this);
		if(!flag){
			NetUtil.setNetwork(ChooseAreaActivity.this);
		}else{
			if(currentLevel==0 || currentLevel==LEVEL_PROVINCE){
				queryProvinces();
			}else if(currentLevel==LEVEL_CITY){
				queryCities();				
			}else if(currentLevel==LEVEL_COUNTY){
				queryCounties();
			}
		}		
	}
	
	@Override
	protected void onDestroy(){		
		super.onDestroy();
	}
	
	/*
	 * 覆写OnClickListener的onClick方法
	 */
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.ib_choosearea_topbar_back:
			if(currentLevel==LEVEL_PROVINCE){
				if(isFromWeatherActivity){
					Intent intent=new Intent(this,WeatherActivity.class);
					startActivity(intent);
					finish();
				}else{
					finish();
				}
			}else if(currentLevel==LEVEL_CITY){
				queryProvinces();
			}else if(currentLevel==LEVEL_COUNTY){
				queryCities();
			}
		}
	}
	
	/*
	 *覆写OnItemClickListener的OnItemClick方法
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0,View view,int index,long arg3){
		if(currentLevel==LEVEL_PROVINCE){
			selectedProvince=provinceList.get(index);
			queryCities();//查询该省份下的城市
		}else if(currentLevel==LEVEL_CITY){
			selectedCity=cityList.get(index);
			queryCounties();//查询该市下的县
		}else if(currentLevel==LEVEL_COUNTY){
			selectedCountyCode=countyList.get(index).getCountyCode();
			Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
			intent.putExtra("county_code",selectedCountyCode);
			startActivity(intent);
			finish();
		}
	}
	
	/*
	 * 查询全国所有省份数据，优先从本地数据库查询，当本地数据库中没有时，从服务器查询
	 */
	private void queryProvinces(){
		provinceList=yizhiWeatherDB.loadProvinces();
		if(provinceList.size()>0){	//如果从本地数据库中查到了数据
			dataList.clear();//首先清空dataList
			for(Province province:provinceList){				
				String provinceName=province.getProvinceName();
				dataList.add(provinceName);
			}
			adapter.notifyDataSetChanged();
			areaList.setSelection(0);
			titleText.setText("中国|选择省份");
			currentLevel=LEVEL_PROVINCE;			
		}
		else{
			queryFromServer(null,"province");
		}
	}
	
	
	/*
	 * 查询某个省份下的所有城市，优先从本地数据库查询，本地数据库中查询不到时，再从服务器查询
	 */
	private void queryCities(){
			cityList=yizhiWeatherDB.loadCities(selectedProvince.getId());
			if(cityList.size()>0){
				dataList.clear();
				for(City city:cityList){
					String cityName=city.getCityName();
					dataList.add(cityName);
				}
				adapter.notifyDataSetChanged();
				titleText.setText(selectedProvince.getProvinceName()+"|选择城市");
				areaList.setSelection(0);
				currentLevel=LEVEL_CITY;
			}else{
				queryFromServer(selectedProvince.getProvinceCode(),"city");
			}
		
	}
	
	/*
	 * 查询某个城市下的县市，优先从本地数据库查询，本地数据库查询不到时，从服务器查询
	 */
	private void queryCounties(){
			countyList=yizhiWeatherDB.loadCounties(selectedCity.getId());
			if(countyList.size()>0){
				dataList.clear();
				for(County county:countyList){
					String countyName=county.getCountyName();
					dataList.add(countyName);
				}
				adapter.notifyDataSetChanged();
				titleText.setText(selectedCity.getCityName()+"|选择县市/城区");
				areaList.setSelection(0);
				currentLevel=LEVEL_COUNTY;
			}else{
				queryFromServer(selectedCity.getCityCode(),"county");				
			}
	}
	
	/*
	 * 从服务器查询省、市、县信息
	 */
	private void queryFromServer(final String code,final String type){
		String address;
		if(code!=null){
			address="http://www.thinkpage.cn/weather/WeatherService.svc/GetChildLocations?id=" + code + "&lang=zh-CHS&provider=CMA";
		}else{
			 address="http://www.thinkpage.cn/weather/WeatherService.svc/GetChildLocations?id=CH&lang=zh-CHS&provider=CMA";
		}		
		
		//发送Http请求前先获取网络状态
		boolean flag=NetUtil.checkNetworkState(ChooseAreaActivity.this);
		if(!flag){
			NetUtil.setNetwork(ChooseAreaActivity.this);//调用系统设置
		}else{
			showProgressDialog();
			HttpUtil.sendHttpRequest(address,new HttpCallbackListener(){
				@Override
				public void onFinish(String response){
					boolean result=false;
					if("province".equals(type)){
						result=Utility.handleProvincesResponse(yizhiWeatherDB,response);
					}else if("city".equals(type)){
						result=Utility.handleCitiesResponse(yizhiWeatherDB,response,selectedProvince.getId());
					}else if("county".equals(type)){
						result=Utility.handleCountiesResponse(yizhiWeatherDB,response,selectedCity.getId());
					}
					if(result){
						//通过runOnUiThread()方法回到主线程
						runOnUiThread(new Runnable(){
							@Override
							public void run(){
								closeProgressDialog();
								if("province".equals(type)){
									queryProvinces();
								}else if("city".equals(type)){
									queryCities();
								}else if("county".equals(type)){
									queryCounties();
								}
							}
						});
					}
				}
				@Override
				public void onError(Exception e){
					/*如果网络请求过程中出现异常，则直接通过runOnUiThread()方法回到主线程*/
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							closeProgressDialog();
							Toast.makeText(ChooseAreaActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
		}		
	}	
	
	/*
	 * 显示progressDialog
	 */
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/*
	 * 关闭progressDialog
	 */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	/*
	 * 捕获Back按键
	 */
	@Override
	public void onBackPressed(){
		if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel==LEVEL_CITY){
			queryProvinces();			
		}else{
			if(isFromWeatherActivity){	//如果是从WeatherActivity跳转过来，则按下back键应该跳回WeatherActivity
				Intent intent=new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();			
		}
	}

}
