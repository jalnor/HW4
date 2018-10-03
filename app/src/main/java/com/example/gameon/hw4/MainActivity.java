/*
Assignment: HW4
Page: MainActivity.java
Authors: Jarrod Norris, Andrew Schlesinger
 */

package com.example.gameon.hw4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String[] names;
    ArrayList<String> pictureUrls;
    Boolean flag = false;
    String newUrl;
    int location = 0;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView keyword = findViewById(R.id.textView2);
        final ImageView iv = findViewById(R.id.imageView);
        iv.setImageDrawable(null);
        final ImageView iv2 = findViewById(R.id.imageView2);
        final ImageView iv3 = findViewById(R.id.imageView3);
        iv2.setEnabled(false);
        iv3.setEnabled(false);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setTitle("Loading").setView(inflater.inflate(R.layout.dialog_bar, null));

       dialog = builder.create();


        pictureUrls = new ArrayList<>();

        if ( isConn() ) {
            Log.d("message", "WTF!");

            new GetStuffAsync().execute("http://dev.theappsdr.com/apis/photos/keywords.php");

        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", (Toast.LENGTH_LONG * 100)).show();
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                location = 0;
                pictureUrls.clear();
                AlertDialog.Builder keys = new AlertDialog.Builder(MainActivity.this);
                keys.setTitle("Choose a Keyword")
                .setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        keyword.setText(names[which]);
                        buildUrl(names[which]);
                        try {
                            Thread.sleep(1000);
                            Log.d("message", "This is the size of picturesUrl " + pictureUrls.size());
                            if ( pictureUrls.size() > 0 ) {
                                getPicture(pictureUrls.get(0));
                            } else {
                                iv.setImageDrawable(null);
                            }
                            Thread.sleep(1000);
                            if ( pictureUrls.size() > 1 ) {
                                iv2.setEnabled(true);
                                iv3.setEnabled(true);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });
                AlertDialog alert = keys.create();
                alert.show();
            }
        });


        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pictureUrls.size() > 0){
                    Log.d("message", "Location is " + location);
                    if (location > 0){
                        location--;

                    } else {
                        location = pictureUrls.size() - 1;
                    }
                    getPicture(pictureUrls.get(location));
                } else {
                    iv2.setEnabled(false);
                }

            }
        });

        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pictureUrls.size() > 1){
                    Log.d("message", "Location is " + location);
                    if (location < (pictureUrls.size() - 1) ){
                        location++;
                    }
                    else {
                       location = 0;
                    }
                    getPicture(pictureUrls.get(location));
                } else {
                    iv3.setEnabled(false);
                }
            }
        });


    }

    private void getPicture(String link){
        GetPicturesAsync picturesAsync = new GetPicturesAsync();
        picturesAsync.setListener(new GetPicturesAsync.MyAsyncTaskListener() {
            @Override
            public void onPreExecuteConcluded() {
                // start progess
                dialog.show();
            }

            @Override
            public void onPostExecuteConcluded(Bitmap result) {
                // stop progess
                dialog.hide();
                ((ImageView)findViewById(R.id.imageView)).setImageBitmap(result);
            }
        });
        picturesAsync.execute(link);
    }


    private boolean isConn() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected() || (ni.getType() != ConnectivityManager.TYPE_WIFI && ni.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private void buildUrl(String name) {
        StringBuilder u = new StringBuilder("http://dev.theappsdr.com/apis/photos/index.php?keyword=");
        u.append(name);
        flag = true;
        new GetStuffAsync().execute(u.toString());
    }

    private class GetStuffAsync extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            BufferedReader br = null;
            HttpURLConnection huc = null;
            Log.d("message", "This is string url in getstuffasync " + strings[0]);
            try {
                URL url = new URL(strings[0]);
                huc = (HttpURLConnection) url.openConnection();
                huc.connect();
                Log.d("message", "The response code is " + huc.getResponseCode());
                if ( huc.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                    br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                    String line = "";

                    while ( (line = br.readLine()) != null ) {

                        if (line.contains(";")) {
                            names = line.split(";");
                            Log.d("message", "The results are " + names[0]);
                       } else {
                            if ( line != null ) {
                                pictureUrls.add(line);
                            }
                        }
                    }
                    Log.d("message", "The results are " + names[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if ( huc != null ) {
                    huc.disconnect();
                }
                if ( br != null ) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
