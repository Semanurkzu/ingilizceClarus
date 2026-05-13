package com.example.clarus; // Kendi paket adınla kontrol et

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Hocam Merhaba,
 * Bu sınıf uygulamamızın ilk giriş noktasıdır.
 * Nesne yönelimli programlama prensiplerine uygun olarak,
 * her işi kendi metodunda (Single Responsibility) yapacak şekilde kurguladık.
 */
public class AcilisEkraniActivity extends AppCompatActivity {

    // 80/20 kuralı: Uygulamanın en kritik ilk 3 saniyesi.
    // Code Smell: 'Magic Number' kullanımından kaçınmak için süreyi sabitledik.
    private static final int GECIKME_SURESI = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acilis_ekrani);

        // Hocam, ana akış mantığını onCreate içinde boğmamak için metodize ettik.
        anaEkranaYonlendir();
    }

    /**
     * Kullanıcıyı 3 saniye bekletip ana ekrana aktaran metot.
     * Handler nesnesini ana thread (Looper.getMainLooper) ile bağlayarak
     * olası bellek sızıntılarının (Memory Leak) önüne geçtik.
     */
    private void anaEkranaYonlendir() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // MainActivity sınıfına geçiş emri veriyoruz.
                Intent gecisIntent = new Intent(AcilisEkraniActivity.this, MainActivity.class);
                startActivity(gecisIntent);

                // finish() metodu çok kritik; arkada gereksiz activity tutmuyoruz (Resource Management).
                finish();
            }
        }, GECIKME_SURESI);
    }
}