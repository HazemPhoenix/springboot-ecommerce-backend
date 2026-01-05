package io.spring.training.boot.server.utils.mappers;

import io.spring.training.boot.server.DTOs.BookOrderSummaryDto;
import io.spring.training.boot.server.DTOs.OrderItemRequestDto;
import io.spring.training.boot.server.DTOs.OrderItemResponseDto;
import io.spring.training.boot.server.models.OrderItem;

public class OrderItemMapper {
    public static OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem){
        BookOrderSummaryDto bookOrderSummaryDto = BookMapper.toBookOrderSummaryDto(orderItem.getBook());
        return new OrderItemResponseDto(orderItem.getId(), bookOrderSummaryDto, orderItem.getQuantity(), orderItem.getTotalItemPrice());
    }

    public static OrderItem fromOrderItemRequestDto(OrderItemRequestDto orderItemRequestDto){
        return new OrderItem(orderItemRequestDto.quantity());
    }
}
