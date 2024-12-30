package com.example.week01_project2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
public class DetailActivity extends AppCompatActivity {

    ImageButton textButton;
    ImageButton callButton;
    ImageButton videoButton;
    ImageView nupjuk;

    private float currentRotationSpeed = 2000; // 초기 회전 속도

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
        nupjuk = findViewById(R.id.nupjuk);

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

        // 좌우 왕복 애니메이션 시작
        setupHorizontalAnimation(nupjuk, 0f, 500f, 500);

        // 넙죽이를 클릭하면 회전 애니메이션 시작
        nupjuk.setOnClickListener(v -> {
            if (currentRotationSpeed <= 500) { // 최대 속도에 도달했을 때
                nupjuk.setImageResource(R.drawable.angry_nupjuk); // drawable angry_nupjuk 설정
            } else {
                setupRotationAnimation(nupjuk, (long) currentRotationSpeed);
                currentRotationSpeed = Math.max(500, currentRotationSpeed - 200); // 클릭할수록 속도 증가
            }
        });
    }

    private void setupHorizontalAnimation(View view, float startX, float endX, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                view,
                "translationX",
                startX,
                endX
        );
        animator.setDuration(duration); // 애니메이션 지속 시간
        animator.setRepeatCount(ObjectAnimator.INFINITE); // 무한 반복
        animator.setRepeatMode(ObjectAnimator.REVERSE); // 왕복 애니메이션
        animator.setInterpolator(new LinearInterpolator()); // 일정한 속도
        animator.start(); // 애니메이션 시작
    }

    private void setupRotationAnimation(View view, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                view,
                "rotation",
                0f,
                360f
        );
        animator.setDuration(duration); // 회전 속도
        animator.setInterpolator(new LinearInterpolator()); // 일정한 속도
        animator.setRepeatCount(ObjectAnimator.INFINITE); // 무한 반복
        animator.start(); // 애니메이션 시작
    }
}