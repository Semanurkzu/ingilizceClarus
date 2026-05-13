package com.example.clarus;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class StoryChainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_chain);

        TextView tvHikaye = findViewById(R.id.tvHikaye);
        ImageView ivGorsel = findViewById(R.id.ivGorsel);
        Button btnOlustur = findViewById(R.id.btnOlustur);

        // Story 7: Öğrenilen kelimelerden bir Word Chain oluşturma
        // Bu kelimeler ileride veritabanından dinamik olarak çekilecek.
        String[] kelimeler = {"Brain", "Night", "Tiger", "Robin", "Noble"};

        // Gemini Modeli Tanımlama (Senin API Anahtarın Entegre Edildi)
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyB0uxe96RqG3L47d1_d_Sq9QWnvYlFswFQ");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        btnOlustur.setOnClickListener(v -> {
            tvHikaye.setText("Hikaye oluşturuluyor, lütfen bekleyin...");

            // LLM'den hikaye ve görsel betimlemesi isteme
            String prompt = "Şu 5 kelimeyi kullanarak Türkçe etkileyici bir kısa hikaye yaz: "
                    + String.join(", ", kelimeler)
                    + ". Ayrıca bu hikayeyi betimleyen bir görsel için detaylı bir İngilizce prompt oluştur.";

            Content content = new Content.Builder().addText(prompt).build();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    runOnUiThread(() -> {
                        // Üretilen metni ekrana yazdırıyoruz
                        tvHikaye.setText(result.getText());

                        // Not: Görsel oluşturma motoru (Nano Banana 2) entegrasyonu
                        // tasarım aşamasında bu noktaya eklenecek.
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    runOnUiThread(() -> tvHikaye.setText("Bağlantı hatası: " + t.getMessage()));
                }
            }, this.getMainExecutor());
        });
    }
}