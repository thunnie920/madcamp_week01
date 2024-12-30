package com.example.week01_project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_profile);

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        int image = intent.getIntExtra("picture", 0);
        String name = intent.getStringExtra("name");
        String telNum = intent.getStringExtra("telNum");
        String email = intent.getStringExtra("email");
        String birthday = intent.getStringExtra("birthday");

        // ImageView
        ImageView profileImageView = findViewById(R.id.contact_image);
        // TextView에 데이터 표시
        TextView nameTextView = findViewById(R.id.info_name);
        TextView telNumTextView = findViewById(R.id.info_telNum);
        TextView emailTextView = findViewById(R.id.info_email);
        TextView bdayTextView = findViewById(R.id.info_bday);

        profileImageView.setImageResource(image);
        nameTextView.setText(name);
        telNumTextView.setText(telNum);
        emailTextView.setText(email);
        bdayTextView.setText(birthday);

    }
}
