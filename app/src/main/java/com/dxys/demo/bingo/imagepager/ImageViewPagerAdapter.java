package com.dxys.demo.bingo.imagepager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.dxys.demo.bingo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.ArrayList;

import static com.dxys.demo.bingo.Utlis.log;

/**
 * Created by dxys on 17/10/3.
 */

public class ImageViewPagerAdapter extends PagerAdapter {
    private ArrayList<WebImage> images;
    private Context context;
    private MyHandler myHandler;
    private DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    private ImageViewPager.BaseHandler baseHandler;

    public ImageViewPagerAdapter(Context context, ArrayList<WebImage> images, ImageViewPager.BaseHandler baseHandler) {
        this.context = context;
        this.images = images;
        myHandler = new MyHandler(context.getMainLooper());
        this.baseHandler = baseHandler;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageHodler imageHodler = new ImageHodler(context);
        onBindImage(imageHodler,position);
        container.addView(imageHodler.getRootView());
        return imageHodler.getRootView();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void onBindImage(final ImageHodler imageHodler, final int position) {
//    这里绑定数据处理异步
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ((images.get(position).imageUrl != null))
                {
                    imageHodler.setImage(images.get(position));
                    Message message = new Message();
                    message.what = 200;
                    message.obj = imageHodler;
                    myHandler.sendMessage(message);
                }else
                {
                }

            }
        }).start();
    }

    public class ImageHodler {
        //        这里初始化view
        private Context context;
        private PhotoView photoView;
        private WebImage image;
        private View rootView;
        private TextView textView;

        public ImageHodler(Context context) {
            this.context = context;
            rootView = LayoutInflater.from(context).inflate(R.layout.image_view_pager,null,false);
            photoView = (PhotoView) rootView.findViewById(R.id.image_view_pager_image);
            textView = (TextView) rootView.findViewById(R.id.image_view_pager_text);
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoView.setMaxScale(4f);
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message message = new Message();
                    message.what = 0;
                    baseHandler.sendMessage(message);
                }
            });
        }

        public View getRootView()
        {
            return rootView;
        }
        public void setImage(WebImage image) {
            this.image = image;
        }

        public WebImage getImage() {
            return image;
        }

        public PhotoView getPhotoView() {
            return photoView;
        }

        public void showTextProgress(float progress)
        {
            textView.setVisibility(View.VISIBLE);
            textView.setText((int)(progress*100)+"%");
        }
        public void disableText()
        {
            textView.setVisibility(View.GONE);
        }

    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 200:
                {
                    if (msg.obj != null)
                    {
                        final ImageHodler imageHodler = (ImageHodler) msg.obj;
                        final PhotoView photoView = imageHodler.getPhotoView();

                        ImageLoader.getInstance().displayImage(imageHodler.getImage().imageUrl, photoView, displayImageOptions,
                                new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String s, View view) {
                                        photoView.setImageResource(R.drawable.preview);
                                    }

                                    @Override
                                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                                        photoView.setImageResource(R.drawable.load_error);
                                    }

                                    @Override
                                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                        photoView.enable();
                                        photoView.enableRotate();
                                        photoView.setImageBitmap(bitmap);
                                        imageHodler.disableText();
                                    }

                                    @Override
                                    public void onLoadingCancelled(String s, View view) {

                                    }
                                }, new ImageLoadingProgressListener() {
                                    @Override
                                    public void onProgressUpdate(String s, View view, int i, int i1) {
                                        imageHodler.showTextProgress((float)i/i1);
                                    }
                                });
                        log("imagepager","has load: "+imageHodler.getImage().imageID);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
