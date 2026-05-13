package com.example.clarus;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WordleActivity extends AppCompatActivity {
    private String gizliKelime = "APPLE"; // İleride VeritabaniYardimcisi'ndan gelecek
    private int denemeSayisi = 0;
    private GridLayout wordleGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordle);

        wordleGrid = findViewById(R.id.wordleGrid);
        EditText etTahmin = findViewById(R.id.etTahmin);
        Button btnTahminEt = findViewById(R.id.btnTahminEt);

        gridiHazirla();

        btnTahminEt.setOnClickListener(v -> {
            String tahmin = etTahmin.getText().toString().toUpperCase().trim();

            if (tahmin.length() != 5) {
                Toast.makeText(this, "Kelime 5 harfli olmalı!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (denemeSayisi < 6) {
                satiriGuncelle(tahmin);
                etTahmin.setText("");

                if (tahmin.equals(gizliKelime)) {
                    Toast.makeText(this, "TEBRİKLER! 🎉", Toast.LENGTH_LONG).show();
                    btnTahminEt.setEnabled(false);
                } else if (denemeSayisi == 6) {
                    Toast.makeText(this, "Hakkınız bitti! Kelime: " + gizliKelime, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void gridiHazirla() {
        for (int i = 0; i < 30; i++) {
            TextView tv = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 140; // Ekran boyutuna göre ayarlayabilirsin
            params.height = 140;
            params.setMargins(6, 6, 6, 6);

            tv.setLayoutParams(params);
            tv.setBackgroundResource(android.R.drawable.editbox_background);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(Color.WHITE);
            tv.setAllCaps(true);

            wordleGrid.addView(tv);
        }
    }

    private void satiriGuncelle(String tahmin) {
        for (int i = 0; i < 5; i++) {
            TextView cell = (TextView) wordleGrid.getChildAt(denemeSayisi * 5 + i);
            char harf = tahmin.charAt(i);
            cell.setText(String.valueOf(harf));

            if (harf == gizliKelime.charAt(i)) {
                cell.setBackgroundColor(Color.parseColor("#538D4E")); // Yeşil
            } else if (gizliKelime.contains(String.valueOf(harf))) {
                cell.setBackgroundColor(Color.parseColor("#B59F3B")); // Sarı
            } else {
                cell.setBackgroundColor(Color.parseColor("#3A3A3C")); // Gri
            }
        }
        denemeSayisi++;
    }
}