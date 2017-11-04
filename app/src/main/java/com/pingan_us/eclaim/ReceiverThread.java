package com.pingan_us.eclaim;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * Created by yshui on 11/3/17.
 */

public class ReceiverThread implements Runnable {
    private Uri srcImageUri;
    private int outWidth;
    private int outHeight;
    private Context context;
    private Bitmap bmp;

    public ReceiverThread(Uri srcImageUri, int outWidth, int outHeight, Context context) {
        this.srcImageUri = srcImageUri;
        this.outWidth = outWidth;
        this.outHeight = outHeight;
        this.context = context;
    }

    public void run() {
        bmp = Bitmap.createBitmap(Utility.compressImageUri(srcImageUri, outWidth, outHeight, context));
        synchronized (srcImageUri) {
            Log.d("threading!!!!!!!!!!!", "thread notified to update list");
            srcImageUri.notify();
        }

    }
    public Bitmap getBitmap() {
        return bmp;
    }
}