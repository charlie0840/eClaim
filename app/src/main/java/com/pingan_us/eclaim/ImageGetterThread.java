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
    private List<byte[]> list = new ArrayList<byte[]>();

    public ImageGetterThread(String id) {
        this.id = id;
    }

    public void run() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ImageList");
        query.whereEqualTo("objectId", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null) {
                    if(object != null) {
                        try {
                            if(object.get("list") != null)
                                list = new ArrayList<byte[]>((List<byte[]>) object.get("list"));
                        } catch (ClassCastException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    public List<byte[]> getList() {
        return list;
    }
}