package com.dxys.demo.bingo.download;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dxys on 17/10/6.
 */

public class DownloadImage {
    private static DownloadImage downloadImage;
    private static String basePath;
    private Context context;
    private boolean isExternalStorageExists = true;
    private File rootDir;

    private DownloadImage(Context context) {
        isExternalStorageExists = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(isExternalStorageExists)
        basePath = Environment.getExternalStorageDirectory().getPath();
        this.context = context;
        rootDir = new File(basePath+"/FreeStation");
        if (!rootDir.exists())
            rootDir.mkdirs();
        Log.e("DownloadImage","nasePath:"+basePath);
    }

    public static DownloadImage getInstence(Context context) {
        if (downloadImage == null)
            downloadImage = new DownloadImage(context);
        return downloadImage;
    }

    public void loadImage(String imageurl, final String groupname, final String imageName) {
        if (rootDir.exists())
        {
            File groupDir = new File(rootDir,groupname);
            if (!groupDir.exists()) groupDir.mkdirs();
            final File image = new File(groupDir,imageName+".jpg");
            if (image.exists()) image.delete();
            Log.e("downloadimage","url is:"+imageurl);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(imageurl, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }
                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    Toast.makeText(context, "获取图片失败！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(image);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100, out);
                        out.flush();
                        out.close();
                        Toast.makeText(context, "已保存第"+imageName+"张到sdcard/FreeStation目录下", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
        else
            Toast.makeText(context, "无法读取sd卡文件，请检查应用权限！", Toast.LENGTH_SHORT).show();
    }

}
