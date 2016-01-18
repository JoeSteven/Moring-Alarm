package com.joe.lazyalarm.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.joe.lazyalarm.R;
import com.joe.lazyalarm.dao.AlarmInfoDao;
import com.joe.lazyalarm.domain.AlarmInfo;
import com.joe.lazyalarm.utils.ConsUtils;
import com.joe.lazyalarm.view.AddItemView;

public class AddAlarmActivity extends BaseActivity implements View.OnClickListener{

    private TimePicker mTimePicker;//闹钟定时
    private ImageView mBack;//后退
    private TextView mDone;//完成
    private int mHours=6;
    private int mMinute=30;
    private AddItemView mTag;
    private AddItemView mLazyLevel;
    private AddItemView mRing;
    private CheckBox mDay1;
    private CheckBox mDay2;
    private CheckBox mDay3;
    private CheckBox mDay4;
    private CheckBox mDay5;
    private CheckBox mDay6;
    private CheckBox mDay7;
    private int mLevel;
    private String mTagDesc;
    private Intent mIntent;
    private String RingName;
    private String Ringid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIntent = getIntent();
        mTimePicker = (TimePicker) findViewById(R.id.tp_set_alarm_add);
        mTimePicker.setIs24HourView(true);
        mBack = (ImageView) findViewById(R.id.iv_back_add);
        mDone = (TextView) findViewById(R.id.tv_done_add);
        mTag = (AddItemView) findViewById(R.id.aiv_tag_add);
        mLazyLevel = (AddItemView) findViewById(R.id.aiv_lazy_add);
        mRing = (AddItemView) findViewById(R.id.aiv_ring_add);
        mLevel=0;
        mTagDesc="闹钟";
        Ringid="everybody.mp3";
        initCheckBox();
    }

    private void initCheckBox() {
        mDay1 = (CheckBox) findViewById(R.id.cb_day_1);
        mDay2 = (CheckBox) findViewById(R.id.cb_day_2);
        mDay3 = (CheckBox) findViewById(R.id.cb_day_3);
        mDay4 = (CheckBox) findViewById(R.id.cb_day_4);
        mDay5 = (CheckBox) findViewById(R.id.cb_day_5);
        mDay6 = (CheckBox) findViewById(R.id.cb_day_6);
        mDay7 = (CheckBox) findViewById(R.id.cb_day_7);
    }

    private void initData() {
        mTimePicker.setCurrentHour(mHours);
        mTimePicker.setCurrentMinute(mMinute);
    }

    //获取TimePicker的时间
    private void initListener() {
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mMinute = minute;
                mHours = hourOfDay;
            }
        });
        mLazyLevel.setOnClickListener(this);
        mRing.setOnClickListener(this);
        mTag.setOnClickListener(this);
    }
    //点击监听
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.aiv_tag_add:
                showTagDialog();
                break;
            case R.id.aiv_lazy_add:
                showLazyDialog();
                break;
            case R.id.aiv_ring_add:
                startActivityForResult(new Intent(this, RingSetActivity.class), ConsUtils.ASK_FOR_RING);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case ConsUtils.RING_SET_CANCEL:
                break;
            case ConsUtils.RING_SET_DONG:
                RingName=data.getStringExtra("songname");
                if(data.getStringExtra("songid")!=null){
                    Ringid=data.getStringExtra("songid");
                }
                mRing.setDesc(RingName);
                break;
        }
    }

    private int[] getRepeatDay() {
        //当用户点击完成时 判断各个CheckBox的勾选情况
        String dayRepeat="";
        if(mDay1.isChecked()){
            dayRepeat+="1"+",";
        }
        if(mDay2.isChecked()){
            dayRepeat+="2"+",";
        }
        if(mDay3.isChecked()){
            dayRepeat+="3"+",";
        }
        if(mDay4.isChecked()){
            dayRepeat+="4"+",";
        }
        if(mDay5.isChecked()){
            dayRepeat+="5"+",";
        }
        if(mDay6.isChecked()){
            dayRepeat+="6"+",";
        }
        if(mDay7.isChecked()){
            dayRepeat+="7"+",";
        }
        if(dayRepeat.equals("")){
            dayRepeat="0,";
        }
        return AlarmInfoDao.getAlarmDayofWeek(dayRepeat);
    }
    public AlarmInfo getAddAlarmInfo(){
        AlarmInfo alarmInfo=new AlarmInfo();
        alarmInfo.setHour(mHours);
        alarmInfo.setMinute(mMinute);

        int[] day=getRepeatDay();
        alarmInfo.setDayOfWeek(day);
        alarmInfo.setLazyLevel(mLevel);

        alarmInfo.setTag(mTagDesc);
        alarmInfo.setRing(RingName);
        alarmInfo.setRingResId(Ringid);
        return alarmInfo;
    }

    //底边栏的两个方法
    public void doneAlarm(View v){
        //当用户完成设置时，将时间封装到对象中，传回给homeActivity
        AlarmInfo alarmInfo=getAddAlarmInfo();
        AlarmInfoDao dao=new AlarmInfoDao(this);

        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putSerializable("alarm", alarmInfo);
        intent.putExtras(bundle);

        if(mIntent.getStringExtra("oldId")!=null){
            //修改数据库
            dao.updateAlarm(mIntent.getStringExtra("oldId"), alarmInfo);
            intent.setClass(this,HomeActivity.class);
            startActivity(intent);
        }else{
            dao.addAlarmInfo(alarmInfo);
            setResult(ConsUtils.SET_ALARM_DONE, intent);
        }
        finish();
    }

    public void cancelAlarm(View v){
        if(mIntent.getStringExtra("oldId")!=null){
            setResult(ConsUtils.UPDATE_ALARM_CANCEL,new Intent());
            startActivity(new Intent(this,HomeActivity.class));
        }
        else{
            setResult(ConsUtils.SET_ALARM_CANCEL, new Intent());
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mIntent.getStringExtra("oldId")!=null){
            setResult(ConsUtils.UPDATE_ALARM_CANCEL,new Intent());
            startActivity(new Intent(this, HomeActivity.class));
        }
        else{
            setResult(ConsUtils.SET_ALARM_CANCEL, new Intent());
        }
        finish();
    }

    //选择赖床级数
    private void showLazyDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("选择你的赖床指数");
        String[] item=new String[]{"本宝宝从不赖床！","稍微拖延个七八分钟啦~"
                ,"半个小时准时起床！","七点的闹钟八点起~","闹钟是什么东西？！"};
        dialog.setSingleChoiceItems(item, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mLevel=which;
                mLazyLevel.setDesc("赖床指数:"+mLevel+"级");
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //编辑标签
    private void showTagDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        AlertDialog dialog=builder.create();
        View edit=View.inflate(this,R.layout.dialog_tag,null);
        final EditText tag= (EditText) edit.findViewById(R.id.et_tag);
      /*  dialog.setTitle();
        dialog.setView(edit);*/
        builder.setTitle("闹钟标签");
        builder.setView(edit);
        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTagDesc=tag.getText().toString();
                mTag.setDesc(mTagDesc);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
