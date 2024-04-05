package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.entity.ShoppingCartItem;
import com.jiezipoi.mall.exception.ExceedsQuantityException;
import com.jiezipoi.mall.service.ShoppingCartItemService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.sql.SQLException;

@Controller
public class ShoppingCartItemController {
    private final ShoppingCartItemService shoppingCartItemService;

    public ShoppingCartItemController(ShoppingCartItemService shoppingCartItemService) {
        this.shoppingCartItemService = shoppingCartItemService;
    }

    @PostMapping("/shopping-cart/add")
    @ResponseBody
    public Response<?> saveShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem, Principal principal) {
        try {
            String userEmail = principal.getName();
            if (userEmail == null || shoppingCartItem.getGoodsId() == null) {
                throw new NullPointerException();
            }
            shoppingCartItemService.saveCartItem(shoppingCartItem);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (ExceedsQuantityException e) {
            Response<String> response = new Response<>();
            response.setCode(400);
            response.setMessage(e.getMessage());
            return response;
        } catch (SQLException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
    }
}