package com.asc.kshksh;

//import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
import java.net.HttpURLConnection;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class vimSend extends Service {

	// change it to run on a different thread. 
	DataOutputStream outputStream = null;
	DataInputStream inputStream = null;
	HttpURLConnection conn;
	
	static final String PREFERENCE = "com.asc.kshksh";
      
	String FileName = ""; //  "/sdcard/mymsg.amr";
	String urlPost = "http://ascs.info/Kashkesh/saveMsg.php";
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary =  "*****";
	
	int bytesRead, bytesAvailable, bufferSize;
	byte[] buffer;
	int maxBufferSize = 10*1024*1024;
	
	Context ctx;
	
	
	//private static final String TAG = "vimService";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		ctx = getApplicationContext();
		//Toast.makeText(ctx, "Sending Message... ", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(ctx, "Message Service Stopped", Toast.LENGTH_LONG).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
    // We want this service to continue running until it is explicitly
    // stopped, so return sticky.
		//return START_STICKY;
		return START_STICKY;
	}

	public void handleCommand(Intent intent) {

		//Toast.makeText(ctx, "in sending", Toast.LENGTH_LONG).show();
		Toast.makeText(ctx, "Sending Message... ", Toast.LENGTH_LONG).show();
		Bundle extras = intent.getExtras();

		FileName = getValue(ctx, "fileName");
		String senderID = getValue(ctx,"senderID"); 
		String recipientID =getValue(ctx,"recipientID"); 
		//Toast.makeText(ctx, FileName + " " + senderID + " " + recipientID, Toast.LENGTH_SHORT).show();
		File file = new File(FileName);
		try {
			HttpClient client = new DefaultHttpClient();
		
			HttpPost post = new HttpPost(urlPost);
			FileBody bin = new FileBody(file);
			MultipartEntity reqEntity = new
						MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			// the file
			reqEntity.addPart("userfile", bin);
			// sender, recipient
			 StringBody from = new StringBody(senderID);
	         StringBody to = new StringBody(recipientID);
	         reqEntity.addPart( "from", from);
	         reqEntity.addPart( "to", to);
	            
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			
			//Toast.makeText(ctx,"Done", Toast.LENGTH_SHORT).show();
			//Toast.makeText(ctx, EntityUtils.toString(resEntity), Toast.LENGTH_LONG).show();
			//Toast.makeText(ctx,"finished post", Toast.LENGTH_SHORT).show();
			//Toast.makeText(ctx, EntityUtils.toString(resEntity), Toast.LENGTH_LONG).show();
			if (resEntity != null) {
				//Log.d("RESPONSE",EntityUtils.toString(resEntity));
			}
			} catch (Exception e) {
				e.printStackTrace();
			} 
	}
	 // Preferences - registration =============================================================
		public String getValue(Context context, String name) {
	        final SharedPreferences prefs = context.getSharedPreferences(
	                PREFERENCE,
	                Context.MODE_PRIVATE);
	        String value = prefs.getString(name, "");
	        return value;
	    }
		/*
	    public static void setRegistrationId(Context context, String registrationId) {
	        final SharedPreferences prefs = context.getSharedPreferences(
	                PREFERENCE,
	                Context.MODE_PRIVATE);
	        Editor editor = prefs.edit();
	        editor.putString("kshksh_registration", registrationId);
	        editor.commit();
	    }
	    */
		
}

