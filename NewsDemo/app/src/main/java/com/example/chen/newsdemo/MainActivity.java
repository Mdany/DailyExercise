package com.example.chen.newsdemo;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ListView mListView;
    private static String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView)findViewById(R.id.listView);
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

    private class NewsAsyncTask extends AsyncTask<String,Void,List<News>>{

        @Override
        protected List<News> doInBackground(String... params) {
            return getData(params[0]);
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);
            NewsAdapter mNewsAdapter = new NewsAdapter(MainActivity.this,news);
            mListView.setAdapter(mNewsAdapter);
        }
    }

}
