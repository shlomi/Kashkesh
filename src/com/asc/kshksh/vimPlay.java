package com.asc.kshksh;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class vimPlay extends Service {

	// change to run on a different thread. 
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		
	    MediaPlayer player = MediaPlayer.create(this, Uri.parse("http://immuno.math.cmu.edu/vim/data/mymsg.amr"));
	    try{ 
	    	player.prepare();
	    	player.start();
	    	Toast.makeText(this, "Player started", Toast.LENGTH_LONG).show();
	    } catch(Exception e){ 
	    	Toast.makeText(this, "Player failed", Toast.LENGTH_LONG).show();
	    } 
	    
		
	}

	@Override
	public void onDestroy() {
		
		Toast.makeText(this, "Player service Stopped", Toast.LENGTH_LONG).show();
	}
	@Override
	public void onStart(Intent intent, int startid) {

		Toast.makeText(this, "Player started", Toast.LENGTH_LONG).show();
		
	}
}
