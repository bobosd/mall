package com.jiezipoi.mall.utils;

public class Validator {
    public static boolean isNumeric(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String integer) {
        try {
            if (integer.contains(".")) {
                return false;
            }
            Integer.parseInt(integer);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
