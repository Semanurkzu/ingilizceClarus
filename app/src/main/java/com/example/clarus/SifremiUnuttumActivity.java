package com.example.clarus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// FIREBASE KÜTÜPHANESİ
import com.google.firebase.auth.FirebaseAuth;

/**
 * Hocam Merhaba,
 * Şifre sıfırlama sürecini Firebase Authentication altyapısına entegre ettik.
 * Deep Link ve yerel şifre güncelleme karmaşasını kaldırarak, güvenliği
 * tamamen Firebase'in standart şifre sıfırlama e-postası (Reset Email) mekanizmasına devrettik.
 */
public class SifremiUnuttumActivity extends AppCompatActivity {

    private EditText etForgotEmail;
    private Button btnSendEmail;
    private LinearLayout layoutStep1;
    private TextView tvTitle;

    // Firebase Auth Nesnesi
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifremi_unuttum);

        // Firebase Auth başlatılıyor
        mAuth = FirebaseAuth.getInstance();

        // XML elemanlarının bağlanması
        tvTitle = findViewById(R.id.tv_title);
        etForgotEmail = findViewById(R.id.et_forgot_email);
        btnSendEmail = findViewById(R.id.btn_send_email);
        layoutStep1 = findViewById(R.id.layout_step1);

        // NOT: XML'deki layout_step2 ve şifre alanlarını kod patlamasın diye gizliyoruz.
        // Firebase tüm şifre belirleme işini kendi gönderdiği maildeki link üzerinden halleder.
        LinearLayout layoutStep2 = findViewById(R.id.layout_step2);
        if (layoutStep2 != null) {
            layoutStep2.setVisibility(View.GONE);
        }

        // FIREBASE ŞİFRE SIFIRLAMA MAİLİ GÖNDERME AKSİYONU
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etForgotEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(SifremiUnuttumActivity.this, "Lütfen e-posta adresinizi girin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // FIREBASE RESET EMAIL MOTORU ÇALIŞIYOR
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Mail başarıyla gönderildi
                                Toast.makeText(SifremiUnuttumActivity.this,
                                        "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi Sema!",
                                        Toast.LENGTH_LONG).show();

                                // Kullanıcıyı giriş ekranına geri yönlendiriyoruz
                                Intent intent = new Intent(SifremiUnuttumActivity.this, GirisYapActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                // Kullanıcı bulunamadıysa veya mail formatı hatalıysa Firebase hatayı söyler
                                String hataMesaji = task.getException() != null ? task.getException().getMessage() : "E-posta gönderilemedi!";
                                Toast.makeText(SifremiUnuttumActivity.this, "Hata: " + hataMesaji, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}