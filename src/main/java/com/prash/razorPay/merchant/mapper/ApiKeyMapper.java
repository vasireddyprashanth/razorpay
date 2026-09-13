package com.prash.razorPay.merchant.mapper;

import com.prash.razorPay.merchant.dto.response.ApiKeyCreateResponse;
import com.prash.razorPay.merchant.dto.response.ApiKeyResponse;
import com.prash.razorPay.merchant.entity.ApiKey;
import com.prash.razorPay.merchant.repository.ApiKeyRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {

    ApiKeyCreateResponse toResponse(ApiKey apiKey);

    List<ApiKeyResponse> toResponseList(List<ApiKey> apiKey);
}
