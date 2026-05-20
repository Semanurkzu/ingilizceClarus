package com.example.clarus;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.Scanner;

public class AcilisEkraniActivity extends AppCompatActivity {

    private static final int SPLASH_SURESI = 3000;
    private VeritabaniYardimcisi vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acilis_ekrani);

        vt = new VeritabaniYardimcisi(this);

        // Operasyonu başlat (Sessiz ve akıllı kontrol)
        oxfordKelimeleriniGeriYukle();

        kullaniciyiYonlendir();
    }

    private void oxfordKelimeleriniGeriYukle() {
        new Thread(() -> {
            SQLiteDatabase db = null;
            try {
                db = vt.getWritableDatabase();

                // 1. KONTROL: Veritabanı zaten dolu mu?
                Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM kelimeler", null);
                cursor.moveToFirst();
                int kelimeSayisi = cursor.getInt(0);
                cursor.close();

                // Eğer içeride zaten kelime varsa, tekrar yükleyip kullanıcıyı darlama
                if (kelimeSayisi > 0) {
                    return; // Metottan sessizce çık
                }

                // 2. YÜKLEME: Veritabanı boşsa işlemleri yap
                InputStream is = getAssets().open("words.json");
                Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
                String jsonStr = scanner.hasNext() ? scanner.next() : "";
                is.close();

                if (jsonStr.isEmpty()) return;

                JSONArray jsonArray = new JSONArray(jsonStr.trim());

                db.beginTransaction();
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        ContentValues cv = new ContentValues();
                        cv.put("ingilizce", obj.optString("eng", ""));
                        cv.put("turkce", obj.optString("tur", ""));
                        cv.put("okunus", obj.optString("phon", ""));
                        cv.put("ornek_cumle", obj.optString("ex", ""));

                        db.insert("kelimeler", null, cv);
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                // Sadece İLK yükleme bittiğinde bir kere haber ver (İstersen bunu da silebilirsin)
                runOnUiThread(() -> {
                    Toast.makeText(AcilisEkraniActivity.this, "Kelimeler Hazır!", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                // Hata olursa yine de bilelim
                final String error = e.getMessage();
                runOnUiThread(() -> {
                    Toast.makeText(AcilisEkraniActivity.this, "Yükleme sorunu: " + error, Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (db != null && db.isOpen()) db.close();
            }
        }).start();
    }

    private void kullaniciyiYonlendir() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(AcilisEkraniActivity.this, GirisYapActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SURESI);
    }
}