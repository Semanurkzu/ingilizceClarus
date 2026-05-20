package com.example.clarus;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Hocam Merhaba,
 * Bu modül, öğrencilerin bilimsel tekrar (Leitner) sürecindeki gelişimini analiz eder.
 * Veritabanındaki gerçek seviye verilerini çekerek dinamik bir rapor sunar
 * ve PDF çıktısı alınmasına olanak tanır.
 */
public class ReportActivity extends AppCompatActivity {

    private VeritabaniYardimcisi vt;
    private TextView tvBasariOrani, tvOgrenilenSayi, tvUstaSayi;
    private ProgressBar pbGenelGelisim;
    private Button btnYazdir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        vt = new VeritabaniYardimcisi(this);
        uiBilesenleriniBagla();
        istatistikleriYukle();

        btnYazdir.setOnClickListener(v -> raporuYazdir());
    }

    private void uiBilesenleriniBagla() {
        tvBasariOrani = findViewById(R.id.tvBasariOrani);
        // XML'de id'lerini buna göre kontrol et kanki
        tvOgrenilenSayi = findViewById(R.id.tvOgrenilenSayi);
        tvUstaSayi = findViewById(R.id.tvUstaSayi);
        pbGenelGelisim = findViewById(R.id.pbBaslangic);
        btnYazdir = findViewById(R.id.btnYazdir);
    }

    private void istatistikleriYukle() {
        // Veritabanından [Toplam, Öğrenilen, Usta] dizisini alıyoruz
        int[] stats = vt.getIstatistikler();
        int toplam = stats[0];
        int ogrenilen = stats[1];
        int usta = stats[2];

        // Başarı yüzdesini hesapla
        int yuzde = 0;
        if (toplam > 0) {
            yuzde = (ogrenilen * 100) / toplam;
        }

        // Arayüzü güncelle
        tvBasariOrani.setText("%" + yuzde);
        pbGenelGelisim.setProgress(yuzde);

        if (tvOgrenilenSayi != null) {
            tvOgrenilenSayi.setText("Öğrenilen Kelime: " + ogrenilen);
        }
        if (tvUstaSayi != null) {
            tvUstaSayi.setText("Tam Ezberlenen: " + usta);
        }
    }

    private void raporuYazdir() {
        try {
            PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
            String jobName = "Clarus_Ogrenim_Raporu_" + System.currentTimeMillis();

            // reportLayout, XML dosmandaki en dıştaki ana layout id'si olmalı
            PrintDocumentAdapter printAdapter = new ViewPrintAdapter(this, findViewById(R.id.reportLayout));

            if (printManager != null) {
                printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Yazdırma hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}