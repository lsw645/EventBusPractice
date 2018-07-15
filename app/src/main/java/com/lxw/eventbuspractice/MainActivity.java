package com.lxw.eventbuspractice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.lxw.eventbuspractice.event.EventBus;
import com.lxw.eventbuspractice.event.Subscribe;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }

    @Subscribe({Constant.LABEL1})
    public void label1(String msg) {
        Log.d(TAG, "label1() returned: " + msg);
    }

    @Subscribe({Constant.LABEL2})
    public void label2(String msg) {
        Log.d(TAG, "label2() returned: " + msg);
    }

    @Subscribe({Constant.LABEL2, Constant.LABEL1})
    public void labelAll(String msg) {
        Log.d(TAG, "labelAll() returned: " + msg);
    }
}
