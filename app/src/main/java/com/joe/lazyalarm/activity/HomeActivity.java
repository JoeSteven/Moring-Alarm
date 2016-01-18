package com.joe.lazyalarm.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.joe.lazyalarm.R;
import com.joe.lazyalarm.domain.AlarmInfo;
import com.joe.lazyalarm.fragment.FragAlarm;
import com.joe.lazyalarm.fragment.FragSlideMenu;
import com.joe.lazyalarm.fragment.FragWether;
import com.joe.lazyalarm.service.WakeServiceOne;
import com.joe.lazyalarm.utils.ConsUtils;
import com.joe.lazyalarm.utils.PrefUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HomeActivity extends SlidingFragmentActivity {

    private FragAlarm mFragAlarm;
    private FragWether mFragWether;
    private FragSlideMenu mFragSlideMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //判斷是否是新用戶
        if(PrefUtils.getBoolean(this,ConsUtils.IS_FIRST_TIME,true)){
            startActivity(new Intent(this,GuideActivity.class));
            finish();
            return;
        }
        //初始化数据库
        initDataBase("china_Province_city_zone.db");
        initSlideMenu();
        initView();
        initFragment();
        initService();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //拷贝数据库数据
    private void initDataBase(String dbName) {
        File file=new File(getFilesDir(),dbName);
        if(file.exists()){
            return;
        }
        InputStream in=null;
        FileOutputStream out=null;
        try {
            //输入流通过Assets.open获取
            in=getResources().getAssets().open(dbName);

            out=new FileOutputStream(file);
            byte[] buffer=new byte[1024];
            int len=0;
            while((len=in.read(buffer))!=-1){
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //初始化侧滑菜单
    private void initSlideMenu() {
        setBehindContentView(R.layout.menu_home);
        //获取到slidingMenu对象
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.RIGHT);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);

        //设置为全屏拉出菜单
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置菜单拉出后剩余屏幕宽度
        slidingMenu.setBehindOffset(200);
        slidingMenu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                Log.d("changecity", "菜单关闭");
                mFragWether.refreshData();
                mFragSlideMenu.stopAlarmMusic();
            }
        });
    }
    private void initService() {
        startService(new Intent(this, WakeServiceOne.class));
    }

    private void initView() {
        mFragAlarm = new FragAlarm();
        mFragWether = new FragWether();
        mFragSlideMenu = new FragSlideMenu();
    }

    private void initFragment() {
        FragmentManager fm=getFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        //加入标记
        ft.replace(R.id.fl_wether,mFragWether,ConsUtils.FRAG_WETHER);
        ft.replace(R.id.fl_menu_content,mFragSlideMenu);
        ft.replace(R.id.fl_alam,mFragAlarm);
        ft.commit();
    }
    //添加闹钟
    public void addAlarm(View v){
        startActivityForResult(new Intent(this, AddAlarmActivity.class), ConsUtils.ADD_ALARM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ConsUtils.ADD_ALARM:
                switch (resultCode){
                    case ConsUtils.SET_ALARM_DONE:
                        Bundle bundle=data.getExtras();
                        AlarmInfo alarmInfo=(AlarmInfo)bundle.getSerializable("alarm");
                        mFragAlarm.addAlarmInfo(alarmInfo);
                        break;
                    case ConsUtils.SET_ALARM_CANCEL:
                        break;
                }

            default:

                break;
        }
    }
    //打开侧滑菜单
    public void openMenu(View view){
        //拿到侧滑菜单对象
        SlidingMenu slidingMenu=getSlidingMenu();
        slidingMenu.toggle();
    }
}
