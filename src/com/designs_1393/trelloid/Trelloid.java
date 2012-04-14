package com.designs_1393.trelloid;

import android.app.Activity;
import android.os.Bundle;

import android.content.Context;

// Logging
import android.util.Log;

// HTTP
import android.net.http.AndroidHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;

// Buffers
import java.io.BufferedReader;
import java.io.InputStreamReader;

// AsyncTask
import android.os.AsyncTask;
import java.lang.Void;


public class Trelloid extends Activity
{
	private class SimpleRequest extends AsyncTask<String, Void, Void>
	{
		protected Void doInBackground(String... APIrequest)
		{
			/* get HTTP client */
			AndroidHttpClient client = AndroidHttpClient.newInstance("Wget/1.13.4", ctx);

			/* create a request */
			HttpGet request = new HttpGet(APIrequest[0]);

			/* create & get response */
			try {
				HttpResponse response = client.execute(request);


				/* read response */
				BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()) );
				String line = "";

				Log.i(TAG, "Got response!");
				while ((line = rd.readLine()) != null)
					Log.i(TAG, line);
			} catch(Exception e){
				Log.w(TAG, "Exception: " +e.toString());
			}
			return null;
		}
	}


	/* this must remain private! */
	final String KEY = "4cc1f116e94dd123afd80f7c81d7a847";
	final String TAG = "Trelloid";

	final Context ctx = getApplication();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		new SimpleRequest().execute("https://api.trello.com/1/members/sjbarag?key=4cc1f116e94dd123afd80f7c81d7a847");

	}
}
