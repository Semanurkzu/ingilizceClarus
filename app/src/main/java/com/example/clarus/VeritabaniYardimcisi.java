package com.example.clarus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.List;

/**
 * Hocam Merhaba,
 * Clarus projemizde veri yönetimini SQLiteOpenHelper ile mimari bir yapıya kavuşturduk.
 * Versiyon 6 ile 'Kelime Öğren' modülü için telaffuz (okunuş) ve örnek cümle
 * alanlarını entegre ettik. 5000+ veri için Transaction mimarisini kurduk.
 */
public class VeritabaniYardimcisi extends SQLiteOpenHelper {

    private static final String VERITABANI_ADI = "KelimeAtolyesi.db";
    private static final int VERITABANI_VERSIYON = 6;

    // Kelimeler Tablosu Sabitleri
    private static final String TABLO_KELIMELER = "kelimeler";
    private static final String COL_ID = "id";
    private static final String COL_INGILIZCE = "ingilizce";
    private static final String COL_TURKCE = "turkce";
    private static final String COL_OKUNUS = "okunus";
    private static final String COL_ORNEK = "ornek_cumle";
    private static final String COL_SEVIYE = "seviye";
    private static final String COL_SON_TEKRAR = "son_tekrar_tarihi";

    // Kullanıcılar Tablosu Sabitleri
    private static final String TABLO_KULLANICILAR = "kullanicilar";
    private static final String COL_USER_ID = "id";
    private static final String COL_EPOSTA = "eposta";
    private static final String COL_KADI = "kullanici_adi";
    private static final String COL_SIFRE = "sifre";

    public VeritabaniYardimcisi(Context context) {
        super(context, VERITABANI_ADI, null, VERITABANI_VERSIYON);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tabloKelimeOlustur = "CREATE TABLE " + TABLO_KELIMELER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_INGILIZCE + " TEXT, " +
                COL_TURKCE + " TEXT, " +
                COL_OKUNUS + " TEXT, " +
                COL_ORNEK + " TEXT, " +
                COL_SEVIYE + " INTEGER DEFAULT 0, " +
                COL_SON_TEKRAR + " LONG DEFAULT 0)";
        db.execSQL(tabloKelimeOlustur);

        String tabloKullaniciOlustur = "CREATE TABLE " + TABLO_KULLANICILAR + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EPOSTA + " TEXT, " +
                COL_KADI + " TEXT, " +
                COL_SIFRE + " TEXT)";
        db.execSQL(tabloKullaniciOlustur);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLO_KELIMELER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLO_KULLANICILAR);
        onCreate(db);
    }

    // --- 5000 KELİME İÇİN OPTİMİZE EDİLMİŞ TOPLU EKLEME ---
    public void topluKelimeEkle(List<KelimeModel> kelimeListesi) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (KelimeModel k : kelimeListesi) {
                ContentValues cv = new ContentValues();
                cv.put(COL_INGILIZCE, k.getIngilizce().trim().toLowerCase());
                cv.put(COL_TURKCE, k.getTurkce().trim().toLowerCase());
                cv.put(COL_OKUNUS, k.getOkunus());
                cv.put(COL_ORNEK, k.getOrnekCumle());
                db.insert(TABLO_KELIMELER, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // --- KELİME ÖĞREN MODÜLÜ İÇİN EKLENEN METOT ---
    public KelimeModel rastgeleOgrenmeKelimesiGetir() {
        KelimeModel kelime = null;
        SQLiteDatabase db = this.getReadableDatabase();

        // Rastgele 1 kelime ve tüm detaylarını seçiyoruz
        Cursor cursor = db.rawQuery("SELECT " + COL_INGILIZCE + ", " + COL_TURKCE + ", " + COL_OKUNUS + ", " + COL_ORNEK + " FROM " + TABLO_KELIMELER + " ORDER BY RANDOM() LIMIT 1", null);

        if (cursor.moveToFirst()) {
            String eng = cursor.getString(0);
            String tur = cursor.getString(1);
            String phon = cursor.getString(2);
            String ex = cursor.getString(3);

            // Eğer null gelen veri varsa boş string yapıyoruz ki uygulama çökmesin
            if (phon == null) phon = "";
            if (ex == null) ex = "";

            kelime = new KelimeModel(eng, tur, phon, ex);
        }
        cursor.close();
        db.close();

        return kelime;
    }

    // Wordle için rastgele kelime seçen metot (Dedektif Korumalı)
    public String getWordleKelime(int harfSayisi) {
        String kelime = "CLARU"; // Bulamazsa yazacağı en son çare
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            // 1. ADIM: SQLite'ın harf sayma huyuna güvenmiyoruz. İçinde boşluk olmayan rastgele 100 kelime çekiyoruz.
            Cursor cursor = db.rawQuery("SELECT ingilizce FROM kelimeler WHERE ingilizce IS NOT NULL AND ingilizce NOT LIKE '% %' ORDER BY RANDOM() LIMIT 100", null);

            while (cursor.moveToNext()) {
                // 2. ADIM: Kelimeyi al, sağındaki solundaki tüm görünmez boşlukları Java'nın gücüyle temizle
                String secilen = cursor.getString(0).trim().toUpperCase();

                // 3. ADIM: Kontrolü Java'da yapıyoruz! Temizlenmiş hali tam 5 harf mi?
                if (secilen.length() == harfSayisi) {
                    kelime = secilen;
                    Log.d("KelimeAtolyesi.db", "Sonunda 5 harfli kelime bulundu: " + kelime);
                    break; // 5 harfliyi bulduğumuz an aramayı durdur ve kelimeyi gönder!
                }
            }
            cursor.close();

        } catch (Exception e) {
            Log.e("KelimeAtolyesi.db", "Arama Hatası: " + e.getMessage());
        }

        db.close();
        return kelime; // Her şey yolundaysa taş gibi 5 harfli kelimen döner
    }

    // Rapor ekranı için başarı yüzdesi
    public int getGenelBasariYuzdesi() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorToplam = db.rawQuery("SELECT COUNT(*) FROM " + TABLO_KELIMELER, null);
        Cursor cursorOgrenilen = db.rawQuery("SELECT COUNT(*) FROM " + TABLO_KELIMELER + " WHERE " + COL_SEVIYE + " > 1", null);

        int toplam = 0, ogrenilen = 0;
        if (cursorToplam.moveToFirst()) toplam = cursorToplam.getInt(0);
        if (cursorOgrenilen.moveToFirst()) ogrenilen = cursorOgrenilen.getInt(0);

        cursorToplam.close();
        cursorOgrenilen.close();

        return (toplam == 0) ? 0 : (ogrenilen * 100) / toplam;
    }

    // Giriş Kontrolü
    public boolean girisKontrol(String kadi, String sifre) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLO_KULLANICILAR + " WHERE " + COL_KADI + "=? AND " + COL_SIFRE + "=?", new String[]{kadi, sifre});
        boolean sonuc = cursor.getCount() > 0;
        cursor.close();
        return sonuc;
    }

    // Kayıt İşlemi
    public boolean kullaniciKaydet(String eposta, String kadi, String sifre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EPOSTA, eposta.trim());
        cv.put(COL_KADI, kadi.trim());
        cv.put(COL_SIFRE, sifre);
        return db.insert(TABLO_KULLANICILAR, null, cv) != -1;
    }

    // Tekli Kelime Ekleme
    public void kelimeEkle(String ingilizce, String turkce) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_INGILIZCE, ingilizce.trim().toLowerCase());
        cv.put(COL_TURKCE, turkce.trim().toLowerCase());
        db.insert(TABLO_KELIMELER, null, cv);
        db.close();
    }

    // Kullanıcı Şifre Güncelleme
    public void kullaniciGuncelle(String kadi, String yeniSifre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SIFRE, yeniSifre);
        db.update(TABLO_KULLANICILAR, cv, COL_KADI + "=?", new String[]{kadi});
        db.close();
    }
}