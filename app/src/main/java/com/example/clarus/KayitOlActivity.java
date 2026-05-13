package com.example.clarus;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class KayitOlActivity extends AppCompatActivity {

    private EditText etEposta, etKadi, etSifre;
    private AppCompatButton btnKayit;
    private VeritabaniYardimcisi vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);

        vt = new VeritabaniYardimcisi(this);

        etEposta = findViewById(R.id.et_kayit_eposta);
        etKadi = findViewById(R.id.et_kayit_kadi);
        etSifre = findViewById(R.id.et_kayit_sifre);
        btnKayit = findViewById(R.id.btn_kayit_ol);

        btnKayit.setOnClickListener(v -> {
            String eposta = etEposta.getText().toString().trim();
            String kadi = etKadi.getText().toString().trim();
            String sifre = etSifre.getText().toString().trim();

            if (!eposta.isEmpty() && !kadi.isEmpty() && !sifre.isEmpty()) {
                vt.kullaniciKaydet(eposta, kadi, sifre);
                Toast.makeText(this, "Kayıt başarılı Sema! Giriş yapabilirsin.", Toast.LENGTH_SHORT).show();
                finish(); // Kayıt bitince sayfayı kapatıp girişe dönsün
            } else {
                Toast.makeText(this, "Tüm alanları doldur kankim!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
