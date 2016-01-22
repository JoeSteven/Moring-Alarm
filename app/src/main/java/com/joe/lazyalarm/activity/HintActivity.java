package com.joe.lazyalarm.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.joe.lazyalarm.R;

public class HintActivity extends AppCompatActivity {

    private LinearLayout hint;
    private TranslateAnimation ta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);
       /* hint = (LinearLayout) findViewById(R.id.ll_hint);
        ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,-1f,
                Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
        ta.setDuration(500);
        ta.setFillAfter(true);*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                finish();
                break;
        }
        return true;
    }

}
