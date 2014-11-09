package com.example.hackscapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;


import android.view.MotionEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View.OnTouchListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.view.WindowManager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity implements SensorEventListener, NavigationDrawerFragment.NavigationDrawerCallbacks {
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = (float)9.0;
	private MediaPlayer mp;
	private int sensor_delay = 200000;
	private ToggleButton tb;
	private ImageButton recBtn;
	private boolean Pressed;
	private ArrayList record;
	private long lastDown;
	private long lastDuration;
	private ShareActionProvider mShareActionProvider;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInitialized = false;
        
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, sensor_delay);
        mp = MediaPlayer.create(MainActivity.this, com.example.hackscapp.R.raw.hi_hat);
        addListenronButton();
        Pressed = false;
    }
    @SuppressLint("NewApi")
    public void addListenronButton(){
    	tb = (ToggleButton) findViewById(R.id.toggleButton);
    	recBtn = (ImageButton) findViewById(R.id.recBtn);
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
					recBtn.setImageResource(R.drawable.rec_btn);
					lastDuration = System.currentTimeMillis()- lastDown;
					int seconds = (int) (lastDuration/1000) % 60;
					int centis = ((int) (lastDuration) - seconds * 1000)/10;
					String duration = Integer.toString(seconds) + "." + Integer.toString(centis);
					Toast.makeText(MainActivity.this, "stopped recording, duration:"+duration+"s", Toast.LENGTH_SHORT).show();
					Pressed = false;
					
					
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
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (position+1) {
		case 1:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					PlaceholderFragment.newInstance(position + 1)).commit();
			break;
		case 2:
			
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					SectionOneFragment.newInstance(position + 1)).commit();
			
			break;
		case 3:
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					SectionTwoFragment.newInstance(position + 1)).commit();
			
			break;
		}

	}
    public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}
    public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = "Home";
			break;
		case 2:
			mTitle = "History";
			break;
		case 3:
			mTitle = "About and Help";
			break;
		}
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
    		float deltaX = Math.abs(mLastX - x);
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
    				try {
    					//mp.stop();
						//mp.reset();						
						//mp = MediaPlayer.create(MainActivity.this,R.raw.hi_hat);
						//mp.start();
    					new PlayAsync().execute("","","");
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

    public void onPrepared(MediaPlayer mp){
    	Log.d("Try", "Playing");
    	mp.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (!mNavigationDrawerFragment.isDrawerOpen()) {
    		// Inflate the menu; this adds items to the action bar if it is present.
    		getMenuInflater().inflate(R.menu.main, menu);
    		restoreActionBar();
    		return true;
    	}
    	return true;
    }
    public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			setHasOptionsMenu(true);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}
	public static class SectionOneFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static SectionOneFragment newInstance(int sectionNumber) {
			
			SectionOneFragment fragment = new SectionOneFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public SectionOneFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_1, container,
					false);
			setHasOptionsMenu(true);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

	public static class SectionTwoFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static SectionTwoFragment newInstance(int sectionNumber) {
			
			SectionTwoFragment fragment = new SectionTwoFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public SectionTwoFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_2, container,
					false);
			setHasOptionsMenu(true);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
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
        	mp = MediaPlayer.create(MainActivity.this,R.raw.hi_hat);
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

