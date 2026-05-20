package com.example.clarus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * Hocam Merhaba,
 * Clarus projemizin ana kontrol merkezi (Dashboard).
 * Tüm modüller arası geçişler ve bilimsel tekrar motoru (SinavActivity)
 * buradan tetiklenir.
 */
public class MainActivity extends AppCompatActivity {

    private CardView cardKelimeEkle, cardSinav, cardAnaliz, cardWordle, cardLLM, cardAyarlar,card_kelimeler;
    private Button btnKelimeOgrenMain;
    private VeritabaniYardimcisi vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vt = new VeritabaniYardimcisi(this);

        clarusBilesenleriniBagla();
        tiklamaOlaylariniYonet();
    }

    private void clarusBilesenleriniBagla() {
        cardKelimeEkle = findViewById(R.id.card_kelime_ekle);
        cardSinav      = findViewById(R.id.card_sinav);
        cardAnaliz     = findViewById(R.id.card_analiz);
        cardWordle     = findViewById(R.id.card_wordle);
        cardLLM        = findViewById(R.id.card_llm);
        cardAyarlar    = findViewById(R.id.card_ayarlar);
        btnKelimeOgrenMain = findViewById(R.id.btnKelimeOgrenMain);
        card_kelimeler = findViewById(R.id.card_kelimeler);
    }

    private void tiklamaOlaylariniYonet() {

        // Kelime Öğrenme Modülü Geçişi
        btnKelimeOgrenMain.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, KelimeOgrenActivity.class)));

        // Manuel Kelime Ekleme Modülü Geçişi
        cardKelimeEkle.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, KelimeEkleActivity.class)));

        // SINAV MODÜLÜ GEÇİŞİ (DÜZELTİLDİ!)
        cardSinav.setOnClickListener(v -> {
            // Kullanıcıya bilgi veriyoruz
            Toast.makeText(this, "Bilimsel Tekrar Sınavı Başlatılıyor...", Toast.LENGTH_SHORT).show();
            // SinavActivity'ye geçişi tetikliyoruz
            Intent intent = new Intent(MainActivity.this, SinavActivity.class);
            startActivity(intent);
        });

        // Yapay Zeka / Hikaye Modülü Geçişi
        cardLLM.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, StoryChainActivity.class)));

        // Wordle Oyunu Geçişi
        cardWordle.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WordleActivity.class)));

        // Rapor ve Analiz Modülü Geçişi
        cardAnaliz.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ReportActivity.class)));

        // Ayarlar Modülü Geçişi
        cardAyarlar.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        // DÜZELTİLMİŞ HALİ (Hedefi gerçek ekrana çevirdik)
        card_kelimeler.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, KelimeOgrenActivity.class)));
    }
}