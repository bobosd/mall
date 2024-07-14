package com.jiezipoi.mall.controller.mall;

import com.jiezipoi.mall.dto.ShoppingCartItemDTO;
import com.jiezipoi.mall.entity.ShoppingCartItem;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.exception.QuantityExceededException;
import com.jiezipoi.mall.service.ShoppingCartItemService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.CurrencyFormatter;
import com.jiezipoi.mall.utils.Response;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ShoppingCartItemController {
    private final ShoppingCartItemService shoppingCartItemService;

    private final UserService userService;

    public ShoppingCartItemController(ShoppingCartItemService shoppingCartItemService, UserService userService) {
        this.shoppingCartItemService = shoppingCartItemService;
        this.userService = userService;
    }

    @GetMapping("/shopping-cart")
    public String shoppingCartPage(Principal principal, ModelMap modelMap) {
        Long userId = userService.getUserIdByEmail(principal.getName());
        List<ShoppingCartItemDTO> itemList = shoppingCartItemService.getUserShoppingCart(userId);
        BigDecimal totalValue = shoppingCartItemService.calcShoppingCartTotalValue(itemList);
        String priceString = CurrencyFormatter.formatCurrency(totalValue);
        modelMap.put("totalPrice", priceString);
        modelMap.put("cartItems", itemList);
        return "mall/shopping-cart";
    }

    @PostMapping("/shopping-cart/add")
    @ResponseBody
    public Response<?> saveShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem, Principal principal) {
        if (shoppingCartItem.getGoodsCount() == null ||
                shoppingCartItem.getGoodsCount() < 1 ||
                shoppingCartItem.getGoodsId() == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            User user = userService.getUserByEmail(principal.getName());
            shoppingCartItemService.addCartItem(shoppingCartItem, user.getUserId());
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
    public Response<?> listUserShoppingCart(Principal principal) {
        Long userId = userService.getUserIdByEmail(principal.getName());
        List<ShoppingCartItemDTO> cartItems = shoppingCartItemService.getUserShoppingCart(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("items", cartItems);
        data.put("totalPrice", shoppingCartItemService.calcShoppingCartTotalValue(cartItems));
        Response<Map<String, Object>> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(data);
        return response;
    }

    @PostMapping("/shopping-cart/delete")
    @ResponseBody
    public Response<?> deleteShoppingCartItem(@RequestParam("goodsId") Long goodsId, Principal principal) {
        if (goodsId == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        long userId = userService.getUserIdByEmail(principal.getName());
        shoppingCartItemService.removeCartItem(userId, goodsId);
        return new Response<>(CommonResponse.DELETE_SUCCESS);
    }

    @PostMapping("/shopping-cart/update")
    @ResponseBody
    public Response<?> updateShoppingCartItem(@RequestBody ShoppingCartItem shoppingCartItem, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        shoppingCartItem.setUserId(userId);
        if (isNotValidShoppingCartItem(shoppingCartItem)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            shoppingCartItemService.updateShoppingCartItem(shoppingCartItem);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NotFoundException e) { //userId and goodsId not match
            return new Response<>(CommonResponse.INVALID_DATA);
        }
    }


    // --- private methods --- //

    private boolean isNotValidShoppingCartItem(ShoppingCartItem item) {
        return item.getGoodsId() == null ||
                item.getGoodsCount() == null ||
                item.getGoodsCount() < 0 ||
                item.getGoodsCount() > 9;
    }
}
