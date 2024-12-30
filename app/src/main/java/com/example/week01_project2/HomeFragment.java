package com.example.week01_project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        return binding.getRoot();
    }
}
