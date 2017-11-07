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

//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if(e == null) {
//                    if(object != null) {
//                        try {
//                            if(object.get("list") != null)
//                                list = new ArrayList<String>((List<String>) object.get("list"));
//                            Log.d("DEBUG!!!!!!", "" + list.size());
//                        } catch (ClassCastException e1) {
//                            Log.d("ERROR!!!!!", e1.toString());
//                            e1.printStackTrace();
//                        }
//                        Log.d("DEBUG!!!!!", "wake the thread!!!");
//                        synchronized (id) {
//                            id.notify();
//                        }
//                    }
//                }
//                else
//                    Log.d("ERROR!!!!!", e.toString());
//
//            }
//        });
//        synchronized (id) {
//            try {
//                Log.d("DEBUG!!!!!", "Start to wait!!!");
//                id.wait();
//                Log.d("DEBUG!!!!!", "wake up!!!!!");
//
//            } catch (InterruptedException e) {
//            }
//        }
    }
    public List<String> getList() {
        Log.d("DEBUG!!!!!!", "" + list.size());
        return list;
    }
}