package com.jiezipoi.mall.utils;

import com.jiezipoi.mall.enums.PaymentType;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class CardDetailGenerator {
    public static PaymentType generateRandomPaymentType() {
        PaymentType[] types = PaymentType.values();
        int randomInt = ThreadLocalRandom.current().nextInt(types.length);
        return types[randomInt];
    }
    public static String generateRandomCVV() {
        final int CVV_LENGTH = 3;
        StringBuilder CVV = new StringBuilder();
        for (int i = 0; i < CVV_LENGTH; i++) {
            int randomInt = ThreadLocalRandom.current().nextInt(10);
            CVV.append(randomInt);
        }
        return CVV.toString();
    }
    public static String generateRandomCardNumber() {
        final int CARD_NUMBER_LENGTH = 16;
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < CARD_NUMBER_LENGTH; i++) {
            int randomInt = ThreadLocalRandom.current().nextInt(10);
            cardNumber.append(randomInt);
        }
        int checkDigit = generateCheckDigit(cardNumber.toString());
        cardNumber.append(checkDigit);
        return cardNumber.toString();
    }

    public static LocalDate generateRandomCardExpireDate() {
        LocalDate currentDate = LocalDate.now();
        LocalDate oneYearLater = currentDate.plusYears(1);
        LocalDate fiveYearsLater = currentDate.plusYears(5);
        long minDay = oneYearLater.toEpochDay();
        long maxDay = fiveYearsLater.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    private static int generateCheckDigit(String cardNumber) {
        int sum = 0;
        boolean alternate = true;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum * 9) % 10;
    }
}
