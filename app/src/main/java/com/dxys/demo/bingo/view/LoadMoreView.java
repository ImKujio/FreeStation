package com.dxys.demo.bingo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by dxys on 17/10/5.
 */

public class LoadMoreView extends View {
    private Context context;
    private boolean isLoading = false;
    private Canvas canvas1;
    private Paint paint;
    private Bitmap bitmap;
    private int lineHeight;
    private int viewHeight = 0;
    private int[] colors = {0xff4a2cff, 0xffff2c63, 0xffffd52c};
    private MyHandler myhandler;

    public LoadMoreView(Context context) {
        super(context);
        initView(context);
    }


    public LoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadMoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public LoadMoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        myhandler = new MyHandler(context.getMainLooper());
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        lineHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null && paint != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    public void setColors(int[] colors)
    {
        this.colors = colors;
    }

    public void show() {
        if (!isLoading)
        {
            bitmap = Bitmap.createBitmap(getWidth(), lineHeight, Bitmap.Config.ARGB_8888);
            if (paint == null) {
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeWidth(lineHeight);
            }
            if (canvas1 == null)
                canvas1 = new Canvas();
            canvas1.setBitmap(bitmap);
            viewHeight = lineHeight;
            requestLayout();
            isLoading = true;
            freshView(paint, canvas1,getWidth());
        }
    }

    private void freshView(final Paint paint, final Canvas canvas, final int width) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int[][] progress = {{0,0},{0,1},{0,2}};
                int improve = dip2px(context,4);
                while (isLoading) {
                    try {
                        progress[0][0] = progress[0][0]+improve;
                        if (progress[0][0] >= (getWidth()>>2))
                        {
                            progress[1][0] = progress[1][0]+improve;
                            if (progress[1][0] >= (getWidth()>>2))
                                progress[2][0] = progress[2][0]+improve;
                        }

                        if (progress[1][0] >= (getWidth() >> 1))
                        {
                            int tamp = progress[0][1];
                            progress[0][0] = progress[1][0];
                            progress[0][1] = progress[1][1];
                            progress[1][0] = progress[2][0];
                            progress[1][1] = progress[2][1];
                            progress[2][0] = 0;
                            progress[2][1] = tamp;
                        }

                        draw(colors[progress[0][1]],width,lineHeight,paint,canvas,progress[0][0]);
                        draw(colors[progress[1][1]],width,lineHeight,paint,canvas,progress[1][0]);
                        draw(colors[progress[2][1]],width,lineHeight,paint,canvas,progress[2][0]);

                        Message message = new Message();
                        message.what = 0;
                        myhandler.sendMessage(message);
                        Thread.sleep(20);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void stop() {
        if (isLoading)
        {
            viewHeight = 0;
            requestLayout();
            isLoading = false;
            if (bitmap != null)
            {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    public boolean isLoading() {
        return isLoading;
    }




    public void draw(int color, int width, int height, Paint paint, Canvas canvas,int progess) {
        paint.setColor(color);
        canvas.drawLine((width >> 1) - progess, height >> 1, (width >> 1) + progess, height >> 1, paint);
    }

    private interface OnLineChangedListener {
        void onComplete();

        void onChanged(float progress);
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0: {
                    invalidate();
                    break;
                }
                case 20000002: {
                    requestLayout();
                }
            }
        }
    }

    protected int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
