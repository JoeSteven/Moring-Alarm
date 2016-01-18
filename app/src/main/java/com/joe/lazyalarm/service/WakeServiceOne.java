package com.joe.lazyalarm.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WakeServiceOne extends Service {
    public WakeServiceOne() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("bad","服务一被启动");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("bad", "onTaskRemoved:进程被杀死了");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d("bad", "服务一ontrim");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
