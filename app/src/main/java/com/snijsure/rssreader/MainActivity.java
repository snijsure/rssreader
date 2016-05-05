package com.snijsure.rssreader;
/**
 * Created by subodhnijsure on 5/4/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.snijsure.rssreader.data.RssDataProvider;
import com.snijsure.rssreader.model.RssFeedItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rss_feed_list;
    List<RssFeedItem> rssFeedItemList;
    private static String TAG = "MainActivity";
    RssItemAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rss_feed_list = (RecyclerView)findViewById(R.id.rss_feed_list);
        rss_feed_list.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rss_feed_list.setLayoutManager(linearLayoutManager);

        /*
        // Code to test UI without actually invoking
        RssDataProvider provider = new RssDataProvider();
        rssFeedItemList = provider.getRssData();
        RssItemAdapter rssItemAdapter = new RssItemAdapter(rssFeedItemList);
        rss_feed_list.setAdapter(rssItemAdapter);
        */
        List<RssFeedItem> items =  new ArrayList<RssFeedItem>();
        adapter = new RssItemAdapter(items);
        rss_feed_list.setAdapter(adapter);
        startService();

    }

    private void startService() {
        Intent intent = new Intent(getApplicationContext(), RssService.class);
        intent.putExtra(RssService.RECEIVER, resultReceiver);
        getApplicationContext().startService(intent);
    }

    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            List<RssFeedItem> items = (List<RssFeedItem>) resultData.getSerializable(RssService.ITEMS);
            if (items != null) {
                adapter = new RssItemAdapter(items);
                //adapter.clearData();
                //adapter.setData(items);
                rss_feed_list.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "Error while downloading the rss feed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    };
}
