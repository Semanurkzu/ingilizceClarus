package com.example.clarus;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WordleActivity extends AppCompatActivity {

    // Bu kelime normalde veritabanından, kullanıcının öğrendiği 5 harfli kelimelerden gelecek
    private String gizliKelime = "APPLE";
    private int denemeSayisi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordle);

        EditText etTahmin = findViewById(R.id.etTahmin);
        Button btnTahminEt = findViewById(R.id.btnTahminEt);

        btnTahminEt.setOnClickListener(v -> {
            String tahmin = etTahmin.getText().toString().trim().toUpperCase();

            // Giriş kontrolü: Kelime tam 5 harfli olmalı
            if (tahmin.length() != 5) {
                Toast.makeText(this, "Lütfen 5 harfli bir kelime girin", Toast.LENGTH_SHORT).show();
                return;
            }

            denemeSayisi++;

            // Kelimeyi kontrol ediyoruz (Yeşil, Sarı, Gri mantığı)
            int[] sonuclar = WordleManager.kontrolEt(tahmin, gizliKelime);

            // Kullanıcı doğru bildiyse
            if (tahmin.equalsIgnoreCase(gizliKelime)) {
                Toast.makeText(this, "Tebrikler! Kelimeyi doğru tahmin ettiniz.", Toast.LENGTH_LONG).show();
                finish(); // Oyunu bitir ve kapat
            }
            // 6 hakkı da dolduysa
            else if (denemeSayisi >= 6) {
                Toast.makeText(this, "Hakkınız bitti! Doğru kelime: " + gizliKelime, Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, (6 - denemeSayisi) + " hakkınız kaldı.", Toast.LENGTH_SHORT).show();
            }

            etTahmin.setText(""); // Yeni tahmin için kutuyu temizle
        });
    }
}