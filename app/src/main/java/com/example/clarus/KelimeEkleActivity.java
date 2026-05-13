package com.example.clarus;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

/**
 * Hocam Merhaba,
 * Kelime ekleme modülünde SQLite veritabanı entegrasyonunu sağladık.
 * Kullanıcıdan alınan verileri VeritabaniYardimcisi sınıfı üzerinden
 * kalıcı depolamaya (Persistence Storage) aktarıyoruz.
 */
public class KelimeEkleActivity extends AppCompatActivity {

    // Sema, XML'deki EditText'lerle uyumlu olması için tipini EditText yaptık
    private EditText et_ingilizce_kelime, et_turkce_anlam;
    private AppCompatButton btn_kelime_kaydet;
    private VeritabaniYardimcisi vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelime_ekle);

        // Veritabanı bağlantısını başlatıyoruz
        vt = new VeritabaniYardimcisi(this);

        // Bileşenleri ID'leri ile bağlıyoruz
        arayuzBilesenleriniBagla();

        // Kaydetme işlemini başlatıyoruz
        kaydetmeIsleminiYonet();
    }

    private void arayuzBilesenleriniBagla() {
        et_ingilizce_kelime = findViewById(R.id.et_ingilizce_kelime);
        et_turkce_anlam = findViewById(R.id.et_turkce_anlam);
        btn_kelime_kaydet = findViewById(R.id.btn_kelime_kaydet);
    }

    private void kaydetmeIsleminiYonet() {
        btn_kelime_kaydet.setOnClickListener(v -> {
            String ingilizce = et_ingilizce_kelime.getText().toString().trim();
            String turkce = et_turkce_anlam.getText().toString().trim();

            // Boş veri kontrolü (Validation)
            if (!ingilizce.isEmpty() && !turkce.isEmpty()) {

                // Sema, veritabanı sınıfındaki metodunu çağırıyoruz
                // Metodunun adının "kelimeEkle" olduğundan emin ol
                vt.kelimeEkle(ingilizce, turkce);

                Toast.makeText(this, "Kelime başarıyla eklendi kankim!", Toast.LENGTH_SHORT).show();

                // Formu temizle
                et_ingilizce_kelime.setText("");
                et_turkce_anlam.setText("");

                // Klavyeyi odak noktasından çek
                et_ingilizce_kelime.clearFocus();

            } else {
                Toast.makeText(this, "Sema, alanları boş bırakamazsın!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}