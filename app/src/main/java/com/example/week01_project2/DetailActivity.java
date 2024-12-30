package com.example.week01_project2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    Button textButton;
    Button callButton;
    Button videoButton;

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

        textButton = findViewById(R.id.button_text);
        callButton = findViewById(R.id.button_call);
        videoButton = findViewById(R.id.button_video);

        textButton.setOnClickListener(v -> {
            if (telNum == null || telNum.isEmpty()) {
                Log.e("ProfileAdapter", "전화번호가 없습니다.");
                return;
            }

            Intent intent2 = new Intent(Intent.ACTION_SENDTO);
            intent2.setData(Uri.parse("smsto:" + telNum));
            intent2.putExtra("sms_body", "안녕하세요~");

            Context context = v.getContext();
            if (intent2.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent2);
            } else {
                Log.e("ProfileAdapter", "메시지 앱이 없습니다.");
            }
        });

        callButton.setOnClickListener(v -> {
            if (telNum == null || telNum.isEmpty()) {
                Log.e("ProfileAdapter", "전화번호가 없습니다.");
                return;
            }

            Intent intent2 = new Intent(Intent.ACTION_DIAL);
            intent2.setData(Uri.parse("tel:" + telNum));

            Context context = v.getContext();
            if (intent2.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent2);
            } else {
                Log.e("ProfileAdapter", "전화 앱이 없습니다.");
            }
        });

        videoButton.setOnClickListener(v -> {
            if (telNum == null || telNum.isEmpty()) {
                Log.e("ProfileAdapter", "전화번호가 없습니다.");
                return;
            }

            String meetUrl = "https://meet.google.com/landing";
            Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(meetUrl));

            try {
                v.getContext().startActivity(intent2);
            } catch (Exception e) {
                Log.e("ProfileAdapter", "Google Meet 앱이 없습니다.", e);
            }
        });
    }
}
