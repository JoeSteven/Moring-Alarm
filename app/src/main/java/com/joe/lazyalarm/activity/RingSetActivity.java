package com.joe.lazyalarm.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.ArrayList;

public class RingSetActivity extends AppCompatActivity {

    private ListView lv_ring;
    private String[] ringName=new String[]{"Everybody","荆棘鸟","加勒比海盗","圣斗士(慎点)",
            "Flower","Time Traval","Thank you for","律动","Morning","Echo","Alarm Clock"};
    private ArrayList<String> ringList;
    private ArrayList<String> ringIDList;
    private String[] songId=new String[]{"everybody.mp3","bird.mp3","galebi.mp3","shendoushi.mp3",
            "flower.mp3","timetravel.mp3","thankufor.mp3","mx1.mp3","mx2.mp3","echo.mp3","clock.mp3"};
    private int currentItem;
    private MyAdapter mAdapter;
    private MediaPlayer mPlayer;

    private String setRingName;//最终选定的名字
    private String setRingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_set);
        ActionBar ab = getSupportActionBar();
        // 设置返回开启
        ab.setDisplayHomeAsUpEnabled(true);
        iniView();
        initAdapter();
        initListener();
    }

    private void iniView() {
        lv_ring = (ListView) findViewById(R.id.lv_ring_set);
        setRingName ="everybody.mp3";
        currentItem=0;
        setRingId=songId[0];
        Log.d("alarm","默认得到的id"+setRingId);
        ringList=new ArrayList<String>();
        ringIDList=new ArrayList<String>();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ring_set, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_done_ring:
                doneRing();
                break;
            case R.id.action_custom_ring:
                startActivityForResult(new Intent(this,CustomRingSetActivity.class),0);
                stopTheSong();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initAdapter() {
        for(int i=0;i<ringName.length;i++){
            ringList.add(ringName[i]);
            ringIDList.add(songId[i]);
        }
        mAdapter = new MyAdapter();
        lv_ring.setAdapter(mAdapter);
    }

    private void initListener() {
        lv_ring.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setRingName = ringList.get(position);
                setRingId=ringIDList.get(position);
                currentItem = position;
                mAdapter.notifyDataSetChanged();
                /*if(isPlaying){
                    stopTheSong();
                }*/
                ringTheSong(position);
            }
        });
    }
    //播放音乐
    private void ringTheSong(int position) {
        AssetFileDescriptor assetFileDescriptor= null;
        if(mPlayer==null){
            mPlayer=new MediaPlayer();
        }
        mPlayer.reset();
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            if(position==0&&!ringList.get(position).equals(ringName[0])){
                mPlayer.setDataSource(ringIDList.get(0));
            }else{
                assetFileDescriptor = this.getAssets().openFd(ringIDList.get(position));
                mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            }
            mPlayer.setVolume(1f, 1f);
            mPlayer.setLooping(true);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //isPlaying=true;
    }
    private void stopTheSong(){
        if(mPlayer!=null){
            Log.d("ring","mPlay"+mPlayer.toString());
            if(mPlayer.isPlaying()){
                mPlayer.stop();
            }
            mPlayer.release();
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
            holder.Name.setText(ringList.get(position));
            if(position==currentItem){
                holder.Radio.setChecked(true);
            }else{
                holder.Radio.setChecked(false);
            }
            return convertView;
        }
        @Override
        public int getCount() {
            return ringList.size();
        }

        @Override
        public Object getItem(int position) {
            return ringList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private void cancelRing(){
        setResult(ConsUtils.RING_SET_CANCEL,new Intent());
        finish();
    }

    private void doneRing(){
        Intent intent=new Intent();
        intent.putExtra("songname", setRingName);
        intent.putExtra("songid",setRingId);
        setResult(ConsUtils.RING_SET_DONG,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelRing();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTheSong();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            ringList.add(0,data.getStringExtra("RingName"));
            ringIDList.add(0,data.getStringExtra("RingPath"));
            currentItem=0;
            setRingId=ringIDList.get(0);
            setRingName=ringList.get(0);
            mAdapter.notifyDataSetChanged();
        }
    }
}
