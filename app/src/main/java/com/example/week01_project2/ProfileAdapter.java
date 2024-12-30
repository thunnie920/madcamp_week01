package com.example.week01_project2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        Button callButton;
        Button textButton;

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
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Profile profile = profileList.get(position);
        holder.picture.setImageResource(profile.getPicture());
        holder.name.setText(profile.getName());
        holder.telNum.setText(profile.getTelNum());

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
