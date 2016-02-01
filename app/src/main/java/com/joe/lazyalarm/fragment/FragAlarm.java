package com.joe.lazyalarm.fragment;

import android.app.AlarmManager;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.lazyalarm.R;
import com.joe.lazyalarm.activity.AddAlarmActivity;
import com.joe.lazyalarm.dao.AlarmInfoDao;
import com.joe.lazyalarm.domain.AlarmClock;
import com.joe.lazyalarm.domain.AlarmInfo;
import com.joe.lazyalarm.reciever.BootReceiver;
import com.joe.lazyalarm.utils.ConsUtils;
import com.joe.lazyalarm.utils.PrefUtils;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

/**
 * Created by Joe on 2016/1/11.
 */
public class FragAlarm extends BaseFragment{
    private ListView lv_alarm;
    private List<AlarmInfo> mAlarmInfoList;
    private MyAdapter mAdapter;
    private AlarmManager mAlarmManager;
    private AlarmInfoDao mDao;
    private AlarmClock mAlarmClock;
    private View onMenu;
    private Boolean isMenuOn;

    public View initView() {
        View view=View.inflate(mActivity,R.layout.fragment_alarm,null);
        lv_alarm = (ListView) view.findViewById(R.id.lv_alarm);
        Intent intent=new Intent(mActivity,BootReceiver.class);
        mActivity.sendBroadcast(intent);
        return view;
    }

    protected void initData() {

        onMenu=null;
        isMenuOn=false;
        mDao = new AlarmInfoDao(mActivity);
        mAlarmClock = new AlarmClock(mActivity);
        mAlarmInfoList = mDao.getAllInfo();
        if(mAlarmInfoList.size()>0){
            new Thread(){
                @Override
                public void run() {
                    for (AlarmInfo alarmInfo:mAlarmInfoList) {
                        Boolean isAlarmOn=PrefUtils.getBoolean(mActivity, alarmInfo.getId(), true);
                        //开启闹钟或关闭闹钟
                        mAlarmClock.turnAlarm(alarmInfo,null,isAlarmOn);
                    }
                }
            }.start();
        }
        initAdapter();
        initListener();
    }

    private void initAdapter() {
        mAdapter = new MyAdapter();
        lv_alarm.setAdapter(mAdapter);
    }

    private void initListener() {
        lv_alarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                //showPopWindow(view, position);
                if(onMenu!=null&&onMenu==view){
                    toggleMenu(view);
                }else if(onMenu!=null&onMenu!=view){
                    toggleMenu(onMenu);
                    toggleMenu(view);
                }else{
                    toggleMenu(view);
                }
                final Button bt_delete = (Button) view.findViewById(R.id.bt_delete_item);
                final Button bt_update = (Button) view.findViewById(R.id.bt_update_item);
                bt_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //关掉菜单
                        toggleMenu(view);
                        //删除的时候取消掉闹钟
                        mAlarmClock.turnAlarm(mAlarmInfoList.get(position),null,false);
                        //将缓存中的数据也删除掉
                        PrefUtils.remove(mActivity,mAlarmInfoList.get(position).getId());
                        //删除该条数据
                        mDao.deleteAlarm(mAlarmInfoList.get(position));
                        //从集合中移除
                        mAlarmInfoList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                bt_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldAlarmID=mAlarmInfoList.get(position).getId();
                        Intent intent=new Intent(mActivity, AddAlarmActivity.class);
                        intent.putExtra("update",true);
                        intent.putExtra("oldId",oldAlarmID);
                        intent.putExtra("location", position);
                        mActivity.startActivityForResult(intent,ConsUtils.UPDATAE_ALARM);
                        //mActivity.finish();
                    }
                });

            }
        });
        lv_alarm.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                 if(isMenuOn){
                    toggleMenu(onMenu);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
               /* if(isMenuOn){
                    toggleMenu(onMenu);
                }*/
            }
        });
    }
    //用于弹出菜单
    private void toggleMenu(View view) {
        Log.d("alarm","此时的菜单打开情况"+isMenuOn);
        RelativeLayout rl_main = (RelativeLayout) view.findViewById(R.id.rl_main_item);
        LinearLayout ll_button = (LinearLayout) view.findViewById(R.id.ll_button_item);
        final Button bt_delete = (Button) view.findViewById(R.id.bt_delete_item);
        final Button bt_update = (Button) view.findViewById(R.id.bt_update_item);
        final SwitchButton sb= (SwitchButton) view.findViewById(R.id.bt_turn_item);
        Log.d("alarm", "后面宽" + ll_button.getWidth() + "前面" + rl_main.getWidth());
        float back = ll_button.getWidth();
        float front = rl_main.getWidth();
        float width = back / front;
        ll_button.setMinimumHeight(rl_main.getHeight());
        Log.d("alarm", width + "宽度");
        TranslateAnimation ta;
        if(isMenuOn){
            //开着的就关
            ta= new TranslateAnimation(Animation.RELATIVE_TO_SELF, width, Animation.RELATIVE_TO_SELF,
                    0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
            onMenu=null;
            isMenuOn=false;
        }else{
            ta= new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                    width, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
            onMenu=view;
            isMenuOn=true;
        }
        ta.setDuration(200);
        ta.setFillAfter(true);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画执行完后
                if (isMenuOn) {
                    bt_delete.setClickable(true);
                    bt_update.setClickable(true);
                    sb.setClickable(false);
                } else {
                    bt_delete.setClickable(false);
                    bt_update.setClickable(false);
                    sb.setClickable(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rl_main.startAnimation(ta);
        Log.d("alarm", "点击后的菜单打开情况" + isMenuOn);
    }



    //添加新的闹钟时回调该方法
    public void addAlarmInfo(AlarmInfo alarmInfo){
        mAlarmInfoList.add(alarmInfo);
        mAdapter.notifyDataSetChanged();
        //添加完数据后刷新下
        initData();
    }

    static class ViewHolder{
        TextView Time;
        TextView Desc;
        SwitchButton Turn;
    }

    //adapter
    class MyAdapter extends BaseAdapter{
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            //如果集合中没有数据
            if(mAlarmInfoList==null) return null;
            final AlarmInfo alarmInfo=mAlarmInfoList.get(position);
            if(convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(mActivity,R.layout.item_alarm,null);
                holder.Time= (TextView) convertView.findViewById(R.id.tv_title_item);
                holder.Desc= (TextView) convertView.findViewById(R.id.tv_desc_item);
                holder.Turn= (SwitchButton) convertView.findViewById(R.id.bt_turn_item);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder)convertView.getTag();
            }
            String hour=alarmInfo.getHour()+"";
            String minute=alarmInfo.getMinute()+"";
            if(alarmInfo.getHour()<10) hour="0"+alarmInfo.getHour();
            if(alarmInfo.getMinute()<10) minute = "0" + alarmInfo.getMinute();
            holder.Time.setText( hour+ ":" + minute);
            Boolean isAlarmOn=PrefUtils.getBoolean(mActivity, alarmInfo.getId(), true);
            //跳转开关
            toggleAlarm(holder, isAlarmOn, alarmInfo);

            holder.Turn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        PrefUtils.putBoolean(mActivity, alarmInfo.getId(), true);
                        toggleAlarm(holder, PrefUtils.getBoolean(mActivity, alarmInfo.getId(), true), alarmInfo);
                        mAlarmClock.turnAlarm(alarmInfo,null,true);
                       // runAlarmClock(alarmInfo);
                    } else {
                        PrefUtils.putBoolean(mActivity, alarmInfo.getId(), false);
                        toggleAlarm(holder, PrefUtils.getBoolean(mActivity, alarmInfo.getId(), true), alarmInfo);
                        mAlarmClock.turnAlarm(alarmInfo,null,false);
                    }
                }
            });
            Log.d("alarm",alarmInfo.toString()+"id:"+alarmInfo.getId());
            return convertView;
        }

        @Override
        public int getCount() {
            return mAlarmInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAlarmInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    private void toggleAlarm(ViewHolder ho,Boolean open,AlarmInfo alarmInfo) {
        ho.Turn.setChecked(open);
        String desc="";
       if(open){
           String day=AlarmInfoDao.getDataDayofWeek(alarmInfo.getDayOfWeek());
           //判断当前的重复天数
           if(day.equals("0")){
               desc="开启      一次性闹钟";
           }else if(day.equals("1,2,3,4,5")){
               desc="开启      工作日";
           }else if(day.equals("1,2,3,4,5,6,7")){
               desc="开启      每天";
           }else if(day.equals("6,7")){
               desc="开启      周末";
           }else{
               desc="开启      每周"+day+"重复";
           }
           ho.Desc.setText(desc);
           ho.Desc.setTextColor(Color.BLACK);
           ho.Time.setTextColor(Color.BLACK);
           PrefUtils.putBoolean(mActivity, alarmInfo.getId(), true);
           //runAlarmClock(alarmInfo);
       }else{
           desc="关闭";
           ho.Desc.setText(desc);
           ho.Desc.setTextColor(Color.GRAY);
           ho.Time.setTextColor(Color.GRAY);
       }
    }


    @Override
    public void onPause() {
        super.onPause();
        //当用户不再交互时关掉菜单
        if(isMenuOn){
            toggleMenu(onMenu);
        }
    }
}
