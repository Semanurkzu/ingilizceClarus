package com.example.clarus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import java.util.List;

/**
 * Hocam Merhaba,
 * Bu sınıf Leitner Sistemi (Spaced Repetition) üzerine kurgulanmış sınav motorudur.
 * Kullanıcının performansına göre kelimeleri 1 gün, 1 hafta, 1 ay gibi periyotlarla
 * tekrar etmesini sağlayan algoritmayı yönetir.
 */
public class SinavActivity extends AppCompatActivity {

    private TextView tvSoru, tvSkor;
    private Button btnA, btnB, btnC, btnD;
    private VeritabaniYardimcisi vt;
    private List<KelimeModel> sinavListesi;
    private int suankiSoruIndex = 0;
    private int dogruSayisi = 0;
    private int yanlisSayisi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinav);

        // UI Bileşenlerinin bağlanması
        tvSoru = findViewById(R.id.tvSoru);
        tvSkor = findViewById(R.id.tvSkor);
        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);
        vt = new VeritabaniYardimcisi(this);

        // Bugün tekrar edilmesi gereken (vakti gelmiş) kelimeleri veritabanından çekiyoruz
        sinavListesi = vt.tekrarKelimeleleriniGetir();

        if (sinavListesi != null && sinavListesi.size() > 0) {
            soruyuGoster();
        } else {
            tvSoru.setText("Bugünlük tüm tekrarlar bitti! Harikasın.");
            secenekleriKapat();
        }
    }

    private void soruyuGoster() {
        KelimeModel suankiKelime = sinavListesi.get(suankiSoruIndex);
        tvSoru.setText(suankiKelime.getIngilizce());

        // Skoru her soruda güncelle
        tvSkor.setText("Doğru: " + dogruSayisi + " | Yanlış: " + yanlisSayisi);

        // Şıkları karıştır ve ata (Rastgele 3 yanlış şık çekme mantığı)
        List<String> sikklar = vt.rastgeleSikklarGetir(suankiKelime.getTurkce());
        sikklar.add(suankiKelime.getTurkce());
        Collections.shuffle(sikklar);

        // Butonlara şıkları yerleştirme
        btnA.setText(sikklar.get(0));
        btnB.setText(sikklar.get(1));
        btnC.setText(sikklar.get(2));
        btnD.setText(sikklar.get(3));
    }

    /**
     * XML tarafında android:onClick="cevapVer" ile bağlanmıştır.
     */
    public void cevapVer(View view) {
        Button secilenButon = (Button) view;
        String cevap = secilenButon.getText().toString();
        KelimeModel kelime = sinavListesi.get(suankiSoruIndex);

        if (cevap.equals(kelime.getTurkce())) {
            dogruSayisi++;
            // Doğru ise seviyeyi artır ve bir sonraki periyoda gönder
            vt.kelimeAsamasiniGuncelle(kelime.getId(), true);
            Toast.makeText(this, "Tebrikler, Doğru!", Toast.LENGTH_SHORT).show();
        } else {
            yanlisSayisi++;
            // Yanlış ise seviyeyi sıfırla ve yarın tekrar sor
            vt.kelimeAsamasiniGuncelle(kelime.getId(), false);
            Toast.makeText(this, "Yanlış! Doğrusu: " + kelime.getTurkce(), Toast.LENGTH_SHORT).show();
        }

        suankiSoruIndex++;
        if (suankiSoruIndex < sinavListesi.size()) {
            soruyuGoster();
        } else {
            sinavBitti();
        }
    }

    private void sinavBitti() {
        tvSkor.setText("BİTTİ! Skorun: " + dogruSayisi + "/" + (dogruSayisi + yanlisSayisi));
        tvSoru.setText("Sınav Tamamlandı!\nAnaliz kısmından gelişimini takip edebilirsin.");
        secenekleriKapat();
    }

    private void secenekleriKapat() {
        btnA.setVisibility(View.GONE);
        btnB.setVisibility(View.GONE);
        btnC.setVisibility(View.GONE);
        btnD.setVisibility(View.GONE);
    }
}