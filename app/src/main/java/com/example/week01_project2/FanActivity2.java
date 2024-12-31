package com.example.week01_project2;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.graphics.Path;

public class FanActivity2 extends AppCompatActivity {
    private Handler speedHandler = new Handler();
    private long autoDecayInterval = 2000;
    private float decayFactor = 1.1f;
    private boolean isDecaying = true;
    private boolean isStopped = false;
    private ObjectAnimator rotateAnimator;
    private ObjectAnimator moveAlongRectangle;
    private long baseDuration = 2000;
    private ConstraintLayout layout;

    private static final int SAMPLE_RATE = 16000;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
    );
    // 넙죽이 더울때 / 추울때 속도
    private void updateNupjukSpeed(long fanSpeedDuration, boolean isCool) {
        long nupjukSpeedDuration;

        if (isCool) {
            nupjukSpeedDuration = Math.min(6000, fanSpeedDuration * 2); // 시원할 때 더 천천히 움직임
        } else {
            nupjukSpeedDuration = Math.max(500, 6000 - fanSpeedDuration); // 더운 상태일 때 더 빠르게 움직임
        }

        moveAlongRectangle.setDuration(nupjukSpeedDuration);
        if (!moveAlongRectangle.isRunning()) {
            moveAlongRectangle.start(); // 넙죽이 애니메이션 시작
        }

        Log.d("FanActivity2", "Nupjuk Speed Updated: " + nupjukSpeedDuration + " ms (Cool: " + isCool + ")");
    }
    @Override
    // 오디오 사용 권한
    public void onRequestPermissionsResult(int requestCode,
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

        layout = findViewById(R.id.fan_layout);
        layout.setBackgroundResource(R.drawable.hot_background); // 기본 시작 배경화면

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
        }

        ImageView fanfan = findViewById(R.id.fanfan);
        ImageView nupjuk = findViewById(R.id.hot_nupjuk);
        Button speedUpButton = findViewById(R.id.speedUpButton);

        rotateAnimator = ObjectAnimator.ofFloat(fanfan, "rotation", 0f, 360f);
        rotateAnimator.setDuration(baseDuration); // baseDuration: 2000. 동안 움직이나..
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setInterpolator(null);
        rotateAnimator.start();

        moveAlongRectangle = createRectangleAnimation(nupjuk);
        double initialDecibel = 0; // 초기는 아무말 안하니 데시벨 0
        adjustRectangleAnimationSpeed(initialDecibel);
        nupjuk.setImageResource(R.drawable.hot_nupjuk);
        moveAlongRectangle.start();

        speedUpButton.setOnClickListener(v -> measureDecibelAndAdjustSpeed(nupjuk));
        startSpeedDecay();
    }
// funfun
    private ObjectAnimator createRectangleAnimation(ImageView hotNupjuk) {
        Path rectanglePath = new Path();
        rectanglePath.moveTo(-200f, 100f);
        rectanglePath.lineTo(200f, 100f);
        rectanglePath.lineTo(200f, -200f);
        rectanglePath.lineTo(-200f, -200f);
        rectanglePath.lineTo(-200f, 100f);

        ObjectAnimator moveAlongRectangle = ObjectAnimator.ofFloat(hotNupjuk, "translationX", "translationY", rectanglePath);
        moveAlongRectangle.setDuration(3000);
        moveAlongRectangle.setRepeatCount(ValueAnimator.INFINITE);
        moveAlongRectangle.setRepeatMode(ValueAnimator.RESTART);
        return moveAlongRectangle;
    }

    private void adjustRectangleAnimationSpeed(double decibel) {
        if (decibel < 10) decibel = 10;
        if (decibel > 70) decibel = 70;

        long newDuration = (long) (500 + (decibel - 10) * 100);

        runOnUiThread(() -> {
            if (moveAlongRectangle != null) {
                moveAlongRectangle.setDuration(newDuration);
                if (!moveAlongRectangle.isRunning()) {
                    moveAlongRectangle.start();
                }
                Log.d("FanActivity2", "Adjusted Rectangle Animation Speed. New Duration: " + newDuration);
            }
        });
    }

    private void measureDecibelAndAdjustSpeed(ImageView nupjuk) {
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
                        nupjuk.setImageResource(R.drawable.cool_nupjuk);
                        layout.setBackgroundResource(R.drawable.cool_background);
                    } else {
                        nupjuk.setImageResource(R.drawable.hot_nupjuk);
                        layout.setBackgroundResource(R.drawable.hot_background);
                    }

                    adjustRectangleAnimationSpeed(averageDecibel);
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
                if (isDecaying && !isStopped) {
                    long currentDuration = rotateAnimator.getDuration();
                    long stopTime = (long) (currentDuration);

                    // 일정 시간 후 애니메이션 중지
                    speedHandler.postDelayed(() -> {
                        runOnUiThread(() -> {
                            if (!isStopped) {
                                rotateAnimator.cancel(); // 팬 애니메이션 중지
                                isStopped = true; // 중지 상태로 설정

                                // 넙죽이를 빨간 상태로 변경
                                ImageView hotNupjuk = findViewById(R.id.hot_nupjuk);
                                hotNupjuk.setImageResource(R.drawable.hot_nupjuk);
                                updateNupjukSpeed(currentDuration, true); // 넙죽이가 천천히 움직임
                                Log.d("FanActivity2", "Fan animation stopped. Nupjuk turned red.");
                            }
                        });
                    }, stopTime);
                }
            }
        }, autoDecayInterval);
    }

    private void adjustFanSpeed(double decibel) {
        if (decibel < 10) decibel = 10; // 최소 데시벨 클램핑
        if (decibel > 70) decibel = 70; // 최대 데시벨 클램핑

        long newDuration = (long) (3491 - 49 * decibel);

        runOnUiThread(() -> {
            if (isStopped) { // 팬이 중지 상태라면
                rotateAnimator.start(); // 팬 애니메이션 재개
                isStopped = false; // 중지 상태 해제

                // 넙죽이를 시원한 상태로 변경
                ImageView hotNupjuk = findViewById(R.id.hot_nupjuk);
                hotNupjuk.setImageResource(R.drawable.cool_nupjuk);
                Log.d("FanActivity2", "Animation restarted. Nupjuk turned cool.");
            }

            rotateAnimator.setDuration(newDuration); // 팬 속도 업데이트
            updateNupjukSpeed(newDuration, false); // 넙죽이 속도 동기화 (빨리 움직임)
            Log.d("FanActivity2", "Adjusted Fan Speed. New Duration: " + newDuration);

            // 새로운 중지 타이머 설정
            long stopTime = newDuration*10; // 팬 속도의 10배 시간 후 중지
            speedHandler.postDelayed(() -> {
                runOnUiThread(() -> {
                    if (!isStopped) {
                        rotateAnimator.cancel(); // 팬 애니메이션 중지
                        isStopped = true; // 중지 상태로 설정

                        // 넙죽이를 빨간 상태로 변경
                        ImageView hotNupjuk = findViewById(R.id.hot_nupjuk);
                        hotNupjuk.setImageResource(R.drawable.hot_nupjuk);
                        updateNupjukSpeed(newDuration, true); // 팬이 멈췄을 때도 넙죽이가 천천히 움직임
                        Log.d("FanActivity2", "Fan animation stopped. Nupjuk turned red.");
                    }
                });
            }, stopTime);
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
