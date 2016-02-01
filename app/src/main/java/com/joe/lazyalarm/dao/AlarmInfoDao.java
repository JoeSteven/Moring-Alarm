package com.joe.lazyalarm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.joe.lazyalarm.domain.AlarmInfo;
import com.joe.lazyalarm.utils.ConsUtils;

import java.util.ArrayList;
import java.util.List;

/**数据库读取工具
 * Created by Joe on 2016/1/12.
 */
public class AlarmInfoDao {
    private Context mContext;
    private AlarmInfoOpenHelper mHelper;
    public AlarmInfoDao(Context mContext) {
        this.mContext = mContext;
        mHelper=new AlarmInfoOpenHelper(mContext);
    }

    public void addAlarmInfo(AlarmInfo alarmInfo){
        //添加一个闹钟时回调
        SQLiteDatabase db=mHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(ConsUtils.ALARM_HOUR,alarmInfo.getHour());
        values.put(ConsUtils.ALARM_MINUTE,alarmInfo.getMinute());
        values.put(ConsUtils.ALARM_LAZY_LEVEL,alarmInfo.getLazyLevel());
        values.put(ConsUtils.ALARM_RING,alarmInfo.getRing());
        values.put(ConsUtils.ALARM_TAG,alarmInfo.getTag());
        values.put(ConsUtils.ALARM_REPEAT_DAY, getDataDayofWeek(alarmInfo.getDayOfWeek()));
        values.put(ConsUtils.ALARM_ID,alarmInfo.getId());
        values.put(ConsUtils.ALARM_RING_ID,alarmInfo.getRingResId());
        db.insert(ConsUtils.ALARM_TABLE, null, values);
        Toast.makeText(mContext, "闹钟设置成功", Toast.LENGTH_SHORT).show();
        if(db!=null){
            db.close();
            values.clear();
            values=null;
        }
    }
    public AlarmInfo findById(String alarmId){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        Cursor cursor=db.query(ConsUtils.ALARM_TABLE, null, ConsUtils.ALARM_ID+"=?", new String[]{alarmId}, null, null, null);
        AlarmInfo alarmInfo=new AlarmInfo();
        if(cursor!=null){
            //Log.d("alarm","cusor不为空");
            if (cursor.moveToNext()){

                alarmInfo.setHour(cursor.getInt(cursor.getColumnIndex(ConsUtils.ALARM_HOUR))) ;
                alarmInfo.setMinute(cursor.getInt(cursor.getColumnIndex(ConsUtils.ALARM_MINUTE)));
                alarmInfo.setLazyLevel(cursor.getInt(cursor.getColumnIndex(ConsUtils.ALARM_LAZY_LEVEL)));
                alarmInfo.setRing(cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_RING)));
                alarmInfo.setTag(cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_TAG)));
                alarmInfo.setRingResId(cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_RING_ID)));
                String dayOfWeek=cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_REPEAT_DAY));

                Log.d("alarm",dayOfWeek);
                int[] day=getAlarmDayofWeek(dayOfWeek);
                if(day!=null){
                   // Log.d("alarm","数据库中重复天数不为空");
                }else {
                   // Log.d("alarm","数据库中重复天数为空");
                }
                alarmInfo.setDayOfWeek(day);
            }
        }
        return alarmInfo;
    }
    public AlarmInfo findByOnlyId(String alarmId){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        Cursor cursor=db.query(ConsUtils.ALARM_TABLE, null, "id=?", new String[]{alarmId}, null, null, null);
        AlarmInfo alarmInfo=new AlarmInfo();
        if(cursor!=null){
            while (cursor.moveToNext()){

                alarmInfo.setHour(cursor.getInt(cursor.getColumnIndex(ConsUtils.ALARM_HOUR))) ;
                alarmInfo.setMinute(cursor.getInt(cursor.getColumnIndex(ConsUtils.ALARM_MINUTE)));
                alarmInfo.setLazyLevel(cursor.getInt(cursor.getColumnIndex(ConsUtils.ALARM_LAZY_LEVEL)));
                alarmInfo.setRing(cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_RING)));
                alarmInfo.setTag(cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_TAG)));
                alarmInfo.setRingResId(cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_RING_ID)));
                String dayOfWeek=cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_REPEAT_DAY));

                Log.d("alarm",dayOfWeek);
                int[] day=getAlarmDayofWeek(dayOfWeek);
                alarmInfo.setDayOfWeek(day);
            }
        }
        return alarmInfo;
    }
    public List<AlarmInfo> getAllInfo(){
        List<AlarmInfo> list=new ArrayList<AlarmInfo>();
        SQLiteDatabase db=mHelper.getWritableDatabase();
        Cursor cursor=db.query(ConsUtils.ALARM_TABLE, null, null, null, null, null, null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                AlarmInfo alarmInfo=new AlarmInfo();
                alarmInfo.setHour(cursor.getInt(cursor.getColumnIndex(ConsUtils.ALARM_HOUR))) ;
                alarmInfo.setMinute(cursor.getInt(cursor.getColumnIndex(ConsUtils.ALARM_MINUTE)));
                alarmInfo.setLazyLevel(cursor.getInt(cursor.getColumnIndex(ConsUtils.ALARM_LAZY_LEVEL)));
                alarmInfo.setRing(cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_RING)));
                alarmInfo.setTag(cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_TAG)));
                alarmInfo.setRingResId(cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_RING_ID)));
                String dayOfWeek=cursor.getString(cursor.getColumnIndex(ConsUtils.ALARM_REPEAT_DAY));

                Log.d("alarm",dayOfWeek);
                int[] day=getAlarmDayofWeek(dayOfWeek);
                alarmInfo.setDayOfWeek(day);
                list.add(alarmInfo);
            }
            cursor.close();
        }
        if(db!=null){
            db.close();
        }
        return list;
    }

    /*
    *删除闹钟
    */
    public void deleteAlarm(AlarmInfo alarmInfo){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        db.delete(ConsUtils.ALARM_TABLE,ConsUtils.ALARM_ID+" = ?",new String[]{alarmInfo.getId()});
        Toast.makeText(mContext, "移除成功", Toast.LENGTH_SHORT).show();
        db.close();
    }

    /*
    *编辑闹钟
    */
    public void updateAlarm(String oldId,AlarmInfo alarmInfo){
        SQLiteDatabase db=mHelper.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(ConsUtils.ALARM_HOUR,alarmInfo.getHour());
        values.put(ConsUtils.ALARM_MINUTE,alarmInfo.getMinute());
        values.put(ConsUtils.ALARM_LAZY_LEVEL,alarmInfo.getLazyLevel());
        values.put(ConsUtils.ALARM_RING,alarmInfo.getRing());
        values.put(ConsUtils.ALARM_TAG,alarmInfo.getTag());
        values.put(ConsUtils.ALARM_REPEAT_DAY, getDataDayofWeek(alarmInfo.getDayOfWeek()));
        values.put(ConsUtils.ALARM_ID,alarmInfo.getId());
        values.put(ConsUtils.ALARM_RING_ID,alarmInfo.getRingResId());
        db.updateWithOnConflict(ConsUtils.ALARM_TABLE, values, ConsUtils.ALARM_ID + " = ?", new String[]{oldId}, SQLiteDatabase.CONFLICT_IGNORE);
        Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
        db.close();
        Log.d("alarm","update完成");
    }
    public int getOnlyId(AlarmInfo alarmInfo){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        int id=-1;
        Cursor cusor=db.query(ConsUtils.ALARM_TABLE,new String[]{"id"},ConsUtils.ALARM_ID+"=?",new String[]{alarmInfo.getId()},null,null,null);
        if(cusor!=null){
            if(cusor.moveToNext()){
                id=cusor.getInt(cusor.getColumnIndex("id"));
            }
        }
        if(db!=null){
            db.close();
        }
        cusor.close();
        return id;
    }
    public static int[] getAlarmDayofWeek(String dayOfWeek) {
        String[] change= dayOfWeek.split(",");
        int[] Day=new int[change.length];
        for (int i=0;i<change.length;i++){
            Day[i]=Integer.parseInt(change[i]);
        }
        return Day;
    }

    public static String getDataDayofWeek(int[] Day) {
        String dayOfWeek = "";
        //将重复的天数从数组变为字符串
        for (int i = 0; i < Day.length; i++) {
            int day = Day[i];
            if (i == Day.length - 1) {
                dayOfWeek = dayOfWeek + day;
            } else {
                dayOfWeek = dayOfWeek + day + ",";
            }
        }
        return dayOfWeek;
    }

}
