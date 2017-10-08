package com.dxys.demo.bingo.altaotu;

import android.support.annotation.Nullable;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static com.dxys.demo.bingo.Utlis.log;

public class MyOkHttp {

    private OkHttpClient okHttpClient;
    private static MyOkHttp myOkHttp;
    private MyOkHttp()
    {
        okHttpClient = new OkHttpClient();
        okHttpClient.getDispatcher().setMaxRequests(200);
        okHttpClient.setConnectTimeout(8, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(8,TimeUnit.SECONDS);
    }

    public static MyOkHttp getInstence()
    {
        if (myOkHttp == null)
        {
            myOkHttp = new MyOkHttp();
        }
        return myOkHttp;
    }

    public interface ResquestSucceedListener
    {
           void onResponse(Response response) throws IOException;
    }
    public interface ResquestFailureListener
    {
        void onFailure(Request request, IOException e);
    }

    public void getHtml(String url, @Nullable final ResquestSucceedListener resquestSucceedListener,
                        @Nullable final ResquestFailureListener resquestFailureListener,String tag)
    {
        Request request = new Request.Builder().url(url).tag(tag).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (resquestFailureListener != null)
                    resquestFailureListener.onFailure(request,e);
            }
            @Override
            public void onResponse(Response response) throws IOException {
                if (resquestSucceedListener != null)
                {
                    resquestSucceedListener.onResponse(response);
                }
            }
        });
    }
    public void setCorePoolSize(int size)
    {
        okHttpClient.getDispatcher().setMaxRequests(size);
    }

    public boolean isNullConnecting()
    {
        log("queuedCallcount is "+okHttpClient.getDispatcher().getQueuedCallCount());
        return okHttpClient.getDispatcher().getQueuedCallCount() == 0;
    }

    public void cancleAllRequest(Object tag)
    {
        okHttpClient.cancel(tag);
    }

}
