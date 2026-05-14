package com.example.clarus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VeritabaniYardimcisi extends SQLiteOpenHelper {

    private static final String VERITABANI_ADI = "KelimeAtolyesi.db";
    private static final int VERITABANI_VERSIYON = 3; // Seviye ve Tarih için versiyonu yükselttik

    private static final String TABLO_KELIMELER = "kelimeler";
    private static final String COL_ID = "id";
    private static final String COL_INGILIZCE = "ingilizce";
    private static final String COL_TURKCE = "turkce";
    private static final String COL_SEVIYE = "seviye";
    private static final String COL_SON_TEKRAR = "son_tekrar_tarihi";

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

    public boolean kelimeEkle(String ing, String tr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_INGILIZCE, ing.trim().toLowerCase());
        cv.put(COL_TURKCE, tr.trim().toLowerCase());
        return db.insert(TABLO_KELIMELER, null, cv) != -1;
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