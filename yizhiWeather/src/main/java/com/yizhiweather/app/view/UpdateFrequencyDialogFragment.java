package com.yizhiweather.app.view;

import com.yizhiweather.app.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class UpdateFrequencyDialogFragment extends DialogFragment implements DialogInterface.OnClickListener{
	private UpdateFrequencyDialogListener listener;
	
	/*定义UpdateFrequencyDialogFragment与activity的通信接口*/
	public interface UpdateFrequencyDialogListener{
		void onDialogItemClick(int which);//当弹出窗口的每个子项被点击时回调该方法
	}
	
	/*
	 * 覆写Fragment中的onAttach()方法获取通信接口实例
	 */
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			 listener = (UpdateFrequencyDialogListener)activity;
			} catch (ClassCastException e) {
			  throw new ClassCastException(activity.toString()
			    + " must implement UpdateFrequencyDialogListener");
			}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.choose_freq)
			   .setNegativeButton(R.string.cancel, this)
		       .setItems(R.array.update_frequency,this);	
		return builder.create();
		
	}

	@Override
	public void onClick(DialogInterface dialog,int which){
		switch(which){
		case AlertDialog.BUTTON_NEGATIVE:
			 break;
		default:
			if(listener!=null){
				listener.onDialogItemClick(which);
			break;		  
		  }		    		  
		}
	}
	
}
