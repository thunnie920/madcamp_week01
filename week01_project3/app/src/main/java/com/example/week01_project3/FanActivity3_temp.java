package com.example.week01_project3;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class FanActivity3_temp implements View.OnClickListener {

    private static final String BASE_URL = "http://143.248.232.67:5000/"; // Flask 서버 URL
    private final Context context;
    private SpeechRecognizer speechRecognizer;
    private boolean isHot;
    private GPTApi api;
    private MediaPlayer mediaPlayer;

    public FanActivity3_temp(Context context, boolean isHot) {
        this.context = context;

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(GPTApi.class);
    }

    private void initializeSpeechRecognition() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    Toast.makeText(context, "말씀하세요...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        sendToServer(matches.get(0));
                    }
                }

                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() {}
                @Override public void onError(int error) {
                    Log.e("SpeechRecognizer", "Error code: " + error);
                    Toast.makeText(context, "음성 인식 오류 발생", Toast.LENGTH_SHORT).show();
                }

                @Override public void onPartialResults(Bundle partialResults) {}
                @Override public void onEvent(int eventType, Bundle params) {}
            });
        }

        startListening();
    }

    private void startListening() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.stopListening(); // 기존 세션 종료
            } catch (Exception e) {
                Log.e("SpeechRecognizer", "Error stopping previous session: " + e.getMessage());
            }
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent);
    }


    private void sendToServer(String userInput) {
        /*
        GPTRequest request = new GPTRequest(
                "넌 지금 더워. 완전히 까칠하고 사납게 말해야해.", // 시스템 입력
                userInput // 사용자의 음성 텍스트 결과
        );*/
        String systemInput;
        GPTRequest request;
        if (isHot) { // 더울때
            systemInput = "넌 지금 더워. 완전히 까칠하고 사납게 말해야해.";
        } else { // 시원할때
            systemInput = "넌 지금 시원해. 상냥하고 침착하게 말해야해..";
        }
        request = new GPTRequest(systemInput, userInput);

        api.chat(request).enqueue(new Callback<GPTResponse>() {
            @Override
            public void onResponse(Call<GPTResponse> call, Response<GPTResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    playAudioFromUrl(BASE_URL + response.body().getAudioUrl());
                } else {
                    Toast.makeText(context, "서버 응답 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GPTResponse> call, Throwable t) {
                Log.e("NetworkError", "Error: " + t.getMessage());
                Toast.makeText(context, "네트워크 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playAudioFromUrl(String audioUrl) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            if (audioUrl == null || !audioUrl.startsWith("http")) {
                Log.e("MediaPlayer", "Invalid URL: " + audioUrl);
                return;
            }

            Log.d("MediaPlayer", "Full URL: " + audioUrl);

            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayer", "Error occurred: what=" + what + ", extra=" + extra);
                Toast.makeText(context, "재생 오류 발생", Toast.LENGTH_SHORT).show();
                return true;
            });

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                startListening(); // 재생 완료 후 음성 인식 재시작
            });
        } catch (IOException e) {
            Log.e("MediaPlayer", "IOException: " + e.getMessage());
        }
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        initializeSpeechRecognition();
    }

    // Retrofit 인터페이스 정의
    public interface GPTApi {
        @POST("/chat")
        Call<GPTResponse> chat(@Body GPTRequest request);
    }

    // Retrofit 요청 데이터 클래스
    public static class GPTRequest {
        private final String system_input;
        private final String user_input;

        public GPTRequest(String system_input, String user_input) {
            this.system_input = system_input;
            this.user_input = user_input;
        }
    }

    // Retrofit 응답 데이터 클래스
    public static class GPTResponse {
        private String audio_url;

        public String getAudioUrl() {
            return audio_url;
        }
    }
}
