package com.example.clarus;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class KelimeOgrenActivity extends AppCompatActivity {

    private TextView tvOgrenEng, tvOgrenPhon, tvOgrenTur, tvOgrenEx;
    private Button btnSonrakiKelime;
    private VeritabaniYardimcisi vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelime_ogren);

        tvOgrenEng = findViewById(R.id.tvOgrenEng);
        tvOgrenPhon = findViewById(R.id.tvOgrenPhon);
        tvOgrenTur = findViewById(R.id.tvOgrenTur);
        tvOgrenEx = findViewById(R.id.tvOgrenEx);
        btnSonrakiKelime = findViewById(R.id.btnSonrakiKelime);

        vt = new VeritabaniYardimcisi(this);

        // Ekran ilk açıldığında bir kelime yükle
        yeniKelimeGetir();

        // Butona tıklandığında yeni kelime getir
        btnSonrakiKelime.setOnClickListener(v -> yeniKelimeGetir());
    }

    private void yeniKelimeGetir() {
        KelimeModel kelime = vt.rastgeleOgrenmeKelimesiGetir();

        if (kelime != null) {
            tvOgrenEng.setText(kelime.getIngilizce().toUpperCase());
            tvOgrenTur.setText(kelime.getTurkce());

            // Eğer okunuş veya örnek cümle boşsa gizle, doluysa göster
            if (kelime.getOkunus().isEmpty()) {
                tvOgrenPhon.setText("");
            } else {
                tvOgrenPhon.setText(kelime.getOkunus());
            }

            if (kelime.getOrnekCumle().isEmpty()) {
                tvOgrenEx.setText("Örnek cümle bulunamadı.");
            } else {
                tvOgrenEx.setText(kelime.getOrnekCumle());
            }
        } else {
            Toast.makeText(this, "Veritabanında kelime bulunamadı!", Toast.LENGTH_SHORT).show();
            tvOgrenEng.setText("HATA");
            tvOgrenTur.setText("Veri Yok");
        }
    }
}