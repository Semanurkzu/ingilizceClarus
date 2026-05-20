package com.example.clarus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.TimeUnit; // ⚡ ZAMAN AŞIMINI ÖNLEMEK İÇİN EKLENDİ

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StoryChainActivity extends AppCompatActivity {

    private EditText etWord1, etWord2, etWord3, etWord4, etWord5;
    private Button btnCreateStory;
    private TextView tvStoryResult;
    private ImageView ivGeneratedImage;


    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private final String GEMINI_API_KEY = "AIzaSyBOZCYcUg4JMUsPjZ_aToi3f5_srndtFbM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_chain);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("AI Word Chain");
        }

        etWord1 = findViewById(R.id.et_word1);
        etWord2 = findViewById(R.id.et_word2);
        etWord3 = findViewById(R.id.et_word3);
        etWord4 = findViewById(R.id.et_word4);
        etWord5 = findViewById(R.id.et_word5);
        btnCreateStory = findViewById(R.id.btn_create_story);
        tvStoryResult = findViewById(R.id.tv_story_result);
        ivGeneratedImage = findViewById(R.id.iv_generated_image);

        ivGeneratedImage.setVisibility(View.GONE);

        btnCreateStory.setOnClickListener(v -> checkChainAndGenerate());
    }

    private void checkChainAndGenerate() {
        String w1 = etWord1.getText().toString().trim().toUpperCase(Locale.ENGLISH);
        String w2 = etWord2.getText().toString().trim().toUpperCase(Locale.ENGLISH);
        String w3 = etWord3.getText().toString().trim().toUpperCase(Locale.ENGLISH);
        String w4 = etWord4.getText().toString().trim().toUpperCase(Locale.ENGLISH);
        String w5 = etWord5.getText().toString().trim().toUpperCase(Locale.ENGLISH);

        if (w1.isEmpty() || w2.isEmpty() || w3.isEmpty() || w4.isEmpty() || w5.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm kelimeleri doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isChainValid = (w1.charAt(w1.length() - 1) == w2.charAt(0)) &&
                (w2.charAt(w2.length() - 1) == w3.charAt(0)) &&
                (w3.charAt(w3.length() - 1) == w4.charAt(0)) &&
                (w4.charAt(w4.length() - 1) == w5.charAt(0));

        if (isChainValid) {
            generateStoryAndImage(w1, w2, w3, w4, w5);
        } else {
            Toast.makeText(this, "Kelimeler zincir kuralına uymuyor! Birinin son harfi, diğerinin ilk harfi olmalı.", Toast.LENGTH_LONG).show();
        }
    }

    private void generateStoryAndImage(String w1, String w2, String w3, String w4, String w5) {
        btnCreateStory.setEnabled(false);
        btnCreateStory.setText("Yapay Zeka Düşünüyor...");
        tvStoryResult.setVisibility(View.VISIBLE);
        tvStoryResult.setText("Hikaye ve görsel yapay zeka tarafından oluşturuluyor...\nLütfen 10-15 saniye bekleyin.");
        ivGeneratedImage.setVisibility(View.GONE);

        String promptText = "Aşağıdaki 5 İngilizce kelimeyi kullanarak Türkçe, kısa ve yaratıcı bir hikaye yaz. " +
                "İngilizce kelimeleri hikayenin içinde orijinal haliyle ve BÜYÜK HARFLERLE kullan. " +
                "Kelimeler: " + w1 + ", " + w2 + ", " + w3 + ", " + w4 + ", " + w5 + ". En fazla 4-5 cümle olsun.";

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject contentObj = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject partObj = new JSONObject();

            partObj.put("text", promptText);
            partsArray.put(partObj);
            contentObj.put("role", "user");
            contentObj.put("parts", partsArray);
            contentsArray.put(contentObj);
            jsonBody.put("contents", contentsArray);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

            String urlGemini = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + GEMINI_API_KEY;

            Request requestGemini = new Request.Builder().url(urlGemini).post(body).build();

            client.newCall(requestGemini).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> resetUI("Hikaye Hatası: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseData = response.body() != null ? response.body().string() : "";
                        if (response.isSuccessful() && !responseData.isEmpty()) {
                            JSONObject resJson = new JSONObject(responseData);
                            String story = resJson.getJSONArray("candidates").getJSONObject(0)
                                    .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");

                            runOnUiThread(() -> tvStoryResult.setText(story));

                            // Görsel üretimini tetikle
                            fetchImageFromPollinations(w1, w2, w3);
                        } else {
                            // Hata durumunda direkt Google'ın gönderdiği gerçek mesajı ekrana basıyoruz
                            runOnUiThread(() -> resetUI("Sunucu Hatası (Kod " + response.code() + "): " + responseData));
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> resetUI("Veri okuma hatası: " + e.getMessage()));
                    }
                }
            });

        } catch (Exception e) {
            resetUI("İstek hazırlanamadı: " + e.getMessage());
        }
    }

    private void fetchImageFromPollinations(String w1, String w2, String w3) {
        new Thread(() -> {
            try {
                String rawImagePrompt = "A magical cinematic illustration of " + w1 + ", " + w2 + " and " + w3 + ", highly detailed, fantasy art";
                String encodedPrompt = URLEncoder.encode(rawImagePrompt, "UTF-8");
                String imageUrl = "https://image.pollinations.ai/prompt/" + encodedPrompt;

                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                final Bitmap generatedBitmap = BitmapFactory.decodeStream(input);
                connection.disconnect();

                runOnUiThread(() -> {
                    ivGeneratedImage.setVisibility(View.VISIBLE);
                    if (generatedBitmap != null) {
                        ivGeneratedImage.setImageBitmap(generatedBitmap);
                    } else {
                        ivGeneratedImage.setImageResource(android.R.drawable.ic_dialog_alert);
                        Toast.makeText(StoryChainActivity.this, "Görsel yüklenemedi.", Toast.LENGTH_SHORT).show();
                    }
                    btnCreateStory.setEnabled(true);
                    btnCreateStory.setText("✨ YENİDEN ÜRET");
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    ivGeneratedImage.setImageResource(android.R.drawable.ic_dialog_alert);
                    ivGeneratedImage.setVisibility(View.VISIBLE);
                    btnCreateStory.setEnabled(true);
                    btnCreateStory.setText("✨ YENİDEN ÜRET");
                });
            }
        }).start();
    }

    private void resetUI(String errorMsg) {
        tvStoryResult.setText(errorMsg);
        btnCreateStory.setEnabled(true);
        btnCreateStory.setText("✨ TEKRAR DENE");
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}