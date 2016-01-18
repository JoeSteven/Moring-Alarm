package com.joe.lazyalarm.domain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.joe.lazyalarm.dao.AlarmInfoDao;
import com.joe.lazyalarm.reciever.AlarmReciver;

import java.util.Calendar;

/**
 * Created by Joe on 2016/1/13.
 */
public class AlarmClock {
    private AlarmInfo alarmInfo;
    private Context context;

    public AlarmClock(Context context) {
        this.context = context;

    }
    public void turnAlarm(AlarmInfo alarmInfo,Boolean isOn){
        this.alarmInfo=alarmInfo;
        AlarmManager mAlamManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmInfoDao dao=new AlarmInfoDao(context);
        int id=dao.getOnlyId(alarmInfo);
        Intent intent = new Intent(context, AlarmReciver.class);
        intent.setAction("com.Joe.RING_ALARM");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("lazylevel", alarmInfo.getLazyLevel());
        intent.putExtra("alarmid", id);
        intent.putExtra("tag", alarmInfo.getTag());
        intent.putExtra("dayofweek", alarmInfo.getDayOfWeek());
        intent.putExtra("ring", alarmInfo.getRing());
        intent.putExtra("hour",alarmInfo.getHour());
        intent.putExtra("minute",alarmInfo.getMinute());
        intent.putExtra("cancel",false);
        intent.putExtra("getid",alarmInfo.getId());
        intent.putExtra("resid",alarmInfo.getRingResId());
        Log.d("alarm", "id" + id);
        //每个闹钟不同的pi
        PendingIntent pi= PendingIntent.getBroadcast(context,id, intent, 0);
        if(isOn){
            startAlarm(mAlamManager,pi);
        }else{
            cancelAlarm(intent);
        }
    }

    private void cancelAlarm(Intent intent) {
        Log.d("alarm","取消闹钟");
        intent.putExtra("cancel",true);
        context.sendBroadcast(intent);
    }


    public void startAlarm(AlarmManager mAlamManager, PendingIntent pi){
        //  Log.d("alarm","启动一次性闹钟");
        Calendar c=Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,alarmInfo.getHour());
        c.set(Calendar.MINUTE,alarmInfo.getMinute());
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND, 0);
        //  Log.d("alarm", "当前系统版本" + Build.VERSION.SDK_INT);
        if(c.getTimeInMillis()<System.currentTimeMillis()){
            if(Build.VERSION.SDK_INT>=19)
            {
                mAlamManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            }else{
                mAlamManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            }
        }else{
            if(Build.VERSION.SDK_INT>=19)
            {
                mAlamManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            }else{
                mAlamManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            }
        }

    }


}
