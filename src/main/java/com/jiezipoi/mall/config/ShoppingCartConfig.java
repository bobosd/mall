package com.jiezipoi.mall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/config/shopping-cart-config.properties")
public class ShoppingCartConfig {
    @Value("${shopping-cart.limit.single}")
    private int itemLimit;
    @Value("${shipping-cart.limit.total}")
    private int shoppingCartTotalLimit;

    public int getItemLimit() {
        return itemLimit;
    }

    public int getShoppingCartTotalLimit() {
        return shoppingCartTotalLimit;
    }
}
