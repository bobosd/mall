package com.jiezipoi.mall.service;

import com.jiezipoi.mall.config.ShoppingCartConfig;
import com.jiezipoi.mall.dao.ShoppingCartItemDao;
import com.jiezipoi.mall.dto.ShoppingCartItemDTO;
import com.jiezipoi.mall.entity.Goods;
import com.jiezipoi.mall.entity.ShoppingCartItem;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.exception.QuantityExceededException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartItemService {
    private final ShoppingCartItemDao shoppingCartItemDao;
    private final GoodsService goodsService;
    private final ShoppingCartConfig shoppingCartConfig;
    private static final String TOTAL_CART_ITEMS_EXCEEDED_MESSAGE = "total item exceeds";
    private static final String SINGLE_ITEMS_COUNT_EXCEEDED_MESSAGE = "single item exceeds";

    public ShoppingCartItemService(ShoppingCartItemDao shoppingCartItemDao,
                                   GoodsService goodsService, ShoppingCartConfig shoppingCartConfig) {
        this.shoppingCartItemDao = shoppingCartItemDao;
        this.goodsService = goodsService;
        this.shoppingCartConfig = shoppingCartConfig;
    }

    /**
     * 传递进需要添加的商品，如果购物车里有相同的商品则增加数量，不存在则新建数据。
     *
     * @param shoppingCartItem 购物车数据实体
     * @throws QuantityExceededException 超过了单个商品购买数量上限或者超过了购物车上限
     * @throws SQLException              没有成功添加
     * @throws NotFoundException         不存在该商品
     */
    public void addCartItem(ShoppingCartItem shoppingCartItem, long userId)
            throws QuantityExceededException, SQLException, NotFoundException {
        shoppingCartItem.setUserId(userId);
        Goods goodsToAdd = goodsService.getGoods(shoppingCartItem.getGoodsId());
        if (goodsToAdd == null || !goodsToAdd.getGoodsSellStatus()) {
            throw new NotFoundException();
        }
        ShoppingCartItem existingCartItem = shoppingCartItemDao.selectByUserIdAndGoodsId(shoppingCartItem.getUserId(), shoppingCartItem.getGoodsId());
        boolean cartAlreadyContainsItem = existingCartItem != null;
        if (cartAlreadyContainsItem) {
            increaseCartItem(existingCartItem, shoppingCartItem.getGoodsCount());
        } else {
            addNewCartItem(shoppingCartItem);
        }
    }

    private void increaseCartItem(ShoppingCartItem existingCartItem, int quantity) throws QuantityExceededException {
        int goodsCount = existingCartItem.getGoodsCount() + quantity;
        int totalCartItemCount = getCartItemCount(existingCartItem.getUserId()) + quantity;
        if (totalCartItemCount > shoppingCartConfig.getShoppingCartTotalLimit()) {
            throw new QuantityExceededException(TOTAL_CART_ITEMS_EXCEEDED_MESSAGE);
        }
        if (goodsCount > shoppingCartConfig.getItemLimit()) {
            throw new QuantityExceededException(SINGLE_ITEMS_COUNT_EXCEEDED_MESSAGE);
        }
        existingCartItem.setGoodsCount(goodsCount);
        existingCartItem.setUpdateTime(LocalDateTime.now());
        shoppingCartItemDao.updateByUserIdAndGoodsIdSelective(existingCartItem);
    }

    private void addNewCartItem(ShoppingCartItem shoppingCartItem) throws QuantityExceededException {
        int totalCartItemCount = getCartItemCount(shoppingCartItem.getUserId());
        totalCartItemCount += shoppingCartItem.getGoodsCount();
        if (totalCartItemCount > shoppingCartConfig.getShoppingCartTotalLimit()) {
            throw new QuantityExceededException(TOTAL_CART_ITEMS_EXCEEDED_MESSAGE);
        }
        if (shoppingCartItem.getGoodsCount() > shoppingCartConfig.getItemLimit()) {
            throw new QuantityExceededException(SINGLE_ITEMS_COUNT_EXCEEDED_MESSAGE);
        }
        shoppingCartItemDao.insertSelective(shoppingCartItem);
    }

    public int getCartItemCount(long userId) {
        return shoppingCartItemDao.selectCountByUserId(userId);
    }

    public void removeCartItem(Long userId, Long goodsId) {

        shoppingCartItemDao.deleteByGoodsIdAndUserId(userId, goodsId);
    }

    public List<ShoppingCartItemDTO> getUserShoppingCart(Long userId) {
        return shoppingCartItemDao.selectByUserId(userId);
    }

    public void updateShoppingCartItem(ShoppingCartItem shoppingCartItem) throws NotFoundException {
        int affectedRow = shoppingCartItemDao.updateByUserIdAndGoodsIdSelective(shoppingCartItem);
        if (affectedRow == 0) {
            throw new NotFoundException();
        }
    }
}
