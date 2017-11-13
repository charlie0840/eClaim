package com.pingan_us.eclaim;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yshui on 11/3/17.
 */

public class ImageGetterThread implements Runnable {
    private String id;
    private List<String> list = new ArrayList<>();

    public ImageGetterThread(String id) {
        this.id = id;
    }

    public void run() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ImageList");
        query.whereEqualTo("objectId", id);
        try {
            ParseObject obj = query.getFirst();
            list = new ArrayList<>((List<String>)obj.get("list"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public List<String> getList() {
        return list;
    }
}