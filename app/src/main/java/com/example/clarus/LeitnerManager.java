package com.example.clarus;

import java.util.Calendar;

public class LeitnerManager {

    // Seviyeye göre eklenecek gün sayıları: 1 gün, 7 gün, 30 gün, 90 gün, 180 gün, 365 gün
    public static int getGunAraligi(int seviye) {
        switch (seviye) {
            case 1: return 1;
            case 2: return 7;
            case 3: return 30;
            case 4: return 90;
            case 5: return 180;
            case 6: return 365;
            default: return 0;
        }
    }

    public static long getSonrakiTarih(int yeniSeviye) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, getGunAraligi(yeniSeviye));
        return cal.getTimeInMillis();
    }
}