package com.example.clarus;

import java.util.ArrayList;
import java.util.List;

public class WordleManager {

    // Harf durumları için sabitler
    public static final int GRI = 0;   // Kelimede yok
    public static final int SARI = 1;  // Kelimede var ama yanlış yer
    public static final int YESIL = 2; // Doğru harf doğru yer

    public static int[] kontrolEt(String tahmin, String gizliKelime) {
        int[] sonuclar = new int[5];
        tahmin = tahmin.toUpperCase();
        gizliKelime = gizliKelime.toUpperCase();

        // Önce yeşilleri (tam doğru yerleri) bulalım
        for (int i = 0; i < 5; i++) {
            if (tahmin.charAt(i) == gizliKelime.charAt(i)) {
                sonuclar[i] = YESIL;
            } else {
                sonuclar[i] = GRI;
            }
        }

        // Sonra sarıları (yanlış yerdeki harfleri) bulalım
        for (int i = 0; i < 5; i++) {
            if (sonuclar[i] != YESIL) {
                for (int j = 0; j < 5; j++) {
                    // Eğer harf kelimenin başka bir yerinde varsa sarı yap
                    if (tahmin.charAt(i) == gizliKelime.charAt(j)) {
                        sonuclar[i] = SARI;
                        break;
                    }
                }
            }
        }
        return sonuclar;
    }
}