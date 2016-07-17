package com.pop.convenienceapp;

public class AppData {
	
	private int stabilityCheckInterval = -1;		//In milliseconds
	
	private int stabilityTimeout = -1;			//In milliseconds
	
	private int arrayLength = -1;
	
	private int batteryLevel = -1;
	
	public AppData() {
		if(stabilityCheckInterval == -1) {
			setDefault();
		}
	}
	
	public void setDefault() {
		stabilityCheckInterval = 200;		//In milliseconds
		
		stabilityTimeout = 5000;			//In milliseconds
		
		arrayLength = stabilityTimeout/stabilityCheckInterval;
		
		batteryLevel = 50;
	}
	
	public void setFields(int stabilityCheckInterval, int stabilityTimeout, int arrayLength, int batteryLevel) {
		
		stabilityCheckInterval = 200;		//In milliseconds
		
		stabilityTimeout = 5000;			//In milliseconds
		
		arrayLength = stabilityTimeout/stabilityCheckInterval;
		
		batteryLevel = 50;
	}

	public int getStabilityCheckInterval() {
		return stabilityCheckInterval;
	}

	public int getStabilityTimeout() {
		return stabilityTimeout;
	}

	public int getArrayLength() {
		return arrayLength;
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}
	
	
	

}
