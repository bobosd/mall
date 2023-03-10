package com.jiezipoi.mall.utils;

import org.springframework.util.DigestUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileNameGenerator {
    public static String generateFileName() {
        Instant instant = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault());
        String timestamp = formatter.format(instant);
        String uuid = UUID.randomUUID().toString();
        String input = timestamp + uuid;
        return DigestUtils.md5DigestAsHex(input.getBytes());
    }
}
