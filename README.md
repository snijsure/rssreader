
This is a simple app that reads RSS stream from URL http://feeds.feedburner.com/TechCrunch/social

There stream is read by RSSService using the Retrofit. The incoming XML is parsed and sent to the
Android activity for display.

The service runs every 10 min using AlarmManager.

Note: I got hints for how to parse XML stream with Retrofit using following stackoverflow posting
      http://stackoverflow.com/questions/35178057/parsing-rss-xml-feed-with-retrofit

* There are no official test stuff for this. Would be interesting to Mock HTTP response as well as 
  test the updates after the "10 min" etc.

* I need to read up on RSS protocol/headers to detect when we have new items. And only request updates after
  the last update that was received etc.

![Application Image](https://github.com/snijsure/rssreader/blob/master/app.png)
