package com.example.chen.newsdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHEN on 2015/6/13.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private Context mContext;

    private List<News> newsList;
    private LayoutInflater mLayoutInflater;
    private ListView listView;

    private ImageLoader imageLoader;

    private int start,end;
    private boolean flag;
    private ArrayList<String> urlList;

    public NewsAdapter(Context context,List<News> newsList,ListView listView){
        this.listView=listView;
        this.listView.setOnScrollListener(this);
        this.mContext=context;
        this.newsList=newsList;
        this.mLayoutInflater=LayoutInflater.from(context);
        this.imageLoader=new ImageLoader(listView);
        this.urlList=new ArrayList<String>();
        for(int i=0;i<newsList.size();i++){
            urlList.add(newsList.get(i).getNewsIconrl());
        }

        flag=true;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VIewHolder viewHolder;
        if(convertView==null){
            viewHolder=new VIewHolder();
            convertView=mLayoutInflater.inflate(R.layout.item_layout,null);
            viewHolder.newsIcon= (ImageView) convertView.findViewById(R.id.image);
            viewHolder.title= (TextView) convertView.findViewById(R.id.title);
            viewHolder.content= (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (VIewHolder) convertView.getTag();
        }
        News news = newsList.get(position);
        String iconURL=news.getNewsIconrl();
        viewHolder.newsIcon.setTag(iconURL);
        viewHolder.newsIcon.setImageResource(R.drawable.ic_launcher);
        //用滚动控制加载显示的部分，因此下面这部分去掉
//        if(imageLoader.getLrcBitmap(iconURL)!=null){
//            viewHolder.newsIcon.setImageBitmap(imageLoader.getLrcBitmap(iconURL));
//        }else{
//            imageLoader.setBitmap(viewHolder.newsIcon,iconURL);
//        }
        //只需从缓存中取，若有则设置上，没有则默认
        if(imageLoader.getLrcBitmap(iconURL)!=null){
            viewHolder.newsIcon.setImageBitmap(imageLoader.getLrcBitmap(iconURL));
        }
        viewHolder.title.setText(news.getNewsName());
        viewHolder.content.setText(news.getNewsDescription());

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==SCROLL_STATE_IDLE){
            this.imageLoader.loadImage(urlList.subList(start,end));//只把显示出来的item的图片url传过去
        }else{
            this.imageLoader.interuptLoadImage();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        start=firstVisibleItem;
        end=start+visibleItemCount;
        if(flag&&visibleItemCount>0){
            imageLoader.loadImage(urlList.subList(start,end));
            flag=false;
        }
    }

    private class VIewHolder{
        private ImageView newsIcon;
        private TextView title;
        private TextView content;
    }

    public void refresh(List<News> newsList){
        this.newsList=newsList;
        this.notifyDataSetChanged();
    }

}
