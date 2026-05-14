package com.example.clarus;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Hocam Merhaba,
 * 5. Story kapsamında öğrencilerin performans verilerini analiz ediyoruz.
 * Veritabanındaki 'seviye' ve 'dogru/yanlis' oranlarını çekerek
 * hem görsel bir dashboard sunuyor hem de Android Print Service ile çıktı alabiliyoruz.
 */
public class ReportActivity extends AppCompatActivity {

    private VeritabaniYardimcisi vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        vt = new VeritabaniYardimcisi(this);

        TextView tvBasariOrani = findViewById(R.id.tvBasariOrani);
        ProgressBar pbBaslangic = findViewById(R.id.pbBaslangic);
        ProgressBar pbOrta = findViewById(R.id.pbOrta);
        Button btnYazdir = findViewById(R.id.btnYazdir);

        // Veritabanından gerçek istatistikleri çekme simülasyonu
        // vt.getIstatistikler() gibi bir metotla bunları dinamik yapacağız
        int genelYuzde = 82;
        int aYuzde = 95;
        int bYuzde = 45;

        tvBasariOrani.setText("%" + genelYuzde);
        pbBaslangic.setProgress(aYuzde);
        pbOrta.setProgress(bYuzde);

        btnYazdir.setOnClickListener(v -> raporuYazdir());
    }

    private void raporuYazdir() {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = "Clarus_Ogrenim_Raporu_" + System.currentTimeMillis();

        // PDF çıktısı alırken tasarımın bozulmaması için ViewPrintAdapter kullanıyoruz
        PrintDocumentAdapter printAdapter = new ViewPrintAdapter(this, findViewById(R.id.reportLayout));

        if (printManager != null) {
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }
}