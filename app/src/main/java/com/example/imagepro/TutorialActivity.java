package com.example.imagepro;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

public class TutorialActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    private TextToSpeech textToSpeech;
    private Vibrator vibrator;

    long[] happyVibrationPattern = {0, 100, 200, 300};
    long[] sadVibrationPattern = {0, 300, 100, 300};
    long[] angryVibrationPattern = {0, 200, 200, 200, 200};
    long[] disgustVibrationPattern = {0, 500, 100, 500};
    long[] surpriseVibrationPattern = {0, 400, 400};

    private String[] emotions = {
            "Welcome to uniface, This tutorial consists of the walkthrough of the uniface app. Uniface scans and detect facial expression of communications and notifies user with unique vibration and loads display for each emotion.",
            "If emotion is detected as happy, a vibration with following pattern will be notified ",
            "For sad the vibration pattern will be",
            "For angry the vibration pattern will be",
            "For disgust the vibration pattern will be",
            "For surprise the vibration pattern will be",
            "and for neutral the vibrations will not be notified.",
            "to began, place the phone to your chest and point the camera towards the direction of the voice.",
            "hope you have a blissfull day"
    };

    private long[][] vibrationPatterns = {
            null,
            happyVibrationPattern,
            sadVibrationPattern,
            angryVibrationPattern,
            disgustVibrationPattern,
            surpriseVibrationPattern,
            null,
            null,
            null,
    };

    private int currentEmotionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        textToSpeech = new TextToSpeech(this, this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "Language not supported");
            } else {
                textToSpeech.setSpeechRate(0.5f);
                speakTextWithEmotion();
            }
        } else {
            Log.e("TextToSpeech", "Initialization failed");
        }
    }

    private void speakTextWithEmotion() {
        if (currentEmotionIndex < emotions.length) {
            String text = emotions[currentEmotionIndex];

            if (textToSpeech != null) {
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");

                textToSpeech.setOnUtteranceCompletedListener(this);
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onUtteranceCompleted(String utteranceId) {
        // Trigger vibration after the text-to-speech is completed
        vibrateWithEmotion();

        // Move to the next emotion
        currentEmotionIndex++;

        // Speak the next emotion or move to the next activity
        if (currentEmotionIndex < emotions.length) {
            speakTextWithEmotion();
        } else {
            moveToNextActivity();
        }
    }

    private void moveToNextActivity() {
        // Start the next activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        // Finish the current activity
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void vibrateWithEmotion() {
        if (currentEmotionIndex < vibrationPatterns.length && vibrator != null) {
            long[] vibrationPattern = vibrationPatterns[currentEmotionIndex];

            if (vibrationPattern != null) {
                VibrationEffect effect = VibrationEffect.createWaveform(vibrationPattern, -1);
                vibrator.vibrate(effect);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
