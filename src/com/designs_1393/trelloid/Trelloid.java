package com.designs_1393.trelloid;

import android.app.Activity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

// Logging
import android.util.Log;

/*// HTTP
import android.net.http.AndroidHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;*/

// URI
import android.net.Uri;

// Buffers
import java.io.BufferedReader;
import java.io.InputStreamReader;

// AsyncTask
import android.os.AsyncTask;
import java.lang.Void;


public class Trelloid extends Activity
{
	/*
	private class SimpleRequest extends AsyncTask<String, Void, Void>
	{
		protected Void doInBackground(String... APIrequest)
		{

		}
	}*/


	/* this must remain private! */
	final String KEY = "4cc1f116e94dd123afd80f7c81d7a847";
	final String TAG = "Trelloid";

	final String CONS_KEY = "4cc1f116e94dd123afd80f7c81d7a847";
	final String CONS_SECRET = "5e9d9f3c79fb0b5f54616420b5cb6be5a625652e69ef6dd8dd0e2ed5899ad8f9";
	final String SCOPE = "read,write";
	final String CB_URL = "trelloid://authorized";

	private String[] token = null;
	private String s_uri = null;

	final Context ctx = getApplication();

	private OAuthHelper oah;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if( s_uri == null )
		{
			try {
				oah = new OAuthHelper(CONS_KEY, CONS_SECRET, SCOPE, CB_URL, TAG);

				Log.i(TAG +"::onCreate", "Creating another browser");
				s_uri = oah.getRequestToken();
				startActivity(new Intent("android.intent.action.VIEW", Uri.parse(s_uri)));
			} catch (Exception e) {Log.e(TAG +"::onCreate", e.toString());}
		}

		//new SimpleRequest().execute("https://api.trello.com/1/members/sjbarag?key=4cc1f116e94dd123afd80f7c81d7a847");
	}

	private String[] getVerifier()
	{
		// extract the token if it exists
		Uri uri = this.getIntent().getData();
		if( uri == null )
			return null;

		String token = uri.getQueryParameter("oauth_token");
		String verifier = uri.getQueryParameter("oauth_verifier");

		Log.i(TAG +"::getVerifier", "token = " +token);
		Log.i(TAG +"::getVerifier", "verifier = " +verifier);

		return new String[] {token, verifier};
	}

	/** Called when the activity is resumed */
	@Override
	public void onResume()
	{
		super.onResume();

		if( token == null)
			token = getVerifier();

		if( token != null )
		{
			try{
				String[] accessToken = oah.getAccessToken(token[1]);
		} catch (Exception e) {Log.e(TAG +"::onResume", e.toString());}
			Log.i(TAG +"::onResume", "On Resume Done");
		}
		else
			Log.w(TAG +"::onResume", "token is still null...");

	}
}
