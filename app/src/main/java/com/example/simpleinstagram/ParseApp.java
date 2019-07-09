package com.example.simpleinstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import model.Post;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("simpleInstagramId")
                .clientKey("Informatica00")
                .server("http://simple-parse-instagram.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);
    }
}
