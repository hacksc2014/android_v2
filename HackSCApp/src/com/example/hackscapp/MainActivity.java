package com.example.hackscapp;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
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
	private final float NOISE = (float)11.0;
	private MediaPlayer mp;
	private int sensor_delay = 150000;
	private ToggleButton tb;
	private ImageButton recBtn;
	private boolean Pressed;
	private long lastDown;
	private long lastDuration;
	private long playback[];
	private int playback_index;
	private int final_index;
	private Button playback_button;
	
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
        addListeneronButton();
        Pressed = false;
        //playback = new ArrayList<Long>();
        playback = new long[100];
        playback_index= 0;
        final_index = 0;
        
    }
    @SuppressLint("NewApi")
    public void addListeneronButton(){
    	tb = (ToggleButton) findViewById(R.id.toggleButton);
    	recBtn = (ImageButton) findViewById(R.id.recBtn);
    	playback_button = (Button) findViewById(R.id.playbackButton);
        playback_button.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		for (int i = 0; i < final_index; i++){
        			if (i ==0){
        				SystemClock.sleep(playback[i]);
        				new PlayAsync().execute("","","");
        			}else{
        				SystemClock.sleep(playback[i] - playback[i-1]);
        				new PlayAsync().execute("","","");
        			}
        		}
        	}
        });
		recBtn.setOnTouchListener(new OnTouchListener(){
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_DOWN)
					{
					Pressed = true;
					recBtn.setImageResource(R.drawable.rec_btn_pressed);
					Toast.makeText(MainActivity.this, "started recording", Toast.LENGTH_SHORT).show();
					lastDown = System.currentTimeMillis();
				
					// do recording stuff here
					}
				else if (event.getAction() == MotionEvent.ACTION_UP)
					{
					Pressed = false;
					recBtn.setImageResource(R.drawable.rec_btn);
					lastDuration = System.currentTimeMillis() - lastDown;
					int seconds = (int) (lastDuration / 1000) % 60 ;
					int centis = ((int) (lastDuration) - seconds * 1000)/10;
					String duration = Integer.toString(seconds) + "." + Integer.toString(centis)                                                                                                                                                                                                                                            ;
					Toast.makeText(MainActivity.this, "stopped recording, duration:"+duration+"s", Toast.LENGTH_SHORT).show();
					//Toast.makeText(MainActivity.this, playback.size(), Toast.LENGTH_SHORT).show();
					// stop recording stuff here
					for (int i = 0; i < playback_index; i++){
						System.out.println(playback[i]);
					}
					final_index = playback_index;
					playback_index = 0;
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
    		float deltaX = -(mLastX - x);
    		float deltaY = Math.abs(mLastY - y);
    		float deltaZ = Math.abs(mLastZ - z);
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
    			playback[playback_index] = System.currentTimeMillis() - lastDown;
    			playback_index++;
    			//playback.add(System.currentTimeMillis() - lastDown);
    			
    				try {

    					new PlayAsync().execute("","","");
    				} catch (IllegalArgumentException e) {
    		            e.printStackTrace();
    		        } catch (IllegalStateException e) {
    		            e.printStackTrace();

    		        }
    				iv.setImageResource(R.drawable.ic_launcher);
    		} else {
    			iv.setVisibility(View.INVISIBLE);
    		}
    	}

    }

    public void onPrepared(MediaPlayer mp){
    	Log.d("Try", "Playing");
    	mp.start();
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
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	if (mp != null){
    		mp.release();
    		mp = null;
    	}
    }
    // hi brandino
    
    class PlayAsync extends AsyncTask<String, String, String> {
    	 
        // Show Progress bar before downloading Music
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Shows Progress Bar Dialog and then call doInBackground method
            mp.release();
			//mp.start();
        }

        // Download Music File from Internet
        @Override
        protected String doInBackground(String... f_url) {
        	mp = MediaPlayer.create(MainActivity.this,R.raw.snare_drum);
        	mp.start();
        	return null;         
        }

        // Once Music File is downloaded
        @Override
        protected void onPostExecute(String file_url) {
            // Dismiss the dialog after the Music file was downloaded
        	
        }
    }

}

