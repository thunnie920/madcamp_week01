package com.example.day01_helloworld;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // button 객체 참조
        Button start01Btn = (Button) findViewById(R.id.start01Btn);
        start01Btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "시작 버튼이 눌렸어요.",
                        Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(getApplicationContext(), NewActivity.class);
                startActivity(myIntent);
            }
        });

        // button 객체 참조
        Button start02Btn = (Button) findViewById(R.id.start02Btn);
        start02Btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://m.naver.com"));
                startActivity(myIntent);
            }
        });

        // button 객체 참조
        Button start03Btn = (Button) findViewById(R.id.start03Btn);
        start03Btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("tel:010-8355-2602"));
                startActivity(myIntent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}