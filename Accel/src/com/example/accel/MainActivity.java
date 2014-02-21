package com.example.accel;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener, LocationListener{

	//Accelerometer varibales
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized; 
	private SensorManager mSensorManager; 
	private Sensor mAccelerometer; 
	private final float NOISE = (float) 2.0;
	private Button btnUpload;
	EditText serverip;
	EditText port;
	public static int portnum;
	public static String ipaddr;


	PrintWriter out;

	public String path;
	File f;
	FileWriter fw;
	BufferedWriter bw;

	int acc_write = 0;
	float deltaX = 0;
	float deltaY = 0;
	float deltaZ = 0;
	long timestamp_acc = 0;


	//GPS variables
	int gps_write = 0;
	long timestamp_gps = 0;
	double la = 0;
	double ln = 0;
	public LocationManager locationManager;
	public LocationListener locationListener;
	public Context context;
	TextView txtLat;
	String lat;
	String provider;
	protected String latitude,longitude; 
	protected boolean gps_enabled,network_enabled;
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = (long) 0.5; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 3000; // in Milliseconds
	
	private final Pattern IP_ADDRESS = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
	        + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
	        + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
	        + "|[1-9][0-9]|[0-9]))");
	private final Pattern PORT = Pattern.compile("(^0*(?:6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|[1-9][0-9]{1,3}|[0-9])$)");

	/** Called when the activity is first created. */

	@Override

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addListenerOnButton();

		//Accelerometer related variables
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		btnUpload.setEnabled(true);
		port=(EditText)findViewById(R.id.port);
		serverip=(EditText)findViewById(R.id.serverip);

		//GPS related initialization
		txtLat = (TextView) findViewById(R.id.textview1);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
		
	}


	public void addListenerOnButton() {
		// TODO Auto-generated method stub
		final Context context = this;
		btnUpload = (Button) findViewById(R.id.btnUpload);

		btnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(port.getText().toString().matches("") || serverip.getText().toString().matches(""))
					{
					Toast.makeText(getApplicationContext(), "Server IP and port no. Cannot be Empty", Toast.LENGTH_LONG).show();
					}
				Matcher matchip = IP_ADDRESS.matcher(serverip.getText().toString());
				Matcher matchport = PORT.matcher(port.getText().toString());
				if (matchip.matches() && matchport.matches()) {
					portnum = Integer.parseInt(port.getText().toString());
					ipaddr = serverip.getText().toString();
					Intent intent = new Intent(context, FileUpload.class);
					startActivity(intent);
					port.setText("");
					serverip.setText("");
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Server IP and port no. not in right format", Toast.LENGTH_LONG).show();
				}
			}
		});
	}


	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		File f1 = new File(MainActivity.this.getFilesDir()+"/abc.txt");
		f1.delete();
	}
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		//	locationManager.removeUpdates(this);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		TextView tvX= (TextView)findViewById(R.id.x_axis);
		TextView tvY= (TextView)findViewById(R.id.y_axis);
		TextView tvZ= (TextView)findViewById(R.id.z_axis);
		ImageView iv = (ImageView)findViewById(R.id.image);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		timestamp_acc = System.currentTimeMillis();
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			tvX.setText("0.0");
			tvY.setText("0.0");
			tvZ.setText("0.0");
			mInitialized = true;
		} else {
			deltaX = (mLastX - x);
			deltaY = (mLastY - y);
			deltaZ = (mLastZ - z);
			if (Math.abs(deltaX) < NOISE) deltaX = (float)0.0;
			if (Math.abs(deltaY) < NOISE) deltaY = (float)0.0;
			if (Math.abs(deltaZ) < NOISE) deltaZ = (float)0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			tvX.setText(Float.toString(deltaX));
			tvY.setText(Float.toString(deltaY));
			tvZ.setText(Float.toString(deltaZ));
			iv.setVisibility(View.VISIBLE);

			path = MainActivity.this.getFilesDir()+"/abc.txt";

		}
		acc_write = 1;
		new fileWrite().execute();
	}

	private class fileWrite extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			path = MainActivity.this.getFilesDir()+"/abc.txt";
			File f = new File(path.toString());

			System.out.println("File size: "+ f.length());
			try {
				System.out.println("File path vlaue: "+f.getPath());
				if(f.exists())
				{
					FileWriter fw = new FileWriter(f,true);
					System.out.println("File exists, go append the data!");
					bw = new BufferedWriter(fw);
					String data = "ACC:"+deltaX+","+deltaY+","+deltaZ+","+timestamp_acc+"\n";
					String gps_data = "GPS:"+la+","+ln+","+timestamp_gps+"\n";
					if(acc_write==1)
					{
						bw.write(data);
						acc_write = 0;
					}

					if(gps_write == 1)
					{
						bw.write(gps_data);
						gps_write = 0;
					}
					System.out.println(data);
					System.out.println(gps_data);
					System.out.println("Wrote to the file!");
					bw.close();
				}
				else
					System.out.println("File is not present! Creating for first time!");
				f.createNewFile();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	
	//GPS related methods

	@Override
	public void onLocationChanged(Location location) {
		txtLat = (TextView) findViewById(R.id.textview1);
		txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());

		gps_write = 1;
		la = location.getLatitude();
		ln = location.getLongitude();
		timestamp_gps = System.currentTimeMillis();
		new fileWrite().execute();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d("Latitude","disable");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d("Latitude","enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("Latitude","status");
	}

}

