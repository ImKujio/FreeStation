package com.dxys.demo.bingo.imagepager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.dxys.demo.bingo.R;
import com.dxys.demo.bingo.altaotu.ImageGroup;
import com.dxys.demo.bingo.altaotu.ImageGroupMannager;
import com.dxys.demo.bingo.download.DownloadImage;
import com.dxys.demo.bingo.permission.PermissionContrler;

import java.util.ArrayList;

public class ImageViewPager extends AppCompatActivity {
    private String[] imageUrls;
    private LinearLayout topBar;
    private boolean topBarIsShow = false;
    private BaseHandler baseHandler;
    private int handlerIndex;
    private Handler handler;
    private Runnable runnable;
    private ImageView download;
    private ImageGroup imageGroup;
    private ViewPager viewPager;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        imageGroup = ImageGroupMannager.getCurrentImageGroup();
        imageUrls = imageGroup.imagesUrls;
        setContentView(R.layout.activity_image_view_pager);
        baseHandler = new BaseHandler(Looper.getMainLooper());
        initView();
    }

    private void initView() {
        final TextView postition = (TextView) findViewById(R.id.position_text);

        viewPager = (ViewPager) findViewById(R.id.image_view_pager);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPageMargin(15);
        final ImageViewPagerAdapter imageViewPagerAdapter = new ImageViewPagerAdapter(getBaseContext(), getImages(), baseHandler);
        viewPager.setAdapter(imageViewPagerAdapter);
        postition.setText("第" + (viewPager.getCurrentItem() + 1) + "张/共" + imageViewPagerAdapter.getCount() + "张");
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                postition.setText("第" + (viewPager.getCurrentItem() + 1) + "张/共" + imageViewPagerAdapter.getCount() + "张");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        topBar = (LinearLayout) findViewById(R.id.top_bar);
        ImageButton backButton = (ImageButton) findViewById(R.id.top_bar_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) topBar.getLayoutParams();
        layoutParams.height = layoutParams.height + getStatusBarHeight(this);
        topBar.setLayoutParams(layoutParams);
        topBarIsShow = false;
        runnable = new Runnable() {
            @Override
            public void run() {
                topBar.setVisibility(View.INVISIBLE);
                topBarIsShow = false;
            }
        };
        handler = new Handler();
        showTopBar();


        download = (ImageButton) findViewById(R.id.top_bar_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionContrler.isHasMermission(activity,0))
                DownloadImage.getInstence(activity).loadImage(imageUrls[viewPager.getCurrentItem()], imageGroup.groupName, String.valueOf(viewPager.getCurrentItem() + 1));
                else PermissionContrler.requestPermission(activity,0);
            }
        });
    }

    private void showTopBar() {
        if (!topBarIsShow) {
            topBar.setVisibility(View.VISIBLE);
            topBarIsShow = true;
            handler = new Handler();
            handler.postDelayed(runnable, 2000);
        }
    }

    private void hideTopBar() {
        if (topBarIsShow) {
            topBar.setVisibility(View.GONE);
            topBarIsShow = false;
            handler.removeCallbacks(runnable);
        }
    }

    private ArrayList<WebImage> getImages() {
        ArrayList<WebImage> images = new ArrayList<>();
        for (String url : imageUrls) {
            WebImage image = new WebImage();
            image.imageUrl = url;
            images.add(image);
        }
        return images;
    }

    //    private double getStatusBarHeight(Context context){
//        double statusBarHeight = Math.ceil(25 * context.getResources().getDisplayMetrics().density);
//        return statusBarHeight;
//    }
    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public class BaseHandler extends Handler {
        public BaseHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    if (!topBarIsShow)
                        showTopBar();
                    else
                        hideTopBar();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionContrler.onRequestResualt(requestCode,grantResults))
            DownloadImage.getInstence(activity).loadImage(imageUrls[viewPager.getCurrentItem()],imageGroup.groupName,String.valueOf(viewPager.getCurrentItem()+1));
        else
            Toast.makeText(this, "权限获取失败！", Toast.LENGTH_SHORT).show();
    }
}
