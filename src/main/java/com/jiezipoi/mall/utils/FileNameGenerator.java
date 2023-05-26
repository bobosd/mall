package com.jiezipoi.mall.utils;

import org.springframework.util.DigestUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileNameGenerator {
    public static String generateFileName() {
        String timestamp = getNowTimeStamp();
        String uuid = UUID.randomUUID().toString();
        String input = timestamp + uuid;
        return DigestUtils.md5DigestAsHex(input.getBytes());
    }

    public static String generateFileName(Long id) {
        String timeStamp = getNowTimeStamp();
        String input = id.toString() + timeStamp;
        return DigestUtils.md5DigestAsHex(input.getBytes());
    }

    private static String getNowTimeStamp() {
        Instant instant = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }
}
