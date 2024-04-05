package com.jiezipoi.mall.service;

import com.jiezipoi.mall.config.ShoppingCartConfig;
import com.jiezipoi.mall.dao.ShoppingCartItemDao;
import com.jiezipoi.mall.entity.Goods;
import com.jiezipoi.mall.entity.ShoppingCartItem;
import com.jiezipoi.mall.exception.ExceedsQuantityException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ShoppingCartItemService {
    private final ShoppingCartItemDao shoppingCartItemDao;
    private final GoodsService goodsService;
    private final ShoppingCartConfig shoppingCartConfig;

    public ShoppingCartItemService(ShoppingCartItemDao shoppingCartItemDao, GoodsService goodsService, ShoppingCartConfig shoppingCartConfig) {
        this.shoppingCartItemDao = shoppingCartItemDao;
        this.goodsService = goodsService;
        this.shoppingCartConfig = shoppingCartConfig;
    }

    public void saveCartItem(ShoppingCartItem shoppingCartItem) throws ExceedsQuantityException, SQLException {
        ShoppingCartItem temp = shoppingCartItemDao.selectByUserIdAndGoodsId(shoppingCartItem.getUserId(), shoppingCartItem.getGoodsId());
        if (temp != null) {
            throw new DuplicateKeyException("Duplicated cart item");
        }
        Goods goods = goodsService.getGoodsById(shoppingCartItem.getGoodsId());
        if (goods == null) {
            throw new NullPointerException();
        }

        if (shoppingCartItem.getGoodsCount() > shoppingCartConfig.getItemLimit()) {
            throw new IllegalArgumentException();
        }

        int totalCartItemCount = shoppingCartItemDao.selectCountByUserId(shoppingCartItem.getUserId());
        if (totalCartItemCount > shoppingCartConfig.getShoppingCartTotalLimit()) {
            throw new ExceedsQuantityException();
        }

        int insertedRow = shoppingCartItemDao.insertSelective(shoppingCartItem);
        if (insertedRow < 1) {
            throw new SQLException();
        }
    }
}
