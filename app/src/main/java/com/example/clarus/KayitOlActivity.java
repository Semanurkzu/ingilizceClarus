package com.example.clarus;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

// FIREBASE KÜTÜPHANELERİ
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Hocam Merhaba,
 * Kayıt sistemimizi bulut tabanlı Firebase Authentication mimarisine entegre ettik.
 * Kullanıcı hesabı oluşturulurken e-posta ve şifre doğrulaması gerçek zamanlı yapılır,
 * 'Kullanıcı Adı' verisi ise Firebase User Profile alanında güvenli bir şekilde saklanır.
 */
public class KayitOlActivity extends AppCompatActivity {

    private EditText etEposta, etKadi, etSifre;
    private AppCompatButton btnKayit;

    // Firebase Auth Nesnesi
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);

        // Firebase Auth başlatılıyor
        mAuth = FirebaseAuth.getInstance();

        etEposta = findViewById(R.id.et_kayit_eposta);
        etKadi = findViewById(R.id.et_kayit_kadi);
        etSifre = findViewById(R.id.et_kayit_sifre);
        btnKayit = findViewById(R.id.btn_kayit_ol);

        btnKayit.setOnClickListener(v -> {
            String eposta = etEposta.getText().toString().trim();
            String kadi = etKadi.getText().toString().trim();
            String sifre = etSifre.getText().toString().trim();

            if (eposta.isEmpty() || kadi.isEmpty() || sifre.isEmpty()) {
                Toast.makeText(this, "Tüm alanları doldur kankim!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Şifre uzunluğu kontrolü (Firebase en az 6 karakter ister, yoksa patlar!)
            if (sifre.length() < 6) {
                Toast.makeText(this, "Şifre en az 6 karakter olmalıdır!", Toast.LENGTH_SHORT).show();
                return;
            }

            // FIREBASE KULLANICI OLUŞTURMA OPERASYONU BAŞLIYOR
            mAuth.createUserWithEmailAndPassword(eposta, sifre)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Kullanıcı başarıyla oluşturuldu
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Kullanıcı adını Firebase profiline ekleme adımı
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(kadi)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(profileTask -> {
                                            if (profileTask.isSuccessful()) {
                                                Toast.makeText(this, "Kayıt başarılı Sema! Giriş yapabilirsin.", Toast.LENGTH_SHORT).show();
                                                finish(); // Sayfayı kapatıp giriş ekranına geri döner
                                            }
                                        });
                            }
                        } else {
                            // Firebase'den dönen hata mesajını (Örn: E-posta zaten kullanımda vb.) yakalıyoruz
                            String hataMesaji = task.getException() != null ? task.getException().getMessage() : "Kayıt başarısız oldu!";
                            Toast.makeText(this, "Hata: " + hataMesaji, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}