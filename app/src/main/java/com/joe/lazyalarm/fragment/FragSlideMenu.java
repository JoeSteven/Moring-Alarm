package com.joe.lazyalarm.fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.lazyalarm.R;
import com.joe.lazyalarm.activity.HomeActivity;
import com.joe.lazyalarm.dao.CityDao;
import com.joe.lazyalarm.utils.ConsUtils;
import com.joe.lazyalarm.utils.PrefUtils;
import com.kyleduo.switchbutton.SwitchButton;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Joe on 2016/1/15.
 */
public class FragSlideMenu extends BaseFragment {

    private LinearLayout mChangeCity;//更换城市
    private TextView mCurrentCity;//当前城市显示
    private SwitchButton mCloseWether;//关闭天气
    private SwitchButton mOpenVibrate;//开启震动
    private ArrayList<String> cityList;//城市列表数据
    private CityDao dao;//读取数据库工具
    private AlertDialog.Builder builder;//更换城市对话框
    private SeekBar mVolume;
    private MediaPlayer mPlayer;
    private AudioManager mAudioManager;
    private Boolean isMusicOn;
    @Override
    public View initView() {
        View view=View.inflate(mActivity, R.layout.fragment_menu,null);
        mChangeCity = (LinearLayout) view.findViewById(R.id.ll_change_city);
        mCurrentCity = (TextView) view.findViewById(R.id.tv_current_city);
        mCloseWether = (SwitchButton) view.findViewById(R.id.sb_close_wether);
        mOpenVibrate = (SwitchButton) view.findViewById(R.id.sb_open_vibrate);
        mVolume = (SeekBar) view.findViewById(R.id.seek_volume_adjust);
        return view;
    }

    @Override
    protected void initData() {
        mCloseWether.setChecked(PrefUtils.getBoolean(mActivity, ConsUtils.SHOULD_WETHER_CLOSE, false));
        mOpenVibrate.setChecked(PrefUtils.getBoolean(mActivity, ConsUtils.IS_VIBRATE, false));
        mCurrentCity.setText(PrefUtils.getString(mActivity, ConsUtils.CURRENT_CITY, "重庆"));
        mVolume.setProgress(PrefUtils.getInt(mActivity, ConsUtils.ALARM_VOLUME, 100));
        mAudioManager = (AudioManager) mActivity.getSystemService(mActivity.AUDIO_SERVICE);
        isMusicOn=false;
        initListener();
    }

    private void initListener() {
        //开关天气显示监听
        mCloseWether.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                stopAlarmMusic();
                //记录是否应该关闭天气数据
                PrefUtils.putBoolean(mActivity, ConsUtils.SHOULD_WETHER_CLOSE, isChecked);
                changeWetherUI(isChecked);
            }
        });
        //开关震动监听
        mOpenVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                stopAlarmMusic();
                if (isChecked) {
                    Vibrator vibrator = (Vibrator) mActivity.getSystemService(mActivity.VIBRATOR_SERVICE);
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(2000);
                        Toast.makeText(mActivity, "震动已开启", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity, "你的手机没有震动功能", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mActivity, "震动已关闭", Toast.LENGTH_SHORT).show();
                }
                PrefUtils.putBoolean(mActivity, ConsUtils.IS_VIBRATE, isChecked);
            }
        });
        //更换城市监听
        mChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarmMusic();
                cityList = new ArrayList<String>();
                final View autoLayout = View.inflate(mActivity, R.layout.auto_edit_view, null);
                final EditText autoText = (EditText) autoLayout.findViewById(R.id.et_change_city);
                ListView listHint = (ListView) autoLayout.findViewById(R.id.lv_change_city);
                initAutoEdit(autoText, listHint);
                dao = new CityDao();
                builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("输入您要更换的城市");
                builder.setView(autoLayout);
                builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cityName = autoText.getText().toString();
                        if (cityName == null || cityName.equals("")) {
                            Toast.makeText(mActivity, "城市不能为空", Toast.LENGTH_SHORT).show();
                        } else if (cityName.matches("^[\u4e00-\u9fa5]+$")) {
                            //cityname为汉字
                            //1.记录下来
                            PrefUtils.putString(mActivity, ConsUtils.CURRENT_CITY, autoText.getText().toString());
                            //2.更改文字
                            changeCurrentCity(autoText.getText().toString());
                            //3.重新请求网络数据
                            PrefUtils.putlong(mActivity, ConsUtils.Last_REQUEST_TIME, 0l);
                        } else {
                            Toast.makeText(mActivity, "城市名只能为汉字", Toast.LENGTH_SHORT).show();
                            autoText.setText("");
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        //调节音量监听
        mVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PrefUtils.putInt(mActivity, ConsUtils.ALARM_VOLUME, progress);
                setSystemVolume(progress);
                playAlarmMusic();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    //初始化自动补全的文本框
    private void initAutoEdit(final EditText autoText, final ListView listHint) {
        //初始化listView
        Log.d("changecity", "初始化adapter");
        final myAdapter adapter=new myAdapter();
        listHint.setAdapter(adapter);
        autoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    Log.d("changecity", "显示listview");
                    //如果字符大于0，显示listview
                    listHint.setVisibility(View.VISIBLE);
                    cityList = dao.find(s.toString());
                    adapter.notifyDataSetChanged();
                } else {
                    listHint.setVisibility(View.GONE);
                }
                int itemCount = listHint.getAdapter().getCount();
                Log.d("changecity", "count" + itemCount);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listHint.getLayoutParams();
                if (itemCount > 4) {
                    params.height = 500;
                } else {
                    params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
                listHint.setLayoutParams(params);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int height = listHint.getHeight();
                Log.d("changecity", height + "文字改变后");

            }
        });

        listHint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView cityName= (TextView) view.findViewById(R.id.tv_hint_city);
                autoText.setText(cityName.getText());
                listHint.setVisibility(View.GONE);
            }
        });
    }

    class myAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return cityList.size();
        }

        @Override
        public Object getItem(int position) {
            return cityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                convertView=View.inflate(mActivity,R.layout.item_change_city,null);
                holder=new ViewHolder();
                holder.hint= (TextView) convertView.findViewById(R.id.tv_hint_city);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.hint.setText(cityList.get(position));
            return convertView;
        }
    }
    static class ViewHolder{
        TextView hint;
    }
    //改变天气UI
    private void changeWetherUI(boolean isChecked) {
        HomeActivity home= (HomeActivity) mActivity;
        FragmentManager fm=home.getFragmentManager();
        FragWether fw= (FragWether) fm.findFragmentByTag(ConsUtils.FRAG_WETHER);
        fw.changeUI(isChecked);
    }
//改变当前的城市文字
    private void changeCurrentCity(String city) {
        mCurrentCity.setText(city);
    }

    //播放并调节当前音量

    private void setSystemVolume(int progress) {
        //获取到音量调节管理器
        int maxVolume= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int setVolume=(maxVolume*progress)/100;
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, setVolume, 0);
    }

    //播放音乐
    private void playAlarmMusic() {

        if(mPlayer==null){
            try {
                mPlayer=new MediaPlayer();
                AssetFileDescriptor assetFileDescriptor=mActivity.getAssets().openFd("everybody.mp3");
                mPlayer.reset();
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                mPlayer.setVolume(1f, 1f);
                mPlayer.prepare();
                mPlayer.start();
                isMusicOn=true;
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

    public void stopAlarmMusic(){
        if(isMusicOn==true){
            mPlayer.stop();
            mPlayer.release();
            isMusicOn=false;
            mPlayer=null;
        }
    }
}
