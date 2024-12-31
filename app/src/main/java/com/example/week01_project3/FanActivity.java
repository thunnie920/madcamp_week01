package com.example.week01_project3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class FanActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fan);

        layout = findViewById(R.id.fan_layout);
        layout.setBackgroundResource(R.drawable.hot_background); // 기본 시작 배경화면

        // 버튼을 클릭 이벤트에 연결
        /*
        findViewById(R.id.speedUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Blow! 버튼 클릭 시 FanActivity2로 이동
                Intent intent = new Intent(FanActivity.this, FanActivity2.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.talkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Elon! 버튼 클릭 시 FanActivity3로 이동
                Intent intent = new Intent(FanActivity.this, FanActivity3.class);
                startActivity(intent);
            }
        });*/

        // Blow! 버튼
        //findViewById(R.id.speedUpButton).setOnClickListener(new FanActivity2(this, layout));

        // Talk! 버튼
        //findViewById(R.id.talkButton).setOnClickListener(new FanActivity3(this, ));
    }
}
