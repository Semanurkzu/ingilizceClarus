package com.example.clarus;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class WordleActivity extends AppCompatActivity {

    private String gizliKelime;
    private int kelimeUzunlugu;
    private final int denemeHakki = 6;
    private int mevcutDeneme = 0;
    private String mevcutTahmin = "";

    private LinearLayout llGameBoard;
    private final List<TextView[]> gridCells = new ArrayList<>();
    private VeritabaniYardimcisi db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordle);

        db = new VeritabaniYardimcisi(this);
        llGameBoard = findViewById(R.id.llGameBoard);

        // 1. Veritabanından kelime çek (Önce 5 harflileri dene)
        String cekilenKelime = db.getWordleKelime(5);
        if (cekilenKelime == null) {
            // Eğer 5 harfli yoksa rastgele bir tane daha dene veya varsayılan ata
            gizliKelime = "APPLE";
        } else {
            gizliKelime = cekilenKelime;
        }

        kelimeUzunlugu = gizliKelime.length();

        // 2. Arayüzü oluştur
        oyunTahtasiniHazirla();
        klavyeyiHazirla();
    }

    private void oyunTahtasiniHazirla() {
        for (int i = 0; i < denemeHakki; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER);
            row.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

            TextView[] rowCells = new TextView[kelimeUzunlugu];
            for (int j = 0; j < kelimeUzunlugu; j++) {
                TextView cell = new TextView(this);
                // Kutucuk boyutlarını harf sayısına göre biraz küçültebilirsin (Örn: 90)
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90, 90);
                params.setMargins(6, 6, 6, 6);
                cell.setLayoutParams(params);
                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(22);
                cell.setTextColor(Color.WHITE);
                cell.setBackground(getEmptyDrawable());
                row.addView(cell);
                rowCells[j] = cell;
            }
            gridCells.add(rowCells);
            llGameBoard.addView(row);
        }
    }

    private void klavyeyiHazirla() {
        LinearLayout container = findViewById(R.id.llKeyboardContainer);
        String[] rows = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};

        for (String rowLetters : rows) {
            LinearLayout layoutRow = new LinearLayout(this);
            layoutRow.setGravity(Gravity.CENTER);
            for (char c : rowLetters.toCharArray()) {
                Button btn = new Button(this, null, 0, androidx.appcompat.R.style.Widget_AppCompat_Button_Small);
                btn.setText(String.valueOf(c));
                btn.setOnClickListener(v -> harfEkle(String.valueOf(c)));
                layoutRow.addView(btn);
            }
            container.addView(layoutRow);
        }

        // Kontrol Butonları
        LinearLayout controlRow = new LinearLayout(this);
        controlRow.setGravity(Gravity.CENTER);

        Button btnDelete = new Button(this);
        btnDelete.setText("SİL");
        btnDelete.setOnClickListener(v -> harfSil());
        controlRow.addView(btnDelete);

        Button btnEnter = new Button(this);
        btnEnter.setText("GİR");
        btnEnter.setOnClickListener(v -> tahminKontrolEt());
        controlRow.addView(btnEnter);

        container.addView(controlRow);
    }

    private void harfEkle(String harf) {
        if (mevcutTahmin.length() < kelimeUzunlugu && mevcutDeneme < denemeHakki) {
            mevcutTahmin += harf;
            gridCells.get(mevcutDeneme)[mevcutTahmin.length() - 1].setText(harf);
        }
    }

    private void harfSil() {
        if (mevcutTahmin.length() > 0) {
            gridCells.get(mevcutDeneme)[mevcutTahmin.length() - 1].setText("");
            mevcutTahmin = mevcutTahmin.substring(0, mevcutTahmin.length() - 1);
        }
    }

    private void tahminKontrolEt() {
        if (mevcutTahmin.length() != kelimeUzunlugu) return;

        String tempGizli = gizliKelime.toUpperCase();
        TextView[] row = gridCells.get(mevcutDeneme);

        for (int i = 0; i < kelimeUzunlugu; i++) {
            char tHarf = mevcutTahmin.charAt(i);
            if (tHarf == tempGizli.charAt(i)) {
                row[i].setBackgroundColor(Color.parseColor("#538D4E")); // Yeşil
            } else if (tempGizli.contains(String.valueOf(tHarf))) {
                row[i].setBackgroundColor(Color.parseColor("#B59F3B")); // Sarı
            } else {
                row[i].setBackgroundColor(Color.parseColor("#3A3A3C")); // Gri
            }
        }

        TextView tvStatus = findViewById(R.id.tvGameStatus);
        if (mevcutTahmin.equals(tempGizli)) {
            tvStatus.setText("TEBRİKLER! KELİME: " + gizliKelime);
        } else if (mevcutDeneme < denemeHakki - 1) {
            mevcutDeneme++;
            mevcutTahmin = "";
        } else {
            tvStatus.setText("KAYBETTİN! KELİME: " + gizliKelime);
        }
    }

    private GradientDrawable getEmptyDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(2, Color.parseColor("#3A3A3C"));
        gd.setCornerRadius(8);
        return gd;
    }
}