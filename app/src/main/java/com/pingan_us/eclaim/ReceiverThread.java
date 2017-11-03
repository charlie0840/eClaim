package com.pingan_us.eclaim;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * Created by yshui on 11/3/17.
 */

public class ReceiverThread extends Thread {
    private Activity activity;
    private ArrayAdapter<String> adapter;

    public ReceiverThread(Activity activity, ArrayAdapter<String> adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    public void run() {
        synchronized (adapter) {
            try {
                Log.d("threading!!!!!!!!!!!", "thread notified to update list");
                adapter.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    Log.d("threading!!!!!!!!!!!", "thread updated list " + adapter.getCount());
                }
            });
        }

    }
}