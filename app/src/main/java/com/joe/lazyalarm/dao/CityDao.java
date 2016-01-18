package com.joe.lazyalarm.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Joe on 2016/1/16.
 */
public class CityDao {
    public ArrayList<String> find(String input){
        String path="data/data/com.joe.lazyalarm/files/china_Province_city_zone.db";
        SQLiteDatabase sql=SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //模糊查询
        ArrayList<String> cityList=new ArrayList<String>();
        Cursor cursor=sql.rawQuery("select CityName from T_City where CityName like '%" + input + "%'", null);
        while (cursor.moveToNext()){

            String cityName=cursor.getString(cursor.getColumnIndex("CityName"));
            cityName=cityName.replace("市","");
            cityList.add(cityName);
        }
        sql.close();
        cursor.close();
        return cityList;
    }

    public Cursor findcursor(String input){
        String[] trainColumns = new String[] {"CityName", "id as _id" };
        String selection = "CityName like \'%" + input + "%\' limit 100";
        String path="data/data/com.joe.lazyalarm/files/china_Province_city_zone.db";
        SQLiteDatabase sql=SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //模糊查询
        Cursor cursor=sql.query("T_City", trainColumns, selection, null, null, null, null);
        return cursor;
    }
}
