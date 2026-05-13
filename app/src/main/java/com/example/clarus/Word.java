package com.example.clarus;

public class Word {
    private int id;
    private String ingilizce;
    private String turkce;
    private int dogruSayisi; //  6 olunca kelime 'ezberlendi' sayılıyor
    private long sonrakiTestTarihi; // Bir sonraki sınav vakti ne zaman? (Milisaniye)

    public Word(String ingilizce, String turkce) {
        this.ingilizce = ingilizce;
        this.turkce = turkce;
        this.dogruSayisi = 0;
        this.sonrakiTestTarihi = System.currentTimeMillis(); // Hemen sorulsun diye bugünü verdik
    }

    // --- BURADAN AŞAĞISI GETTER-SETTER TARAFI ---

    public String getIngilizce() { return ingilizce; }
    public void setIngilizce(String ingilizce) { this.ingilizce = ingilizce; }

    public String getTurkce() { return turkce; }
    public void setTurkce(String turkce) { this.turkce = turkce; }

    public int getDogruSayisi() { return dogruSayisi; }
    public void setDogruSayisi(int dogruSayisi) { this.dogruSayisi = dogruSayisi; }

    public long getSonrakiTestTarihi() { return sonrakiTestTarihi; }
    public void setSonrakiTestTarihi(long sonrakiTestTarihi) { this.sonrakiTestTarihi = sonrakiTestTarihi; }
}