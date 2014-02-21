package com.example.accel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class FileUpload extends Activity {

	private Socket client;
	PrintWriter out;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_upload);
		// Show the Up button in the action bar.
		setupActionBar();
		System.out.println("Inside FileUpload Activity");
		
		new fileTransfer().execute();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_upload, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class fileTransfer extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			FileInputStream fis = null;
			BufferedReader reader = null;
			String path = getFilesDir()+"/abc.txt";
			
			try{
				client = new Socket(MainActivity.ipaddr, MainActivity.portnum);
				
				System.out.println("Path of the file reading from : "+ path);
				
				File f = new File(path.toString());
				
				
				System.out.println("File size: "+ f.length());
				fis = new FileInputStream(path);
				
				reader = new BufferedReader(new InputStreamReader(fis));
				
				
				String line = reader.readLine();
				while(line!=null){
					System.out.println(line);
					line = reader.readLine();
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));
					out.println(line);
					System.out.println("Wrote line");
					out.flush();
				}
				reader.close(); 
				System.out.println("OutputStrem Closed, Transfer complete!");
				client.close();
			}
			catch (UnknownHostException e) {
				e.printStackTrace();
				// TODO: handle exception
			}
			catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			} 	
			return null;
		}
	}
}
