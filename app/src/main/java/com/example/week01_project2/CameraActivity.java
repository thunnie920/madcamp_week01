package com.example.week01_project2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class CameraActivity extends AppCompatActivity {

    public static String getPhotoMetadata(Context context, String resourceName) {
        try {
            InputStream is = context.getAssets().open("photo_metadata.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            // JSON 파싱
            JSONObject metadata = new JSONObject(jsonBuilder.toString());

            // 리소스 이름으로 데이터 검색
            if (metadata.has(resourceName)) {
                JSONObject photoInfo = metadata.getJSONObject(resourceName);
                String time = photoInfo.getString("time");
                String location = photoInfo.getString("location");
                return "Time: " + time + "\nLocation: " + location;
            } else {
                return "Metadata not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error reading metadata: " + e.getMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        FrameLayout frameLayout = findViewById(R.id.bottom_sheet);
        frameLayout.setOnClickListener(v -> finish());

        int resID = getIntent().getIntExtra("image_info", -1);
        if (resID != -1) {
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageResource(resID);

            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(this, GalleryActivity.class);
                startActivity(intent);
                finish();
            });

            String resourceName = getResources().getResourceEntryName(resID);
            String metadata = getPhotoMetadata(this, resourceName); // 수정된 부분
            Button detailsButton = findViewById(R.id.detailsButton);
            detailsButton.setOnClickListener(v -> {
                Toast.makeText(this, metadata, Toast.LENGTH_LONG).show(); // 메타데이터 표시
            });
        } else {
            Toast.makeText(this, "이미지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}