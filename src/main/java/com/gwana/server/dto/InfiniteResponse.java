package com.gwana.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfiniteResponse<T> {
    private T data;
    private int page;
    private int size;
    private long totalCount;
    private boolean hasNext;
    private BigDecimal averageRating;
}
