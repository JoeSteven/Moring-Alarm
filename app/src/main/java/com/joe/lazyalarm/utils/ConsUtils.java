package com.joe.lazyalarm.utils;

import com.joe.lazyalarm.R;

/**
 * Created by Joe on 2016/1/11.
 */
public  class ConsUtils {
    //Fragment的tag
    public static final String FRAG_WETHER="fragwether";

    //请求码与返回码
    public static final int SET_ALARM_DONE=0;//闹钟设置成功
    public static final int SET_ALARM_CANCEL=1;//闹钟设置取消
    public static final int UPDATE_ALARM_DONE=2;//闹钟设置成功
    public static final int UPDATE_ALARM_CANCEL=3;//闹钟修改取消
    public static final int ADD_ALARM=0;//跳转到闹钟添加
    public static final int UPDATAE_ALARM=8;//跳转到闹钟添加
    public static final int ASK_FOR_RING=5;//跳转到闹钟添加
    public static final int RING_SET_DONG=6;//跳转到闹钟添加
    public static final int RING_SET_CANCEL=7;//跳转到闹钟添加
    //数据库
    public static final String SQLDB_NAME="alarm.db";//闹钟信息数据库
    public static final String ALARM_TABLE="AlarmInfo";//闹钟的信息表名
    public static final String ALARM_HOUR="Hour";//闹钟小时
    public static final String ALARM_MINUTE="Minute";//闹钟分钟
    public static final String ALARM_RING="Ring";//闹钟的铃声
    public static final String ALARM_LAZY_LEVEL="LazyLevel";//闹钟赖床级别
    public static final String ALARM_TAG="Tag";//闹钟标签
    public static final String ALARM_REPEAT_DAY="DayOfWeek";//一周重复的天
    public static final String ALARM_ID="AlarmID";//一周重复的天
    public static final String ALARM_RING_ID="RingId";//一周重复的天

    //天气模块
    public static final String WETHER_API_KEY="sercret";//请求接口的key
    public static final int[] WETHER_IMG_DAY=new int[]{
            R.mipmap.day_01,R.mipmap.day_02,R.mipmap.day_03,R.mipmap.day_04,R.mipmap.day_05,R.mipmap.day_06,
            R.mipmap.day_07,R.mipmap.day_08,R.mipmap.day_09,R.mipmap.day_10,R.mipmap.day_11,R.mipmap.day_12,
            R.mipmap.day_13,R.mipmap.day_14,R.mipmap.day_15,R.mipmap.day_16,R.mipmap.day_17,R.mipmap.day_18,
            R.mipmap.day_19,R.mipmap.day_20,R.mipmap.day_21,R.mipmap.day_22,R.mipmap.day_23,R.mipmap.day_24,
            R.mipmap.day_25,R.mipmap.day_26,R.mipmap.day_27,R.mipmap.day_28,R.mipmap.day_29,R.mipmap.day_30,
            R.mipmap.day_31,R.mipmap.day_32
    };//白天图片库
    public static final int[] WETHER_IMG_NIGHT=new int[]{
            R.mipmap.night_01,R.mipmap.night_02,R.mipmap.night_03,R.mipmap.night_04,R.mipmap.night_05,R.mipmap.night_06,
            R.mipmap.night_07,R.mipmap.night_08,R.mipmap.night_09,R.mipmap.night_10,R.mipmap.night_11,R.mipmap.night_12,
            R.mipmap.night_13,R.mipmap.night_14,R.mipmap.night_15,R.mipmap.night_16,R.mipmap.night_17,R.mipmap.night_18,
            R.mipmap.night_19,R.mipmap.night_20,R.mipmap.night_21,R.mipmap.night_22,R.mipmap.night_23,R.mipmap.night_24,
            R.mipmap.night_25,R.mipmap.night_26,R.mipmap.night_27,R.mipmap.night_28,R.mipmap.night_29,R.mipmap.night_30,
            R.mipmap.night_31,R.mipmap.day_32
    };//夜晚图片库
    public static final int[] WETHER_IMG_FUTURE=new int[]{
            R.mipmap.future_01,R.mipmap.future_02,R.mipmap.future_03,R.mipmap.future_04,R.mipmap.future_05,R.mipmap.future_06,
            R.mipmap.future_07,R.mipmap.future_08,R.mipmap.future_09,R.mipmap.future_10,R.mipmap.future_11,R.mipmap.future_12,
            R.mipmap.future_13,R.mipmap.future_14,R.mipmap.future_15,R.mipmap.future_16,R.mipmap.future_17,R.mipmap.future_18,
            R.mipmap.future_19,R.mipmap.future_20,R.mipmap.future_21,R.mipmap.future_22,R.mipmap.future_23,R.mipmap.future_24,
            R.mipmap.future_25,R.mipmap.future_26,R.mipmap.future_27,R.mipmap.future_28,R.mipmap.future_29,R.mipmap.future_30,
            R.mipmap.future_31,R.mipmap.future_32
    };//白天图片库

    //Pref
    public static final String SHOULD_WETHER_CLOSE="shouldwetherclose";//是否不显示天气
    public static final String Last_REQUEST_TIME="lastrequesttime";//上一次请求网络的时间
    public static final String IS_VIBRATE="isvibrate";//是否打开震动
    public static final String IS_FIRST_TIME="isfirsttime";//是否打开震动
    public static final String CURRENT_CITY="currentcity";//当前选择的城市
    public static final String ALARM_VOLUME="alarmvolume";//当前选择的城市
}
