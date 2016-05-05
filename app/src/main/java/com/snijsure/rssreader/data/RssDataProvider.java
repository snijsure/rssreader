package com.snijsure.rssreader.data;

import com.snijsure.rssreader.model.RssFeedItem;

import java.util.Arrays;
import java.util.List;
/**
 * Created by subodhnijsure on 5/4/16.
 */
// This is class that provides dummy data for UX testing
public class RssDataProvider {
    public List<RssFeedItem> getRssData() {
        return Arrays.asList(
                new RssFeedItem("Title1", "Description1", "PubDate1"),
                new RssFeedItem("Title2", "Description2", "PubDate2"),
                new RssFeedItem("Title3", "Description2", "PubDate3"),
                new RssFeedItem("Title4", "Description2", "PubDate4"),
                new RssFeedItem("Title5", "Description2", "PubDate5"),
                new RssFeedItem("Title6", "Description2", "PubDate6"),
                new RssFeedItem("Title7", "Description2", "PubDate7"),
                new RssFeedItem("Title8", "Description2", "PubDate8"),
                new RssFeedItem("Title9", "Description2", "PubDate9"),
                new RssFeedItem("Title10", "Description2", "PubDate10"),
                new RssFeedItem("Title11", "Description2", "PubDate11"),
                new RssFeedItem("Title12", "Description2", "PubDate12"),
                new RssFeedItem("Title13", "Description2", "PubDate13"),
                new RssFeedItem("Title14", "Description2", "PubDate14"),
                new RssFeedItem("Title15", "Description2", "PubDate15"),
                new RssFeedItem("Title16", "Description2", "PubDate16"),
                new RssFeedItem("Title17", "Description2", "PubDate17")
                );
    }
}
