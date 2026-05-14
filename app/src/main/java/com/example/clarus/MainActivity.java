package com.example.clarus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * Hocam Merhaba,
 * Clarus projemizin ana kontrol merkezi (Dashboard).
 * İlk açılışta veritabanı kurulum işlemleri AcilisEkraniActivity'ye taşınmıştır.
 * Bu sınıf sadece arayüz bileşenlerini ve modüller arası geçişleri yönetir.
 */
public class MainActivity extends AppCompatActivity {

    private CardView cardKelimeEkle, cardSinav, cardAnaliz, cardWordle, cardLLM, cardAyarlar;
    private Button btnKelimeOgrenMain; // YENİ EKLENEN BUTON
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

        // YENİ BUTONUN EŞLEŞTİRİLMESİ
        btnKelimeOgrenMain = findViewById(R.id.btnKelimeOgrenMain);
    }

    private void tiklamaOlaylariniYonet() {

        // YENİ MODÜLE GEÇİŞ TETİKLEYİCİSİ
        btnKelimeOgrenMain.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, KelimeOgrenActivity.class)));

        cardKelimeEkle.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, KelimeEkleActivity.class)));

        cardSinav.setOnClickListener(v ->
                Toast.makeText(this, "Kelime öğrenme modülü başlatılıyor...", Toast.LENGTH_SHORT).show());

        cardLLM.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, StoryChainActivity.class)));

        cardWordle.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WordleActivity.class)));

        cardAnaliz.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ReportActivity.class)));

        cardAyarlar.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }
}