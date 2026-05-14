package com.example.clarus;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Hocam Merhaba,
 * Wordle modülümüzde artık dairesel birleştirme yerine standart
 * klavye ve tahminleme mekanizmasını kullanıyoruz.
 * Kelimeler VeritabaniYardimcisi (Versiyon 6) üzerinden dinamik olarak çekilmektedir.
 */
public class WordleActivity extends AppCompatActivity {

    private String gizliKelime;
    private int denemeSayisi = 0;
    private VeritabaniYardimcisi vt;
    private TextView tvGameStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordle);

        vt = new VeritabaniYardimcisi(this);

        // 5 bin kelimelik dev listeden 5 harfli rastgele bir kelime seçiyoruz
        gizliKelime = vt.getWordleKelime(5);

        // Eğer veritabanı boşsa hata vermemesi için bir varsayılan atayalım
        if (gizliKelime == null) {
            gizliKelime = "CLARU";
        }

        EditText etTahmin = findViewById(R.id.etTahmin);
        Button btnTahminEt = findViewById(R.id.btnTahminEt);
        tvGameStatus = findViewById(R.id.tvGameStatus);

        btnTahminEt.setOnClickListener(v -> {
            String tahmin = etTahmin.getText().toString().trim().toUpperCase();

            if (tahmin.length() != 5) {
                Toast.makeText(this, "Lütfen 5 harfli bir kelime girin", Toast.LENGTH_SHORT).show();
                return;
            }

            denemeSayisi++;

            // Kelimeyi kontrol ediyoruz (Yeşil, Sarı, Gri mantığı)
            // WordleManager sınıfındaki algoritmayı kullanıyoruz
            int[] sonuclar = WordleManager.kontrolEt(tahmin, gizliKelime);

            if (tahmin.equals(gizliKelime)) {
                tvGameStatus.setText("Tebrikler! Kelime: " + gizliKelime);
                Toast.makeText(this, "Mükemmel! Doğru tahmin.", Toast.LENGTH_LONG).show();

                // Başarı durumunda veritabanındaki istatistiği güncelliyoruz
                // Not: Kelime ID'sini bulup vt.istatistikGuncelle çağrılabilir

                finish();
            } else if (denemeSayisi >= 6) {
                tvGameStatus.setText("Oyun Bitti! Doğru: " + gizliKelime);
                Toast.makeText(this, "Hakkınız doldu.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                tvGameStatus.setText((6 - denemeSayisi) + " hakkınız kaldı.");
            }

            etTahmin.setText("");
        });
    }
}