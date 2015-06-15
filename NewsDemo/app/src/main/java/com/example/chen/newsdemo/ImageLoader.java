package com.example.chen.newsdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by CHEN on 2015/6/13.
 *
 * 图片下载类，用线程和Handler
 *
 * 异步任务封装了线程池和Handler，也许用单纯的用线程和Handler好一些
 */
public class ImageLoader {

    private ListView listView;
    private ImageView imageView;
    private String url;

    private LruCache<String,Bitmap> lruCache;
    private LoadPartialImage loadPartialImage;
    private ArrayList<ImageView> imageViews;

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

    public ImageLoader(ListView listView){
        this.imageViews=new ArrayList<ImageView>();
        this.listView=listView;
        //根据实际运行最大内存的四分之一来缓存图片，《Android内存性能优化》
        lruCache=new LruCache<String,Bitmap>((int) (Runtime.getRuntime().maxMemory())/4){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    /**
     * 添加图片到缓存
     * @param url
     * @param bitmap
     */
    public void addLrcBitmap(String url,Bitmap bitmap){
        if(url!=null&&bitmap!=null){
            lruCache.put(url, bitmap);
        }
    }

    /**
     * 从缓存取图片
     * @param url
     * @return
     */
    public Bitmap getLrcBitmap(String url){
        return lruCache.get(url);
    }

    /**
     * 为item的imageView设置图片
     * @param imageView
     * @param url
     */
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
                    Message msg=new Message();//msg=mHandler.obtainMessage();
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

    public Bitmap getBitmap(String url){
        URL mURL = null;
        Bitmap bitmap = null;
        try {
            mURL = new URL(url);
            InputStream is = mURL.openConnection().getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            addLrcBitmap(url,bitmap);
            Message msg=new Message();//msg=mHandler.obtainMessage();
            msg.obj=bitmap;
            msg.what=1;
            handler.sendMessage(msg);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //加载部分显示的图片
    public void loadImage(List<String> urls){
        this.loadPartialImage=new LoadPartialImage();
        this.loadPartialImage.execute(urls);
    }

    //停止加载部分显示的图片
    public void interuptLoadImage(){
        this.loadPartialImage.cancel(true);
    }

    private class LoadPartialImage extends AsyncTask<List<String>,Void,List<Bitmap>>{

        @Override
        protected List<Bitmap> doInBackground(List<String>... params) {
            ArrayList<String> urls= (ArrayList<String>) params[0];
            ArrayList<Bitmap> bitmaps=new ArrayList<Bitmap>();
            Bitmap bitmap;
            ImageView imageViewTemp;
            for(int i=0;i<urls.size();i++){
                bitmap=getLrcBitmap(urls.get(i));
                imageViewTemp= (ImageView) listView.findViewWithTag(urls.get(i));
                imageViews.add(imageViewTemp);
                if(bitmap!=null){
                    bitmaps.add(bitmap);
                }else{
                    bitmap=getBitmap(urls.get(i));//download bitmap
                    bitmaps.add(bitmap);
                }
            }
            return bitmaps;
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            for(int j=0;j<bitmaps.size();j++){
                imageViews.get(j).setImageBitmap(bitmaps.get(j));
            }
        }
    }
}
