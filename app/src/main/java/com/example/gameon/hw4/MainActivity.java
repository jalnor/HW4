package com.example.gameon.hw4;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String[] names;
    String[] pictureUrls;
    Boolean flag = false;
    String newUrl;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView keyword = findViewById(R.id.textView2);
        final ImageView iv = findViewById(R.id.imageView);

        if ( isConn() ) {

            new GetStuffAsync().execute("http://dev.theappsdr.com/apis/photos/keywords.php");


        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder keys = new AlertDialog.Builder(MainActivity.this);
                keys.setTitle("Choose a Keyword")
                .setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        keyword.setText(names[which]);
                        buildUrl(names[which]);
                        try {
                            Thread.sleep(1000);
                            new GetPicturesAsync().execute(pictureUrls[0]);
                            Thread.sleep(1000);
                            iv.setImageBitmap(bm);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });
                AlertDialog alert = keys.create();
                alert.show();
            }
        });


        findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("message", "Clicked!" + newUrl);

            }
        });

        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("message", "Clicked!");

            }
        });


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

            BufferedReader br;
            HttpURLConnection huc = null;
            int count = 0;

            try {

                URL url = new URL(strings[0]);
                huc = (HttpURLConnection) url.openConnection();
                huc.connect();
                if ( huc.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                    br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                    String line = "";

                    while ( (line = br.readLine()) != null ) {

                        if (line.contains(";")) {
                            names = line.split(";");
                            //Log.d("message", "The results are " + names[0]);
                       } else {
                            if ( line != null ) {
                                pictureUrls = line.split("\\n");
                                Log.d("message", "The results are " + pictureUrls.length);
                            }
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if ( huc != null ) {
                    huc.disconnect();
                }                
            }

            //Log.d("message", "The results are ");
            return null;
        }
    }



    private class GetPicturesAsync extends AsyncTask<String, Void, Bitmap>  {


        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    bm = myBitmap;
                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
