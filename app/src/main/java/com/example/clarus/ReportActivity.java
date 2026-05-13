package com.example.clarus;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        TextView tvBasariOrani = findViewById(R.id.tvBasariOrani);
        Button btnYazdir = findViewById(R.id.btnYazdir);

        // Analiz verilerini hesaplıyoruz (Şimdilik örnek veriler)
        // Arkadaşın DB'yi bitirince buradan gerçek rakamları çekeceğiz
        int toplamSoru = 100;
        int dogruCevap = 75;
        int yuzde = (dogruCevap * 100) / toplamSoru;

        tvBasariOrani.setText("Başarı Oranı: %" + yuzde);

        btnYazdir.setOnClickListener(v -> {
            raporuYazdir();
        });
    }

    // Android yazdırma servisini kullanarak ekranın çıktısını alma işlemi
    private void raporuYazdir() {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = getString(R.string.app_name) + " Raporu";

        // Ekrandaki layout'u PDF dokümanına dönüştürür
        PrintDocumentAdapter printAdapter = new ViewPrintAdapter(this, findViewById(R.id.reportLayout));

        if (printManager != null) {
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }
}