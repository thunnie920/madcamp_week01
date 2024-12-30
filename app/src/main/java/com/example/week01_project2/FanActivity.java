package com.example.week01_project2;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class FanActivity extends AppCompatActivity {

    private ObjectAnimator rotateAnimator;
    private long currentDuration = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fan);

        ImageView fanfan = findViewById(R.id.fanfan);
        Button speedUpButton = findViewById(R.id.speedUpButton);

        rotateAnimator = ObjectAnimator.ofFloat(fanfan, "rotation", 0f, 360f);
        rotateAnimator.setDuration(currentDuration);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setInterpolator(null); // 일정한 속도로 회전
        rotateAnimator.start();

        speedUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDuration > 20) {
                    currentDuration = (int) (currentDuration *0.95);
                }

                float currentPlayTime = rotateAnimator.getCurrentPlayTime();
                rotateAnimator.cancel();
                rotateAnimator.setDuration(currentDuration);
                rotateAnimator.start();
                rotateAnimator.setCurrentPlayTime((long) currentPlayTime);
            }
        });
    }
}