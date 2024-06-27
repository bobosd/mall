package com.jiezipoi.mall.controller.mall;


import com.jiezipoi.mall.dto.UserPaymentDTO;
import com.jiezipoi.mall.entity.User;
import com.jiezipoi.mall.entity.UserPayment;
import com.jiezipoi.mall.exception.LimitExceededException;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.service.UserPaymentService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @ResponseBody
    public Response<?> createPayment(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        if (user == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }

        try {
            UserPayment payment = userPaymentService.createUserPayment(user);
            Response<UserPayment> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(payment);
            return response;
        } catch (LimitExceededException e) {
            Response<String> error = new Response<>(CommonResponse.FORBIDDEN);
            error.setData("limit exceeded");
            return error;
        }
    }

    @DeleteMapping
    @ResponseBody
    public Response<?> removePayment(@Param("paymentId") Long paymentId, Principal principal) {
        if (paymentId == null) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        long userId = userService.getUserIdByEmail(principal.getName());
        try {
            UserPayment newDefault = userPaymentService.deletePayment(userId, paymentId);
            Response<UserPaymentDTO> response = new Response<>(CommonResponse.DELETE_SUCCESS);
            response.setData(new UserPaymentDTO(newDefault));
            return response;
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
    }

    @PutMapping("/default")
    @ResponseBody
    public Response<?> setDefaultPayment(@Param("paymentId") Long paymentId, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        try {
            userPaymentService.setAsDefaultPayment(userId, paymentId);
            return new Response<>(CommonResponse.SUCCESS);
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        }
    }
}