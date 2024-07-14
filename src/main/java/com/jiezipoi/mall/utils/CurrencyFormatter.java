package com.jiezipoi.mall.utils;

import java.math.BigDecimal;

public class CurrencyFormatter {
    public static String formatCurrency(BigDecimal amount) {
        return amount.toString().replaceAll("\\.", ",") + "€";
    }
}
