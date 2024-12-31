package com.example.week01_project3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CameraActivity extends AppCompatActivity {

    public static String getPhotoMetadata(Context context, String fileName) {
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

            // 파일 이름에서 확장자 제거
            String baseFileName = fileName.replaceFirst("[.][^.]+$", ""); // 확장자 제거
            if (metadata.has(baseFileName)) {
                JSONObject photoInfo = metadata.getJSONObject(baseFileName);
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

        String imagePath = getIntent().getStringExtra("image_path");
        if (imagePath != null) {
            ImageView imageView = findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "이미지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(this, GalleryActivity.class);
                startActivity(intent);
                finish();
            });



            File imageFile = new File(imagePath);
            String metadata = getPhotoMetadata(this, imageFile.getName());

            Button detailsButton = findViewById(R.id.detailsButton);
            detailsButton.setOnClickListener(v ->
                    Toast.makeText(this, metadata, Toast.LENGTH_LONG).show()
            );
        } else {
            Toast.makeText(this, "이미지를 로드할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}