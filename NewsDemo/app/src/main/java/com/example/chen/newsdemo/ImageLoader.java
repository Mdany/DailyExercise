package com.example.chen.newsdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by CHEN on 2015/6/13.
 *
 * 图片下载类，用线程和Handler
 *
 * 异步任务封装了线程池和Handler，也许用单纯的用线程和Handler好一些
 */
public class ImageLoader {

    private ImageView imageView;
    private String url;

    private LruCache<String,Bitmap> lruCache;

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                if(imageView.getTag().equals(url)){
                    imageView.setImageBitmap((Bitmap) msg.obj);
                }
            }
        }
    };

    public ImageLoader(){
        //根据实际运行最大内存的四分之一来缓存图片，《Android内存性能优化》
        lruCache=new LruCache<String,Bitmap>((int) (Runtime.getRuntime().maxMemory())/4){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void addLrcBitmap(String url,Bitmap bitmap){
        if(url!=null&&bitmap!=null){
            lruCache.put(url, bitmap);
        }
    }

    public Bitmap getLrcBitmap(String url){
        return lruCache.get(url);
    }

    public void setBitmap(ImageView imageView, final String url) {
        this.imageView=imageView;
        this.url=url;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mURL = new URL(url);
                    InputStream is = mURL.openConnection().getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    addLrcBitmap(url,bitmap);
                    Message msg=new Message();
                    msg.obj=bitmap;
                    msg.what=1;
                    handler.sendMessage(msg);
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
