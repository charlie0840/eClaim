package com.pingan_us.eclaim;

import android.os.AsyncTask;

/**
 * Created by yshui on 11/1/17.
 */

public class CompressTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        //return compressImage();//执行压缩操作
        return "";
    }

    @Override
    protected void onPreExecute() {
        //if (compressListener != null) {
        //    compressListener.onCompressStart();//监听回调（开始压缩）
        //}
    }

    @Override
    protected void onPostExecute(String imageOutPath) {
        //if (compressListener != null) {
        //    compressListener.onCompressEnd(imageOutPath);//监听回调（压缩结束）
        //}
    }
}

