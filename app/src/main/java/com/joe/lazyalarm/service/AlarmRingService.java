package com.joe.lazyalarm.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.joe.lazyalarm.utils.ConsUtils;
import com.joe.lazyalarm.utils.PrefUtils;

import java.io.IOException;

public class AlarmRingService extends Service {
    private String Song;
    private MediaPlayer mPlayer;
    private Vibrator mVibrator;

    public AlarmRingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Song=intent.getStringExtra("resid");
        if (Song==null){
            Song="everybody.mp3";
        }
        ringTheAlarm(Song);
        return super.onStartCommand(intent, flags, startId);
    }

    private void ringTheAlarm(String song) {
        AssetFileDescriptor assetFileDescriptor= null;
        try {
            mPlayer=new MediaPlayer();
            assetFileDescriptor = this.getAssets().openFd(song);
            mPlayer.reset();
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mPlayer.setVolume(1f, 1f);
            mPlayer.setLooping(true);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopTheAlarm(){
        if(mPlayer!=null){
            mPlayer.stop();
            mPlayer.release();
        }

    }
    @Override
    public void onCreate() {
        super.onCreate();
        if(PrefUtils.getBoolean(this, ConsUtils.IS_VIBRATE,false)){
            startVibrate();
        }
    }

    private void stopVibrate() {
        if(mVibrator!=null){
            mVibrator.cancel();
        }
    }

    private void startVibrate() {
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        if(mVibrator.hasVibrator()){
            mVibrator.vibrate(new long[]{500, 1500, 500, 1500}, 0);//off on off on
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTheAlarm();
        stopVibrate();
        Log.d("alarm", "铃声关闭");
    }

}
