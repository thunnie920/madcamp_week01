package com.example.week01_project3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class FanActivity3 implements View.OnClickListener {

    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private GPTApi api;
    private ConstraintLayout layout;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    private static final String BASE_URL = "http://143.248.232.67:5000/";

    private final Context context;
    private final boolean isHot;

    // ★ 생성자 오버로딩
    public FanActivity3(Context context, boolean isHot) {
        this.context = context;
        this.isHot = isHot;

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(GPTApi.class);
    }


    private void initializeSpeechRecognition() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
            return;
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(context, "말씀하세요...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                Toast.makeText(context, "음성 인식 오류 발생!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    // 사용자가 말한 문장 matches.get(0)을 서버로 전송
                    sendToServer(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        startListening();
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent);
    }

    private void sendToServer(String userInput) {
        // isHot 상태에 따라 다른 system_input 사용
        String systemInput;
        if (isHot) {
            systemInput = "넌 지금 너무 더워서 까칠한 상태야. 날카롭고 앙칼지게 대답해.";
            Toast.makeText(context, "hot", Toast.LENGTH_SHORT).show();
        } else {
            systemInput = "넌 지금 시원해진 상태야. 다정하고 부드럽게 대답해.";
            Toast.makeText(context, "cold", Toast.LENGTH_SHORT).show();
        }

        // 이제 systemInput을 포함하여 서버로 전송
        GPTRequest request = new GPTRequest(systemInput, userInput);

        api.chat(request).enqueue(new Callback<GPTResponse>() {
            @Override
            public void onResponse(Call<GPTResponse> call, Response<GPTResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GPTResponse gptResponse = response.body();

                    // 1) 텍스트는 원하는 대로 쓰세요
                    String text = gptResponse.getText();
                    Toast.makeText(context, "GPT 응답: " + text, Toast.LENGTH_SHORT).show();

                    // 2) 서버가 준 audio_url
                    String audioUrl = gptResponse.getAudio_url();

                    String fullAudioUrl = BASE_URL + audioUrl;

                    // 3) MediaPlayer로 해당 mp3 재생
                    playMp3FromUrl(fullAudioUrl);
                } else {
                    Toast.makeText(context, "응답 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GPTResponse> call, Throwable t) {
                Toast.makeText(context, "네트워크 오류 발생! " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playMp3FromUrl(String mp3Url) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(mp3Url); // URL 세팅
            mediaPlayer.setOnPreparedListener(mp -> {
                // 준비 완료되면 재생
                mp.start();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(context, "MediaPlayer 오류 발생", Toast.LENGTH_SHORT).show();
                return false;
            });
            // 비동기로 준비
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "mp3 재생 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void speak(String text) {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.KOREAN);
                    // 초기화 성공 로그
                    Log.d("TTS", "TTS init SUCCESS");
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1");
                } else {
                    // 초기화 실패 로그
                    Log.e("TTS", "TTS init FAILED, status = " + status);
                }
            });
        } else {
            Log.d("TTS", "TTS is already initialized");
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1");
        }
    }


    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onClick(View v) {
        initializeSpeechRecognition();
    }

    public interface GPTApi {
        @POST("/chat")
        Call<GPTResponse> chat(@Body GPTRequest request);
    }

    public static class GPTRequest {
        private String system_input;
        private String user_input;

        public GPTRequest(String system_input, String user_input) {
            this.system_input = system_input;
            this.user_input = user_input;
        }
    }

    public static class GPTResponse {
        private String text;
        private String audio_url;
        public String getText() {
            return text;
        }
        public String getAudio_url() {
            return audio_url;
        }
    }


}
