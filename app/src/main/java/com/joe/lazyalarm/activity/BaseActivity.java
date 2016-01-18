package com.joe.lazyalarm.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joe.lazyalarm.domain.ActivityCollection;

/**
 * Created by Joe on 2016/1/12.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollection.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollection.remove(this);
    }

    protected void finishAll(){
        ActivityCollection.finishAll();
    }
}
