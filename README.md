Hive SerDe for Flattened JSON
=============================

# Overview

This SerDe takes a file of JSON documents and flattens all nested objects to one level.
There are two steps to the process:

* Build the Hive DDL table schema.
* Load the data through the SerDe.

**Sample JSON feed files**

* persons.jsonfeed - Simple sample with nested object and array.
* pull\_request_comments.jsonfeed - Converted BSON file from Github's [GHTorrent](http://ghtorrent.org) project.
* tweets.jsonfeed - [Twitter tweets](https://dev.twitter.com/docs/platform-objects/tweets) - Note: API version 1.0.

**Scripts**

run.sh TABLE\_NAME HDFS\_DIRECTORY JSON\_FILES - Generates DDL 

* TABLE_NAME - name of table in create statement.
* HDFS_DIRECTORY - value of LOCATION in create statement.
* JSON_FILES - list of JSON files to process.  Each line is expected to be a legal JSON document - hence the extension 'jsonfeed'.

sample.sh - Generates Twitter tweets DDL 
```
  run.sh tweets /data/tweets data/tweets.jsonfeed
```

# Build and Run

In the shell:
```
  mvn package
  sample.sh | tee tweets.ddl
  hdfs dfs -mkdir /data/persons
  hdfs dfs -put data/persons.jsonfeed /data/persons

```

In Hive:

```
	add jar $MY_DIR/target/amm-serde-json-1.0-SNAPSHOT.jar ; 
       or add to your Hive lib directory

	create external table persons (...) - Paste the contents of tweets.ddl

	select * from persons ;

    name   yob   status  address_city  address_state  ids_0  ids_1
    john   2000  true    seattle       WA             10     11
    mary   2001  false   miami         FL             20     21
    juan   1990  true    Hermosillo    Sonora         10     11
    maria  1991  false   Bella Unión   Artigas        20     21

```

# Simple Sample

## JSON File

```
{ "name" : "john", "yob" : 2000, "status" : true,  "address" : { "city": "seattle", "state": "WA" }, "ids": [ 10, 11 ] }
{ "name" : "mary", "yob" : 2001, "status" : false, "address" : { "city": "miami" , "state": "FL"}, "ids": [ 20, 21 ]  }
{ "name" : "juan", "yob" : 1990, "status" : true,  "address" : { "city": "Hermosillo", "state": "Sonora" }, "ids": [ 10, 11 ] }
{ "name" : "maria", "yob" : 1991, "status" : false, "address" : { "city": "Bella Unión" , "state": "Artigas"}, "ids": [ 20, 21 ]  }
```

## JSON - Formatted sample line
```
{
  "name" : "john",
  "yob" : 2000,
  "status" : true,
  "address" : {
    "city" : "seattle",
    "state" : "WA"
  },
  "ids" : [ 10, 11 ]
}
```

## DDL
```
CREATE EXTERNAL TABLE person (
  name string,
  yob int,
  status boolean,
  address_city string,
  address_state string,
  ids_0 int,
  ids_1 int
)
ROW FORMAT SERDE 'com.amm.hive.serde.flattened.JsonSerDe'
LOCATION '/data/persons';
```

# GHTorrent Sample

## JSON - Formatted sample line
```
{
  "_id" : {
    "$oid" : "52a5c7c1bd35431ec500000a"
  },
  "url" : "https://api.github.com/repos/jokesterfr/node-pcsc/pulls/comments/8004021",
  "id" : 8004021,
  "diff_hunk" : "@@ -44,11 +44,11 @@ function readCard() {\n \t\t\tprocess.send(evt);\n \t\t}\n \t});\n-\n-\t// C++ side is blocking, recursion here does not harm\n-\treturn readCard();\n };\n-readCard();\n+// In general: recursion is a nice, but most architectures don't like them much.\n+// The Stack size gets incremented and we got an stack overflow when we removed the card reader.\n+// Furthermore the C++ side might not be blocking (pre 1.6.x PCSClite).\n+while(1) readCard();\n ",
  "path" : "lib/node-pcsc.js",
  "position" : 13,
  "original_position" : 13,
  "commit_id" : "426d87a897c1b373d57df7d2928b4a943aab5160",
  "original_commit_id" : "426d87a897c1b373d57df7d2928b4a943aab5160",
  "user" : {
    "login" : "jokesterfr",
    "id" : 1536672,
    "avatar_url" : "https://2.gravatar.com/avatar/c3c2d3774a31803e93500a36a20a48b7?d=https%3A%2F%2Fidenticons.github.com%2Fdb24d7098cdf8a681537b5da98e1cb22.png&r=x",
    "gravatar_id" : "c3c2d3774a31803e93500a36a20a48b7",
    "url" : "https://api.github.com/users/jokesterfr",
    "html_url" : "https://github.com/jokesterfr",
    "followers_url" : "https://api.github.com/users/jokesterfr/followers",
    "following_url" : "https://api.github.com/users/jokesterfr/following{/other_user}",
    "gists_url" : "https://api.github.com/users/jokesterfr/gists{/gist_id}",
    "starred_url" : "https://api.github.com/users/jokesterfr/starred{/owner}{/repo}",
    "subscriptions_url" : "https://api.github.com/users/jokesterfr/subscriptions",
    "organizations_url" : "https://api.github.com/users/jokesterfr/orgs",
    "repos_url" : "https://api.github.com/users/jokesterfr/repos",
    "events_url" : "https://api.github.com/users/jokesterfr/events{/privacy}",
    "received_events_url" : "https://api.github.com/users/jokesterfr/received_events",
    "type" : "User",
    "site_admin" : false
  },
  "body" : "Ah ah! I'm understanding some border line effects...",
  "created_at" : "2013-11-29T15:37:42Z",
  "updated_at" : "2013-11-29T15:37:42Z",
  "html_url" : "https://github.com/jokesterfr/node-pcsc/pull/13#discussion_r8004021",
  "pull_request_url" : "https://api.github.com/repos/jokesterfr/node-pcsc/pulls/13",
  "_links" : {
    "self" : {
      "href" : "https://api.github.com/repos/jokesterfr/node-pcsc/pulls/comments/8004021"
    },
    "html" : {
      "href" : "https://github.com/jokesterfr/node-pcsc/pull/13#discussion_r8004021"
    },
    "pull_request" : {
      "href" : "https://api.github.com/repos/jokesterfr/node-pcsc/pulls/13"
    }
  },
  "repo" : "node-pcsc",
  "owner" : "jokesterfr",
  "pullreq_id" : 13
}
```

## DDL
```
CREATE EXTERNAL TABLE foo (
  id___oid string,
  url string,
  id_ int,
  diff_hunk string,
  path string,
  position int,
  original_position int,
  commit_id string,
  original_commit_id string,
  user_login string,
  user_id int,
  user_avatar_url string,
  user_gravatar_id string,
  user_url string,
  user_html_url string,
  user_followers_url string,
  user_following_url string,
  user_gists_url string,
  user_starred_url string,
  user_subscriptions_url string,
  user_organizations_url string,
  user_repos_url string,
  user_events_url string,
  user_received_events_url string,
  user_type string,
  user_site_admin boolean,
  body string,
  created_at string,
  updated_at string,
  html_url string,
  pull_request_url string,
  links_self_href string,
  links_html_href string,
  links_pull_request_href string,
  repo string,
  owner string,
  pullreq_id int
)
ROW FORMAT SERDE 'com.amm.hive.serde.flattened.JsonSerDe'
LOCATION '/data/ghtorrent';
```

# Twitter Tweets Sample

## JSON - Formatted sample line
```
{
    "contributors": null, 
    "coordinates": null, 
    "created_at": "Sat Nov 05 00:24:23 +0000 2011", 
    "entities": {
        "hashtags": [], 
        "urls": [], 
        "user_mentions": []
    }, 
    "favorited": false, 
    "geo": null, 
    "id": 132614199451975681, 
    "id_str": "132614199451975681", 
    "in_reply_to_screen_name": null, 
    "in_reply_to_status_id": null, 
    "in_reply_to_status_id_str": null, 
    "in_reply_to_user_id": null, 
    "in_reply_to_user_id_str": null, 
    "place": null, 
    "retweet_count": 0, 
    "retweeted": false, 
    "source": "<a href=\"http://twitter.com/#!/download/iphone\" rel=\"nofollow\">Twitter for iPhone</a>", 
    "text": "Si creen que se chinguen Facebook?", 
    "truncated": false, 
    "user": {
        "contributors_enabled": false, 
        "created_at": "Fri Mar 26 15:24:18 +0000 2010", 
        "default_profile": false, 
        "default_profile_image": false, 
        "description": "ni yo se...", 
        "favourites_count": 61, 
        "follow_request_sent": null, 
        "followers_count": 324, 
        "following": null, 
        "friends_count": 257, 
        "geo_enabled": true, 
        "id": 126654779, 
        "id_str": "126654779", 
        "is_translator": false, 
        "lang": "es", 
        "listed_count": 18, 
        "location": "Aguascalientes,Mexico", 
        "name": "Que no se!", 
        "notifications": null, 
        "profile_background_color": "1A1B1F", 
        "profile_background_image_url": "http://a1.twimg.com/images/themes/theme9/bg.gif", 
        "profile_background_image_url_https": "https://si0.twimg.com/images/themes/theme9/bg.gif", 
        "profile_background_tile": false, 
        "profile_image_url": "http://a2.twimg.com/profile_images/1584208328/image_normal.jpg", 
        "profile_image_url_https": "https://si0.twimg.com/profile_images/1584208328/image_normal.jpg", 
        "profile_link_color": "2FC2EF", 
        "profile_sidebar_border_color": "181A1E", 
        "profile_sidebar_fill_color": "252429", 
        "profile_text_color": "666666", 
        "profile_use_background_image": true, 
        "protected": false, 
        "screen_name": "Notnz", 
        "show_all_inline_media": false, 
        "statuses_count": 22267, 
        "time_zone": "Central Time (US & Canada)", 
        "url": null, 
        "utc_offset": -21600, 
        "verified": false
    }
}
```

## DDL
```
CREATE EXTERNAL TABLE foo (
  text string,
  created_at string,
  favorited boolean,
  source string,
  retweet_count int,
  truncated boolean,
  retweeted boolean,
  user_statuses_count int,
  user_profile_background_image_url string,
  user_screen_name string,
  user_friends_count int,
  user_profile_link_color string,
  user_created_at string,
  user_profile_image_url_https string,
  user_profile_background_color string,
  user_description string,
  user_contributors_enabled boolean,
  user_lang string,
  user_profile_background_tile boolean,
  user_profile_sidebar_fill_color string,
  user_default_profile boolean,
  user_show_all_inline_media boolean,
  user_listed_count int,
  user_is_translator boolean,
  user_profile_sidebar_border_color string,
  user_protected boolean,
  user_profile_background_image_url_https string,
  user_time_zone string,
  user_location string,
  user_name string,
  user_profile_use_background_image boolean,
  user_favourites_count int,
  user_id int,
  user_id_str string,
  user_default_profile_image boolean,
  user_verified boolean,
  user_geo_enabled boolean,
  user_utc_offset int,
  user_profile_text_color string,
  user_followers_count int,
  user_profile_image_url string,
  id_ bigint,
  id_str string,
  entities_urls_0_indices_0 int,
  entities_urls_0_indices_1 int,
  entities_urls_0_display_url string,
  entities_urls_0_expanded_url string,
  entities_urls_0_url string,
  possibly_sensitive boolean,
  user_url string,
  in_reply_to_user_id int,
  entities_user_mentions_0_screen_name string,
  entities_user_mentions_0_indices_0 int,
  entities_user_mentions_0_indices_1 int,
  entities_user_mentions_0_name string,
  entities_user_mentions_0_id int,
  entities_user_mentions_0_id_str string,
  in_reply_to_screen_name string,
  in_reply_to_user_id_str string
)
ROW FORMAT SERDE 'com.amm.hive.serde.flattened.JsonSerDe'
LOCATION '/data/tweets';
```

# TODOs

* Find canonical reference for Hive reserved keywords.
* Externalize keywords into file that is loaded as resource.
* Allow configurable name mapping strategies, e.g. backtick, etc.

