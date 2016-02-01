package com.joe.lazyalarm.activity;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.joe.lazyalarm.R;
import com.joe.lazyalarm.domain.CustomRing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomRingSetActivity extends AppCompatActivity {

    private ListView mListView;
    private List<CustomRing> mListRing;
    private int currentItem;
    private myAdapter mAdapter;
    private MediaPlayer mPlayer;
    private LinearLayout mLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_ring_set);
        ActionBar ab = getSupportActionBar();
        // 设置返回开启
        ab.setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_ring_set_custom);
        mLoading = (LinearLayout) findViewById(R.id.ll_loading_custom);
    }

    private void initData() {
        mListRing = new ArrayList<CustomRing>();
        currentItem=0;
        mPlayer=new MediaPlayer();
        new Thread(){
            @Override
            public void run() {
                Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                if(cursor!=null){
                    while (cursor.moveToNext()){
                        CustomRing customRing=new CustomRing();
                        customRing.setRingName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                        customRing.setRingPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                        mListRing.add(customRing);
                        Log.d("ring",customRing.getRingName()+";"+customRing.getRingPath());
                    }
                }
                cursor.close();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initAdapter();
            mLoading.setVisibility(View.GONE);
        }
    };

    private void initAdapter() {
        mAdapter = new myAdapter();
        mListView.setAdapter(mAdapter);
        initListener();
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.播放音乐
                ringTheSong(position);
                //2.重新赋值
                currentItem = position;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void ringTheSong(int position) {
        mPlayer.reset();
        try {
            mPlayer.setDataSource(mListRing.get(position).getRingPath());
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mPlayer.setVolume(1f, 1f);
            mPlayer.setLooping(true);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopTheSong(){
        if(mPlayer.isPlaying()){
            mPlayer.stop();
        }
    }
    static class ViewHolder{
        TextView Name;
        RadioButton Radio;
    }
    class myAdapter extends BaseAdapter{

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            CustomRing customRing=mListRing.get(position);
            if(convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(CustomRingSetActivity.this,R.layout.item_ringset,null);
                holder.Name= (TextView) convertView.findViewById(R.id.tv_name_ring);
                holder.Radio= (RadioButton) convertView.findViewById(R.id.rb_check_ring);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.Name.setText(customRing.getRingName());
            if(position==currentItem){
                holder.Radio.setChecked(true);
            }else{
                holder.Radio.setChecked(false);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return mListRing.size();
        }

        @Override
        public Object getItem(int position) {
            return mListRing.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ring_custom, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_done_custom:
                Intent intent=new Intent();
                intent.putExtra("RingName",mListRing.get(currentItem).getRingName());
                intent.putExtra("RingPath",mListRing.get(currentItem).getRingPath());
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTheSong();
    }
}
