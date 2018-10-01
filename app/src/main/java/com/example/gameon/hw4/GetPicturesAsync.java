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

public class GetPicturesAsync  {


    private String url;
    private Bitmap bm;

    public void setUrl(String url) {
        this.url = url;
    }

    public GetPicturesAsync() {
        this.url = null;
    }
    public void callAsync() {
        new getAsync().execute(url);
    }

    public Bitmap getBitmap() {
        return bm;
    }


    private class getAsync extends AsyncTask<String, Integer, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... strings) {
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

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Log.d("message", "This is the length of the byte array in getpicturesasync  " + byteArray.length);
                Log.d("message", "This is the context in GetPicturesAsync ");


//                Intent i = new Intent(context, MainActivity.class);
//                i.putExtra("image", byteArray);
//                context.startActivity(i);

                bm = myBitmap;
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
            }

            return null;
        }
    }

}
