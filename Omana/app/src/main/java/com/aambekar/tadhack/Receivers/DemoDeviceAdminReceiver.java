package com.aambekar.tadhack.Receivers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aambekar.tadhack.Activities.CameraView;
import com.aambekar.tadhack.Activities.HooterService;
import com.aambekar.tadhack.Data.Local;

public class DemoDeviceAdminReceiver extends DeviceAdminReceiver
{
	static final String TAG = "DemoDeviceAdminReceiver";

	@Override
	public void onEnabled(Context context, Intent intent) {
		super.onEnabled(context, intent);
		Log.d(TAG, "onEnabled");

	}

	@Override
	public void onDisabled(Context context, Intent intent)
	{
		super.onDisabled(context, intent);
		Log.d(TAG, "onDisabled");
	}


	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		super.onPasswordChanged(context, intent);
		Log.d(TAG, "onPasswordChanged");
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent)
	{
		super.onPasswordFailed(context, intent);
		Log.d(TAG, "onPasswordFailed");
		Local local = new Local(context);
		int att = local.incrementFailedAttempts();
		if(att >= 3){
			local.resetFailedAttempts();
			if(local.getTriggerChoice()){
				Intent i = new Intent(context, CameraView.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}


			if(local.getHooterDecision())
			{
				Intent intent1 =new Intent(context, HooterService.class);
				intent1.setAction("playalarm");
				context.startService(intent1);
			}
		}



	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent)
	{
		super.onPasswordSucceeded(context, intent);
		Intent intent1 =new Intent(context, HooterService.class);
		intent1.setAction("stopalarm");
		context.startService(intent1);
	}


	

}