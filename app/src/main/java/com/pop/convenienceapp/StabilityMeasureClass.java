package com.pop.convenienceapp;

public class StabilityMeasureClass {
	
	static int stabilityCheckInterval = 200;	//In milliseconds
	
	int stabilityTimeout = 5000;			//In milliseconds
	
	int arrayLength = stabilityTimeout/stabilityCheckInterval;
	
	float x[], y[], z[];
	
	private boolean deviceIsStable;

	public StabilityMeasureClass(String str) {
		
		deviceIsStable = false;
				
		x = new float[arrayLength];
		y = new float[arrayLength];
		z = new float[arrayLength];
		
		for(int i = 0; i < arrayLength; i++) {
			x[i] = 0.00f;
			y[i] = 0.00f;
			z[i] = 0.00f;
		}
	}
	
	public void addValues(float x, float y, float z) {
		//Log.i("StabilityMeasureClass", "stabilityMeasureClass 0: " + new Gson().toJson(this));
		
		for(int i = 0; i <= arrayLength-2; i++) {
			this.x[i] = this.x[i+1];
			this.y[i] = this.y[i+1];
			this.z[i] = this.z[i+1];
		}
		this.x[arrayLength-1] = x;
		this.y[arrayLength-1] = y;
		this.z[arrayLength-1] = z;
		

		//Log.i("StabilityMeasureClass", "stabilityMeasureClass 1: " + new Gson().toJson(this));
		
	}
	
	public boolean deviceIsStable() {
		
		float xShift = 1.0f;
		float yShift = 1.0f;
		float zShift = 1.0f;
		
		for(int i = 0; i < arrayLength-1; i++) {
			xShift = x[i] - x[i+1];
			yShift = y[i] - y[i+1];
			zShift = z[i] - z[i+1];
			
			//Log.i("StabilityMeasureClass", "x[ " + i + "] = " + x[i] + ", x[ " + i+1 + "] = " + x[i+1] + ", xShift = " + xShift + ", ");
			
			if(xShift >= 1.0f || yShift >= 1.0f || zShift >= 1.0f) {
				
				//Log.i("StabilityMeasureClass", "Shift " + i + ": (xShift = " + xShift + ", yShift = " + yShift + ", zShift = " + zShift);
				//Log.i("StabilityMeasureClass", "Shift " + i + ": (xShift = " + xShift + ", yShift = " + yShift + ", zShift = " + zShift);
				//Log.i("StabilityMeasureClass", "Shift " + i + ": (xShift = " + xShift + ", yShift = " + yShift + ", zShift = " + zShift);
				
				deviceIsStable = false;
				return deviceIsStable;
			}
		}
		
		//Log.i("StabilityMeasureClass", "device is stable");
		
		deviceIsStable = true;
		return deviceIsStable;
	}
	
}