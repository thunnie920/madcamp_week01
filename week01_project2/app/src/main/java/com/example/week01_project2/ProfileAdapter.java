package com.example.week01_project2;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.CustomViewHolder> {

    private ArrayList<Profile> profileList;

    // Constructor
    public ProfileAdapter(ArrayList<Profile> profileList) {
        this.profileList = profileList;
    }

    // Custom ViewHolder Class
    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;
        TextView name, telNum, email, birthDay;
        ImageButton callButton;
        ImageButton textButton;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.contact_image);
            name = itemView.findViewById(R.id.contact_name);
            telNum = itemView.findViewById(R.id.contact_phoneNumber);
            callButton = itemView.findViewById(R.id.call_button);
            textButton = itemView.findViewById(R.id.text_button);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cursorPosition = holder.getAdapterPosition();
                Profile profile = profileList.get(cursorPosition);

                Intent intent = new Intent(parent.getContext(), DetailActivity.class);
                intent.putExtra("picture", profile.getPicture());
                intent.putExtra("name", profile.getName());
                intent.putExtra("telNum", profile.getTelNum());
                intent.putExtra("email", profile.getEmail());
                intent.putExtra("birthday", profile.getBirthDay());

                // DetailActivity 실행
                parent.getContext().startActivity(intent);
            }
        });

        holder.itemView.findViewById(R.id.contact_image).setOnClickListener(new View.OnClickListener() {
            private boolean isImageMoved = false; // 이동 상태 플래그
            private View transparentOverlay = null; // 투명 오버레이 뷰

            @Override
            public void onClick(View v) {
                if (!isImageMoved) {
                    // 사진 애니메이션 - 아래로 이동
                    ObjectAnimator animator = ObjectAnimator.ofFloat(
                            v,
                            "translationY",
                            v.getHeight() * 2 // 아래로 이동
                    );
                    animator.setDuration(500);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.start();

                    // 투명 오버레이 뷰 추가
                    ViewGroup parent = (ViewGroup) v.getParent();
                    transparentOverlay = new View(v.getContext());
                    transparentOverlay.setLayoutParams(new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    ));
                    transparentOverlay.setBackgroundColor(0x00000000); // 완전 투명
                    parent.addView(transparentOverlay);

                    // 투명 뷰 클릭 리스너 설정
                    transparentOverlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View overlayView) {
                            // 두 번째 클릭 시 Drawable 이미지 표시
                            ImageView imageView = new ImageView(v.getContext());
                            imageView.setImageResource(R.drawable.file); // drawable 파일 설정

                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.WRAP_CONTENT,
                                    FrameLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.gravity = Gravity.CENTER; // 초기 위치는 화면 하단
                            imageView.setLayoutParams(params);
                            parent.addView(imageView);

                            // 이미지 애니메이션 - 천천히 위로 올라오기
                            imageView.setTranslationY(parent.getHeight()); // 초기 위치 설정 (화면 하단)
                            ObjectAnimator imageAnimator = ObjectAnimator.ofFloat(
                                    imageView,
                                    "translationY",
                                    0 // 화면 중앙으로 이동
                            );
                            imageAnimator.setDuration(700); // 애니메이션 지속 시간
                            imageAnimator.setInterpolator(new DecelerateInterpolator()); // 느려지며 이동
                            imageAnimator.start();

                            // 초기화
                            isImageMoved = false;
                            parent.removeView(transparentOverlay);
                            transparentOverlay = null;
                        }
                    });

                    isImageMoved = true;
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Profile profile = profileList.get(position);
        holder.picture.setImageResource(profile.getPicture());
        holder.name.setText(profile.getName());
        holder.telNum.setText(profile.getTelNum());
        //holder.email.setText(profile.getEmail());
        //holder.birthDay.setText(profile.getBirthDay());

        // call 버튼 클릭 리스너 추가
        holder.callButton.setOnClickListener(v -> {
            String telNum = profile.getTelNum();
            if (telNum == null || telNum.isEmpty()) {
                Log.e("ProfileAdapter", "전화번호가 없습니다.");
                return;
            }

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + telNum));

            Context context = v.getContext();
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Log.e("ProfileAdapter", "전화 앱이 없습니다.");
            }
        });

        // text 버튼 클릭 리스너 추가
        holder.textButton.setOnClickListener(v -> {
            String telNum = profile.getTelNum();
            if (telNum == null || telNum.isEmpty()) {
                Log.e("ProfileAdapter", "전화번호가 없습니다.");
                return;
            }

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + telNum));
            intent.putExtra("sms_body", "안녕하세요~");

            Context context = v.getContext();
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Log.e("ProfileAdapter", "메시지 앱이 없습니다.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }
}
