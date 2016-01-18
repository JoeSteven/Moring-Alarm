package com.joe.lazyalarm.domain;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe on 2016/1/12.
 */
public class ActivityCollection {
    public static List<Activity> list=new ArrayList<Activity>();
    public static void add(Activity activity){
        list.add(activity);
    }
    public static void remove(Activity activity){
        list.remove(activity);
    }
    public static void finishAll(){
        for (Activity activity:list) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
