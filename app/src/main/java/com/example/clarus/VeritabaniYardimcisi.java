package com.example.clarus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.List;

/**
 * Hocam Merhaba,
 * Clarus projemizde veri yönetimini SQLiteOpenHelper ile mimari bir yapıya kavuşturduk.
 * Versiyon 6 ile 'Kelime Öğren' modülü için telaffuz (okunuş) ve örnek cümle
 * alanlarını entegre ettik. 5000+ veri için Transaction mimarisini kurduk.
 */
public class VeritabaniYardimcisi extends SQLiteOpenHelper {

    private static final String VERITABANI_ADI = "KelimeAtolyesi.db";
    // Versiyon 6: Okunuş ve Örnek Cümle sütunları eklendi.
    private static final int VERITABANI_VERSIYON = 6;

    // Kelimeler Tablosu
    private static final String TABLO_KELIMELER = "kelimeler";
    private static final String COL_ID = "id";
    private static final String COL_INGILIZCE = "ingilizce";
    private static final String COL_TURKCE = "turkce";
    private static final String COL_OKUNUS = "okunus";       // Yeni eklenen
    private static final String COL_ORNEK = "ornek_cumle";   // Yeni eklenen
    private static final String COL_SEVIYE = "seviye";
    private static final String COL_SON_TEKRAR = "son_tekrar_tarihi";

    // Kullanıcılar Tablosu
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
        // Kelime tablosu: Okunuş ve Örnek Cümle alanları dahil edildi.
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
        // Sema kanki, yapı değiştiği için eski tabloları uçurup temiz kurulum yapıyoruz.
        db.execSQL("DROP TABLE IF EXISTS " + TABLO_KELIMELER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLO_KULLANICILAR);
        onCreate(db);
    }

    // --- 5000 KELİME İÇİN OPTİMİZE EDİLMİŞ TOPLU EKLEME ---
    public void topluKelimeEkle(List<KelimeModel> kelimeListesi) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction(); // Performans için işlemi başlatıyoruz
        try {
            for (KelimeModel k : kelimeListesi) {
                ContentValues cv = new ContentValues();
                cv.put(COL_INGILIZCE, k.getIngilizce().trim().toLowerCase());
                cv.put(COL_TURKCE, k.getTurkce().trim().toLowerCase());
                cv.put(COL_OKUNUS, k.getOkunus());
                cv.put(COL_ORNEK, k.getOrnekCumle());
                db.insert(TABLO_KELIMELER, null, cv);
            }
            db.setTransactionSuccessful(); // Tüm veriler başarıyla hazırlandıysa onayla
        } finally {
            db.endTransaction(); // İşlemi bitir
        }
    }

    // Wordle için rastgele kelime seçen metot
    public String getWordleKelime(int uzunluk) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_INGILIZCE + " FROM " + TABLO_KELIMELER +
                        " WHERE LENGTH(" + COL_INGILIZCE + ") = ? ORDER BY RANDOM() LIMIT 1",
                new String[]{String.valueOf(uzunluk)});
        String kelime = null;
        if (cursor.moveToFirst()) {
            kelime = cursor.getString(0).toUpperCase();
        }
        cursor.close();
        return kelime;
    }

    // Rapor ekranı için genel başarı yüzdesini hesaplar
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

    // Kullanıcı profilini günceller (SettingsActivity için)
    public void kullaniciGuncelle(String kadi, String sifre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (!kadi.isEmpty()) cv.put(COL_KADI, kadi.trim());
        if (!sifre.isEmpty()) cv.put(COL_SIFRE, sifre);

        if (cv.size() > 0) {
            db.update(TABLO_KULLANICILAR, cv, null, null);
        }
        db.close();
    }

    public boolean kullaniciKaydet(String eposta, String kadi, String sifre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EPOSTA, eposta.trim());
        cv.put(COL_KADI, kadi.trim());
        cv.put(COL_SIFRE, sifre);
        return db.insert(TABLO_KULLANICILAR, null, cv) != -1;
    }

    public boolean girisKontrol(String kadi, String sifre) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLO_KULLANICILAR + " WHERE " + COL_KADI + "=? AND " + COL_SIFRE + "=?", new String[]{kadi, sifre});
        boolean sonuc = cursor.getCount() > 0;
        cursor.close();
        return sonuc;
    }
}