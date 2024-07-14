package com.jiezipoi.mall.service;

import com.jiezipoi.mall.dao.OrderDao;
import com.jiezipoi.mall.dao.OrderDetailDao;
import com.jiezipoi.mall.dto.ShoppingCartItemDTO;
import com.jiezipoi.mall.entity.*;
import com.jiezipoi.mall.enums.OrderStatus;
import com.jiezipoi.mall.enums.PaymentStatus;
import com.jiezipoi.mall.exception.OutOfStockException;
import com.jiezipoi.mall.exception.UnavailableGoodsException;
import com.jiezipoi.mall.utils.OrderNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final ShoppingCartItemService shoppingCartItemService;
    private final GoodsService goodsService;
    private final UserPaymentService userPaymentService;
    private final UserAddressService userAddressService;
    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;

    public OrderService(ShoppingCartItemService shoppingCartItemService, GoodsService goodsService,
                        UserPaymentService userPaymentService, UserAddressService userAddressService,
                        OrderDao orderDao, OrderDetailDao orderDetailDao) {
        this.shoppingCartItemService = shoppingCartItemService;
        this.goodsService = goodsService;
        this.userPaymentService = userPaymentService;
        this.userAddressService = userAddressService;
        this.orderDao = orderDao;
        this.orderDetailDao = orderDetailDao;
    }

    @Transactional
    public void checkout(long userId, long paymentId, long addressId) throws UnavailableGoodsException, OutOfStockException {
        List<ShoppingCartItemDTO> shoppingCartItems = shoppingCartItemService.getUserShoppingCart(userId);
        List<Long> goodsIds = shoppingCartItems.stream()
                .map(ShoppingCartItemDTO::getGoodsId)
                .toList();
        List<Goods> goodsToBuy = goodsService.getGoodsListById(goodsIds);
        if (containsUnavailableGoods(goodsToBuy)) {
            throw new UnavailableGoodsException();
        }
        if (outOfStock(shoppingCartItems, goodsToBuy)) {
            throw new OutOfStockException();
        }

        Map<Long, Integer> quantityList = shoppingCartItems.stream()
                .collect(Collectors.toMap(ShoppingCartItemDTO::getGoodsId, ShoppingCartItemDTO::getGoodsCount));
        goodsService.reduceGoodsStock(quantityList);
        Order orderWithHeader = generateOrderWithHeader(userId, paymentId, addressId);
        createOrder(orderWithHeader, shoppingCartItems);
        clearShoppingCart(userId);
    }

    private void clearShoppingCart(long userId) {
        shoppingCartItemService.removeAllCartItem(userId);
    }

    private void createOrderDetails(Long orderId, List<ShoppingCartItemDTO> cartItems) {
        List<OrderDetail> list = cartItems.stream()
                .map((item) -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrderId(orderId);
                    orderDetail.setGoodsId(item.getGoodsId());
                    orderDetail.setGoodsName(item.getGoodsName());
                    orderDetail.setGoodsCoverImg(item.getGoodsCoverImg());
                    orderDetail.setSellingPrice(item.getSellingPrice());
                    orderDetail.setQuantity(item.getGoodsCount());
                    orderDetail.setCreateTime(LocalDateTime.now());
                    return orderDetail;
                })
                .toList();
        orderDetailDao.insertBatch(list);
    }

    private void createOrder(Order headOrder, List<ShoppingCartItemDTO> shoppingCartItems) {
        BigDecimal totalValue = shoppingCartItemService.calcShoppingCartTotalValue(shoppingCartItems);
        headOrder.setTotalPrice(totalValue);
        orderDao.insert(headOrder);
        createOrderDetails(headOrder.getOrderId(), shoppingCartItems);
    }


    private Order generateOrderWithHeader(long userId, long paymentId, long addressId) {
        Order order = new Order();
        UserPayment payment = userPaymentService.getUserPayment(userId, paymentId);
        UserAddress address = userAddressService.getUserAddress(addressId);
        order.setOrderNumber(OrderNumberGenerator.generate());
        order.setUserId(userId);
        order.setPayStatus(PaymentStatus.PAID);
        order.setPayType(payment.getPaymentType());
        order.setOrderStatus(OrderStatus.PAID);
        order.setExtraInfo("");
        order.setName(address.getFirstName());
        order.setSurname(address.getSurname());
        order.setShippingPhoneNumber(address.getPhoneNumber());
        order.setShippingProvince(address.getProvince());
        order.setShippingCity(address.getCity());
        order.setShippingStreetAddress(address.getStreetAddress());
        order.setShippingPostalCode(address.getPostalCode());
        order.setCreateTime(LocalDateTime.now());
        order.setDeleted(false);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return order;
    }

    private boolean containsUnavailableGoods(List<Goods> goodsList) {
        return goodsList.stream().anyMatch(goods -> !goods.getGoodsSellStatus());
    }

    private boolean outOfStock(List<ShoppingCartItemDTO> shoppingCart, List<Goods> goodsList) {
        Map<Long, Integer> shoppingList = shoppingCart.stream()
                .collect(Collectors.toMap(ShoppingCartItemDTO::getGoodsId, ShoppingCartItemDTO::getGoodsCount));
        return goodsList.stream().anyMatch(goods -> {
            int amount = shoppingList.get(goods.getGoodsId());
            int stock = goods.getStockNum();
            return stock < amount;
        });
    }
}
