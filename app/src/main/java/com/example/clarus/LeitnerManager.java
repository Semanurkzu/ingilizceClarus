package com.example.clarus;
import java.util.Calendar;

public class LeitnerManager {

    // Kanka hoca dedi ya "1 gün, 1 hafta, 1 ay..." diye, o hesaplamayı burada yapıyoruz
    public static long hesaplaZaman(int seviye) {
        Calendar cal = Calendar.getInstance();

        switch (seviye) {
            case 1: cal.add(Calendar.DAY_OF_YEAR, 1); break; // 1 gün sonra
            case 2: cal.add(Calendar.WEEK_OF_YEAR, 1); break; // 1 hafta sonra
            case 3: cal.add(Calendar.MONTH, 1); break; // 1 ay sonra
            case 4: cal.add(Calendar.MONTH, 3); break; // 3 ay sonra
            case 5: cal.add(Calendar.MONTH, 6); break; // 6 ay sonra
            case 6: cal.add(Calendar.YEAR, 1); break;  // 1 yıl sonra
            default: break; // Zaten 6'yı geçtiyse emekli olmuştur kelime
        }
        return cal.getTimeInMillis();
    }
}