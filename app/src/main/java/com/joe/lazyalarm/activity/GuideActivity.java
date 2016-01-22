package com.joe.lazyalarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.joe.lazyalarm.R;
import com.joe.lazyalarm.utils.ConsUtils;
import com.joe.lazyalarm.utils.PrefUtils;

public class GuideActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private int[] color=new int[]{R.mipmap.guide_step1_2,R.mipmap.guide_step2_1,R.mipmap.guide_step3_3};
    private Button bt;
    private LinearLayout pointGray;
    private RelativeLayout rl;
    private View redPoint;
    private int mPointWidth;//小灰圆点的间距
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initAdapter();
        initListener();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager_guide);
        bt = (Button) findViewById(R.id.bt_guide);
        pointGray = (LinearLayout) findViewById(R.id.ll_point_guide);
        rl = (RelativeLayout) findViewById(R.id.rl_redpoint_guide);

    }

    private void initAdapter() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v=View.inflate(GuideActivity.this,R.layout.item_viewpager_guide,null);
                RelativeLayout background= (RelativeLayout) v.findViewById(R.id.item_viewpager);
                background.setBackgroundResource(color[position]);
                container.addView(v);
                return v;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        //动态画出小圆点
        for(int i=0;i<3;i++){
            View point=new View(this);
            //设置小圆点的宽高
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(20, 20);
            if(i>0){
                //从第二个小圆点开始设置margin
                params.leftMargin=20;
            }
            point.setBackgroundResource(R.drawable.point_gray_shape);
            point.setLayoutParams(params);
            //将point加入到线性布局中去
            pointGray.addView(point);
        }
        redPoint = new View(this);
        redPoint.setBackgroundResource(R.drawable.point_red_shape);
        redPoint.setLayoutParams(new RelativeLayout.LayoutParams(20, 20));
        rl.addView(redPoint);

        getPointMargin();
    }

    private void initListener() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//滑动时，通过滑动百分比和小圆点之间的距离来计算小红点的移动距离
                int currentPosition = (int) (mPointWidth * positionOffset + position * mPointWidth);
                //获取到小红圆点的params
                RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) redPoint.getLayoutParams();
                layoutParams.leftMargin = currentPosition;
                redPoint.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                //当前页面被选中时
                //如果为第三个页面，就显示button
                if (position == 2) {
                    bt.setVisibility(View.VISIBLE);
                } else {
                    bt.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void getPointMargin() {
        //拿到小圆点的线性布局View的ViewTreeObserver
        pointGray.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获得宽后要将该监听移除
                pointGray.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mPointWidth = pointGray.getChildAt(1).getLeft() - pointGray.getChildAt(0).getLeft();

            }
        });
    }
    public void startHome(View v){
        PrefUtils.putBoolean(this, ConsUtils.IS_FIRST_TIME,false);
        Intent intent=new Intent(this, HomeActivity.class);
        intent.putExtra("showGuide",true);
        startActivity(intent);
        finish();
    }
}
