package com.example.clarus;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Hocam Merhaba,
 * SettingsActivity üzerinde hem SharedPreferences ile uygulama tercihlerini
 * hem de SQLite (Versiyon 6) ile kullanıcı profil verilerini senkronize yönetiyoruz.
 */
public class SettingsActivity extends AppCompatActivity {

    private EditText etKelimeSayisi, etYeniKadi, etYeniSifre;
    private SwitchCompat switchDarkMode;
    private Button btnKaydet;
    private VeritabaniYardimcisi vt;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        vt = new VeritabaniYardimcisi(this);
        preferences = getSharedPreferences("Ayarlar", MODE_PRIVATE);

        // Bileşenleri Bağla
        etKelimeSayisi = findViewById(R.id.etKelimeSayisi);
        etYeniKadi = findViewById(R.id.etYeniKadi);
        etYeniSifre = findViewById(R.id.etYeniSifre);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        btnKaydet = findViewById(R.id.btnKaydet);

        // Mevcut Ayarları Arayüze Yükle
        yükleMevcutAyarlar();

        // Karanlık Mod Dinleyicisi
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            preferences.edit().putBoolean("dark_mode", isChecked).apply();
        });

        btnKaydet.setOnClickListener(v -> ayarlariKaydet());
    }

    private void yükleMevcutAyarlar() {
        int mevcutLimit = preferences.getInt("kelime_limiti", 10);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);

        etKelimeSayisi.setText(String.valueOf(mevcutLimit));
        switchDarkMode.setChecked(isDarkMode);
    }

    private void ayarlariKaydet() {
        String limitStr = etKelimeSayisi.getText().toString();
        String kadi = etYeniKadi.getText().toString().trim();
        String sifre = etYeniSifre.getText().toString().trim();

        // 1. Uygulama Tercihlerini (Limit vb.) Kaydet
        if (!limitStr.isEmpty()) {
            preferences.edit().putInt("kelime_limiti", Integer.parseInt(limitStr)).apply();
        }

        // 2. Veritabanı (SQLite) Profil Güncelleme (Versiyon 6 Uyumu)
        if (!kadi.isEmpty() || !sifre.isEmpty()) {
            // Veritabanı sınıfındaki kullaniciGuncelle metodunu tetikler
            vt.kullaniciGuncelle(kadi, sifre);
        }

        Toast.makeText(this, "Değişiklikler başarıyla uygulandı ✨", Toast.LENGTH_SHORT).show();
        finish(); // Ayarlar yapıldıktan sonra bir önceki ekrana (Main) döner
    }
}