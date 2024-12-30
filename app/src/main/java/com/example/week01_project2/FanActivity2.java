package com.example.week01_project2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FanActivity2 extends AppCompatActivity {

    private ObjectAnimator rotateAnimator;
    private long baseDuration = 2000; // 기본 회전 시간
    private static final int SAMPLE_RATE = 16000; // 샘플링 레이트
    private ObjectAnimator moveAlongRectangle; // 넙죽이 경로 애니메이션
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
    );
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            // 권한 획득 성공 / 실패 처리
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("FanActivity2", "RECORD_AUDIO 권한 허용됨");
            } else {
                Log.d("FanActivity2", "RECORD_AUDIO 권한 거부됨");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fan);

        // onCreate() 안에 추가
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1000);
        }

        ImageView fanfan = findViewById(R.id.fanfan);
        Button speedUpButton = findViewById(R.id.speedUpButton);

        rotateAnimator = ObjectAnimator.ofFloat(fanfan, "rotation", 0f, 360f);
        rotateAnimator.setDuration(baseDuration);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setInterpolator(null);
        rotateAnimator.start();


        // 넙죽이 움직이는 부분
        ImageView hotNupjuk = findViewById(R.id.hot_nupjuk);
        moveAlongRectangle = createRectangleAnimation(hotNupjuk);

        // 버튼 클릭 시 데시벨 측정 및 넙죽이 상태 변경
        speedUpButton.setOnClickListener(v -> measureDecibelAndAdjustSpeed(hotNupjuk));

    }

    private ObjectAnimator createRectangleAnimation(ImageView hotNupjuk) {
        // 사각형 경로 정의
        Path rectanglePath = new Path();
        rectanglePath.moveTo(-200f, 0f); // 시작점 (왼쪽 아래)
        rectanglePath.lineTo(200f, 0f); // 오른쪽 아래로 이동
        rectanglePath.lineTo(200f, -200f); // 오른쪽 위로 이동
        rectanglePath.lineTo(-200f, -200f); // 왼쪽 위로 이동
        rectanglePath.lineTo(-200f, 0f); // 시작점으로 돌아옴

        // ObjectAnimator 생성
        ObjectAnimator moveAlongRectangle = ObjectAnimator.ofFloat(hotNupjuk, "translationX", "translationY", rectanglePath);
        moveAlongRectangle.setDuration(3000); // 3초 동안 사각형 이동
        moveAlongRectangle.setRepeatCount(ValueAnimator.INFINITE); // 무한 반복
        moveAlongRectangle.setRepeatMode(ValueAnimator.RESTART); // 처음부터 다시 시작
        return moveAlongRectangle;
    }

    private void measureDecibelAndAdjustSpeed(ImageView hotNupjuk) {
        // 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
            return; // 권한이 없으면 측정 시작하지 않음
        }

        // 권한이 있을 경우 측정 시작
        new Thread(() -> {
            AudioRecord audioRecord = null;
            try {
                audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        BUFFER_SIZE
                );

                byte[] buffer = new byte[BUFFER_SIZE];
                audioRecord.startRecording();
                Log.d("FanActivity2", "Started audio recording...");

                long startTime = System.currentTimeMillis();
                double totalDecibel = 0; // 데시벨 합계를 저장할 변수
                int sampleCount = 0; // 유효한 샘플의 개수

                while (System.currentTimeMillis() - startTime < 3000) { // 3초 동안 측정
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        double rms = calculateRMS(buffer, read);
                        double decibel = 20 * Math.log10(rms);
                        if (decibel > 0) { // RMS 값이 유효한 경우만 계산
                            totalDecibel += decibel; // 데시벨 합산
                            sampleCount++; // 샘플 개수 증가
                        }
                    }
                }

// 평균값 계산
                double averageDecibel = sampleCount > 0 ? totalDecibel / sampleCount : 0;

                Log.d("FanActivity2", "Average Decibel: " + averageDecibel);

// 측정 완료 메시지 표시
                runOnUiThread(() -> {
                    Toast.makeText(FanActivity2.this,
                        "3초 측정 완료! 평균 데시벨: " + (int) averageDecibel + " dB",
                        Toast.LENGTH_SHORT).show();

                    // 데시벨에 따른 넙죽이 상태 변경
                    if (averageDecibel > 60) { // 데시벨 기준
                        hotNupjuk.setImageResource(R.drawable.cool_nupjuk); // 시원한 넙죽이
                        if (!moveAlongRectangle.isRunning()) {
                            moveAlongRectangle.cancel(); // 넙죽이 움직이기
                        }
                    } else {
                        hotNupjuk.setImageResource(R.drawable.hot_nupjuk); // 더운 넙죽이
                        if (moveAlongRectangle.isRunning()) {
                            moveAlongRectangle.start(); // 넙죽이 움직이기
                        }
                    }

                });
// 팬 속도 조정
                adjustFanSpeed(averageDecibel);

            } catch (Exception e) {
                Log.e("FanActivity2", "Error during audio recording", e);
            } finally {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    Log.d("FanActivity2", "AudioRecord resources released.");
                }
            }
        }).start();
    }


    private void adjustFanSpeed(double decibel) {
        // 데시벨 범위 (10~80) -> 회전 시간 (3000ms ~ 100ms)
        if (decibel < 10) decibel = 10; // 최소 데시벨 클램핑
        if (decibel > 70) decibel = 70; // 최대 데시벨 클램핑

        // 회전 시간 계산 (3000ms ~ 100ms, 데시벨이 높을수록 빠르게)
        long newDuration = (long) (3491 - 49 * decibel) ;

        runOnUiThread(() -> {
            float currentPlayTime = rotateAnimator.getCurrentPlayTime();
            rotateAnimator.cancel();
            rotateAnimator.setDuration(newDuration);
            rotateAnimator.start();
            rotateAnimator.setCurrentPlayTime((long) currentPlayTime);
            Log.d("FanActivity2", "Adjusted Fan Speed. New Duration: " + newDuration);
        });
    }
    private double calculateRMS(byte[] buffer, int read) {
        long sum = 0;
        for (int i = 0; i < read; i += 2) {
            short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
            sum += sample * sample;
        }
        return Math.sqrt(sum / (read / 2.0));
    }


}