package com.aambekar.tadhack.EndPoints;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.aambekar.tadhack.Model.Contact;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API extends AsyncTask<Void,Void,Void> {

    public API(List<Contact> list, String text, Context context){
        this.list = list;
        this.text = text;
        this.context = context;
    }

    Context context;
    List<Contact> list;
    String text;
    private static final String from = "13126373857";
    private static final String TAG = "API";
    static String apiKey = "3b7a09e4";
    static String  secret = "ffe6d4d8efa435921dd1b24a9df5d865";
    private static String url2  ="https://api4.apidaze.io/3b7a09e4/sms/send?api_secret=ffe6d4d8efa435921dd1b24a9df5d865";

    private void sendSMS(){

        OkHttpClient client = new OkHttpClient();
        for(Contact contact:list){


            String con = contact.contact_no.replace("+","");
            if(con.length() > 10){
               con = con.substring( con.length()-10, con.length()-1);
            }

            con = "1"+con;

            RequestBody requestBody = new FormBody.Builder()
                    .add("from", from)
                    .add("to", con)
                    .add("body", text)
                    .build();

            Request request = new Request.Builder()
                    .url(url2)
                    .post(requestBody)
                    .build();
            Log.d(TAG, "sendSMS: url is "+url2);
            Log.d(TAG, "sendSMS: no is "+con);

            try
            {
               Response response= client.newCall(request).execute();
               Log.d(TAG, "sendSMS: "+response.message());
//               Toast.makeText(context,"Alerts sent successfully!",Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    protected Void doInBackground(Void... voids) {
        sendSMS();
        return null;
    }


}
