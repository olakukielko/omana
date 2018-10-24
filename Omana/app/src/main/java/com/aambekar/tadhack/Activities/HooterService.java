package com.aambekar.tadhack.Activities;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class HooterService extends Service {

    Player player;
    MainActivity ma;

    @Override
    public void onCreate() {
        super.onCreate();

        player=new Player(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent!=null)
        {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals("playalarm"))
                {
                    player.StartPlayer();
                }
                else if (action.equals("stopalarm"))
                {
                    player.StopPlayer();
                }
                ///recording code
                else if (action.equals("startrecording"))
                {
                    ma.startRecording();

                }
                ///end recording code
            }
        }
        return START_STICKY;
    }
}
