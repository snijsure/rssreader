package com.snijsure.rssreader;
/**
 * Created by subodhnijsure on 5/4/16.
 */

import com.snijsure.rssreader.model.RssFeed;

import retrofit2.Call;
import retrofit2.http.GET;

interface RssRetrofitAdapter {
    @GET("/TechCrunch/social")
    Call<RssFeed> getItems();
}
