package com.pop.convenienceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupIntentReceiver extends BroadcastReceiver {
    
	@Override
	public void onReceive(Context context, Intent intent) 
    {
        Intent intent2 = new Intent(context, ProxService.class);
        
        context.startService(intent2);
        
        Log.i("Autostart", "started");
    }
}
