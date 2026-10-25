package com.github.chenpeeer.sample;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.chenpeeer.alphaclockview.AlphaClockView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final Random mRandom = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AlphaClockView clock = findViewById(R.id.clock);

        findViewById(R.id.btn_color).setOnClickListener(v -> {
            int color = Color.rgb(
                    60 + mRandom.nextInt(180),
                    60 + mRandom.nextInt(180),
                    60 + mRandom.nextInt(180));
            clock.setClockColor(color);
        });
    }
}