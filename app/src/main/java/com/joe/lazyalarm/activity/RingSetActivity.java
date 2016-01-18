package com.joe.lazyalarm.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.joe.lazyalarm.R;
import com.joe.lazyalarm.utils.ConsUtils;

import java.io.IOException;

public class RingSetActivity extends AppCompatActivity {

    private ListView lv_ring;
    private String[] ringName=new String[]{"Everybody","荆棘鸟","加勒比海盗","圣斗士(慎点)",
            "Flower","Time Traval","Thank you for","律动","Morning","Echo","Alarm Clock"};
    private String[] songId=new String[]{"everybody.mp3","bird.mp3","galebi.mp3","shendoushi.mp3",
            "flower.mp3","timetravel.mp3","thankufor.mp3","mx1.mp3","mx2.mp3","echo.mp3","clock.mp3"};
    private int currentItem;
    private MyAdapter mAdapter;
    private MediaPlayer mPlayer;
    private Boolean isPlaying;


    private String serRingName;//最终选定的名字
    private String setRingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_set);
        iniView();
        initAdapter();
        initListener();
    }

    private void iniView() {
        lv_ring = (ListView) findViewById(R.id.lv_ring_set);
        serRingName="everybody.mp3";
        currentItem=0;
        setRingId=songId[0];
        Log.d("alarm","默认得到的id"+setRingId);
        isPlaying=false;
    }

    private void initAdapter() {
        mAdapter = new MyAdapter();
        lv_ring.setAdapter(mAdapter);
    }

    private void initListener() {
        lv_ring.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                serRingName = ringName[position];
                setRingId=songId[position];
                currentItem = position;
                mAdapter.notifyDataSetChanged();
                if(isPlaying){
                    stopTheSong();
                }
                ringTheSong(position);
            }
        });
    }
    //播放音乐
    private void ringTheSong(int position) {
        AssetFileDescriptor assetFileDescriptor= null;
        try {
            mPlayer=new MediaPlayer();
            assetFileDescriptor = this.getAssets().openFd(songId[position]);
            mPlayer.reset();
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mPlayer.setVolume(1f, 1f);
            mPlayer.setLooping(true);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        isPlaying=true;
    }
    private void stopTheSong(){
        if(mPlayer!=null){
            mPlayer.stop();
            mPlayer.release();
            isPlaying=false;
        }
    }


    static class ViewHolder{
        TextView Name;
        RadioButton Radio;
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(RingSetActivity.this,R.layout.item_ringset,null);
                holder.Name= (TextView) convertView.findViewById(R.id.tv_name_ring);
                holder.Radio= (RadioButton) convertView.findViewById(R.id.rb_check_ring);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.Name.setText(ringName[position]);
            if(position==currentItem){
                holder.Radio.setChecked(true);
            }else{
                holder.Radio.setChecked(false);
            }
            return convertView;
        }
        @Override
        public int getCount() {
            return ringName.length;
        }

        @Override
        public Object getItem(int position) {
            return ringName[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public void cancelRing(View v){
        stopTheSong();
        setResult(ConsUtils.RING_SET_CANCEL,new Intent());
        finish();
    }

    public void doneRing(View v){
        stopTheSong();
        Intent intent=new Intent();
        intent.putExtra("songname",serRingName);
        intent.putExtra("songid",setRingId);
        setResult(ConsUtils.RING_SET_DONG,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(ConsUtils.RING_SET_CANCEL,new Intent());
        finish();
    }
}
