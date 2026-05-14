package com.example.clarus;

/**
 * Hocam Merhaba,
 * 5000 kelimelik veriyi hafızada verimli yönetmek için bu model sınıfını oluşturduk.
 * Veritabanı (Versiyon 6) ile tam uyumlu alanlara sahiptir.
 */
public class KelimeModel {
    private String ingilizce;
    private String turkce;
    private String okunus;
    private String ornekCumle;

    // Constructor (Yapıcı Metot)
    public KelimeModel(String ingilizce, String turkce, String okunus, String ornekCumle) {
        this.ingilizce = ingilizce;
        this.turkce = turkce;
        this.okunus = okunus;
        this.ornekCumle = ornekCumle;
    }

    // Getter Metotları (Veritabanına kaydederken kullanacağız)
    public String getIngilizce() { return ingilizce; }
    public String getTurkce() { return turkce; }
    public String getOkunus() { return okunus; }
    public String getOrnekCumle() { return ornekCumle; }
}