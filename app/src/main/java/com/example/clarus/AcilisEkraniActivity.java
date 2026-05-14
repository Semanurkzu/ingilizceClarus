package com.example.clarus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Hocam Merhaba,
 * Bu sınıf Clarus projemizin Entry Point (Giriş Noktası) katmanıdır.
 * Uygulama yaşam döngüsünü (Lifecycle) optimize etmek ve kullanıcı deneyimini (UX)
 * artırmak için Splash Screen tasarımını burada yönetiyoruz.
 */
public class AcilisEkraniActivity extends AppCompatActivity {

    // Hocam, Splash ekran süresini 3 saniye (3000ms) olarak sabitledik.
    // 'Magic Number' antipattern'inden kaçınmak için sabit (constant) kullandık.
    private static final int SPLASH_SURESI = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acilis_ekrani);

        // Uygulama akışını (Navigation Flow) yöneten metodu çağırıyoruz.
        kullaniciyiYonlendir();
    }

    /**
     * Kullanıcıyı belirlenen süre sonunda Kimlik Doğrulama (Authentication)
     * ekranına yönlendiren metot.
     */
    private void kullaniciyiYonlendir() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                 * Sema, burası çok kritik!
                 * Artık direkt MainActivity'ye değil, Giriş Yap ekranına gidiyoruz.
                 */
                Intent intent = new Intent(AcilisEkraniActivity.this, GirisYapActivity.class);
                startActivity(intent);

                // Resource Management: Splash ekranını stack'ten temizleyerek bellek tasarrufu sağlıyoruz.
                finish();
            }
        }, SPLASH_SURESI);
    }
}