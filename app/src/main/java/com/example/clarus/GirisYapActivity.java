package com.example.clarus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Hocam Merhaba,
 * Giriş sistemini Veritabanı Versiyon 6 ile tam entegre hale getirdik.
 * SharedPreferences kullanarak 'Beni Hatırla' mekanizmasını devreye aldık.
 */
public class GirisYapActivity extends AppCompatActivity {

    private EditText etKullaniciAdi, etSifre;
    private Button btnGiris;
    private TextView tvKayitOl;
    private VeritabaniYardimcisi vt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Önce oturum kontrolü: Kullanıcı daha önce girdi mi?
        sharedPreferences = getSharedPreferences("ClarusLogin", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            gitMain();
        }

        setContentView(R.layout.activity_giris_yap);

        // Veritabanı bağlantısı
        vt = new VeritabaniYardimcisi(this);

        // Görünümleri bağlama
        etKullaniciAdi = findViewById(R.id.etKullaniciAdi);
        etSifre = findViewById(R.id.etSifre);
        btnGiris = findViewById(R.id.btnGiris);
        tvKayitOl = findViewById(R.id.tvKayitOl);

        btnGiris.setOnClickListener(v -> {
            String kadi = etKullaniciAdi.getText().toString().trim();
            String sifre = etSifre.getText().toString().trim();

            if (kadi.isEmpty() || sifre.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Veritabanı kontrolü (Versiyon 6)
            if (vt.girisKontrol(kadi, sifre)) {
                // Oturum bilgilerini kaydet
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("currentUsername", kadi);
                editor.apply();

                Toast.makeText(this, "Hoş geldin, " + kadi + "!", Toast.LENGTH_SHORT).show();
                gitMain();
            } else {
                Toast.makeText(this, "Kullanıcı adı veya şifre hatalı!", Toast.LENGTH_LONG).show();
            }
        });

        tvKayitOl.setOnClickListener(v -> {
            startActivity(new Intent(GirisYapActivity.this, KayitOlActivity.class));
        });
    }

    private void gitMain() {
        Intent intent = new Intent(GirisYapActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Geri tuşuyla giriş ekranına dönülmesin
    }
}