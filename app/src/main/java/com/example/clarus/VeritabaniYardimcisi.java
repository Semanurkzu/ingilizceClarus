package com.example.clarus;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Hocam Merhaba,
 * Clarus projemizde veri kalıcılığını (Data Persistence) SQLite ile sağlıyoruz.
 * Yeni güncelleme ile 'kullanicilar' tablosunu ekleyerek Authentication (Kimlik Doğrulama)
 * altyapısını kurduk. Versiyonu 2'ye yükselterek 'onUpgrade' mekanizmasını tetikledik.
 */
public class VeritabaniYardimcisi extends SQLiteOpenHelper {

    private static final String VERITABANI_ADI = "KelimeAtolyesi.db";
    // Sema, versiyonu 2 yaptım çünkü tablo yapısı değişti.
    private static final int VERITABANI_VERSIYON = 2;

    // Kelimeler Tablosu
    private static final String TABLO_KELIMELER = "kelimeler";
    private static final String COL_ID = "id";
    private static final String COL_INGILIZCE = "ingilizce";
    private static final String COL_TURKCE = "turkce";
    private static final String COL_SEVIYE = "seviye";

    // Kullanıcılar Tablosu (YENİ)
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
        // Kelimeler Tablosu Oluşturma
        String tabloKelimeOlustur = "CREATE TABLE " + TABLO_KELIMELER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_INGILIZCE + " TEXT, " +
                COL_TURKCE + " TEXT, " +
                COL_SEVIYE + " INTEGER DEFAULT 0)";
        db.execSQL(tabloKelimeOlustur);

        // Kullanıcılar Tablosu Oluşturma (Sema'nın istediği e-posta ve kadi dahil)
        String tabloKullaniciOlustur = "CREATE TABLE " + TABLO_KULLANICILAR + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EPOSTA + " TEXT, " +
                COL_KADI + " TEXT, " +
                COL_SIFRE + " TEXT)";
        db.execSQL(tabloKullaniciOlustur);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Geliştirme aşamasında olduğumuz için eski tabloları uçurup yeniden kuruyoruz.
        db.execSQL("DROP TABLE IF EXISTS " + TABLO_KELIMELER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLO_KULLANICILAR);
        onCreate(db);
    }

    // --- KELİME MODÜLÜ METOTLARI ---
    public boolean kelimeEkle(String ing, String tr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_INGILIZCE, ing.trim().toLowerCase());
        cv.put(COL_TURKCE, tr.trim().toLowerCase());

        long sonuc = db.insert(TABLO_KELIMELER, null, cv);
        return sonuc != -1;
    }

    // --- KAYIT MODÜLÜ METOTLARI ---
    public boolean kullaniciKaydet(String eposta, String kadi, String sifre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EPOSTA, eposta.trim());
        cv.put(COL_KADI, kadi.trim());
        cv.put(COL_SIFRE, sifre); // Hocam gerçek projede burası hash'lenmelidir.

        long sonuc = db.insert(TABLO_KULLANICILAR, null, cv);
        db.close();
        return sonuc != -1;
    }

    // --- GİRİŞ MODÜLÜ METOTLARI (Sema Buraya Dikkat) ---
    public boolean girisKontrol(String kadi, String sifre) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Kullanıcı adı ve şifre eşleşiyor mu kontrol ediyoruz
        String sorgu = "SELECT * FROM " + TABLO_KULLANICILAR +
                " WHERE " + COL_KADI + "=? AND " + COL_SIFRE + "=?";

        android.database.Cursor cursor = db.rawQuery(sorgu, new String[]{kadi, sifre});
        boolean sonuc = cursor.getCount() > 0;
        cursor.close();
        return sonuc;
    }
}