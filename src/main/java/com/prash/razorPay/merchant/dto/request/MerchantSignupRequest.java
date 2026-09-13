package com.prash.razorPay.merchant.dto.request;

import com.prash.razorPay.common.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantSignupRequest(

        @NotNull(message = "Name is required")
        @Size(max = 50)
         String name,

         @Email
         @NotNull(message = "email is required")
         String email,

         @NotNull(message = "password is required")
         @Size(min = 8, message = "Password should be least 8 characters long")
         String password,

         @Size(max = 50, message = "Business name should not be more than 50 characters long")
         String businessName,

         BusinessType businessType
) {
}
