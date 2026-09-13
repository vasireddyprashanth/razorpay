package com.prash.razorPay.merchant.service;

import com.prash.razorPay.merchant.dto.request.LoginRequest;
import com.prash.razorPay.merchant.dto.response.LoginResponse;
import com.prash.razorPay.merchant.dto.test.MerchantGetRequest;
import com.prash.razorPay.merchant.dto.request.MerchantSignupRequest;
import com.prash.razorPay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    MerchantResponse getMerchant(MerchantGetRequest request);

    LoginResponse login(LoginRequest request);
}
