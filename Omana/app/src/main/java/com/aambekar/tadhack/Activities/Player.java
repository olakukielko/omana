package com.aambekar.tadhack.Activities;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;

import com.aambekar.tadhack.R;

public class Player
{
    MediaPlayer player;
    Context context;

    public Player(Context context)
    {
        this.context=context;
    }

    public void StopPlayer()
    {
        if(player!=null)
        {
            if(player.isPlaying())
            {
                player.stop();
                player.release();
                player=null;
            }
        }

    }

    void StartPlayer()
    {
        countDownTimer.start();
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        if(player==null)
        {
            player=MediaPlayer.create(context, R.raw.alarm);
            player.setLooping(true);
            player.setOnErrorListener(errorListener);
            player.start();

        }
        else {
            if(!player.isPlaying())
            {
                player=MediaPlayer.create(context, R.raw.alarm);
                player.setLooping(true);
                player.start();
            }
        }
    }

    private CountDownTimer countDownTimer=new CountDownTimer(10000,1000)
    {
        @Override
        public void onTick(long millisUntilFinished)
        {
            Log.d("ASA", "Inside ontick");



        }

        @Override
        public void onFinish()
        {
            Log.d("ASA", "inside onfinish");
            StopPlayer();
        }
    };

    private MediaPlayer.OnErrorListener errorListener=new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra)
        {
            mp.reset();
            return false;
        }
    };
}