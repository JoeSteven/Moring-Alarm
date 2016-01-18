package com.joe.lazyalarm.domain;

import com.joe.lazyalarm.dao.AlarmInfoDao;

import java.io.Serializable;

/**
 * Created by Joe on 2016/1/11.
 */
public class AlarmInfo implements Serializable {
    private int Hour;
    private int Minute;
    private int LazyLevel;
    private String Ring ;
    private String Tag;
    private int[] dayOfWeek;
    private String ringResId;

    public String getRingResId() {
        return ringResId;
    }

    public void setRingResId(String ringResId) {
        this.ringResId = ringResId;
    }
    //private String id=

    public String getId(){
        String id=""+Hour+Minute+ AlarmInfoDao.getDataDayofWeek(dayOfWeek);
        return id;
    }
    @Override
    public String toString() {
        return "AlarmInfo{" +
                getId()+
                ", Tag='" + Tag + '\'' +
                ", Ring='" + Ring + '\'' +
                ", LazyLevel=" + LazyLevel +
                '}';
    }

    public int getHour() {
        return Hour;
    }

    public void setHour(int hour) {
        Hour = hour;
    }

    public int getMinute() {
        return Minute;
    }

    public void setMinute(int minute) {
        Minute = minute;
    }

    public int getLazyLevel() {
        return LazyLevel;
    }

    public void setLazyLevel(int lazyLevel) {
        LazyLevel = lazyLevel;
    }


    public String getRing() {
        return Ring;
    }

    public void setRing(String ring) {
        Ring = ring;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public int[] getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int[] dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
