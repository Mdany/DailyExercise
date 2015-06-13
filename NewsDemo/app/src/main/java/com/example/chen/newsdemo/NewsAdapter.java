package com.example.chen.newsdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CHEN on 2015/6/13.
 */
public class NewsAdapter extends BaseAdapter {

    private Context mContext;

    private List<News> newsList;
    private LayoutInflater mLayoutInflater;

    public NewsAdapter(Context context,List<News> newsList){
        this.mContext=context;
        this.newsList=newsList;
        mLayoutInflater=LayoutInflater.from(context);
    }

    public NewsAdapter(){}

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
        //viewHolder.newsIcon.setImageBitmap();

        viewHolder.title.setText(news.getNewsName());
        viewHolder.content.setText(news.getNewsDescription());

        return convertView;
    }

    private class VIewHolder{
        private ImageView newsIcon;
        private TextView title;
        private TextView content;
    }


}
