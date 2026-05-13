package com.example.clarus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText etKelimeSayisi = findViewById(R.id.etKelimeSayisi);
        Button btnKaydet = findViewById(R.id.btnKaydet);

        // SharedPreferences kullanarak verileri telefona kaydediyoruz
        SharedPreferences preferences = getSharedPreferences("Ayarlar", MODE_PRIVATE);

        // Daha önce kaydedilmiş bir sayı varsa onu kutucuğa yazdırıyoruz (varsayılan 10)
        int mevcutSayi = preferences.getInt("kelime_limiti", 10);
        etKelimeSayisi.setText(String.valueOf(mevcutSayi));

        btnKaydet.setOnClickListener(v -> {
            String input = etKelimeSayisi.getText().toString();

            if (!input.isEmpty()) {
                int yeniLimit = Integer.parseInt(input);

                // Veriyi kaydediyoruz
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("kelime_limiti", yeniLimit);
                editor.apply();

                Toast.makeText(this, "Ayarlar kaydedildi", Toast.LENGTH_SHORT).show();
                finish(); // Ekranı kapatıp geri döner
            }
        });
    }
}