package com.pop.convenienceapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class ProxService extends Service implements SensorEventListener {
	
	Intent intent;
	
	private SensorManager proximitySensorManager, accelerometerSensorManager;
	private Sensor proximitySensor, accelerometerSensor;
	
	Gson gson;
	
	StabilityMeasureClass stabilityMeasureClass;
	
	AppData appData;

	//private float lastProximity = -1f;
	//private long lastProximityUpdate = -1;
	
	private long lastUpdate = -1;
    //private float x, y, z;
    //private float last_x, last_y, last_z;
    //private static final int SHAKE_THRESHOLD = 100;
	
	Vibrator v;
	
	int vibrateTime;
	
	TextView tv;
	
	private static boolean isRunning = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
	    super.onCreate();
	    isRunning = true;
	}
	
	public void createAllGoodies() {
		
		gson = new Gson();
		
		readAppData();
		
		proximitySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	    proximitySensor = proximitySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	    
	    
	    accelerometerSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	    accelerometerSensor = accelerometerSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    
	    proximitySensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
	    
		accelerometerSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
		
		stabilityMeasureClass = new StabilityMeasureClass("ProxService");
		
		Log.i("StabilityMeasureClass", "StabilityMeasureClass constructor called from ProxService");
		
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		//Intent intent = get
		
		//Accelerometer
		//boolean accelSupported = mSensorManager.registerListener(this,
		//	SensorManager.SENSOR_ACCELEROMETER,
		//	SensorManager.SENSOR_DELAY_GAME);

	    
	    showToastInIntentService("Service started...");
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
	
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		
		Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
		restartServiceIntent.setPackage(getPackageName());

		PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);

		super.onTaskRemoved(rootIntent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
		showToastInIntentService("Service being destroyed");
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		//Log.i("ProxService", "stabilityMeasureClass 0: " + gson.toJson(stabilityMeasureClass));
		
		Sensor sensor = event.sensor;
		
		//ShowToastInIntentService(Integer.toString(sensor.getType()) + " - TYPE_PROXIMITY(" + Integer.toString(sensor.TYPE_PROXIMITY) + ") TYPE_ACCELEROMETER(" + Integer.toString(sensor.TYPE_ACCELEROMETER) + ")");
        
		if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
			
			//long curTime = System.currentTimeMillis();
    	    // only allow one update every 100ms.
    	    //if ((curTime - lastProximityUpdate) < 100) { return; }
			
        	if (event.values[0] == 0) {
    			//tv.setText("NEAR!!!");
        		
    			PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
    			final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
    			
    			if(!pm.isScreenOn() && stabilityMeasureClass.deviceIsStable()) {
    				showToastInIntentService("Near - Screen Off - Device stable - Turning Screen On");
    				
    				Runnable r = new Runnable() {
    					
    					@Override
    					public void run() {
    						
    						//v.vibrate(500);
    						wl.acquire();
    						wl.release();
    					}
    				};
    				r.run();
    			}
    			if(!pm.isScreenOn() && !stabilityMeasureClass.deviceIsStable()) {
    				showToastInIntentService("Near - Screen On - Device unstable - Ignoring...");
    			}
    			if(pm.isScreenOn() && !stabilityMeasureClass.deviceIsStable()) {
    				showToastInIntentService("Near - Screen On - Device unstable - Ignoring...");
    			}
    			if(pm.isScreenOn() && stabilityMeasureClass.deviceIsStable()) {
    				
    				final PowerManager.WakeLock wl2 = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag");
    				wl2.acquire();
    				wl2.release();
    				
    				//pm.goToSleep(System.currentTimeMillis() + 2000);
    				showToastInIntentService("Near - Screen On - Device stable - Ignoring...");
    			}
    			
    			//lastProximity = event.values[0];
    			
    			//Toast.makeText(this, "NEAR", Toast.LENGTH_SHORT).show();
    		}
        	//else {
        	//	showToastInIntentService("FAR + " + Float.toString(event.values[0]));
        	//}
        }
        else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        	
        	long curTime = System.currentTimeMillis();
    	    // only allow one update every 200ms.
    	    if ((curTime - lastUpdate) > StabilityMeasureClass.stabilityCheckInterval) {
	    		//long diffTime = (curTime - lastUpdate);
	    		lastUpdate = curTime;
	     
	    		float x = event.values[SensorManager.DATA_X];
	    		float y = event.values[SensorManager.DATA_Y];
	    		float z = event.values[SensorManager.DATA_Z];
	    		
	    		//Log.i("ProxService", "Accelerometer data,: x = " + Float.toString(x) + ", y = " + Float.toString(y) + ", z = " + Float.toString(z));
	     
	    		//Log.i("ProxService", "stabilityMeasureClass 1: " + gson.toJson(stabilityMeasureClass));
	    		
	    		stabilityMeasureClass.addValues(x, y, z);

	    		//Log.i("ProxService", "stabilityMeasureClass 2: " + gson.toJson(stabilityMeasureClass));
	    		 
	    		//String jsonStr = gson.toJson(stabilityMeasureClass);
	    		
	    		/*if(stabilityMeasureClass.deviceIsStable()) {
	    			proximitySensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
	    		}
	    		else {
	    			proximitySensorManager.unregisterListener(this, proximitySensor);
	    		}*/
    	    }
    	    
    	    Intent intent = new Intent(MainActivity.MAIN_ACTIVITY_UI_UPDATE_INTENT);
    		intent.putExtra("device_is_stable", stabilityMeasureClass.deviceIsStable());
    		intent.putExtra("stability_measure_json", gson.toJson(stabilityMeasureClass));
            sendBroadcast(intent);
        }
		
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		this.intent = intent;
		
		createAllGoodies();
		
		showToastInIntentService("Service started");
		
		return START_STICKY;
	}
	
	/*Handler handler = new Handler();

	Runnable runnable = new Runnable(){

	    @Override
	    public void run() {
	        // TODO Auto-generated method stub
	        createAllGoodies();
	        handler.postDelayed(this, 1000); // 1000 - Milliseconds
	    }
	 };*/
	 
	public static boolean isRunning() {
        return isRunning;
    }
	
	public void readAppData() {
		//FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		//fos.write(string.getBytes());
		//fos.close();
		
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
	}
}


