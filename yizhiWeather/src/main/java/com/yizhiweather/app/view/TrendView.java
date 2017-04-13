package com.yizhiweather.app.view;


import com.yizhiweather.app.R;
import com.yizhiweather.app.util.SizeUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TrendView extends View {
	
	/*
	 * TrendView中以控件左上角为原点，
	 * 以向右为x轴正向，向下为y轴正向建立坐标系
	 *  
	 */
	public static final int TYPE_HIGH=0;//高温曲线类型值
	
	public static final int TYPE_LOW=1;//低温曲线类型值
	
	
	/*x坐标集合*/
	private float xCrds[]=new float[5];
	
	/*高温y坐标集合*/
	private float yCrdsHigh[]=new float[5];
	
	/*低温y坐标集合*/
	private float yCrdsLow[]=new float[5];
	
	/*日期y坐标(所有日期均在同一高度上）*/
	private float yCrdsDate;
		
	/*总点数*/
	private static final int COUNTS=5;
	
	/*高温数据集合*/
	private int tempHigh[]=new int[5];
	
	/*低温数据集合*/
	private int tempLow[]=new int[5];
	
	/*日期数据集合*/
	private String dates[]=new String[5];
	
	/*控件高度*/
	private int height;
	
	/*字体大小*/
	private float textSize;
	
	/*字体颜色*/
	private int textColor;
	
	/*文字距连接点的偏移距离*/
	private float textSpace;
	
	/*连接点半径*/
	private float radius;
	
	private float radiusToday;
	
	/*线条粗细*/
	private float strokeWidth;
	
	/*高温折线颜色*/
	private int highLineColor;
	
	/*低温折线颜色*/
	private int lowLineColor;	
	
	/*高温连接点颜色*/
	private int highDotColor;
	
	/*低温连接点颜色*/
	private int lowDotColor;
	
	/*屏幕密度*/
	private float density;
	
	/*坐标系距控件边缘的留白距离*/
	private float space;
	
	public TrendView(Context context){
		super(context);
	}
	
	
	@TargetApi(23) public TrendView(Context context,AttributeSet attrs){
		super(context,attrs);
		TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.TrendView);//从属性集合中获取自定义属性
		textSize=ta.getDimensionPixelSize(R.styleable.TrendView_textSize,SizeUtil.sp2px(context, 15));//从自定义属性中获取textSize,如果未指定则默认为15sp
		//判断系统的sdk版本，因为getColor(int index,int defValue)方法要求最小API为23，且Resources中的getColor(int)方法在API23中已被弃用。
		if(android.os.Build.VERSION.SDK_INT>=23){
			highLineColor=ta.getColor(R.styleable.TrendView_highLineColor,getResources().getColor(R.color.defaultHighColor,null));
			lowLineColor=ta.getColor(R.styleable.TrendView_lowLineColor, getResources().getColor(R.color.defaultLowColor,null));
			textColor=ta.getColor(R.styleable.TrendView_textColor, getResources().getColor(R.color.weatherTextColor,null));
			highDotColor=ta.getColor(R.styleable.TrendView_highDotColor, getResources().getColor(R.color.defaultHighColor,null));
			lowDotColor=ta.getColor(R.styleable.TrendView_lowDotColor, getResources().getColor(R.color.defaultLowColor,null));
		}else{
			highLineColor=ta.getInt(R.styleable.TrendView_highLineColor,getResources().getColor(R.color.defaultHighColor));
			lowLineColor=ta.getInt(R.styleable.TrendView_lowLineColor, getResources().getColor(R.color.defaultLowColor));
			textColor=ta.getInt(R.styleable.TrendView_textColor, getResources().getColor(R.color.weatherTextColor));
			highDotColor=ta.getInt(R.styleable.TrendView_highDotColor, getResources().getColor(R.color.defaultHighColor));
			lowDotColor=ta.getInt(R.styleable.TrendView_lowDotColor, getResources().getColor(R.color.defaultLowColor));
		}
		density=SizeUtil.getScreenDensity(context);//获取屏幕密度
		radius=3*density;
		radiusToday=5*density;
		space=3*density;
		textSpace=8*density;
		strokeWidth=2*density;
		yCrdsDate=space+textSpace+textSize;//日期纵坐标为space+textSpace
		ta.recycle();
	}
	
	
	/*
	 * 设置高温数据集合
	 */
	public void setTempHigh(int[] tempHigh){
		this.tempHigh=tempHigh;
	}
	
	/*
	 * 设置低温数据集合
	 */
	public void setTempLow(int[] tempLow){
		this.tempLow=tempLow;
	}
	
	/*
	 * 设置日期数据集合
	 */
	public void setDates(String[] dates){
		this.dates=dates;
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		//获取控件高度
		height=getHeight();
		
		//设置各点的x坐标
		setXCrds();
		
		//计算各温度点的y坐标
		computeYCrdsValues();
		
		
		//画高温折线图
		drawChart(canvas,highLineColor,highDotColor,tempHigh,yCrdsHigh,0);
		
		//画低温折线图
		drawChart(canvas,lowLineColor,lowDotColor,tempLow,yCrdsLow,1);
		
		//绘制日期文字
		drawDateText(canvas,dates);
		

		
	}
	
	
	/*
	 * 设置各点的x坐标
	 */
	private void setXCrds(){
		int width=getWidth();	//获取控件宽度
		float xInterval=width/(COUNTS*2);	//计算x轴单位间隔（每两个点之间在x轴方向上的间隔）
		xCrds[0]=xInterval;
		xCrds[1]=xInterval*3;
		xCrds[2]=xInterval*5;
		xCrds[3]=xInterval*7;
		xCrds[4]=xInterval*9;
	}
	
	
	/*
	 * 计算各温度点的y坐标值
	 */
	private void computeYCrdsValues(){
		//找出高温的最大值和最小值
		int maxTempHigh=tempHigh[0];
		int minTempHigh=tempHigh[0];
		for(int i=0;i<tempHigh.length;i++){
			if(tempHigh[i]<minTempHigh){
				minTempHigh=tempHigh[i];
			}
			if(tempHigh[i]>maxTempHigh){
				maxTempHigh=tempHigh[i];
			}
		}
		
		//找出低温的最大值和最小值
		int maxTempLow=tempLow[0];
		int minTempLow=tempLow[0];
		for(int i=0;i<tempLow.length;i++){
			if(tempLow[i]<minTempLow){
				minTempLow=tempLow[i];
			}
			if(tempLow[i]>maxTempLow){
				maxTempLow=tempLow[i];
			}
		}
		
		//找出所有温度值中的最大值和最小值
		int minTemp=minTempLow < minTempHigh ? minTempLow : minTempHigh;
		int maxTemp=maxTempHigh > maxTempLow ? maxTempHigh : maxTempLow;
		
		//计算最大值与最小值之间的差值
		int dif=maxTemp-minTemp;
		
		//日期文字到控件顶端的距离
		float d1=space+textSpace+textSize;
		
		//y轴顶端到日期文字的距离
		float d2=textSpace+textSize+textSpace+radius;//连接点半径+温度文字距连接点的空白高度+温度文字高度+温度文字距日期文字的空白高度
		
		//y轴顶端到控件顶端的距离
		float d3=d1+d2;
		
		//y轴底端到控件底端的距离
		float d4=d2+space;
		
		//计算y轴高度
		float yAxisHeight=height-d3-d4;//y轴高度等于控件高度减去y轴上下两端据控件上下两端的高度
		
		if(dif==0){	//只有当所有温度数据都相同时才会出现该情况
			for(int i=0;i<COUNTS;i++){
				yCrdsHigh[i]=yAxisHeight/2+d3;//当所有温度数据都相同时，趋势图为直线且高温线与低温线重合，所有点都具有相同的y坐标，
				yCrdsLow[i]=yAxisHeight/2+d3;//将该坐标设为y轴总高度的一半+y轴顶部距控件顶部的高度，从而使得温度曲线在坐标系中垂直居中
			}
		}else{
			float yInterval=yAxisHeight/dif;//计算y轴单位间隔（y轴高度/温度差值，得到1个温度单位代表的y轴间隔）
			for(int i=0;i<COUNTS;i++){
				yCrdsHigh[i]=height-((tempHigh[i]-minTemp)*yInterval + d4);//获取各高温点的y坐标值
				yCrdsLow[i]=height-((tempLow[i]-minTemp)*yInterval + d4);//获取各低温点的y坐标值
			}
		}
	}
	
	
	/*
	 * 画折线图
	 */
	private void drawChart(Canvas canvas,int lineColor,int dotColor,int tempData[],float yCrds[],int type){
		//创建线画笔
		Paint linePaint=new Paint();
		linePaint.setAntiAlias(true);//抗锯齿
		linePaint.setStrokeWidth(strokeWidth);//设置线宽
		linePaint.setColor(lineColor);//设置线条颜色
		
		//创建点画笔
		Paint dotPaint=new Paint();
		dotPaint.setAntiAlias(true);
		dotPaint.setColor(dotColor);
		
		//创建字体画笔
		Paint textPaint=new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(textColor);
		textPaint.setTextSize(textSize);
		textPaint.setTextAlign(Paint.Align.CENTER);
		
		
		for(int i=0;i<COUNTS;i++){
			//画线
			if(i<COUNTS-1){
			linePaint.setAlpha(255);//设置透明度，255为不透明
			canvas.drawLine(xCrds[i], yCrds[i], xCrds[i+1],yCrds[i+1], linePaint);
			}
			
			//画点
			if(i!=0){
				dotPaint.setAlpha(255);
				canvas.drawCircle(xCrds[i],yCrds[i],radius,dotPaint);
			}else{	//i==0时画今天，使用大的点半径
				dotPaint.setAlpha(255);
				canvas.drawCircle(xCrds[i], yCrds[i], radiusToday, dotPaint);
			}
			
			//绘制温度文字
			textPaint.setAlpha(255);
			drawTempText(canvas,textPaint,i,tempData,yCrds,type);
			
			
		}
	}
	
	/*
	 * 绘制温度文字
	 */
	private void drawTempText(Canvas canvas,Paint textPaint,int i,int[] tempData,float[] yCrds,int type){
		switch(type){
		case TYPE_HIGH:
			canvas.drawText(tempData[i] + "°", xCrds[i], yCrds[i]-radius-textSpace, textPaint);
			break;
		case TYPE_LOW:
			canvas.drawText(tempData[i] + "°", xCrds[i], yCrds[i]+radius+textSize+textSpace, textPaint);
			break;
		}
	}
	
	/*
	 * 绘制日期文字
	 */
	private void drawDateText(Canvas canvas,String dates[]){
		//创建字体画笔
		Paint textPaint=new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(textColor);
		textPaint.setTextSize(textSize);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setAlpha(255);
		for(int i=0;i<COUNTS;i++){
			canvas.drawText(dates[i], xCrds[i], yCrdsDate, textPaint);
		}
	}
	
	


}
