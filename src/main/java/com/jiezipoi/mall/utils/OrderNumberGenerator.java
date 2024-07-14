package com.jiezipoi.mall.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OrderNumberGenerator {
    public static String generate() {
        Random random = new Random();
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String formattedDateTime = localDateTime.format(formatter);
        int randomNumber = random.nextInt(10000);
        String formattedNumber = String.format("%04d", randomNumber);
        return formattedDateTime + formattedNumber;
    }
}
