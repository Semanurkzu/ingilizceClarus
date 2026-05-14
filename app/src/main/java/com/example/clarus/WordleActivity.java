package com.example.clarus;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WordleActivity extends AppCompatActivity {

    private String gizliKelime;
    private int denemeSayisi = 0;
    private VeritabaniYardimcisi vt;

    private TextView tvGameStatus;
    private LinearLayout llGameBoard;
    private EditText etTahmin;
    private Button btnTahminEt;

    // 5 satır (deneme hakkı) ve 5 sütun (harf) tutacak matris
    private TextView[][] kutuMatrisi = new TextView[5][5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordle);

        etTahmin = findViewById(R.id.etTahmin);
        btnTahminEt = findViewById(R.id.btnTahminEt);
        tvGameStatus = findViewById(R.id.tvGameStatus);
        llGameBoard = findViewById(R.id.llGameBoard);

        vt = new VeritabaniYardimcisi(this);

        // 1. EKRAN AÇILIR AÇILMAZ BOŞ KUTULARI ÇİZ
        bosTahtayiOlustur();

        // 2. KELİMEYİ VERİTABANINDAN ÇEK
        kelimeyiBelirle();

        btnTahminEt.setOnClickListener(v -> {
            String tahmin = etTahmin.getText().toString().trim().toUpperCase();

            // Sadece 5 harfli kelimeleri kabul et
            if (tahmin.length() != 5) {
                Toast.makeText(this, "Lütfen tam 5 harfli bir kelime yazın!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Harflerin doğruluğunu kontrol et
            int[] sonuclar = WordleManager.kontrolEt(tahmin, gizliKelime);

            // 3. İLGİLİ SATIRDAKİ KUTULARI GÜNCELLE
            satiriGuncelle(tahmin, sonuclar, denemeSayisi);

            denemeSayisi++;

            // Oyun bitiş kontrolleri
            if (tahmin.equals(gizliKelime)) {
                tvGameStatus.setText("Tebrikler! Doğru Kelime: " + gizliKelime);
                oyunuBitir();
            } else if (denemeSayisi >= 5) {
                tvGameStatus.setText("Hakkınız Bitti! Doğru Kelime: " + gizliKelime);
                oyunuBitir();
            } else {
                tvGameStatus.setText((5 - denemeSayisi) + " hakkınız kaldı.");
            }

            // Yeni tahmin için kutuyu temizle
            etTahmin.setText("");
        });
    }

    private void kelimeyiBelirle() {
        gizliKelime = vt.getWordleKelime(5);

        // Eğer veritabanı boşsa veya hata varsa çökmemesi için güvenlik ağı
        if (gizliKelime == null || gizliKelime.isEmpty() || gizliKelime.length() != 5) {
            gizliKelime = "APPLE";
            Toast.makeText(this, "DB'den kelime alınamadı, yedek kelime devrede.", Toast.LENGTH_SHORT).show();
        }

        tvGameStatus.setText("Oyun Başladı! 5 Hakkınız Var.");
    }

    private void bosTahtayiOlustur() {
        llGameBoard.removeAllViews();

        for (int satir = 0; satir < 5; satir++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER);
            row.setPadding(0, 8, 0, 8);

            for (int sutun = 0; sutun < 5; sutun++) {
                TextView tvKutu = new TextView(this);
                tvKutu.setText("");
                tvKutu.setTextSize(28);
                tvKutu.setTextColor(Color.WHITE);
                tvKutu.setGravity(Gravity.CENTER);

                // Boş kutuların varsayılan rengi (Açık Gri)
                tvKutu.setBackgroundColor(Color.parseColor("#CCCCCC"));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(130, 130);
                params.setMargins(8, 8, 8, 8);
                tvKutu.setLayoutParams(params);

                // Kutuyu matrise kaydet ki sonradan içini doldurabilelim
                kutuMatrisi[satir][sutun] = tvKutu;
                row.addView(tvKutu);
            }
            llGameBoard.addView(row);
        }
    }

    private void satiriGuncelle(String tahmin, int[] sonuclar, int aktifSatir) {
        for (int i = 0; i < 5; i++) {
            TextView tvKutu = kutuMatrisi[aktifSatir][i];
            tvKutu.setText(String.valueOf(tahmin.charAt(i)));

            // Renkleri Clarus temasına göre değiştir
            if (sonuclar[i] == WordleManager.RENK_YESIL) {
                tvKutu.setBackgroundColor(Color.parseColor("#7D2039")); // Doğru Yer: Bordo
            } else if (sonuclar[i] == WordleManager.RENK_SARI) {
                tvKutu.setBackgroundColor(Color.parseColor("#FFD700")); // Yanlış Yer: Altın/Sarı
            } else {
                tvKutu.setBackgroundColor(Color.parseColor("#3A3A3C")); // Yok: Koyu Gri
            }
        }
    }

    private void oyunuBitir() {
        btnTahminEt.setEnabled(false);
        etTahmin.setEnabled(false);
    }
}