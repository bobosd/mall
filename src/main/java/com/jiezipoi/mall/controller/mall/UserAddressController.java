package com.jiezipoi.mall.controller.mall;

import com.jiezipoi.mall.entity.UserAddress;
import com.jiezipoi.mall.exception.LimitExceededException;
import com.jiezipoi.mall.exception.NotFoundException;
import com.jiezipoi.mall.service.UserAddressService;
import com.jiezipoi.mall.service.UserService;
import com.jiezipoi.mall.utils.CommonResponse;
import com.jiezipoi.mall.utils.Response;
import com.jiezipoi.mall.utils.Validator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/user/address")
public class UserAddressController {
    private final UserAddressService addressService;
    private final UserService userService;

    public UserAddressController(UserAddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @PostMapping("/create")
    @ResponseBody
    public Response<?> storeAddress(@RequestBody UserAddress userAddress, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        userAddress.setUserId(userId);
        if (!isValidForStore(userAddress)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            int addressCount = addressService.getUserAddressCount(userId);
            if (addressCount < 1) {
                userAddress.setDefaultAddress(true);
            }
            addressService.createUserAddress(userAddress);
            Response<UserAddress> response = new Response<>(CommonResponse.SUCCESS);
            response.setData(userAddress);
            return response;
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        } catch (LimitExceededException e) {
            Response<String> response = new Response<>(CommonResponse.FORBIDDEN);
            response.setData("limit exceeded");
            return response;
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Response<?> updateAddress(@RequestBody UserAddress userAddress, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        userAddress.setUserId(userId);
        if (!isValidForStore(userAddress)) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            addressService.updateUserAddress(userAddress);
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        }
        return new Response<>(CommonResponse.SUCCESS);
    }

    @PostMapping("/delete")
    @ResponseBody
    public Response<?> deleteAddress(@RequestParam("addressId") Long userAddressId, Principal principal) {
        long userId = userService.getUserIdByEmail(principal.getName());
        if (userAddressId == null || userAddressId <= 0) {
            return new Response<>(CommonResponse.INVALID_DATA);
        }
        try {
            Response<UserAddress> response = new Response<>(CommonResponse.DELETE_SUCCESS);
            UserAddress removedAddress = addressService.deleteUserAddress(userId, userAddressId);
            if (removedAddress.isDefaultAddress()) {
                UserAddress promotedDefaultAddress = addressService.setAnyAsDefaultAddress(userId);
                response.setData(promotedDefaultAddress);
            }
            return response;
        } catch (NotFoundException e) {
            return new Response<>(CommonResponse.DATA_NOT_EXIST);
        }
    }

    private boolean isValidForStore(UserAddress userAddress) {
        return userAddress.getUserId() != null &&
                userAddress.getFirstName() != null &&
                !userAddress.getFirstName().isBlank() &&
                userAddress.getSurname() != null &&
                !userAddress.getSurname().isBlank() &&
                userAddress.getPhoneNumber() != null &&
                Validator.isInteger(userAddress.getPhoneNumber()) &&
                userAddress.getProvince() != null &&
                !userAddress.getProvince().isBlank() &&
                userAddress.getCity() != null &&
                !userAddress.getCity().isBlank() &&
                userAddress.getStreetAddress() != null &&
                !userAddress.getStreetAddress().isBlank() &&
                userAddress.getPostalCode() != null &&
                Validator.isInteger(userAddress.getPostalCode()) &&
                userAddress.getPostalCode().length() == 5;
    }
}
