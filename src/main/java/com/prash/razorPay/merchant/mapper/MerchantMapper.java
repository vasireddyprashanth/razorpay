package com.prash.razorPay.merchant.mapper;

import com.prash.razorPay.merchant.dto.response.MerchantResponse;
import com.prash.razorPay.merchant.entity.AppUser;
import com.prash.razorPay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    MerchantResponse toSignup(AppUser user);

    MerchantResponse toGetMerchant(Merchant merchant);
}
