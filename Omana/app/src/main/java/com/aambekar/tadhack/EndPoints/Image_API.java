package com.aambekar.tadhack.EndPoints;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.aambekar.tadhack.Data.Local;
import com.aambekar.tadhack.Model.Contact;
import org.json.JSONObject;
import java.util.List;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Image_API extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "Image_API";
    public Image_API(List<Contact> list,String image, Context context){
        this.image = image;
        this.list = list;
        this.context = context;
    }
    Context context;
    List<Contact> list;
    String url ="http://jeevanrekha.aambekar.com/API2/index.php/postImage?";
    String image;
    private void sendSMS(){

        OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("image", image)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try
            {
                Response response= client.newCall(request).execute();

                String d = response.body().string();
                JSONObject object = new JSONObject(d);
                String url = object.getString("url");
                Local local = new Local(context);
                String text = local.getAlertMessage();

                new API(list,text+ "\n Picture : "+url,context).execute();
                Log.d(TAG, "sendSMS: url is"+url);
            }
            catch (Exception e){
                e.printStackTrace();
                Log.d(TAG, "sendSMS: crashed");
            }

    }

    @Override
    protected Void doInBackground(Void... voids) {
        sendSMS();
        return null;
    }


}