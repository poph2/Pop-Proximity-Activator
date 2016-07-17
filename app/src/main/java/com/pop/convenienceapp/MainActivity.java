package com.pop.convenienceapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	IntentFilter filter;
	
	BroadcastReceiver broadcastReceiver;

	TextView textView;
	Intent proxServiceIntent;
	
	MainFragment mainFragment;
	
	Gson gson;
	
	AppData appData;

	public static String MAIN_ACTIVITY_UI_UPDATE_INTENT = "com.pop.convenienceapp.action.MAIN_ACTIVITY_UI_UPDATE_INTENT";
	public static String MAIN_ACTIVITY_APPDATA_UPDATE_INTENT = "com.pop.convenienceapp.action.MAIN_ACTIVITY_APPDATA_UPDATE_INTENT";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mainFragment = (MainFragment)getFragmentManager().findFragmentById(R.id.fragment1);

		textView = (TextView) findViewById(R.id.textView1);

		proxServiceIntent = new Intent(this, ProxService.class);
		
		gson = new Gson();
		
		//com.pop.convenienceapp.action.MAINACTIVITYINTENT
		
		filter = new IntentFilter(MAIN_ACTIVITY_UI_UPDATE_INTENT);
		
		broadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean deviceIsStable = intent.getBooleanExtra("device_is_stable", false);
            	
            	if(!deviceIsStable) {
            		textView.setTextColor(Color.GRAY);
            		//textView.setText("false");
            		textView.setText(intent.getStringExtra("stability_measure_json"));
            	}
            	else {
            		textView.setTextColor(Color.WHITE);
            		textView.setText(intent.getStringExtra("stability_measure_json"));
            	}
			}
		};
		
        registerReceiver(broadcastReceiver, filter);
        
        new LoadAppDataTask().execute(0);
        
        Button button = (Button)findViewById(R.id.turnScreenOff);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WindowManager.LayoutParams params = getWindow().getAttributes(); 
				params.flags |= LayoutParams.FLAG_KEEP_SCREEN_ON; 
				params.screenBrightness = 0; 
				getWindow().setAttributes(params);
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(broadcastReceiver, filter);
	 }
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
	 }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		showToastInIntentService("Application being destroyed");
	}
	
	public void showToastInIntentService(final String sText) {
		final Context MyContext = this;
	    new Handler(Looper.getMainLooper()).post(new Runnable() {
	    	@Override
	    	public void run() {
    			Toast toast1 = Toast.makeText(MyContext, sText, Toast.LENGTH_SHORT);
    			toast1.show(); 
	        }
	    });
	}
	
	class LoadAppDataTask extends AsyncTask<Integer, Integer, AppData> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected AppData doInBackground(Integer... params) {
			AppData appData;
			
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(openFileInput("convenience_app_data")));
				String json = br.readLine();
				
				if(json == null) {
					throw new IOException();
				}
				appData = gson.fromJson(json, AppData.class);
				br.close();
			}
			catch (IOException e) {
				appData = new AppData();
				try {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput("convenience_app_data", Context.MODE_PRIVATE)));
					bw.write(gson.toJson(appData));
					bw.flush();
					bw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return appData;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(AppData result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			appData = result;
			
			if(!ProxService.isRunning()) {
				startService(proxServiceIntent);
			}
		}
		
	}
}





