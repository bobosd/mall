package com.jiezipoi.mall.controller.mall;

import com.jiezipoi.mall.dto.UserPaymentDTO;
import com.jiezipoi.mall.entity.UserAddress;
import com.jiezipoi.mall.exception.OutOfStockException;
import com.jiezipoi.mall.exception.UnavailableGoodsException;
import com.jiezipoi.mall.service.*;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.CurrencyFormatter;
import com.jiezipoi.mall.utils.Response;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrderController {
    private final UserService userService;
    private final UserAddressService addressService;
    private final UserPaymentService paymentService;
    private final ShoppingCartItemService shoppingCartItemService;
    private final OrderService orderService;

    public OrderController(UserService userService, UserAddressService addressService, UserPaymentService paymentService,
                           ShoppingCartItemService shoppingCartItemService, OrderService orderService) {
        this.userService = userService;
        this.addressService = addressService;
        this.paymentService = paymentService;
        this.shoppingCartItemService = shoppingCartItemService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String checkoutPage(ModelMap modelMap, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        List<UserAddress> addresses = addressService.getUserAddresList(userId);
        List<UserPaymentDTO> payments = paymentService.getUserPaymentList(userId)
                .stream()
                .map(UserPaymentDTO::new)
                .collect(Collectors.toList());
        BigDecimal totalPrice = shoppingCartItemService.getShoppingCartTotalValue(userId);
        modelMap.put("addresses", addresses);
        modelMap.put("payments", payments);
        modelMap.put("totalPrice", CurrencyFormatter.formatCurrency(totalPrice));
        return "mall/checkout";
    }

    @PostMapping("/checkout")
    @ResponseBody
    public Response<?> checkout(Long paymentId, Long addressId, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        try {
            orderService.checkout(userId, paymentId, addressId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (UnavailableGoodsException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        } catch (OutOfStockException e) {
            return new Response<>(CommonResponse.DATA_INTEGRITY_VIOLATION);
        }
    }
}
