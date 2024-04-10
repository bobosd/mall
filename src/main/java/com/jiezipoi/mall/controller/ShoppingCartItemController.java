package com.jiezipoi.mall.controller;

import com.jiezipoi.mall.dto.ShoppingCartItemDTO;
import com.jiezipoi.mall.entity.MallUser;
import com.jiezipoi.mall.entity.ShoppingCartItem;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.exception.QuantityExceededException;
import com.jiezipoi.mall.service.MallUserService;
import com.jiezipoi.mall.service.ShoppingCartItemService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

@Controller
public class ShoppingCartItemController {
    private final ShoppingCartItemService shoppingCartItemService;

    private final MallUserService mallUserService;

    public ShoppingCartItemController(ShoppingCartItemService shoppingCartItemService, MallUserService mallUserService) {
        this.shoppingCartItemService = shoppingCartItemService;
        this.mallUserService = mallUserService;
    }

    @PostMapping("/shopping-cart/add")
    @ResponseBody
    @PreAuthorize("hasAuthority('mallUser')")
    public Response<?> saveShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem, Principal principal) {
        try {
            MallUser mallUser = mallUserService.getMallUserByEmail(principal.getName());
            shoppingCartItemService.saveCartItem(shoppingCartItem, mallUser.getId());
            return new Response<>(CommonResponse.SUCCESS);
        } catch (QuantityExceededException e) {
            Response<String> response = new Response<>();
            response.setCode(400);
            response.setMessage(e.getMessage());
            return response;
        } catch (SQLException e) {
            return new Response<>(CommonResponse.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException | NullPointerException | NotFoundException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
    }

    @PostMapping("/shopping-cart/list")
    @ResponseBody
    @PreAuthorize("hasAuthority('mallUser')")
    public Response<?> listUserShoppingCart() {
        List<ShoppingCartItemDTO> cartContent = shoppingCartItemService.getUserShoppingCart();
        Response<List<ShoppingCartItemDTO>> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(cartContent);
        return response;
    }

    @PostMapping("/shopping-cart/delete")
    @ResponseBody
    public Response<?> deleteShoppingCartItem() {
        return new Response<>();
    }
}