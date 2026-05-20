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

// FIREBASE KÜTÜPHANELERİ
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Hocam Merhaba,
 * Giriş sistemini yerel veritabanından Firebase Authentication mimarisine taşıdık.
 * Böylece kullanıcı kimlik doğrulama işlemlerini bulut tabanlı, güvenli ve
 * gerçek zamanlı (Real-time) olarak yönetiyoruz.
 */
public class GirisYapActivity extends AppCompatActivity {

    private EditText etEposta, etSifre; // Kadi yerine eposta yapısı Firebase için daha uygundur
    private Button btnGiris;
    private TextView tvKayitOl, tvSifremiUnuttum;
    private SharedPreferences sharedPreferences;

    // Firebase Auth Nesnesi
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_yap);

        // Firebase Auth ve SharedPreferences başlatılıyor
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("GirisBilgileri", Context.MODE_PRIVATE);

        // Arayüz bileşenlerinin eşlenmesi
        // NOT: Eğer XML'de id hala etKullaniciAdi ise onu koruyabilirsin, koda etEposta olarak bağladık.
        etEposta = findViewById(R.id.etKullaniciAdi);
        etSifre = findViewById(R.id.etSifre);
        btnGiris = findViewById(R.id.btnGiris);
        tvKayitOl = findViewById(R.id.tvKayitOl);
        tvSifremiUnuttum = findViewById(R.id.tvSifremiUnuttum);

        // 1. FIREBASE GİRİŞ YAP BUTON AKSİYONU
        btnGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eposta = etEposta.getText().toString().trim();
                String sifre = etSifre.getText().toString().trim();

                if (eposta.isEmpty() || sifre.isEmpty()) {
                    Toast.makeText(GirisYapActivity.this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // FIREBASE AUTHENTICATION SORGUSU BAŞLIYOR
                mAuth.signInWithEmailAndPassword(eposta, sifre)
                        .addOnCompleteListener(GirisYapActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Giriş başarılı, Firebase kullanıcısını alıyoruz
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Beni Hatırla mekanizması için SharedPreferences kaydı
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                if (user != null && user.getEmail() != null) {
                                    editor.putString("kullaniciEposta", user.getEmail());
                                }
                                editor.apply();

                                Toast.makeText(GirisYapActivity.this, "Giriş Başarılı!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(GirisYapActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Eğer giriş başarısız olursa Firebase'den gelen hata mesajını gösterir
                                String hataMesaji = task.getException() != null ? task.getException().getMessage() : "Hatalı Giriş Bilgileri!";
                                Toast.makeText(GirisYapActivity.this, "Hata: " + hataMesaji, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // 2. KAYIT OL EKRANINA GEÇİŞ
        tvKayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GirisYapActivity.this, KayitOlActivity.class);
                startActivity(intent);
            }
        });

        // 3. ŞİFREMİ UNUTTUM EKRANINA GEÇİŞ
        tvSifremiUnuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GirisYapActivity.this, SifremiUnuttumActivity.class);
                startActivity(intent);
            }
        });
    }
}