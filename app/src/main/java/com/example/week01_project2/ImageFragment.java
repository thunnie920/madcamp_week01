package com.example.week01_project2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.week01_project2.databinding.FragmentImageBinding;

public class ImageFragment extends Fragment {
    private FragmentImageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater, container, false);

        // View와 동작 설정 (필요한 경우 추가)
        return binding.getRoot();
    }
}
