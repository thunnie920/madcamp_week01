package com.example.week01_project2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final Context context;
    private final List<File> imageFiles;

    public ImageAdapter(Context context, List<File> imageFiles) {
        this.context = context;
        this.imageFiles = imageFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File imageFile = imageFiles.get(position);

        // 이미지 로드
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        holder.imageView.setImageBitmap(bitmap);

        // 정사각형 크기로 설정
        holder.itemView.post(() -> {
            int width = holder.itemView.getWidth();
            ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
            layoutParams.height = width; // 높이를 너비와 동일하게 설정
            holder.imageView.setLayoutParams(layoutParams);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CameraActivity.class);
            intent.putExtra("image_path", imageFile.getAbsolutePath());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}