package com.joe.lazyalarm.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Joe on 2016/1/15.
 */
public class NetUtils {
    public static boolean isInternetAvilable(Context context) {
        Boolean isOn=false;
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo net=cm.getActiveNetworkInfo();
        if(net!=null){
            isOn=cm.getActiveNetworkInfo().isAvailable();
        }
        return isOn;
    }
    //从本地读取缓存
    public static void getWetherInfoFromLocal(Activity mActivity,Class<? extends Object> classOfT) {
        File file=new File(mActivity.getCacheDir(),"wether.json");
        //如果本地有缓存，从本地读取
        if(file.exists()){
            BufferedReader br=null;
            try {
                br=new BufferedReader(new FileReader(file));
                StringBuffer sb=new StringBuffer();
                String read;
                while((read=br.readLine())!=null){
                    sb.append(read);
                }
                parseData(mActivity,sb.toString(),classOfT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void getWetherInfoFromServer(final Activity mActivity, final Class<? extends Object> classOfT) {

        new Thread(){
            @Override
            public void run() {
                String result=WetherUtils.getRequest1("武汉");

                //拿到数据后缓存到本地；并记录时间
                if(result!=null)
                    saveDataToLocal(mActivity,result);
                PrefUtils.putlong(mActivity,ConsUtils.Last_REQUEST_TIME,System.currentTimeMillis());
            }
        }.start();

    }

    public static Object parseData(Activity activity,String result,Class<? extends Object> classOfT) {
        if (result == null) {
            Toast.makeText(activity, "请求天气数据出错，请检查网络", Toast.LENGTH_SHORT).show();
            return null;
        }
        Gson gson = new Gson();

        return gson.fromJson(result,classOfT);
    }

    public static void saveDataToLocal(Activity activity,String result) {
        File file=new File(activity.getCacheDir(),"wether.json");
        FileWriter fw=null;
        BufferedWriter bw=null;
        try {
            if(!file.exists()) file.createNewFile();
            fw=new FileWriter(file);
            bw=new BufferedWriter(fw);
            bw.write(result);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
