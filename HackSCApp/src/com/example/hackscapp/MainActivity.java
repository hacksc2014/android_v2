package com.example.hackscapp;

import android.support.v7.app.ActionBarActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

public class MainActivity extends ActionBarActivity implements SensorEventListener {
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float)8.0;
	private MediaPlayer mp;
	private int sensor_delay = 50000;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, sensor_delay);
        mp = MediaPlayer.create(getBaseContext(), R.raw.hi_hat);
        //mp2 = MediaPlayer.create(getBaseContext(), R.raw.hi_hat);
        //mp2.setVolume(0.2f, 0.2f);
    }
    @Override
    protected void onResume(){
    	super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, sensor_delay);

    }
    @Override
    protected void onPause(){
    	super.onPause();
    	mSensorManager.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
    	
    }
    @Override
    public void onSensorChanged(SensorEvent event){
    	TextView tvX= (TextView)findViewById(R.id.x_axis);
    	TextView tvY= (TextView)findViewById(R.id.y_axis);
    	TextView tvZ= (TextView)findViewById(R.id.z_axis);
    	ImageView iv = (ImageView)findViewById(R.id.image);
    	float x = event.values[0];
    	float y = event.values[1];
    	float z = event.values[2];
    	if (!mInitialized) {
    		mLastX = x;
    		mLastY = y;
    		mLastZ = z;
    		tvX.setText("0.0");
    		tvY.setText("0.0");
    		tvZ.setText("0.0");
    		mInitialized = true;
    	} else {
    		float deltaX = mLastX - x;
    		float deltaY = mLastY - y;
    		float deltaZ = mLastZ - z;
    		if (deltaX < NOISE) deltaX = (float)0.0;
    		if (deltaY < NOISE) deltaY = (float)0.0;
    		if (deltaZ < NOISE) deltaZ = (float)0.0;
    		mLastX = x;
    		mLastY = y;
    		mLastZ = z;
    		tvX.setText(Float.toString(deltaX));
    		tvY.setText(Float.toString(deltaY));
    		tvZ.setText(Float.toString(deltaZ));
    		iv.setVisibility(View.VISIBLE);
    		
    		if (deltaX > deltaY) {
    		//	if (deltaX > 13.0f){
    				mp.prepareAsync();
    				mp.start();
    		//	}else{
    		//		mp2.start();
    		//	}
    		} else if (deltaY > deltaX) {
    		//	if (deltaY > 13.0f){
    				mp.prepareAsync();
    				mp.start();
    		//	}else{
    		//		mp2.start();
    		//	}
    		} else {
    			iv.setVisibility(View.INVISIBLE);
    		}
    	}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // hi brandino
}
