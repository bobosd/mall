package com.jiezipoi.mall.controller.mall;


import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.entity.UserPayment;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.service.UserPaymentService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping("/user/payment")
public class UserPaymentController {
    private final UserPaymentService userPaymentService;
    private final UserService userService;

    public UserPaymentController(UserPaymentService userPaymentService, UserService userService) {
        this.userPaymentService = userPaymentService;
        this.userService = userService;
    }

    @PostMapping("/create")
    @ResponseBody
    public Response<?> createPayment(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        if (user == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        UserPayment payment = userPaymentService.createUserPayment(user);
        Response<UserPayment> response = new Response<>(CommonResponse.SUCCESS);
        response.setData(payment);
        return response;
    }

    @PostMapping("/delete")
    @ResponseBody
    public Response<?> removePayment(@Param("paymentId") Long paymentId, Principal principal) {
        if (paymentId == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        long userId = userService.getUserIdByEmail(principal.getName());
        try {
            userPaymentService.deletePayment(userId, paymentId);
            return new Response<>(CommonResponse.DELETE_SUCCESS);
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
    }
}