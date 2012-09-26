package com.asc.kshksh;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class RegisterActivity extends Activity {
	// register at web server and also keep ID at 
	// preferences
	
	Button regBtn, addBtn, doneBtn;
	static TextView tv;
	static String email = "";
	static String regResult = null; 
	static final String PREFERENCE = "com.asc.kshksh";
	Context ctx;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	       setContentView(R.layout.register);
	       
	       ctx = getApplicationContext();
	       String regID = getRegistrationId(ctx);
	       
	       tv = (TextView) findViewById(R.id.regResult);
	       
	       regBtn = (Button) findViewById(R.id.registerBtn);
	       doneBtn = (Button) findViewById(R.id.doneBtn);
	       if( regID.length() == 0){
	    	   regBtn.setEnabled(true);
	    	   doneBtn.setEnabled(false);
	       }
	       else {
	    	   regBtn.setEnabled(false);
	    	   doneBtn.setEnabled(true);
	       }
	       
	       
	       
	       
	       regBtn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	// upload reg info in a new thread. 
	            	 EditText edit =  (EditText) findViewById(R.id.regEmail);
	            	 email = edit.getText().toString();
	            	 
	            	 regResult = register(email); // need to be done in a separate thread!!

					if( regResult.equalsIgnoreCase("OK") )
	            		 setRegistrationId( RegisterActivity.this,  email);
	            	 
					tv.setText(regResult);
					doneBtn.setEnabled(true);
					
	            }
	        });
	       

	       doneBtn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	// 
	            	finish();	
	            }
	        });
	       

	      
	   	}
	
	 public static String register(String userID){

		 String url = "http://ascs.info/Ituran/kshkshRegister.php";
	   		DefaultHttpClient client = new DefaultHttpClient();
	   		HttpPost httppost = new HttpPost(url);
	   		String response = ""; 
				try {
					 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			         nameValuePairs.add(new BasicNameValuePair("username", userID));

			   		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	   				HttpResponse execute = client.execute(httppost);
	   				InputStream content = execute.getEntity().getContent();

	   				BufferedReader buffer = new BufferedReader(
	   						new InputStreamReader(content));
	   				String s = "";
	 					while ((s = buffer.readLine()) != null) {
	  						response += s;
   					}

	   				} catch (Exception e) {
	   					e.printStackTrace();
	   				}
			return response;
	 }

	 
		// package
	    public static void setRegistrationId(Context context, String registrationId) {
	        final SharedPreferences prefs = context.getSharedPreferences(
	                PREFERENCE,
	                Context.MODE_PRIVATE);
	        Editor editor = prefs.edit();
	        editor.putString("kshksh_registration", registrationId);
	        editor.commit();
	    }
	    
	    public static String getRegistrationId(Context context) {
	        final SharedPreferences prefs = context.getSharedPreferences(
	                PREFERENCE,
	                Context.MODE_PRIVATE);
	        String registrationId = prefs.getString("kshksh_registration", "");
	        return registrationId;
	    }
}
