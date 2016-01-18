package com.joe.lazyalarm.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.joe.lazyalarm.utils.ConsUtils;

/**
 * Created by Joe on 2016/1/12.
 */
public class AlarmInfoOpenHelper extends SQLiteOpenHelper {
    private Context mContext;
    public static final String CREATE_ALARMINFO="create table AlarmInfo" +
            "(id integer primary key autoincrement,"+
            ConsUtils.ALARM_HOUR+" integer,"+
            ConsUtils.ALARM_MINUTE+" integer ,"+
            ConsUtils.ALARM_LAZY_LEVEL+" integer,"+
            ConsUtils.ALARM_RING+" text,"+
            ConsUtils.ALARM_TAG+" text,"+
            ConsUtils.ALARM_REPEAT_DAY+" text,"+
            ConsUtils.ALARM_RING_ID+" text,"+
            ConsUtils.ALARM_ID+" text"+
            ")";
    public AlarmInfoOpenHelper(Context context) {
        super(context, ConsUtils.SQLDB_NAME, null, 1);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建一个数据库
        db.execSQL(CREATE_ALARMINFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
