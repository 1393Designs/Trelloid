package com.designs_1393.trelloid;

/** Inspired by OAuthHelper by ldx at nilvec.com:
	http://nilvec.com/implementing-smtp-or-imap-xoauth-authentication-in-java/
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.SortedSet;
import java.util.Map.Entry;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import android.util.Base64;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.commonshttp.HttpRequestAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.signature.HmacSha1MessageSigner;
import oauth.signpost.signature.OAuthMessageSigner;


public class OAuthHelper
{
	static private String TAG = "Trelloid.OAuthHelper";

	private OAuthConsumer mConsumer;
	private OAuthProvider mProvider;

	private String mCallbackUrl;

	public OAuthHelper(String consumerKey,
	                   String consumerSecret,
	                   String scope,
	                   String callbackUrl,
	                   String appname) throws UnsupportedEncodingException
	{
		String reqUrl;

		if( appname == null )
			reqUrl = OAuth.addQueryParameters(
				"https://trello.com/1/OAuthGetRequestToken",
				"scope", scope);
		else
			reqUrl = OAuth.addQueryParameters(
				"https://trello.com/1/OAuthGetRequestToken",
				"scope", scope,
				"xoauth_displayname", appname);

		mConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);

		mProvider = new CommonsHttpOAuthProvider(reqUrl,
			"https://trello.com/1/OAuthGetAccessToken",
			"https://trello.com/1/OAuthAuthorizeToken?hd=default"); // DBG: ???

		mProvider.setOAuth10a(true);

		mCallbackUrl = (callbackUrl == null ? OAuth.OUT_OF_BAND : callbackUrl);
	}

	public String getRequestToken() throws
		OAuthMessageSignerException,
		OAuthNotAuthorizedException,
		OAuthExpectationFailedException,
		OAuthCommunicationException
	{
		String authUrl = mProvider.retrieveRequestToken(mConsumer, mCallbackUrl);
		return authUrl;
	}

	public String[] getAccessToken(String verifier)	throws
		OAuthMessageSignerException,
		OAuthNotAuthorizedException,
		OAuthExpectationFailedException,
		OAuthCommunicationException
	{
		mProvider.retrieveAccessToken(mConsumer, verifier);
		return new String[] {mConsumer.getToken(), mConsumer.getTokenSecret()};
	}

	public String[] getToken()
	{
		return new String[] {mConsumer.getToken(), mConsumer.getTokenSecret()};
	}

	public void setToken(String token, String secret)
	{
		mConsumer.setTokenWithSecret(token, secret);
	}

	public String getUrlContent(String url) throws
		OAuthMessageSignerException,
		OAuthExpectationFailedException,
		OAuthCommunicationException,
		IOException
	{
		HttpGet request = new HttpGet(url);

		// sign the request
		mConsumer.sign(request);

		// send the request
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);

		// get content
		BufferedReader in = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
		StringBuffer sb = new StringBuffer("");
		String line = "";
		String NL = System.getProperty("line.separator");
		while( (line = in.readLine()) != null )
			sb.append(line + NL);
		in.close();

		return sb.toString();
	}

	// DBG: I think this isn't necesary for Trello integration...
	//public String buildXOAuth(String email);
}
