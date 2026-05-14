package com.example.clarus;

/**
 * Hocam Merhaba,
 * Wordle algoritmasını karakter frekanslarını dikkate alacak şekilde güncelledik.
 * Bu sayede mükerrer harf tahminlerinde (örneğin kelimede tek 'E' varken
 * iki 'E' girilmesi) hatalı 'Sarı' uyarısı verilmesinin önüne geçtik.
 */
public class WordleManager {

    public static final int RENK_GRI = 0;   // Harf kelimede hiç yok
    public static final int RENK_SARI = 1;  // Harf var ama yeri yanlış
    public static final int RENK_YESIL = 2; // Harf ve yeri doğru

    public static int[] kontrolEt(String tahmin, String gizli) {
        int[] sonuclar = new int[5];
        boolean[] gizliKullanildi = new boolean[5];
        boolean[] tahminKullanildi = new boolean[5];

        // 1. Adım: Önce tam eşleşenleri (Yeşil) bul
        for (int i = 0; i < 5; i++) {
            if (tahmin.charAt(i) == gizli.charAt(i)) {
                sonuclar[i] = RENK_YESIL;
                gizliKullanildi[i] = true;
                tahminKullanildi[i] = true;
            }
        }

        // 2. Adım: Kalan harfler içinde var olanları (Sarı) bul
        for (int i = 0; i < 5; i++) {
            if (tahminKullanildi[i]) continue;

            for (int j = 0; j < 5; j++) {
                if (!gizliKullanildi[j] && tahmin.charAt(i) == gizli.charAt(j)) {
                    sonuclar[i] = RENK_SARI;
                    gizliKullanildi[j] = true;
                    break;
                }
            }
        }

        // Geri kalanlar varsayılan olarak RENK_GRI (0) kalır
        return sonuclar;
    }
}