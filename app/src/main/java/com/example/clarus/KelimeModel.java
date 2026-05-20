package com.example.clarus;

public class KelimeModel {
    private int id; // BU EKSİKTİ!
    private String ingilizce;
    private String turkce;
    private String okunus;
    private String ornekCumle;

    // Hem ID'li hem ID'siz constructor (kurucu metot) ekliyoruz
    public KelimeModel(int id, String ingilizce, String turkce, String okunus, String ornekCumle) {
        this.id = id;
        this.ingilizce = ingilizce;
        this.turkce = turkce;
        this.okunus = okunus;
        this.ornekCumle = ornekCumle;
    }

    // Mevcut constructor'ı bozmuyoruz (Hata vermemesi için)
    public KelimeModel(String ingilizce, String turkce, String okunus, String ornekCumle) {
        this.ingilizce = ingilizce;
        this.turkce = turkce;
        this.okunus = okunus;
        this.ornekCumle = ornekCumle;
    }

    // --- KRİTİK GETTER VE SETTER METOTLARI ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; } // İŞTEimage_84f4c2.jpg'DEKİ HATAYI ÇÖZEN SATIR!

    public String getIngilizce() { return ingilizce; }
    public String getTurkce() { return turkce; }
    public String getOkunus() { return okunus; }
    public String getOrnekCumle() { return ornekCumle; }
}