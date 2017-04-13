package com.yizhiweather.app.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/*
 * 自定义的可实现弹性滑动的ScrollView
 */
public class ElasticScrollView extends ScrollView {
	//移动因子（达到延迟效果，比如手指移动了100px,则View只移动50px
	private static final float MOVE_FACTOR=0.5f;
	
	//松开手指后界面回到正常位置所需要的动画时间
	private static final int ANIM_TIME=300;
	
	//ScrollView的子View
	private View contentView;
	
	//记录手指按下时的Y值
	private float startY;
	
	//记录正常的布局位置
	private Rect originalRect=new Rect();
	
	//记录手指按下时是否可以继续下拉
	private boolean canPullDown=false;
	
	//记录手指按下时是否可以继续上拉
	private boolean canPullUp=false;
	
	//记录手指滑动过程中是否触发了弹性滑动操作（仅在发生上拉或下拉时才会触发弹性滑动）
	private boolean isMoved=false;
	
	public ElasticScrollView(Context context){
		super(context);
	}
	
	public ElasticScrollView(Context context,AttributeSet attrs){
		super(context,attrs);
	}
	public ElasticScrollView(Context context,AttributeSet attrs,int defStyle){
		super(context,attrs,defStyle);
	}
	@Override
	protected void onFinishInflate(){
		if(getChildCount()>0){
			contentView=getChildAt(0);//获取ScrollView中的唯一子View
		}
	}
	
	@Override
	protected void onLayout(boolean changed,int l,int t,int r,int b){
		super.onLayout(changed,l,t,r,b);
		if(contentView==null){
			return;
		}
		//获取ScrollView中唯一子View的位置信息
		originalRect.set(contentView.getLeft(),contentView.getTop(),
				         contentView.getRight(),contentView.getBottom());
	}
	
	/*
	 * 处理触摸事件中的上拉和下拉逻辑
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		if(contentView==null){
			return super.dispatchTouchEvent(ev);
		}
		int action=ev.getAction();//获取动作事件
		switch(action){
		case MotionEvent.ACTION_DOWN://按下时
			//判断是否可以上拉和下拉
			canPullDown=isCanPullDown();
			canPullUp=isCanPullUp();
			
			//记录按下时的Y值
			startY=ev.getY();
			break;
		case MotionEvent.ACTION_UP://抬起时
			if(!isMoved){	//如果没有移动布局，则跳过执行
				break;
			}
			
			//如果滑动过程中触发了弹性滑动效果，则开启动画，动画结束后使控件回到原位
			TranslateAnimation anim=new TranslateAnimation(0,0,contentView.getTop(),originalRect.top);
			anim.setDuration(ANIM_TIME);
			contentView.startAnimation(anim);
			//设置回到正常的布局位置
			contentView.layout(originalRect.left, originalRect.top,
					           originalRect.right, originalRect.bottom);
			//子View回到正常布局位置后，重置各标志位
			canPullDown=false;
			canPullUp=false;
			isMoved=false;
			break;
		case MotionEvent.ACTION_MOVE://移动时
			//如果在移动过程中既没有滚动到可以上拉的程度也没有滚动到可以下拉的程度
			if(!canPullDown && !canPullUp){
				startY=ev.getY();
				canPullDown=isCanPullDown();
				canPullUp=isCanPullUp();
				break;
			}
			//计算手指移动的距离
			float nowY=ev.getY();
			int deltaY=(int)(nowY-startY);
			
			//判断是否应该实现弹性滑动
			boolean shouldMove=(canPullDown && deltaY>0) //可以下拉，并且手指向下移动时
					           ||(canPullUp && deltaY<0) //可以上拉，并且手指向上移动时
					           ||(canPullUp && canPullDown);//既可以上拉又可以下拉时（即contentView高度本来就小于ScrollView高度时）
			if(shouldMove){
				//计算偏移量
				int offset=(int)(deltaY*MOVE_FACTOR);
				//开启弹性滑动效果（即重新绘制控件位置）
				contentView.layout(originalRect.left, originalRect.top + offset,
						           originalRect.right,originalRect.bottom + offset);
				isMoved=true;
			}
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	/*
	 * 判断是否滚动到顶部（即是否可以下拉）
	 */
	private boolean isCanPullDown(){
		return getScrollY()==0 || contentView.getHeight()<getHeight()+getScrollY();
	}
	
	/*
	 * 判断是否滚动到底部（即是否可以上拉）
	 */
	private boolean isCanPullUp(){
		return contentView.getHeight()<=getHeight()+getScrollY();
	}
	
	//getScrollY()获取的是ContentView顶部相对于ScrollView顶部的距离
}
