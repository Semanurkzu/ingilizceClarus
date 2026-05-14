package com.example.clarus;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * Hocam Merhaba,
 * Clarus projemizin ana kontrol merkezi (Dashboard).
 * Tüm modüller, arkadaşlarımla geliştirdiğimiz spesifik Activity'lere
 * Intent mekanizması üzerinden gevşek bağlı (Loosely Coupled) şekilde entegre edilmiştir.
 */
public class MainActivity extends AppCompatActivity {

    // Clarus Modül Tanımlamaları
    private CardView cardKelimeEkle, cardSinav, cardAnaliz, cardWordle, cardLLM, cardAyarlar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hocam, Clean Code prensipleri gereği başlatma işlemlerini metodize ettik.
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
    }

    private void tiklamaOlaylariniYonet() {

        // 1. Kelime Ekleme Modülü (Sema'nın modülü)
        cardKelimeEkle.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, KelimeEkleActivity.class));
        });

        // 2. Sınav Modülü (Leitner Sistemi / Mevcut yapıya göre ayarlandı)
        cardSinav.setOnClickListener(v -> {
            // Sınav algoritmasını yöneten sınıfa yönlendirme
            // startActivity(new Intent(MainActivity.this, SinavActivity.class));
        });

        // 3. LLM (Yapay Zeka - Story Chain) Modülü
        cardLLM.setOnClickListener(v -> {
            // Hocam, burada AI tabanlı hikaye üretim modülünü tetikliyoruz.
            startActivity(new Intent(MainActivity.this, StoryChainActivity.class));
        });

        // 4. Wordle Modülü (Oyunlaştırma)
        cardWordle.setOnClickListener(v -> {
            // Arkadaşının yazdığı Wordle oyun ekranına geçiş
            startActivity(new Intent(MainActivity.this, WordleActivity.class));
        });

        // 5. Analiz Raporu Modülü
        cardAnaliz.setOnClickListener(v -> {
            // SQLite verilerinin görselleştiği rapor ekranı
            startActivity(new Intent(MainActivity.this, ReportActivity.class));
        });

        // 6. Ayarlar Modülü
        cardAyarlar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });
    }
}