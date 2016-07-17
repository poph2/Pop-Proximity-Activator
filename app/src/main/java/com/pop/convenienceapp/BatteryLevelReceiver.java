package com.pop.convenienceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryLevelReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        
		if(batteryLevel < 20) {
			Intent intent2 = new Intent(context, ProxService.class);
	        
			context.stopService(intent2);
		}
		else {
			Intent intent2 = new Intent(context, ProxService.class);
	        
			context.startService(intent2);
		}
	}

}
