package com.example.appprogramming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity3 extends AppCompatActivity implements View.OnClickListener {

    ImageView happySongBtn, calmSongBtn, energeticSongBtn, relaxedSongBtn, motivatedSongBtn, romanticSongBtn;

    String genre, latitude, longitude , mainWeather , mood;
    TextView genreview;

    ImageView weatherImage;

    @SuppressLint("WrongViewCast")
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

        happySongBtn = findViewById(R.id.happyBtn);
        calmSongBtn = findViewById(R.id.calmBtn);
        energeticSongBtn = findViewById(R.id.energeticBtn);
        relaxedSongBtn = findViewById(R.id.relaxedBtn);
        motivatedSongBtn = findViewById(R.id.motivatedBtn);
        romanticSongBtn = findViewById(R.id.romanticBtn);

        // 모든 버튼에 대해 OnClickListener 설정
        happySongBtn.setOnClickListener(this);
        calmSongBtn.setOnClickListener(this);
        energeticSongBtn.setOnClickListener(this);
        relaxedSongBtn.setOnClickListener(this);
        motivatedSongBtn.setOnClickListener(this);
        romanticSongBtn.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.weatherBtn) {

        } else if (id == R.id.happyBtn) {
            mood = "happy";
            gptApi(genreview,mood);
        } else if (id == R.id.calmBtn) {
            mood = "calm";
            gptApi(genreview,mood);
        } else if (id == R.id.energeticBtn) {
            mood = "energetic";
            gptApi(genreview,mood);
        } else if (id == R.id.relaxedBtn) {
            mood = "relaxed";
            gptApi(genreview,mood);
        } else if (id == R.id.motivatedBtn) {
            mood = "motivated";
            gptApi(genreview,mood);
        } else if (id == R.id.romanticBtn) {
            mood = "romantic";
            gptApi(genreview,mood);
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
                mainWeather = weatherObject.getString("main");
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

    //장르와 분위기로 노래 추천
    private void gptApi (TextView genreview, String mood ) {
        final String TAG = "MainActivity3";
        final String API_KEY = BuildConfig.OPENAI_API_KEY;
        final String API_URL = "https://api.openai.com/v1/chat/completions";
        OkHttpClient client = new OkHttpClient();

        String genre = genreview.getText().toString();

        try {
            JSONObject json = new JSONObject();
            json.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "system");
            message.put("content", "You are a program that always recommends two songs depending on the weather or a person's mood. You must provide a response in JSON format. You should only recommend songs from 2010 or later.  "+
                    "Json response example is {\"songs\": [" +
                    "{\"song\": \"When We Were Young\", \"artist\": \"Adele\", \"reason\": \"아델의 감성적인 목소리와 함께 지나간 시간들을 추억하며 들을 수 있는 곡입니다.\", \"}," +
                    "{\"song\": \"Someone Like You\", \"artist\": \"Adele\", \"reason\": \"이 곡은 이별의 슬픔을 담담하게 표현한 곡으로, 많은 사람들의 공감을 얻은 노래입니다.\"}" +
                    "]}");

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", "The current vibe is "+ mood +". Can you recommend some "+ genre + " songs?");

            messages.put(message);
            messages.put(userMessage);
            json.put("messages", messages);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY )
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Network request failed", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("Value", "33");
                    if (response.isSuccessful()) {
                        Log.d("Value", "33");
                        String responseData = response.body().string();
                        Log.d(TAG, "Response: " + responseData);
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray choices = jsonResponse.getJSONArray("choices");
                            JSONObject firstChoice = choices.getJSONObject(0);
                            String gptResponse = firstChoice.getJSONObject("message").getString("content");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("Value", String.valueOf(json));
                                    //genreview로 youtube로 값 넘기기
                                    genreview.setText(gptResponse);
                                }
                            });
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error", e);
                        }
                    } else {
                        Log.e(TAG, "Unsuccessful response: " + response.message());
                        Log.e(TAG, "Response body: " + response.body().string());
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "JSON creation error", e);
        }
    }
}