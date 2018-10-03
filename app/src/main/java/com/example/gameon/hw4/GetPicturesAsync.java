/*
Assignment: HW4
Page: MainActivity.java
Authors: Jarrod Norris, Andrew Schlesinger
 */
package com.example.gameon.hw4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetPicturesAsync extends AsyncTask<String, Void, Bitmap> {

    public interface MyAsyncTaskListener {
        void onPreExecuteConcluded();
        void onPostExecuteConcluded(Bitmap result);
    }

    private MyAsyncTaskListener mListener;

    final public void setListener(MyAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    final protected Bitmap doInBackground(String... strings) {
        HttpURLConnection connection = null;
            InputStream input = null;
            Log.d("message", "This is string url in GetPicturesAsync  " + strings[0]);
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }return null;
    }

    @Override
    protected void onPreExecute() {
        if (mListener != null)
            mListener.onPreExecuteConcluded();
    }


    @Override
    protected void onPostExecute(Bitmap result) {
       mListener.onPostExecuteConcluded(result);
    }
}

