package com.example.week01_project3;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FanActivity2 extends AppCompatActivity {
    private ConstraintLayout layout;
    private ImageView fanfan;
    private ImageView nupjuk;
    private Button speedUpButton;
    private ObjectAnimator rotateAnimator;
    private ObjectAnimator moveAlongRectangle;
    private boolean isStopped = true;
    private boolean isHot; // 기본 상태 더움


    // Handler를 메인 스레드의 Looper와 연결
    private Handler decayHandler = new Handler(Looper.getMainLooper());
    private Runnable decayRunnable;
    private Handler setHotHandler = new Handler(Looper.getMainLooper());
    private Runnable setHotRunnable;
    private FanActivity3 fanActivity3;

    private static final int SAMPLE_RATE = 16000;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
    );
    /*
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
    }*/

    @Override
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
        fanfan = findViewById(R.id.fanfan);
        nupjuk = findViewById(R.id.hot_nupjuk);
        speedUpButton = findViewById(R.id.speedUpButton);

        set_hot();

        // 팬 회전 애니메이션 (초기에는 멈춤)
        rotateAnimator = ObjectAnimator.ofFloat(fanfan, "rotation", 0f, 360f);
        rotateAnimator.setDuration(2000); // 기본 회전 속도
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE); // 무한 반복
        rotateAnimator.setInterpolator(null); // 선형 인터폴레이터

        // 넙죽이 사각 경로 이동 애니메이션
        moveAlongRectangle = createRectangleAnimation(nupjuk);
        moveAlongRectangle.setDuration(3000);
        moveAlongRectangle.setRepeatCount(ValueAnimator.INFINITE);
        moveAlongRectangle.setRepeatMode(ValueAnimator.RESTART);
        moveAlongRectangle.start();

        // 버튼 클릭 시 데시벨 측정 후 팬 속도 & 상태 조절
        speedUpButton.setOnClickListener(v -> measureDecibelAndAdjustSpeed());

        // FanActivity3 객체 초기화 (isHot 상태 전달)
        //fanActivity3 = new FanActivity3(this, isHot);
        //fanActivity3 = new FanActivity3(this);

        // Talk! 버튼 리스너 설정
        //findViewById(R.id.talkButton).setOnClickListener(fanActivity3);

        /*
        findViewById(R.id.talkButton).setOnClickListener(
                new FanActivity3(this, isHot)
        );*/
        findViewById(R.id.talkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 최신 isHot 값 사용
                fanActivity3 = new FanActivity3(v.getContext(), isHot);
                fanActivity3.onClick(v); // 클릭 이벤트 처리
            }
        });
    }

    // hot 상태
    private void set_hot() {
        layout.setBackgroundResource(R.drawable.hot_background);
        nupjuk.setImageResource(R.drawable.hot_nupjuk);
        isHot = true;
        Log.d("FanActivity2", "상태 변경: Hot");
    }

    // cool 상태
    private void set_cool() {
        layout.setBackgroundResource(R.drawable.cool_background);
        nupjuk.setImageResource(R.drawable.cool_nupjuk);
        isHot = false;
        Log.d("FanActivity2", "상태 변경: Cool");
    }

    /**
     * 일정 시간 후에 Hot 상태로 전환 (타이머)
     */
    private void scheduleSetHot() {
        // 이전 예약 취소
        if (setHotRunnable != null) {
            setHotHandler.removeCallbacks(setHotRunnable);
        }
        // 5초 후 Hot
        setHotRunnable = () -> {
            set_hot();
            Log.d("FanActivity2", "set_hot() 호출됨 (타이머 완료)");
        };
        setHotHandler.postDelayed(setHotRunnable, 5000);
        Log.d("FanActivity2", "set_hot() 타이머 시작됨 (5초 후 호출)");
    }

    /**
     * 넙죽이 사각 경로 이동 애니메이션 생성
     */
    private ObjectAnimator createRectangleAnimation(ImageView hotNupjuk) {
        Path rectanglePath = new Path();
        rectanglePath.moveTo(-200f, 100f);
        rectanglePath.lineTo(200f, 100f);
        rectanglePath.lineTo(200f, -200f);
        rectanglePath.lineTo(-200f, -200f);
        rectanglePath.lineTo(-200f, 100f);

        return ObjectAnimator.ofFloat(hotNupjuk, "translationX", "translationY", rectanglePath);
    }

    private void measureDecibelAndAdjustSpeed() {
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

                while (System.currentTimeMillis() - startTime < 3000) { // 3초 동안 측정
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        long sum = 0;
                        for (int i = 0; i < read; i += 2) {
                            if (i + 1 >= buffer.length) break;
                            short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
                            sum += sample * sample;
                        }
                        double rms = Math.sqrt(sum / (read / 2.0));
                        double decibel = 20 * Math.log10(rms == 0 ? 1 : rms);
                        if (decibel > 0) {
                            totalDecibel += decibel;
                            sampleCount++;
                        }
                    }
                }

                double averageDecibel = sampleCount > 0 ? totalDecibel / sampleCount : 0;
                Log.d("FanActivity2", "Average Decibel: " + averageDecibel);

                // 데시벨 -> 팬 지속 시간 변환
                //averageDecibel = Math.max(10, Math.min(averageDecibel, 70)); // 10~70 범위로 제한
                averageDecibel = 50;
                long duration = (long) (3800 * Math.exp(-0.1 * (averageDecibel - 20)) + 200);

                final double averageDecibelFinal = averageDecibel; // final 변수로 복사
                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "평균 데시벨: " + (int) averageDecibelFinal + " dB\n" +
                                    "지속 시간: " + duration + " ms",
                            Toast.LENGTH_SHORT).show();if (averageDecibelFinal > 40) {
                        set_cool();
                    } else {
                        scheduleSetHot();
                    }
                    adjustFanSpeed(duration);
                });

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
        // 기존 decayRunnable 제거
        if (decayRunnable != null) {
            decayHandler.removeCallbacks(decayRunnable);
        }

        decayRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isStopped) {
                    long currentDuration = rotateAnimator.getDuration();
                    long plus = (long) (0.25 * currentDuration);
                    if(currentDuration<400) plus = 20;
                    // 감속 로직: 지속 시간을 점진적으로 증가
                    long newDuration = currentDuration + plus; // 100ms씩 증가
                    if (newDuration >= 4500) { // 최대 지속 시간 도달 시 멈춤
                        rotateAnimator.cancel();
                        isStopped = true;
                        set_hot();
                        Log.d("FanActivity2", "Fan animation stopped due to maximum decay.");
                    } else {
                        // 진행률 유지
                        long currentPlayTime = rotateAnimator.getCurrentPlayTime();
                        float progress = (float) currentPlayTime / currentDuration;

                        rotateAnimator.setDuration(newDuration);
                        rotateAnimator.setCurrentPlayTime((long) (progress * newDuration));

                        Log.d("FanActivity2", "Fan animation decayed to duration: " + newDuration + " ms");

                        // 다음 감속 호출
                        decayHandler.postDelayed(this, 1000); // 1초마다 감속 호출
                    }
                } else {
                    Log.d("FanActivity2", "Fan is stopped. Decay process halted.");
                }
            }
        };

        // 초기 감속 시작
        decayHandler.postDelayed(decayRunnable, 1000);
    }

    private void adjustFanSpeed(long newDuration) {
        if (rotateAnimator == null) return; // 애니메이터가 초기화되지 않은 경우 무시

        // 현재 진행률 및 재생 시간 계산
        long currentPlayTime = rotateAnimator.getCurrentPlayTime(); // 현재 진행 시간(ms)
        long totalDuration = rotateAnimator.getDuration();          // 기존 총 지속 시간(ms)
        float progress = totalDuration > 0 ? (float) currentPlayTime / totalDuration : 0f; // 현재 진행률 계산

        // 새로운 지속 시간 설정
        rotateAnimator.setDuration(newDuration);

        // 진행률 유지
        rotateAnimator.setCurrentPlayTime((long) (progress * newDuration));

        // 애니메이션 실행 상태 확인
        if (!rotateAnimator.isStarted()) {
            rotateAnimator.start(); // 애니메이션이 멈춰있으면 다시 시작
            isStopped = false;
            Log.d("FanActivity2", "Fan animation started with duration: " + newDuration + " ms");
        } else {
            Log.d("FanActivity2", "Fan animation speed adjusted to duration: " + newDuration + " ms");
        }

        // 감속 타이머 시작
        startSpeedDecay();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 핸들러의 모든 콜백 제거
        if (setHotRunnable != null) {
            setHotHandler.removeCallbacks(setHotRunnable);
        }
        if (decayRunnable != null) {
            decayHandler.removeCallbacks(decayRunnable);
        }
        decayHandler.removeCallbacksAndMessages(null);
        setHotHandler.removeCallbacksAndMessages(null);
    }
}
