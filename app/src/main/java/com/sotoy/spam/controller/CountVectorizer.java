package com.sotoy.spam.controller;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by goman on 9/21/16.
 */

public class CountVectorizer {


    private static int FEATURES_COUNT = 1321;
    private String [] keys;
    private Map<String, Integer> mapper;

    private static CountVectorizer sInstance;

    private CountVectorizer(Context context) {
        keys = new String[FEATURES_COUNT];
        mapper = new HashMap<>();
        initFeatures(context);
    }

    public static CountVectorizer getInstance(Context context) {
        if (sInstance == null) sInstance = new CountVectorizer(context);
        return sInstance;
    }

    public int [] transform(String text) {
        text = text.toLowerCase();
        text = text.replaceAll("[^A-Za-z0-9]", " ");
        String [] terms = text.split(" ");
        int [] features = new int[FEATURES_COUNT];
        for(String term : terms) {
            Integer result = mapper.get(term);
            if (result != null) {
                features[result] += 1;
            }

        }
        return features;

    }

    private void initFeatures(Context context) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("feature.txt"), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            int row = 0;
            while ((mLine = reader.readLine()) != null) {
                keys[row] = mLine;
                mapper.put(mLine, row);
                row+=1;
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

}
