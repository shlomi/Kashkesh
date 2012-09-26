package com.asc.kshksh;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class MainActivity extends ListActivity
{
	Button addBtn, recordsendBtn, playBtn; 
	//ListView mContactList;
	static final String PREFERENCE = "com.asc.kshksh";
	Context ctx; 
	String[] myContacts = { "Noam", "Yaara", "Omri", "Ayala", "Meirav", "Arale", "Shlomi", "Irit"};
	String[] emails = { "noamltaasan@gmail.com"}; //"taasan@gmail.com", "irittaasan@gmail.com",
	String myEmail = ""; 
	int to = -1;
	ToneGenerator tg; 
	 private MediaPlayer mediaPlayer;
	 private MediaRecorder recorder;
	 private String OUTPUT_DIR, OUTPUT_FILE; 
	 private String fileName = "/myMessage.3gpp";
	 boolean isRecording = false;
	 private LayoutInflater mInflater;
	 String url = "http://ascs.info/Ituran/uploadAudio.php";
	 String senderID = "taasan@gmail.com";
	 String recipientID = "taasan@gmail.com";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.main);
	     ctx =  getApplicationContext();
	     //setRegistrationId(ctx, null); 
	     mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	     
	     Account[] accounts=AccountManager.get(this).getAccountsByType("com.google");
	     for(Account account: accounts) {
	         myEmail = account.name;
	         //Toast.makeText(ctx, myEmail, Toast.LENGTH_LONG).show();
	     }

	        
	     OUTPUT_DIR = Environment.getExternalStorageDirectory() + "/kashkesh";
	     File outputDir = new File(OUTPUT_DIR);
	     if( !outputDir.isDirectory())
	    	 outputDir.mkdir();
	     
	     OUTPUT_FILE = OUTPUT_DIR + fileName; 

	     String regID = getRegistrationId(ctx);
	    
	     if( regID.length() == 0){
	    	Intent intent = new Intent(ctx, RegisterActivity.class);
	        startActivity(intent);
	     }
	    	
	     playBtn = (Button) findViewById(R.id.listenBtn);
	     playBtn.setOnClickListener(new OnClickListener() {
	    	 public void onClick(View view) {
	    		//tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 5);
	    		try {
	    			
					playRecording();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	 }
	     });
	     
	     recordsendBtn = (Button) findViewById(R.id.recordsendBtn);
	     recordsendBtn.setOnClickListener(new OnClickListener() {
	    	 public void onClick(View view) {
	    		// tg.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 5);
	    		if( isRecording)
	    		{
	    			try {
						stopRecording();
						recordsendBtn.setText("Record");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			
	    		} else {
	    			
	    			try {
	    				
						beginRecording();
						
						recordsendBtn.setText("Stop/Send");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			
	    		}
	    		//startRecording or stop recording and send
	    	 }
	     });
	    
	   
	     setListAdapter(new ArrayAdapter<String>(this, R.layout.contact_entry, myContacts));  
	     ListView lv = getListView();
	     lv.setTextFilterEnabled(true);

	     lv.setOnItemClickListener(  new OnItemClickListener(){
	   
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(ctx,  "Hi" + Integer.toString(arg2), Toast.LENGTH_SHORT).show();
				arg1.setBackgroundColor(0xff00ff);
			}
	     });

	
	}
	
	
	 
	
	 // Preferences - registration =============================================================
	public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(
                PREFERENCE,
                Context.MODE_PRIVATE);
        String registrationId = prefs.getString("kshksh_registration", "");
        return registrationId;
    }
    public static void setRegistrationId(Context context, String registrationId) {
        final SharedPreferences prefs = context.getSharedPreferences(
                PREFERENCE,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString("kshksh_registration", registrationId);
        editor.commit();
    }
    
    
    // audio stuff ==============================================================================
    private void beginRecording() throws Exception {
        killMediaRecorder();

        File outFile = new File(OUTPUT_FILE);

        if(outFile.exists()) {
            outFile.delete();
        }
        
        recorder = new MediaRecorder();
	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(OUTPUT_FILE);
        recorder.prepare();
        isRecording = true;
        
        recorder.start();
    }

    private void stopRecording() throws Exception {
        if (recorder != null) {
            recorder.stop();
            isRecording = false;
            
            // now send the info to webserver.
            Intent i = new Intent(this, vimSend.class);
            i.putExtra("fileName", OUTPUT_FILE);
            i.putExtra("senderID",senderID); 
            i.putExtra("recipientID",recipientID); 
            startService(new Intent(this, vimSend.class));
        }
    }

    private void killMediaRecorder() {
        if (recorder != null) {
            recorder.release();
            isRecording = false;
        }
    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                isRecording = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void playRecording() throws Exception {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(OUTPUT_FILE);

        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    private void stopPlayingRecording() throws Exception {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        killMediaRecorder();
        killMediaPlayer();
    }



}
