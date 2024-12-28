package com.example.photo1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout imageContainer = findViewById(R.id.imageContainer);
        String imgPrefix = "p"; // 이미지 리소스 이름의 접두사

        for (int i = 0; i < 20; i++) {
            int resID = getResources().getIdentifier(imgPrefix + (i + 1), "drawable", getPackageName());

            if (resID != 0) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(resID);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        dpToPx(103),
                        dpToPx(103)
                );
                imageView.setLayoutParams(params);
                imageView.setX(dpToPx((i % 4) * 103));
                imageView.setY(dpToPx((i / 4) * 103));
                imageContainer.addView(imageView);

                imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, CameraActivity.class);
                    intent.putExtra("image_info", resID);
                    startActivity(intent);
                });
            }
        }

        // Spacer 추가
        View spacer = new View(this);
        RelativeLayout.LayoutParams spacerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                dpToPx(800)
        );
        spacer.setLayoutParams(spacerParams);
        imageContainer.addView(spacer);



    }

}