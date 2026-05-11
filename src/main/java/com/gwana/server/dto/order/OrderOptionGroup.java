package com.gwana.server.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOptionGroup {
    private Long productId;
    private String productName;
    private String productThumbnailUrl;
    private String categoryName;
    private List<OrderOption> orderOptions;
}
