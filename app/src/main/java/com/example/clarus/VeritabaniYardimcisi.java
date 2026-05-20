package com.example.clarus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class VeritabaniYardimcisi extends SQLiteOpenHelper {

    private static final String VERITABANI_ADI = "KelimeAtolyesi.db";
    private static final int VERITABANI_VERSIYON = 7;

    private static final String TABLO_KELIMELER = "kelimeler";
    private static final String COL_ID = "id";
    private static final String COL_INGILIZCE = "ingilizce";
    private static final String COL_TURKCE = "turkce";
    private static final String COL_OKUNUS = "okunus";
    private static final String COL_ORNEK = "ornek_cumle";
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
        // Gelişmiş onUpgrade mantığı: Eğer tablolar yoksa oluşturur, varsa verileri silmez.
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLO_KELIMELER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_INGILIZCE + " TEXT, " +
                COL_TURKCE + " TEXT, " +
                COL_OKUNUS + " TEXT, " +
                COL_ORNEK + " TEXT, " +
                COL_SEVIYE + " INTEGER DEFAULT 0, " +
                COL_SON_TEKRAR + " LONG DEFAULT 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLO_KULLANICILAR + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EPOSTA + " TEXT, " +
                COL_KADI + " TEXT, " +
                COL_SIFRE + " TEXT)");
    }

    // 💥 ŞİFRE SIFIRLAMA İÇİN E-POSTA KONTROL METODU
    public boolean epostaVarMi(String eposta) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLO_KULLANICILAR + " WHERE " + COL_EPOSTA + "=?", new String[]{eposta.trim()});
        boolean varMi = c.getCount() > 0;
        c.close();
        return varMi;
    }

    // 💥 DERİN BAĞLANTIDAN (DEEP LINK) GELEN E-POSTAYA GÖRE ŞİFRE GÜNCELLEME METODU
    public boolean sifreGuncelleEpostaIle(String eposta, String yeniSifre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SIFRE, yeniSifre);

        int rows = db.update(TABLO_KULLANICILAR, cv, COL_EPOSTA + "=?", new String[]{eposta.trim()});
        return rows > 0;
    }

    // --- RAPOR EKRANI İÇİN DİNAMİK İSTATİSTİK METODU ---
    public int[] getIstatistikler() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorToplam = db.rawQuery("SELECT COUNT(*) FROM " + TABLO_KELIMELER, null);
        Cursor cursorOgrenilen = db.rawQuery("SELECT COUNT(*) FROM " + TABLO_KELIMELER + " WHERE " + COL_SEVIYE + " > 0", null);
        Cursor cursorUsta = db.rawQuery("SELECT COUNT(*) FROM " + TABLO_KELIMELER + " WHERE " + COL_SEVIYE + " = 6", null);

        int toplam = 0, ogrenilen = 0, usta = 0;

        if (cursorToplam.moveToFirst()) toplam = cursorToplam.getInt(0);
        if (cursorOgrenilen.moveToFirst()) ogrenilen = cursorOgrenilen.getInt(0);
        if (cursorUsta.moveToFirst()) usta = cursorUsta.getInt(0);

        cursorToplam.close();
        cursorOgrenilen.close();
        cursorUsta.close();

        return new int[]{toplam, ogrenilen, usta};
    }

    // --- SINAV MODÜLÜ METOTLARI ---
    public List<KelimeModel> tekrarKelimeleleriniGetir() {
        List<KelimeModel> liste = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        long suan = System.currentTimeMillis();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLO_KELIMELER +
                        " WHERE " + COL_SON_TEKRAR + " <= ? ORDER BY RANDOM() LIMIT 20",
                new String[]{String.valueOf(suan)});

        if (cursor.moveToFirst()) {
            do {
                KelimeModel k = new KelimeModel(
                        cursor.getInt(0),    // id
                        cursor.getString(1), // eng
                        cursor.getString(2), // tur
                        cursor.getString(3), // phon
                        cursor.getString(4)  // ex
                );
                liste.add(k);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return liste;
    }

    public void kelimeAsamasiniGuncelle(int id, boolean dogruMu) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_SEVIYE + " FROM " + TABLO_KELIMELER + " WHERE " + COL_ID + "=?", new String[]{String.valueOf(id)});
        int mevcutSeviye = 0;
        if (cursor.moveToFirst()) mevcutSeviye = cursor.getInt(0);
        cursor.close();

        int yeniSeviye;
        long ekSure = 0;
        long GUN = 24 * 60 * 60 * 1000L;

        if (dogruMu) {
            yeniSeviye = Math.min(mevcutSeviye + 1, 6);
            switch (yeniSeviye) {
                case 1: ekSure = GUN; break;
                case 2: ekSure = 7 * GUN; break;
                case 3: ekSure = 30 * GUN; break;
                case 4: ekSure = 90 * GUN; break;
                case 5: ekSure = 180 * GUN; break;
                case 6: ekSure = 365 * GUN; break;
            }
        } else {
            yeniSeviye = 0;
            ekSure = GUN;
        }

        ContentValues cv = new ContentValues();
        cv.put(COL_SEVIYE, yeniSeviye);
        cv.put(COL_SON_TEKRAR, System.currentTimeMillis() + ekSure);
        db.update(TABLO_KELIMELER, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<String> rastgeleSikklarGetir(String dogruCevap) {
        List<String> sikklar = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_TURKCE + " FROM " + TABLO_KELIMELER +
                " WHERE " + COL_TURKCE + " != ? ORDER BY RANDOM() LIMIT 3", new String[]{dogruCevap});
        if (cursor.moveToFirst()) {
            do { sikklar.add(cursor.getString(0)); } while (cursor.moveToNext());
        }
        cursor.close();
        return sikklar;
    }

    // --- DİĞER METOTLAR ---
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
        } finally { db.endTransaction(); }
    }

    public KelimeModel rastgeleOgrenmeKelimesiGetir() {
        KelimeModel kelime = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLO_KELIMELER + " ORDER BY RANDOM() LIMIT 1", null);
        if (cursor.moveToFirst()) {
            kelime = new KelimeModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        }
        cursor.close();
        return kelime;
    }

    public String getWordleKelime(int harfSayisi) {
        String kelime = "CLARU";
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT ingilizce FROM " + TABLO_KELIMELER + " WHERE ingilizce NOT LIKE '% %' ORDER BY RANDOM() LIMIT 100", null);
            while (cursor.moveToNext()) {
                String secilen = cursor.getString(0).trim().toUpperCase();
                if (secilen.length() == harfSayisi) { kelime = secilen; break; }
            }
            cursor.close();
        } catch (Exception e) { Log.e("Hata", e.getMessage()); }
        return kelime;
    }

    // 💥 Geliştirilmiş Giriş Kontrolü: Kullanıcı hem kullanıcı adı hem de eposta ile giriş yapabilir!
    public boolean girisKontrol(String kadiVeyaEposta, String sifre) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLO_KULLANICILAR + " WHERE (" + COL_KADI + "=? OR " + COL_EPOSTA + "=?) AND " + COL_SIFRE + "=?",
                new String[]{kadiVeyaEposta.trim(), kadiVeyaEposta.trim(), sifre});
        boolean s = c.getCount() > 0;
        c.close();
        return s;
    }

    public boolean kullaniciKaydet(String eposta, String kadi, String sifre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EPOSTA, eposta.trim());
        cv.put(COL_KADI, kadi.trim());
        cv.put(COL_SIFRE, sifre);
        return db.insert(TABLO_KULLANICILAR, null, cv) != -1;
    }

    public void kullaniciGuncelle(String kadi, String yeniSifre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SIFRE, yeniSifre);
        db.update(TABLO_KULLANICILAR, cv, COL_KADI + "=?", new String[]{kadi});
    }

    // 💥 Boş bırakılan kelime ekleme metodu dolduruldu:
    public void kelimeEkle(String ingilizce, String turkce) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_INGILIZCE, ingilizce.trim().toLowerCase());
        cv.put(COL_TURKCE, turkce.trim().toLowerCase());
        db.insert(TABLO_KELIMELER, null, cv);
    }
}