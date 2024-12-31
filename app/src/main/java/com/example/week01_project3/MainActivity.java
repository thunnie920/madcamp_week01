package com.example.week01_project3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class MainActivity extends AppCompatActivity {

    private void saveImagesToGallery() {
        try {
            // 외부 저장소 경로 가져오기
            File galleryDir = new File(getExternalFilesDir(null), "GalleryImages");
            if (!galleryDir.exists()) {
                galleryDir.mkdirs(); // 디렉토리 생성
            }

            // 반복문으로 p1~p20 처리
            for (int i = 1; i <= 20; i++) {
                // Drawable 리소스 이름 생성
                String drawableName = "p" + i;
                int resID = getResources().getIdentifier(drawableName, "drawable", getPackageName());

                // 리소스 ID 확인 및 처리
                if (resID != 0) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resID);

                    // 파일 생성
                    File imageFile = new File(galleryDir, drawableName + ".jpg");
                    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // Bitmap 저장
                    }

                    // 갤러리에 파일 스캔 요청
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(imageFile));
                    sendBroadcast(mediaScanIntent);
                }
            }

            Toast.makeText(this, "사진이 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveImagesToGallery();
    }
}