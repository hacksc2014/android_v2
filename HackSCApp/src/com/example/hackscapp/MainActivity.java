package com.example.hackscapp;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity implements SensorEventListener {
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float)4.0;
	private MediaPlayer mp;
	private int sensor_delay = 25000;
	private ToggleButton tb;
	private ImageButton recBtn;
	private boolean Pressed;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, sensor_delay);
        mp = MediaPlayer.create(MainActivity.this, com.example.hackscapp.R.raw.snare_drum);
        addListenronButton();
        Pressed = false;

    }
    public void addListenronButton(){
    	tb = (ToggleButton) findViewById(R.id.toggleButton);
    	recBtn = (ImageButton) findViewById(R.id.recBtn);
		recBtn.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_DOWN)
					{
					Pressed = true;
					recBtn.setImageResource(R.drawable.rec_btn_pressed);
					Toast.makeText(MainActivity.this, "started recording", Toast.LENGTH_SHORT).show();
					// do recording stuff here
					}
				else if (event.getAction() == MotionEvent.ACTION_UP)
					{
					Pressed = false;
					recBtn.setImageResource(R.drawable.rec_btn);
					Toast.makeText(MainActivity.this, "stopped recording", Toast.LENGTH_SHORT).show();
					// stop recording stuff here
					}
				return true;
			}
		});	
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
    		
    		if (deltaX > 0f && Pressed) {
    				try {
    					//mp.stop();
						//mp.reset();						
						//mp = MediaPlayer.create(MainActivity.this,R.raw.hi_hat);
						//mp.prepareAsync();
						mp.start();
    				} catch (IllegalArgumentException e) {
    		            e.printStackTrace();
    		        } catch (IllegalStateException e) {
    		            e.printStackTrace();
    		        //} catch (IOException e) {
    		        //   e.printStackTrace();
    		        }
    				iv.setImageResource(R.drawable.ic_launcher);
    		} else {
    			iv.setVisibility(View.INVISIBLE);
    		}
    	}

    }

    //public void onPrepared(MediaPlayer mp){
    //	mp.start();
    //}
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
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	if (mp != null){
    		mp.release();
    		mp = null;
    	}
    }
    // hi brandino
}

