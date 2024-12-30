package com.example.week01_project2;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Fragment의 레이아웃을 inflate
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // RecyclerView 초기화
        RecyclerView rvProfile = view.findViewById(R.id.recyclerView_profile);

        // JSON 데이터를 파싱하여 Profile 리스트 생성
        ArrayList<Profile> profileList = loadProfilesFromJson();

        // RecyclerView 레이아웃 매니저 설정
        rvProfile.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvProfile.setHasFixedSize(true);

        // RecyclerView 어댑터 설정
        ProfileAdapter adapter = new ProfileAdapter(profileList);
        rvProfile.setAdapter(adapter);

        return view;
    }

    private ArrayList<Profile> loadProfilesFromJson() {
        ArrayList<Profile> profileList = new ArrayList<>();
        try {
            // AssetManager를 사용해 JSON 파일 읽기
            AssetManager assetManager = getContext().getAssets();
            InputStream is = assetManager.open("profiles.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            // JSON 파싱
            String json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int picture = getResources().getIdentifier(
                        obj.getString("picture"), "drawable", getContext().getPackageName()
                );
                String name = obj.getString("name");
                String telNum = obj.getString("telNum");
                String email = obj.getString("email");
                String birthDay = obj.getString("birthDay");

                profileList.add(new Profile(picture, name, telNum, email, birthDay));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profileList;
    }
}
