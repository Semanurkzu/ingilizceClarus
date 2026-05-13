package com.example.clarus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

/**
 * Hocam Merhaba,
 * Clarus projemizin ana kontrol merkezi.
 * İsimlendirme standartlarına sadık kalarak (Naming Conventions)
 * tüm modülleri CardView yapısı üzerinden dinamik hale getirdik.
 */
public class MainActivity extends AppCompatActivity {

    // Clarus Modül Tanımlamaları
    private CardView cardKelimeEkle, cardSinav, cardAnaliz, cardWordle, cardLLM, cardAyarlar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hocam, Single Responsibility gereği metotları ayrıştırdık.
        clarusBilesenleriniBagla();
        tiklamaOlaylariniYonet();
    }

    /**
     * XML tarafındaki CardView bileşenlerini Java referanslarına bağlar.
     */
    private void clarusBilesenleriniBagla() {
        cardKelimeEkle = findViewById(R.id.card_kelime_ekle);
        cardSinav      = findViewById(R.id.card_sinav);
        cardAnaliz     = findViewById(R.id.card_analiz);
        cardWordle     = findViewById(R.id.card_wordle);
        cardLLM        = findViewById(R.id.card_llm);
        cardAyarlar    = findViewById(R.id.card_ayarlar);
    }

    /**
     * Tüm modüllerin tıklama olaylarını (Click Listeners) yönetir.
     * Hocam, kod tekrarını (Duplicate Code) önlemek için merkezi yönetim sağladık.
     */
    private void tiklamaOlaylariniYonet() {

        // 1. Kelime Ekleme Modülü
        cardKelimeEkle.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, KelimeEkleActivity.class);
            startActivity(intent);
        });

        // 2. Sınav Modülü
        cardSinav.setOnClickListener(v -> {
            // Gelecekteki SinavActivity için hazır yapı
            // startActivity(new Intent(MainActivity.this, SinavActivity.class));
        });

        // 3. LLM (Yapay Zeka) Modülü
        cardLLM.setOnClickListener(v -> {
            // Hocam, burada projemizin AI bacağını çalıştıracağız.
            // startActivity(new Intent(MainActivity.this, AIActivity.class));
        });

        // 4. Wordle Modülü
        cardWordle.setOnClickListener(v -> {
            // Oyunlaştırma (Gamification) ekranına geçiş
        });

        // 5. Analiz Raporu
        cardAnaliz.setOnClickListener(v -> {
            // SQLite verilerinin raporlandığı ekran
        });
    }
}