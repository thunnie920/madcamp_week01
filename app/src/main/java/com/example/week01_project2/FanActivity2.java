package com.example.week01_project2;

import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FanActivity2 extends AppCompatActivity {
    private Handler speedHandler = new Handler(); // 핸들러 생성
    private long autoDecayInterval = 2000; // 속도 감소 주기 (2초)
    private float decayFactor = 1.1f; // 속도 감소 계수 (10%씩 느려짐)
    private boolean isDecaying = true; // 자동 감속 활성화 여부
    private ObjectAnimator rotateAnimator;
    private ObjectAnimator moveAlongRectangle; // 넙죽이 경로 애니메이션
    private long baseDuration = 2000; // 기본 회전 시간
    private static final int SAMPLE_RATE = 16000; // 샘플링 레이트
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
    );

    @Override
    protected void onRequestPermissionsResult(int requestCode,
                                              @NonNull String[] permissions,
                                              @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
        }

        ImageView fanfan = findViewById(R.id.fanfan);
        ImageView hotNupjuk = findViewById(R.id.hot_nupjuk);
        Button speedUpButton = findViewById(R.id.speedUpButton);

        // 팬 회전 애니메이션
        rotateAnimator = ObjectAnimator.ofFloat(fanfan, "rotation", 0f, 360f);
        rotateAnimator.setDuration(baseDuration);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setInterpolator(null);
        rotateAnimator.start();

        // 넙죽이 이동 애니메이션
        moveAlongRectangle = createRectangleAnimation(hotNupjuk);

        // 버튼 클릭 시 데시벨 측정 및 상태 변경
        speedUpButton.setOnClickListener(v -> measureDecibelAndAdjustSpeed(hotNupjuk));

        // 자동 감속 시작
        startSpeedDecay();
    }

    private ObjectAnimator createRectangleAnimation(ImageView hotNupjuk) {
        Path rectanglePath = new Path();
        rectanglePath.moveTo(-200f, 0f); // 시작점 (왼쪽 아래)
        rectanglePath.lineTo(200f, 0f); // 오른쪽 아래로 이동
        rectanglePath.lineTo(200f, -200f); // 오른쪽 위로 이동
        rectanglePath.lineTo(-200f, -200f); // 왼쪽 위로 이동
        rectanglePath.lineTo(-200f, 0f); // 시작점으로 돌아옴

        ObjectAnimator moveAlongRectangle = ObjectAnimator.ofFloat(hotNupjuk, "translationX", "translationY", rectanglePath);
        moveAlongRectangle.setDuration(3000); // 3초 동안 사각형 이동
        moveAlongRectangle.setRepeatCount(ValueAnimator.INFINITE); // 무한 반복
        moveAlongRectangle.setRepeatMode(ValueAnimator.RESTART); // 처음부터 다시 시작
        return moveAlongRectangle;
    }

    private void measureDecibelAndAdjustSpeed(ImageView hotNupjuk) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
            return;
        }

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
                double totalDecibel = 0;
                int sampleCount = 0;

                while (System.currentTimeMillis() - startTime < 3000) {
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        double rms = calculateRMS(buffer, read);
                        double decibel = 20 * Math.log10(rms);
                        if (decibel > 0) {
                            totalDecibel += decibel;
                            sampleCount++;
                        }
                    }
                }

                double averageDecibel = sampleCount > 0 ? totalDecibel / sampleCount : 0;
                Log.d("FanActivity2", "Average Decibel: " + averageDecibel);

                runOnUiThread(() -> {
                    Toast.makeText(FanActivity2.this,
                            "3초 측정 완료! 평균 데시벨: " + (int) averageDecibel + " dB",
                            Toast.LENGTH_SHORT).show();

                    if (averageDecibel > 60) {
                        hotNupjuk.setImageResource(R.drawable.fffann);
                        if (!moveAlongRectangle.isRunning()) {
                            moveAlongRectangle.start();
                        }
                    } else {
                        hotNupjuk.setImageResource(R.drawable.heyyy);
                        if (moveAlongRectangle.isRunning()) {
                            moveAlongRectangle.cancel();
                        }
                    }
                });

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

    private void startSpeedDecay() {
        speedHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDecaying) {
                    long currentDuration = rotateAnimator.getDuration();
                    long newDuration = (long) (currentDuration * decayFactor);

                    if (newDuration > 5000) newDuration = 5000;

                    rotateAnimator.setDuration(newDuration);
                    Log.d("FanActivity2", "Auto-Decayed Fan Speed. New Duration: " + newDuration);
                }
                speedHandler.postDelayed(this, autoDecayInterval);
            }
        }, autoDecayInterval);
    }

    private void adjustFanSpeed(double decibel) {
        if (decibel < 10) decibel = 10;
        if (decibel > 70) decibel = 70;

        long newDuration = (long) (3491 - 49 * decibel);

        runOnUiThread(() -> {
            rotateAnimator.setDuration(newDuration);
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