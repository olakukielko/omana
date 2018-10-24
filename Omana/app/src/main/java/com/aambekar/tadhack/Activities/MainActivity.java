package com.aambekar.tadhack.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aambekar.tadhack.Data.Database;
import com.aambekar.tadhack.Data.Local;
import com.aambekar.tadhack.EndPoints.API;
import com.aambekar.tadhack.Model.Contact;
import com.aambekar.tadhack.R;
import com.aambekar.tadhack.Receivers.DemoDeviceAdminReceiver;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_Admin = 101;
    public static final int REQUEST_CODE_Permission = 102;
    public static final int REQUEST_CODE_Contact = 103;
    private static final String TAG = "MainActivity";
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;
    ///recording code
    Button btnRecord;
    String pathSave = "";
    MediaRecorder mediaRecorder;
    String extension = "";
    ///end recording code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, DemoDeviceAdminReceiver.class);
        btnRecord = (Button) findViewById(R.id.btn_record);
        if (!mDPM.isAdminActive(mAdminName))
        {
            buildAlertMessageNoAdmin();
        }
        ////recording code
        if(checkPermission()){
            btnRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    extension = "/" + UUID.randomUUID().toString() + "_audio_record.3gp";
                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + extension;
                    setupMediaRecorder();
                    try{
                        Intent recordIntent =new Intent(getApplicationContext(), HooterService.class);
                        recordIntent.setAction("startrecording");
                        startService(recordIntent);
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e){
                        e.printStackTrace();
                        }
                }
            });
        }
        ///end recording code

    }
    ///recording code
    public void startRecording(){
        mediaRecorder.setMaxDuration(6000);
    }

    private void setupMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }
    ///end recording code

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_CONTACTS,Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_Permission);
            return false;
        }
        return true;
    }

    public void selectContacts(View view){
            if(checkPermission())
            {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_Contact);
            }

    }
    public void showActiveList(View view){
        startActivity(new Intent(getApplicationContext(),ActiveList.class));
    }

    public void sendSMS(View view){
        Local local = new Local(getApplicationContext());
        String text = local.getAlertMessage();
        Database database = new Database(getApplicationContext());
        new API(database.getContacts(),text,getApplicationContext()).execute();
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
            }
        }
        else if(REQUEST_CODE_Admin == requestCode){

            if(resultCode==RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: admin denied");
            } else if(resultCode==RESULT_OK) {
                Log.d(TAG, "onActivityResult: Admin enabled");
            }
        }

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


    private  void buildAlertMessageNoAdmin()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Admin Access");
        builder.setMessage("In order to capture the password failure attempts the app requires the Device Admin policy to be enabled. Please activate the setting and come back to this menu to enable following alerts.")
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener()
                {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        getadminaccess();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void getadminaccess()
    {

        if (!mDPM.isAdminActive(mAdminName))
        {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Activate to get intruder Alerts.");
            startActivityForResult(intent, REQUEST_CODE_Admin);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }





}
