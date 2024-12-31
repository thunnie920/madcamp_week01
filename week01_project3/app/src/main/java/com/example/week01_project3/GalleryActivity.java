package com.example.week01_project3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week01_project3.ImageAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private ScaleGestureDetector scaleGestureDetector;
    private int spanCount = 3;
    private static final long SCALE_DELAY_MS = 500;
    private long lastScaleTime = 0;

    private Uri photoUri;    // 찍은 사진을 임시로 저장할 Uri

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image);
        checkPermissions();

        recyclerView = findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        // 초기 사진 목록 세팅
        List<File> imageFiles = loadImages();
        adapter = new ImageAdapter(this, imageFiles);
        recyclerView.setAdapter(adapter);

        // DividerItemDecoration 추가
        int dividerColor = getResources().getColor(android.R.color.black); // 경계선 색상
        int dividerSize = 5; // 경계선 두께 (px)
        recyclerView.addItemDecoration(new DividerItemDecoration(dividerColor, dividerSize));

        // 핀치 줌 설정
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastScaleTime < SCALE_DELAY_MS) return false;

                float scaleFactor = detector.getScaleFactor();
                if (scaleFactor > 1.0f && spanCount > 1) {
                    spanCount--;
                } else if (scaleFactor < 1.0f && spanCount < 5) {
                    spanCount++;
                }
                updateSpanCount();
                lastScaleTime = currentTime;
                return true;
            }
        });

        // RecyclerView 터치 리스너
        recyclerView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event); // 핀치 줌 이벤트 처리
            return scaleGestureDetector.isInProgress(); // 핀치 줌 중이 아니면 기본 이벤트 전달
        });

        // FloatingActionButton 눌렀을 때 카메라 실행
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(
                            this,
                            "com.example.week01_project3.fileprovider",
                            photoFile
                    );
                    Log.d("GalleryActivity", "Photo URI: " + photoUri.toString());
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    try {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    } catch (Exception e) {
                        Log.e("GalleryActivity", "Camera start error: " + e.getMessage());
                    }
                } else {
                    Log.e("GalleryActivity", "Photo file creation failed");
                }
            } else {
                Log.e("GalleryActivity", "No camera app available");
            }
        });
    }

    // 사진 촬영 후 돌아왔을 때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 찍힌 사진이 photoUri 에 저장됨
            // 갱신된 이미지 목록 불러와 어댑터 갱신
            List<File> imageFiles = loadImages();
            adapter.updateImages(imageFiles);
        }
    }

    // 스팬 개수 업데이트
    private void updateSpanCount() {
        gridLayoutManager.setSpanCount(spanCount);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    // 이미지 파일 생성 (GalleryImages 폴더 내에 저장)
    private File createImageFile() {
        File storageDir = new File(getExternalFilesDir(null), "GalleryImages");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        return new File(storageDir, fileName);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    // 폴더 내 .jpg 파일 불러오기
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // FloatingActionButton 영역 외부에서만 터치 이벤트 처리
        if (findViewById(R.id.fab).isPressed()) {
            return false; // FAB 클릭 이벤트가 우선 처리되도록 반환
        }
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    // DividerItemDecoration 클래스 (경계선 추가)
    private static class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private final int dividerSize; // 경계선 두께
        private final Paint paint;    // 경계선 색상

        public DividerItemDecoration(int color, int dividerSize) {
            this.dividerSize = dividerSize;
            paint = new Paint();
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                // 아이템 간 경계선 그리기 (넉넉하게 덮도록 보정)
                int left = child.getLeft() - params.leftMargin - dividerSize;
                int right = child.getRight() + params.rightMargin + dividerSize;
                int top = child.getTop() - params.topMargin - dividerSize;
                int bottom = child.getBottom() + params.bottomMargin + dividerSize;

                // 상단 경계선
                canvas.drawRect(left, top, right, top + dividerSize, paint);
                // 하단 경계선
                canvas.drawRect(left, bottom - dividerSize, right, bottom, paint);
                // 좌측 경계선
                canvas.drawRect(left, top, left + dividerSize, bottom, paint);
                // 우측 경계선
                canvas.drawRect(right - dividerSize, top, right, bottom, paint);
            }
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            // 아이템 간 간격 설정
            outRect.set(dividerSize, dividerSize, dividerSize, dividerSize);
        }
    }
}