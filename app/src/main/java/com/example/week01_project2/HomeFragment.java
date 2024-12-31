package com.example.week01_project2;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.week01_project2.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // FragmentHomeBinding을 사용하여 레이아웃 바인딩
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // 버튼 클릭 이벤트 설정
        binding.buttonContact.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_contactFragment)
        );

        binding.buttonImage.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), GalleryActivity.class);
            startActivity(intent);
        });

        binding.buttonElse.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), FanActivity.class);
            startActivity(intent);
        });

        // 버튼 애니메이션 추가 (속도 빠르게)
        setupButtonAnimation(binding.buttonContact, 0f, 200f, 100);
        setupButtonAnimation(binding.buttonImage, 50f, 250f, 400);
        setupButtonAnimation(binding.buttonElse, 100f, 300f, 200);

        return binding.getRoot();
    }

    private void setupButtonAnimation(View button, float startY, float endY, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                button,
                "translationY",
                startY,
                endY
        );
        animator.setDuration(duration); // 애니메이션 지속 시간 (500ms)
        animator.setRepeatCount(ObjectAnimator.INFINITE); // 무한 반복
        animator.setRepeatMode(ObjectAnimator.REVERSE); // 왕복 애니메이션
        animator.setInterpolator(new LinearInterpolator()); // 일정한 속도
        animator.start(); // 애니메이션 시작
    }
}
