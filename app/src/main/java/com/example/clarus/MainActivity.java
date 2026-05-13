package com.example.clarus;

import android.content.Intent; // Ayarlar ekranına gitmek için gerekli
import android.content.SharedPreferences; // Kayıtlı limiti okumak için gerekli
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Kelime nesnesini tanımlıyoruz
    Word suAnkiKelime = new Word("Apple", "Elma");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Arayüz elemanlarını bağlıyoruz
        TextView tvSoru = findViewById(R.id.tvSoru);
        EditText etCevap = findViewById(R.id.etCevap);
        Button btnOnayla = findViewById(R.id.btnOnayla);

        // Ayarlar ekranına gitmek için bir butonunuz olduğunu varsayıyorum
        // Eğer yoksa bir buton ekleyip id'sini btnAyarlar yapabilirsiniz
        Button btnAyarlar = findViewById(R.id.btnAyarlar);

        tvSoru.setText(suAnkiKelime.getIngilizce());

        // Kullanıcının belirlediği kelime limitini SharedPreferences üzerinden okuyoruz
        SharedPreferences prefs = getSharedPreferences("Ayarlar", MODE_PRIVATE);
        int kelimeLimiti = prefs.getInt("kelime_limiti", 10); // Varsayılan değer 10

        // Ayarlar butonuna basınca diğer ekrana geçiş
        if (btnAyarlar != null) {
            btnAyarlar.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        btnOnayla.setOnClickListener(v -> {
            String kullaniciCevabi = etCevap.getText().toString().trim();

            // Cevap kontrolü yapılıyor
            if (kullaniciCevabi.equalsIgnoreCase(suAnkiKelime.getTurkce())) {

                // Doğru cevap: Seviye artırılır ve yeni test tarihi hesaplanır
                int yeniSeviye = suAnkiKelime.getDogruSayisi() + 1;
                suAnkiKelime.setDogruSayisi(yeniSeviye);
                suAnkiKelime.setSonrakiTestTarihi(LeitnerManager.hesaplaZaman(yeniSeviye));

                Toast.makeText(this, "Doğru! Yeni Seviye: " + yeniSeviye, Toast.LENGTH_SHORT).show();
            } else {

                // Yanlış cevap: Seviye sıfırlanır ve süreç başa döner
                suAnkiKelime.setDogruSayisi(0);
                suAnkiKelime.setSonrakiTestTarihi(System.currentTimeMillis());

                Toast.makeText(this, "Hatalı cevap. Süreç başa döndü.", Toast.LENGTH_SHORT).show();
            }

            etCevap.setText(""); // Giriş alanını temizle
        });
    }
}