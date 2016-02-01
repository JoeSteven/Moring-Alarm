package com.joe.lazyalarm.fragment;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joe.lazyalarm.R;
import com.joe.lazyalarm.domain.WetherData;
import com.joe.lazyalarm.utils.ConsUtils;
import com.joe.lazyalarm.utils.NetUtils;
import com.joe.lazyalarm.utils.PrefUtils;
import com.joe.lazyalarm.utils.WetherUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Joe on 2016/1/11.
 */
public class FragWether extends BaseFragment {
    private WetherData mWetherData;
    private LinearLayout mBackground;
    private ImageView mIcon;
    private TextView mWether;
    private TextView mTemperature;
    private TextView mWind;
    private TextView mWindPower;
    private TextView mPM25;
    private TextView mPM25Desc;
    private ImageView mMenu;
    private WetherData.TodayData mTodayData;//今天的天气情况
    private WetherData.PM25 mPm25Data;//今天的PM25
    private ArrayList<WetherData.FutureWether> mFutureList;
    private TextView mDay11;
    private ArrayList<TextView> mfutureTextList;
    private LinearLayout mBottom;
    private Boolean isClose;
    private String cityName;

    @Override
    public View initView() {
        View v=View.inflate(mActivity, R.layout.fragment_wether,null);
        isClose=PrefUtils.getBoolean(mActivity,ConsUtils.SHOULD_WETHER_CLOSE,false);

        mBackground = (LinearLayout) v.findViewById(R.id.ll_background);
        mBottom = (LinearLayout) v.findViewById(R.id.ll_future_content);
        mIcon = (ImageView) v.findViewById(R.id.iv_wether_img);
        mWether = (TextView) v.findViewById(R.id.tv_wether_desc);
        mTemperature = (TextView) v.findViewById(R.id.tv_temperature);
        mWind = (TextView) v.findViewById(R.id.tv_wind);
        mWindPower = (TextView) v.findViewById(R.id.tv_wind_power);
        mPM25 = (TextView) v.findViewById(R.id.tv_pm25);
        mPM25Desc = (TextView) v.findViewById(R.id.tv_pm25_desc);
        mMenu = (ImageView) v.findViewById(R.id.iv_menu);
        //判断要不要展示天气
        changeUI(isClose);
        initIconAnimation(mIcon, 0);
        initTextAnimation();
        initFutureView(v);
        return v;
    }

    private void initFutureView(View v) {
        TextView mDay1 = (TextView) v.findViewById(R.id.tv_future_1);
        TextView mDay2 = (TextView) v.findViewById(R.id.tv_future_2);
        TextView mDay3 = (TextView) v.findViewById(R.id.tv_future_3);
        TextView mDay4 = (TextView) v.findViewById(R.id.tv_future_4);
        TextView mDay5 = (TextView) v.findViewById(R.id.tv_future_5);
        TextView mDay6 = (TextView) v.findViewById(R.id.tv_future_6);
        mfutureTextList = new ArrayList<>();
        mfutureTextList.add(mDay1);
        mfutureTextList.add(mDay2);
        mfutureTextList.add(mDay3);
        mfutureTextList.add(mDay4);
        mfutureTextList.add(mDay5);
        mfutureTextList.add(mDay6);
        for (int i=0;i<mfutureTextList.size();i++){
            int delay=(i+1)*80;
            initIconAnimation(mfutureTextList.get(i),delay);
        }
    }

    //入场动画
    private void initTextAnimation() {
        TranslateAnimation ta=new TranslateAnimation(Animation.RELATIVE_TO_PARENT,1f,Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
        ta.setDuration(1000);
        ta.setFillAfter(true);
        mWether.startAnimation(ta);
        mTemperature.startAnimation(ta);
        mWind.startAnimation(ta);
        mWindPower.startAnimation(ta);
        mPM25.startAnimation(ta);
        mPM25Desc.startAnimation(ta);
    }
    //入场动画
    private void initIconAnimation(View view, int delay) {
        TranslateAnimation ta=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_PARENT,1f,Animation.RELATIVE_TO_SELF,0f);
        ta.setDuration(1000);
        ta.setStartOffset(delay);
        ta.setFillAfter(true);
        view.startAnimation(ta);
    }
/*
* 初始化数据
* */
    protected void initData() {
        cityName=PrefUtils.getString(mActivity,ConsUtils.CURRENT_CITY,"重庆");
        //如果不展示直接return
        if(isClose) return;
        //每次如果本地有缓存的话 先从本地读取缓存
        getWetherInfoFromLocal();
        //判断当前有没有网络
        if(NetUtils.isInternetAvilable(mActivity)){
            //如果有网络请求网络数据前判断一下上一次请求是什么时候，如果超过四个小时就请求
            long last=PrefUtils.getlong(mActivity,ConsUtils.Last_REQUEST_TIME,0l);
            long currentTime=System.currentTimeMillis();
            Log.d("changecity","last"+last+"current"+currentTime);
            if((currentTime-last)>4*60*60*1000){
                getWetherInfoFromServer();
            }
        }else{
            Toast.makeText(mActivity,"更新天气失败，请检查网络",Toast.LENGTH_SHORT).show();
        }
    }



    //从本地读取缓存
    private void getWetherInfoFromLocal() {
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
                parseData(sb.toString(),0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void getWetherInfoFromServer() {

        new Thread(){
            @Override
            public void run() {

                String result=WetherUtils.getRequest1(cityName);
                parseData(result,1);
                //拿到数据后缓存到本地；并记录时间
                if(result!=null)
                saveDataToLocal(result);

            }
        }.start();
    }

    private void parseData(String result,int where) {
        Log.d("wether","返回数据"+result);
        if (result==null){
            Toast.makeText(mActivity,"请求天气数据出错，请检查网络",Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson=new Gson();

        mWetherData = gson.fromJson(result, WetherData.class);
        Log.d("wether", mWetherData.toString());
        if(mWetherData.error_code==0){
            WetherData.WetherResultData wetherResultData=mWetherData.result;
            WetherData.WetherDataData wetherDataData=wetherResultData.data;
            mTodayData = wetherDataData.realtime;
            mPm25Data = wetherDataData.pm25;
            mFutureList = wetherDataData.weather;
            if(where==1) PrefUtils.putlong(mActivity,ConsUtils.Last_REQUEST_TIME,System.currentTimeMillis());
            //是在子线程中进行的，不能直接更新UI
            handler.sendEmptyMessage(0);
        }else if(mWetherData.error_code==207302){
            //没有城市信息
            PrefUtils.putlong(mActivity,ConsUtils.Last_REQUEST_TIME,0l);
            handler.sendEmptyMessage(1);
        }else if(mWetherData.error_code==207303){
            //网络错误，稍后重试
            PrefUtils.putlong(mActivity,ConsUtils.Last_REQUEST_TIME,0l);
            handler.sendEmptyMessage(2);
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    initUI();
                    break;
                case 1:
                    Toast.makeText(mActivity,"抱歉，没有查询到当前城市的天气信息",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(mActivity,"网络错误，更新天气失败",Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    private void initUI() {
        Log.d("wether","初始化UI");
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
        Log.d("wether","现在时间"+currentHour);
        int img=Integer.parseInt(mTodayData.weather.img);
        if(currentHour>=6&&currentHour<=18){
            //白天
            if(img<ConsUtils.WETHER_IMG_DAY.length){
                mIcon.setImageResource(ConsUtils.WETHER_IMG_DAY[img]);
            }else{
                mIcon.setImageResource(ConsUtils.WETHER_IMG_DAY[0]);
            }
        }else{
            //防止脚标越界
            Log.d("wether", "img" + img);
            if(img<ConsUtils.WETHER_IMG_NIGHT.length){
                mIcon.setImageResource(ConsUtils.WETHER_IMG_NIGHT[img]);
            }else{
                mIcon.setImageResource(ConsUtils.WETHER_IMG_NIGHT[0]);
            }

        }
        mWether.setText(mTodayData.weather.info);
        mTemperature.setText(mTodayData.weather.temperature+"°");
        mWind.setText(mTodayData.wind.direct);
        mWindPower.setText(mTodayData.wind.power);
        mPM25.setText("PM2.5     "+mPm25Data.pm25.pm25);
        mPM25Desc.setText(mPm25Data.pm25.quality);
        //拿到未來六天的數據
        for(int i=1;i<mFutureList.size();i++){
            WetherData.FutureWether future=mFutureList.get(i);
            if(i>mfutureTextList.size()) break;
            int imgfu=Integer.parseInt(future.info.day.get(0));//图片
            String tvfu=future.info.day.get(1);
            TextView fu=mfutureTextList.get(i-1);

            if(imgfu<ConsUtils.WETHER_IMG_FUTURE.length) {
                Drawable drawable=getResources().getDrawable(ConsUtils.WETHER_IMG_FUTURE[imgfu]);
                //必须设置该属性才能显示
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                fu.setCompoundDrawables(null, drawable, null, null);
            }
            fu.setText(tvfu);
        }
    }


    private void saveDataToLocal(String result) {
        File file=new File(mActivity.getCacheDir(),"wether.json");
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

    public void changeUI(boolean isChecked) {
        isClose=isChecked;
        if(isChecked){
            mBackground.setVisibility(View.INVISIBLE);
            mBottom.setVisibility(View.INVISIBLE);
        }else{
            mBackground.setVisibility(View.VISIBLE);
            mBottom.setVisibility(View.VISIBLE);
            initData();
        }
    }
    public void refreshData(){
        Log.d("changecity", "刷新data");
        if(!cityName.equals(PrefUtils.getString(mActivity,ConsUtils.CURRENT_CITY,"重庆"))) {
            initData();
            initIconAnimation(mIcon, 0);
            initTextAnimation();
            for (int i = 0; i < mfutureTextList.size(); i++) {
                int delay = (i + 1) * 80;
                initIconAnimation(mfutureTextList.get(i), delay);
            }
        }
    }
}
