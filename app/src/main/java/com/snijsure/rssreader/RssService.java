package com.snijsure.rssreader;
/**
 * Created by subodhnijsure on 5/4/16.
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.snijsure.rssreader.model.RssFeed;
import com.snijsure.rssreader.model.RssFeedItem;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/*
 RssChannel, RssFeed, RssFeedItem constructs are derived from following posting
 on stack overflow
 http://stackoverflow.com/questions/35178057/parsing-rss-xml-feed-with-retrofit
*/

public class RssService extends Service {

    private static final String RSS_LINK = "http://feeds.feedburner.com/";
    public static final String ITEMS = "items";
    public static final String RECEIVER = "receiver";
    private final static String TAG = "RssService";
    private final static int UPDATE_INTERVAL_MIN = 10;
    List<RssFeedItem> cachedList = null;
    Intent mIntent;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.d(TAG, "Service started");
        mIntent = intent;
        updateRssItems();
        sendCachedList();
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * UPDATE_INTERVAL_MIN),
                PendingIntent.getService(getApplicationContext(), 0, new Intent(this, RssService.class), 0)
        );
        return Service.START_STICKY;
    }

    void updateRssItems( ) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        SimpleXmlConverterFactory conv = SimpleXmlConverterFactory.createNonStrict();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RSS_LINK)
                .client(client)
                .addConverterFactory(conv)
                .build();


        RssRetrofitAdapter retrofitService = retrofit.create(RssRetrofitAdapter.class);
        Call<RssFeed> call = retrofitService.getItems();
        call.enqueue(new Callback<RssFeed>() {
            @Override
            public void onResponse(Call<RssFeed> call, Response<RssFeed> response) {
                RssFeed feed = response.body();
                List<RssFeedItem> mItems = feed.getChannel().getItemList();
                // This is first time activity is connecting to service
                // so initialized cachedList
                if ( cachedList == null && mItems != null) {
                    cachedList = new ArrayList<RssFeedItem>(mItems);
                    Log.d(TAG, "Initialized  cached list");
                    sendCachedList();
                }
                else if ( mItems != null ) {
                    // Prepend mItems to cachedList if they don't exists
                    // I know this n^2 complexity there has to be better
                    // way to merge these two list for now this will suffice.
                    boolean itemsUpdated = false;
                    for ( int k = mItems.size() -1 ; k >= 0 ; k--) {
                        RssFeedItem item =mItems.get(k);
                        boolean itemExists = false;
                        for (RssFeedItem i: cachedList) {
                            if (i.isEqualTo(item)) {
                                itemExists = true;
                                break;
                            }
                        }
                        if (!itemExists) {
                            itemsUpdated = true;
                            Log.d(TAG, "Found a new item " + item.getTitle());
                            cachedList.add(0, item);
                        }
                    }
                    if (itemsUpdated) {
                        Log.d(TAG, "Finished updating cached list");
                        sendCachedList();
                    }
                    else {
                        Log.d(TAG,"No updates to cache no need to send an update");
                    }
                }
            }

            @Override
            public void onFailure(Call<RssFeed> call, Throwable t) {
                Log.d(TAG, "OnFailure Error is " + t);
            }
        });
    }

    void sendCachedList( ) {
        if ( cachedList != null ) {
            Log.d(TAG, "Sending cachedList");
            Bundle bundle = new Bundle();
            bundle.putSerializable(ITEMS, (Serializable) cachedList);
            ResultReceiver receiver = mIntent.getParcelableExtra(RECEIVER);
            if (receiver != null)
                receiver.send(0, bundle);
        }
        else {
            Log.d(TAG, "Cached list is empty!");
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        mIntent = intent;
        Log.d(TAG, "Bound to service");

        return null;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "Service is destroyed");
    }

}
