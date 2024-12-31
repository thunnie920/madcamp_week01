package com.example.week01_project2;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment의 레이아웃을 inflate
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // RecyclerView 초기화
        RecyclerView rvProfile = view.findViewById(R.id.recyclerView_profile);

        // Profile 리스트 생성
        ArrayList<Profile> profileList = new ArrayList<>();
        profileList.add(new Profile(R.drawable.p1, "유관순", "010-0808-1515", "korea@korea.com", "08.15"));
        profileList.add(new Profile(R.drawable.p2, "홍길동", "010-1234-5678", "gildong@chosun.com", "03.05"));
        profileList.add(new Profile(R.drawable.p3, "유재석", "010-7777-8888", "leejs@naver.com", "07.07"));
        profileList.add(new Profile(R.drawable.p4, "엘사", "010-3333-9999", "elsa@gmail.com", "12.25"));
        profileList.add(new Profile(R.drawable.p5, "모피어스", "010-2222-3434", "morpheus@gmail.com", "07.12"));
        profileList.add(new Profile(R.drawable.p6, "네오", "010-0505-4040", "gildong@chosun.com", "28.05"));
        profileList.add(new Profile(R.drawable.p7, "지예은", "010-2323-9090", "yeeun@daum.net", "03.08"));
        profileList.add(new Profile(R.drawable.p8, "정상훈", "010-5555-7878", "jeong@snlkorea.com", "11.30"));
        profileList.add(new Profile(R.drawable.p9, "이수지", "010-3030-7080", "suj@snlkorea.com", "06.20"));
        profileList.add(new Profile(R.drawable.p10, "김슬기", "010-4747-5656", "seulgi@snlkorea.com", "28.05"));

        // RecyclerView 레이아웃 매니저 설정
        rvProfile.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvProfile.setHasFixedSize(true);

        // RecyclerView 어댑터 설정
        ProfileAdapter adapter = new ProfileAdapter(profileList);
        rvProfile.setAdapter(adapter);

        return view;
    }
}
