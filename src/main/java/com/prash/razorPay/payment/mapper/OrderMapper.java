package com.prash.razorPay.payment.mapper;

import com.prash.razorPay.payment.dto.response.OrderResponse;
import com.prash.razorPay.payment.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(source = "orderStatus", target = "status")
    OrderResponse toResponse(OrderRecord record);


}
