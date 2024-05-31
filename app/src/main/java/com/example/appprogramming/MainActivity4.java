package com.example.appprogramming;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity4 extends AppCompatActivity {
    private TextView songTextView1, songTextView2, artistTextView1, artistTextView2, reasonTextView1, reasonTextView2;
    String Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        songTextView1 = findViewById(R.id.songTextView);
        artistTextView1 = findViewById(R.id.artistTextView);
        reasonTextView1 = findViewById(R.id.reasonTextView);
        songTextView2 = findViewById(R.id.songTextView2);
        artistTextView2 = findViewById(R.id.artistTextView2);
        reasonTextView2 = findViewById(R.id.reasonTextView2);

        Intent intent = getIntent();
        if (intent != null) {
            Value = intent.getStringExtra("Value");
                int startIndex = Value.indexOf("{");
                int endIndex = Value.length();
            Value = Value.substring(startIndex, endIndex);
        }

        try {
            JSONObject jsonObject = new JSONObject(Value);
            JSONArray songsArray = jsonObject.getJSONArray("songs");

            List<String> videoIds = new ArrayList<>();

            for (int i = 0; i < songsArray.length(); i++) {
                JSONObject songObject = songsArray.getJSONObject(i);
                String song = songObject.getString("song");
                String artist = songObject.getString("artist");
                String reason = songObject.getString("reason");

                if (i == 0) {
                    songTextView1.setText("Song: " + song);
                    artistTextView1.setText("Artist: " + artist);
                    reasonTextView1.setText("Reason: " + reason);
                } else if (i == 1) {
                    songTextView2.setText("Song: " + song);
                    artistTextView2.setText("Artist: " + artist);
                    reasonTextView2.setText("Reason: " + reason);
                }

                String query = song + " " + artist;
                String youtubeSearchUrl = "https://www.youtube.com/results?search_query=" + query.replace(" ", "+") + " 듣기";

                String videoId = new YoutubeSearchTask().execute(youtubeSearchUrl).get();
                videoIds.add(videoId);
            }

            setupYouTubePlayer(videoIds);

        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setupYouTubePlayer(List<String> videoIds) {
        YouTubePlayerView youTubePlayerView1 = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView1);
        YouTubePlayerView youTubePlayerView2 = findViewById(R.id.youtube_player_view2);
        getLifecycle().addObserver(youTubePlayerView2);

        youTubePlayerView1.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoIds.get(0), 0);
            }
        });

        youTubePlayerView2.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoIds.get(1), 0);
            }
        });
    }

    private class YoutubeSearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String youtubeSearchUrl = params[0];
            String videoId = "SlC_YHwN9Lg"; // 값 불러오기 실패 비디오

            try {
                Document doc = Jsoup.connect(youtubeSearchUrl).get();
                Elements results = doc.select("script");

                for (Element script : results) {
                    if (script.html().contains("var ytInitialData")) {
                        String result = script.html();
                        int startIndex = result.indexOf("{");
                        int endIndex = result.length();
                        result = result.substring(startIndex, endIndex);

                        JSONObject jsonObject = new JSONObject(result);

                        JSONArray contents = jsonObject.getJSONObject("contents")
                                .getJSONObject("twoColumnSearchResultsRenderer")
                                .getJSONObject("primaryContents")
                                .getJSONObject("sectionListRenderer")
                                .getJSONArray("contents")
                                .getJSONObject(0)
                                .getJSONObject("itemSectionRenderer")
                                .getJSONArray("contents");
                        int videoCount = 0;
                        for (int i = 0; i < contents.length(); i++) {
                            JSONObject item = contents.getJSONObject(i);
                            if (item.has("videoRenderer")) {
                                videoCount++;
                                if (videoCount == 1) {
                                    JSONObject videoRenderer = item.getJSONObject("videoRenderer");
                                    videoId = videoRenderer.getString("videoId");
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return videoId;
        }
    }
}