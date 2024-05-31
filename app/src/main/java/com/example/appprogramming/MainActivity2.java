package com.example.appprogramming;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    Button pop, kpop, jpop;
    String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pop = findViewById(R.id.pop);
        kpop = findViewById(R.id.kpop);
        jpop = findViewById(R.id.jpop);

        Intent intent = getIntent();

        if (intent != null) {
            latitude = intent.getStringExtra("latitude");
            longitude = intent.getStringExtra("longitude");
        }

        pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genre = "english";
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                intent.putExtra("genre",genre);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                Log.d("CheckValue2", "latitude : "+ latitude + "longitude : " +longitude);
                startActivity(intent);
            }
        });

        kpop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genre = "korea";
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                intent.putExtra("genre",genre);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                Log.d("CheckValue2", "latitude : "+ latitude + "longitude : " +longitude);
                startActivity(intent);
            }
        });

        jpop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genre = "japan";
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                intent.putExtra("genre",genre);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                Log.d("CheckValue2", "latitude : "+ latitude + "longitude : " +longitude);
                startActivity(intent);
            }
        });
    }
}