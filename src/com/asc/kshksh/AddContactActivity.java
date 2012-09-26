package com.asc.kshksh;


import java.util.Random;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AddContactActivity extends ListActivity{
	// keep all contacts in DB
	// if contact is not enrolled web server sends an invitation email. 
	// Pick contacts from ContactManager. 
	// either Email or phone number including country code is OK. 
	
	public static final String TAG = "Kashkesh";

    private Button mAddAccountButton;
    private ListView mContactList;
    private boolean mShowInvisible;

    /**
     * Called when the activity is first created. Responsible for initializing the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.v(TAG, "Activity State: onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        // Obtain handles to UI objects
        //mAddAccountButton = (Button) findViewById(R.id.addContactButton);
        //mContactList = (ListView) findViewById(R.id.contactlist);
        //mContactList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        

        // Register handler for UI elements
        mAddAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// put info into the DB
            	FavoritesDbAdapter db = new FavoritesDbAdapter();
    			db.open();

    			SparseBooleanArray checked = mContactList.getCheckedItemPositions();
    	        for (int i = 0; i < checked.size(); i++) {
    	            if(checked.valueAt(i) == true) {
    	            	String email = mContactList.getItemAtPosition(checked.keyAt(i)).toString();
    	            	Toast.makeText(getBaseContext(), email , Toast.LENGTH_SHORT);
    	                db.createFavorite(email);  
    	            }
    	        }
            }
        });
       
        // Populate the contact list
        populateContactList();
        
    }

    public void populateList()
    {
    	
    }
    /**
     * Populate the contact list based on account currently selected in the account spinner.
     */
    private void populateContactList() {
        // Build adapter with contact entries
        Cursor cursor = getContacts();
        String[] fields = new String[] {
                ContactsContract.Data.DISPLAY_NAME
        };
        ///SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.contact_entry, cursor,
           //     fields, new int[] {R.id.contactEntryText});
        //mContactList.setAdapter(adapter);
    }

    /**
     * Obtains the contact list for the currently selected account.
     *
     * @return A cursor for for accessing the contact list.
     */
    private Cursor getContacts()
    {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" +
                (mShowInvisible ? "0" : "1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
    }
    
    
}


