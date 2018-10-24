package com.kukielko.tadhack.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aambekar.tadhack.Adapters.ActiveListAdapter;
import com.aambekar.tadhack.Data.Database;
import com.aambekar.tadhack.Model.Contact;
import com.aambekar.tadhack.R;

import java.util.ArrayList;
import java.util.List;

public class ActiveList extends AppCompatActivity {


    public static final int REQUEST_CODE_Permission = 102;
    public static final int REQUEST_CODE_Contact = 103;

    RecyclerView rv_al;
    ActiveListAdapter mAdapter;
    List<Contact> list = new ArrayList<>();
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_list);
        rv_al = findViewById(R.id.rv_al);
        database = new Database(getApplicationContext());
        list = database.getContacts();

        mAdapter = new ActiveListAdapter(list, this);
        rv_al.setAdapter(mAdapter);
        rv_al.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_contact:
                if(checkPermission())
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_Contact);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CONTACTS,Manifest.permission.CAMERA}, REQUEST_CODE_Permission);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        boolean status = true;
        if(requestCode == REQUEST_CODE_Permission){
            for(int i : grantResults){
                if(i!= PackageManager.PERMISSION_GRANTED){
                    status = false;
                    break;
                }
            }
        }

        if(status == false){
            Toast.makeText(getApplicationContext(),"Action Failed! Not enough permissions!",Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_Contact);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_Contact) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();

                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();

                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                if (hasPhone.equals("1")) {
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");

                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        Contact contact = new Contact(name,number);
                        Database database = new Database(this);
                        database.addContact(contact);
                        Toast.makeText(getApplicationContext(), "Contact added Successfully " + name, Toast.LENGTH_SHORT).show();
                    }
                    phones.close();
                }
                if(mAdapter!=null){
                    mAdapter.updateList(database.getContacts());
                }
            }
        }

    }
}
