package com.example.chen.newsdemo;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private SwipeRefreshLayout pullRefreshView;
    private ListView mListView;
    private NewsAdapter mNewsAdapter;

    private static String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                mNewsAdapter.refresh((List<News>) msg.obj);//只刷新item信息，如果图片变更则URL会变，根据url不同缓存查不出来自然回去加载
                pullRefreshView.setRefreshing(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView)findViewById(R.id.listView);
        pullRefreshView= (SwipeRefreshLayout) findViewById(R.id.pullRefresh);
        pullRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
        pullRefreshView.setColorScheme(android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
        new NewsAsyncTask().execute(URL);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取数据
     * @param url   地址
     * @return      返回json中所有News对象
     */
    private List<News> getData(String url){
        List<News> newsList = new ArrayList<News>();
        News news;
        try {
            String json=readJSON(new URL(url));
            JSONObject jsonObject = new JSONObject(json);
            JSONArray data = jsonObject.getJSONArray("data");
            for(int i=0;i<data.length();i++){
                news = new News();
                news.setNewsIconrl(data.getJSONObject(i).getString("picSmall"));
                news.setNewsDescription(data.getJSONObject(i).getString("description"));
                news.setNewsName(data.getJSONObject(i).getString("name"));
                newsList.add(news);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    /**
     * 读取json
     * @param url  url对象
     * @return     返回json
     */
    private String readJSON(URL url){
        StringBuffer str = new StringBuffer();
        InputStream is;
        try {
            is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String line ="";
            while((line=br.readLine())!=null){
                str.append(line);
            }
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    private void refreshList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg=handler.obtainMessage(1,getData(URL));
                //handler.sendMessageDelayed(msg,5000);
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 异步任务获取一组News初始化adapter
     */
    private class NewsAsyncTask extends AsyncTask<String,Void,List<News>>{

        @Override
        protected List<News> doInBackground(String... params) {
            return getData(params[0]);
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);
            mNewsAdapter = new NewsAdapter(MainActivity.this,news,mListView);
            mListView.setAdapter(mNewsAdapter);
        }
    }

}
