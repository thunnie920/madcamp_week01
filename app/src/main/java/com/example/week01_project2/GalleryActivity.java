package com.example.week01_project2;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ScaleGestureDetector scaleGestureDetector;
    private int spanCount = 3; // 초기 열 개수 (3)

    private long lastScaleTime = 0; // 마지막 스케일 변경 시간
    private static final long SCALE_DELAY_MS = 500; // 500ms 딜레이

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image);

        recyclerView = findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        // 이미지 파일 불러오기
        List<File> imageFiles = loadImages();

        if (imageFiles.isEmpty()) {
            Toast.makeText(this, "저장된 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            ImageAdapter adapter = new ImageAdapter(this, imageFiles);
            recyclerView.setAdapter(adapter);
        }

        // 핀치 줌을 처리하는 ScaleGestureDetector 설정
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                long currentTime = System.currentTimeMillis();

                // 딜레이 확인
                if (currentTime - lastScaleTime < SCALE_DELAY_MS) {
                    return false; // 딜레이 중에는 이벤트 무시
                }

                float scaleFactor = detector.getScaleFactor();
                if (scaleFactor > 1.0f) {
                    // 줌 인: 열 개수 줄임
                    if (spanCount > 1) {
                        spanCount--;
                        updateSpanCount();
                        lastScaleTime = currentTime; // 마지막 변경 시간 갱신
                    }
                } else if (scaleFactor < 1.0f) {
                    // 줌 아웃: 열 개수 늘림
                    if (spanCount < 5) { // 최대 열 개수
                        spanCount++;
                        updateSpanCount();
                        lastScaleTime = currentTime; // 마지막 변경 시간 갱신
                    }
                }
                return true;
            }
        });

        // RecyclerView 터치 리스너 설정
        recyclerView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void updateSpanCount() {
        gridLayoutManager.setSpanCount(spanCount);
        recyclerView.getAdapter().notifyDataSetChanged(); // 레이아웃 갱신
    }

    private List<File> loadImages() {
        List<File> imageFiles = new ArrayList<>();
        File galleryDir = new File(getExternalFilesDir(null), "GalleryImages");
        if (galleryDir.exists() && galleryDir.isDirectory()) {
            File[] files = galleryDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jpg")) {
                        imageFiles.add(file);
                    }
                }
            }
        }
        return imageFiles;
    }

    private int calculateSpanCount(int itemWidthDp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return Math.max(1, (int) (screenWidthDp / itemWidthDp));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}