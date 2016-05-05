package com.snijsure.rssreader;
/**
 * Created by subodhnijsure on 5/4/16.
 */


import java.io.Serializable;
import java.util.List;


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
    private final static int UPDATE_INTERVAL_MIN = 20;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.d(TAG, "Service started");

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
                Bundle bundle = new Bundle();
                bundle.putSerializable(ITEMS, (Serializable) mItems);
                ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
                if (receiver != null)
                    receiver.send(0, bundle);
                stopSelf();

            }

            @Override
            public void onFailure(Call<RssFeed> call, Throwable t) {
                Log.d(TAG, "OnFailure Error is " + t);
                stopSelf();
            }
        });
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * UPDATE_INTERVAL_MIN),
                PendingIntent.getService(getApplicationContext(), 0, new Intent(this, RssService.class), 0)
        );
    }
}
