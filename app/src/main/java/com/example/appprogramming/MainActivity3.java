package com.example.appprogramming;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity3 extends AppCompatActivity {
    String genre, latitude, longitude;
    TextView genreview;

    ImageView weatherImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        genreview = findViewById(R.id.genre);
        weatherImage = findViewById(R.id.imageView);

        Intent intent = getIntent();

        if (intent != null) {
            genre = intent.getStringExtra("genre");
            latitude = intent.getStringExtra("latitude");
            longitude = intent.getStringExtra("longitude");
            if (genre.equals("english")) {
                genreview.setText("Genre : PoP");
            } else if (genre.equals("korea")) {
                genreview.setText("Genre : K-PoP");
            } else if (genre.equals("japan")) {
                genreview.setText("Genre : J-PoP");
            }
            Log.d("CheckValue3", "latitude : "+ latitude + "longitude : " +longitude);
        }

        String Url = "https://api.openweathermap.org/data/2.5/weather" +
                "?appid=f2862e5c64f5a4f8ad16954c71739b61" +
                "&units=metric" +
                "&lat=" + 37 +
                "&lon=" +127;
        Log.d("TAG", "Value :"+ Url);
        try {
            String weather = new WeaterSearchTask().execute(Url).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    private class WeaterSearchTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // 응답 코드 확인
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    result = sb.toString();
                }
                JSONObject jsonObject = new JSONObject(result);

                JSONArray weatherArray = jsonObject.getJSONArray("weather");

                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String mainWeather = weatherObject.getString("main");
                String weatherIcon = weatherObject.getString("icon");
                String imageUrl = "https://openweathermap.org/img/w/"+weatherIcon+".png";
                Bitmap bmp = null;
                URL imageurl = new URL(imageUrl);
                bmp = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());
                weatherImage.setImageBitmap(bmp);

                conn.disconnect();

            } catch (Exception e) {

            }
            return null;
        }
    }
}