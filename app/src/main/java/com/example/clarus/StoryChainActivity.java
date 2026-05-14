package com.example.clarus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class StoryChainActivity extends AppCompatActivity {

    private EditText etWord1, etWord2, etWord3, etWord4, etWord5;
    private TextView tvHikaye;
    private ImageView ivGorsel;
    private Button btnOlustur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_chain);

        // Arayüz elemanlarını bağlıyoruz
        etWord1 = findViewById(R.id.etWord1);
        etWord2 = findViewById(R.id.etWord2);
        etWord3 = findViewById(R.id.etWord3);
        etWord4 = findViewById(R.id.etWord4);
        etWord5 = findViewById(R.id.etWord5);

        tvHikaye = findViewById(R.id.tvHikaye);
        ivGorsel = findViewById(R.id.ivGorsel);
        btnOlustur = findViewById(R.id.btnOlustur);

        // Gemini API Bağlantısı
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDE78DxVjG-jjsl-izFEf4hQ2MUtRMNZss");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        btnOlustur.setOnClickListener(v -> {
            // Kullanıcının yazdığı kelimeleri alıyoruz
            String w1 = etWord1.getText().toString().trim();
            String w2 = etWord2.getText().toString().trim();
            String w3 = etWord3.getText().toString().trim();
            String w4 = etWord4.getText().toString().trim();
            String w5 = etWord5.getText().toString().trim();

            // Herhangi bir kutu boş bırakılmış mı diye kontrol ediyoruz
            if (w1.isEmpty() || w2.isEmpty() || w3.isEmpty() || w4.isEmpty() || w5.isEmpty()) {
                Toast.makeText(this, "Lütfen 5 kelimenin hepsini doldurun!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnOlustur.setEnabled(false);

            // Kullanıcının seçtiği kelimeleri birleştiriyoruz
            String[] kelimeler = {w1, w2, w3, w4, w5};
            String secilenKelimelerMetni = String.join(", ", kelimeler);

            tvHikaye.setText("Gemini hikayeyi ve promptu yazıyor, bekle...");

            // Promptun içine KULLANICININ YAZDIĞI kelimeleri gömüyoruz
            String prompt = "Şu 5 kelimeyi kullanarak Türkçe etkileyici bir kısa hikaye yaz: "
                    + secilenKelimelerMetni
                    + ". Ayrıca bu hikayeyi betimleyen bir görsel için detaylı bir İngilizce prompt oluştur. "
                    + "Yanıtı SADECE şu JSON formatında ver, başka hiçbir kelime ekleme: {\"hikaye\": \"...\", \"prompt\": \"...\"}";

            Content content = new Content.Builder().addText(prompt).build();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    try {
                        String gelenMetin = result.getText();

                        // KESİN ÇÖZÜM (CIMBIZ YÖNTEMİ): Android Studio'nun kopyalarken hata verdiği
                        // o lanet olası tırnak işaretleriyle (```) hiç uğraşmıyoruz.
                        // Metnin içindeki ilk süslü parantezi bulup son süslü paranteze kadar olan kısmı çekiyoruz.
                        int baslangic = gelenMetin.indexOf("{");
                        int bitis = gelenMetin.lastIndexOf("}");

                        if (baslangic != -1 && bitis != -1) {
                            String finalJson = gelenMetin.substring(baslangic, bitis + 1);

                            JSONObject jsonObject = new JSONObject(finalJson);
                            String uretilenHikaye = jsonObject.getString("hikaye");
                            String uretilenPrompt = jsonObject.getString("prompt");

                            runOnUiThread(() -> {
                                tvHikaye.setText("Kullanıcının Kelimeleri: " + secilenKelimelerMetni + "\n\n" + uretilenHikaye);
                                tvHikaye.append("\n\n(Görsel çiziliyor, lütfen bekle...)");
                            });

                            // JSON başarıyla çözüldüyse görseli üretmeye başla
                            gorseliOlusturVeKaydet(uretilenPrompt);

                        } else {
                            runOnUiThread(() -> {
                                tvHikaye.setText("Yapay Zeka uygun formatta yanıt vermedi, lütfen tekrar deneyin.");
                                btnOlustur.setEnabled(true);
                            });
                        }

                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            tvHikaye.setText("JSON Çözümleme Hatası: " + e.getMessage());
                            btnOlustur.setEnabled(true);
                        });
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    runOnUiThread(() -> {
                        tvHikaye.setText("Bağlantı hatası: " + t.getMessage());
                        btnOlustur.setEnabled(true);
                    });
                }
            }, this.getMainExecutor());
        });
    }

    private void gorseliOlusturVeKaydet(String ingilizcePrompt) {
        new Thread(() -> {
            try {
                // İngilizce promptu URL formatına uygun hale getiriyoruz
                String encodedPrompt = URLEncoder.encode(ingilizcePrompt, "UTF-8");
                String urlString = "https://image.pollinations.ai/prompt/" + encodedPrompt + "?width=512&height=512&nologo=true";

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                // Klasöre Kaydetme İşlemi
                File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File imageFile = new File(directory, "Clarus_Story_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                runOnUiThread(() -> {
                    ivGorsel.setImageBitmap(bitmap);
                    tvHikaye.append("\n\n✓ Görsel başarıyla telefona kaydedildi!");
                    Toast.makeText(StoryChainActivity.this, "Görsel Kaydedildi: " + imageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    btnOlustur.setEnabled(true); // İşlem bitince butonu tekrar aktif et
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(StoryChainActivity.this, "Görsel indirilemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvHikaye.append("\n\n(Görsel oluşturma hatası)");
                    btnOlustur.setEnabled(true);
                });
            }
        }).start();
    }
}
