package com.netbanking.netbanking_app.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CardUtils {

    public static String generateCardNumber() {
        // Format: 16-digit number starting with 4 (like Visa)
        Random rand = new Random();
        StringBuilder sb = new StringBuilder("4");
        for (int i = 0; i < 15; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }

    public static String generateCVV() {
        Random rand = new Random();
        int cvv = rand.nextInt(900) + 100; // generates 3-digit CVV
        return String.valueOf(cvv);
    }

    public static String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static LocalDate generateExpiryDate() {
        return LocalDate.now().plusYears(5); // 5 years validity
    }

    public static double defaultDailyLimit() {
        return 50000.00;
    }

    public static String getTodayFormatted() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
