package com.dxys.demo.bingo.setting;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dxys.demo.bingo.R;

public class SettingActivity extends AppCompatActivity {

    private SettingActivity settingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingActivity = this;
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView okio = (TextView) findViewById(R.id.okio);
        TextView okhttp = (TextView) findViewById(R.id.okhttp);
        TextView imageloader = (TextView) findViewById(R.id.imageloader);
        TextView photoview = (TextView) findViewById(R.id.photoview);

        MyOnClickLisener myOnClickLisener = new MyOnClickLisener();
        okio.setOnClickListener(myOnClickLisener);
        okhttp.setOnClickListener(myOnClickLisener);
        imageloader.setOnClickListener(myOnClickLisener);
        photoview.setOnClickListener(myOnClickLisener);
    }

    private class MyOnClickLisener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.okio: {
                    intent.setData(Uri.parse("https://github.com/square/okio"));
                    break;
                }
                case R.id.okhttp:
                {
                    intent.setData(Uri.parse("https://github.com/square/okhttp"));
                    break;
                }
                case R.id.imageloader:
                {
                    intent.setData(Uri.parse("https://github.com/nostra13/Android-Universal-Image-Loader"));
                    break;
                }
                case R.id.photoview:
                {
                    intent.setData(Uri.parse("https://github.com/bm-x/PhotoView"));
                    break;
                }
            }
            startActivity(intent);
        }
    }
}
