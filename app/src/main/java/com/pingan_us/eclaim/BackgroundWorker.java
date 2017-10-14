package com.pingan_us.eclaim;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by WMS_12 on 7/7/2016.
 */
public class BackgroundWorker extends AsyncTask<String, Void, String>{
    Context context;

    String add_info_url;
    AlertDialog alertDialog;
    String type, retval;

    BackgroundWorker(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {

        type = params[0];
        String login_url = "";
        String register_url = "";
        String unregister_url = "";
        String createsession_url = "";
        String joinsession_url = "";
        String deletesession_url = "";
        String imageupdate_url = "";
        String updateIpAndPort_url = "",
                updatesession_url = "",
                updateImage_url = "",
                sessionIsStart_url = "";


        if(type.equals("login")) {
            try {
                String user_name = params[1];
                String user_password = params[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = "";//URLEncoder.encode("user_name", "UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+
                        //"&"+URLEncoder.encode("user_password","UTF-8")+"="+URLEncoder.encode(user_password,"UTF-8");
                bufferWriter.write(post_data);
                bufferWriter.flush();
                bufferWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result = "";
                String line = "";
                while((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(type.equals("register")) {
            try {
                String first_name = params[1];
                String last_name = params[2];
                String email = params[3];
                String phone = params[4];
                String password = params[5];
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = "";
                        //URLEncoder.encode("user_name", "UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+
                        //"&"+URLEncoder.encode("user_password","UTF-8")+"="+URLEncoder.encode(user_password,"UTF-8");
                bufferWriter.write(post_data);
                bufferWriter.flush();
                bufferWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result = "";
                String line = "";
                while((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        else if(type.equals("unregister")) {
            try {
                String group_name = params[1];
                String permission_code = params[2];
                URL url = new URL(unregister_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name", "UTF-8")+"="+URLEncoder.encode(group_name,"UTF-8")+
                        "&"+URLEncoder.encode("user_password","UTF-8")+"="+URLEncoder.encode(permission_code,"UTF-8");
                bufferWriter.write(post_data);
                bufferWriter.flush();
                bufferWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result = "";
                String line = "";
                while((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if(type.equals("updateimage")) {
            try {
                String class_id = params[1];
                String session_name = params[2];
                String bitmap = params[3];
                String user_id = params[4];
                URL url = new URL(imageupdate_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("class_id", "UTF-8") + "=" + URLEncoder.encode(class_id, "UTF-8") +
                        "&" + URLEncoder.encode("session_name", "UTF-8") + "=" + URLEncoder.encode(session_name, "UTF-8") +
                        "&" + URLEncoder.encode("bitmap", "UTF-8") + "=" + URLEncoder.encode(bitmap, "UTF-8") +
                        "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
                bufferWriter.write(post_data);
                bufferWriter.flush();
                bufferWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if(type.equals("downloadimage")) {
            try {
                String class_id = params[1];
                String session_name = params[2];
                String bitmap = params[3];
                URL url = new URL(imageupdate_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("class_id", "UTF-8") + "=" + URLEncoder.encode(class_id, "UTF-8") +
                        "&" + URLEncoder.encode("session_name", "UTF-8") + "=" + URLEncoder.encode(session_name, "UTF-8") +
                        "&" + URLEncoder.encode("bitmap", "UTF-8") + "=" + URLEncoder.encode(bitmap, "UTF-8");
                bufferWriter.write(post_data);
                bufferWriter.flush();
                bufferWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(type.equals("uploadimage")) {
            try {
                String image = params[1];
                URL url = new URL(updateImage_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = "";
                //URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8") +
                        //"&" + URLEncoder.encode("session_id", "UTF-8") + "=" + URLEncoder.encode(session_id, "UTF-8");
                bufferWriter.write(post_data);
                bufferWriter.flush();
                bufferWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result) {
        //Toast.makeText(context, type + " : " + result + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@", Toast.LENGTH_LONG).show();
        //System.out.println(type + " : " + result + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
