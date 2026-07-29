package com.gwana.server.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long categoryId;
    private Long parentId;
    private String name;
    private String slug;
    private Integer depth;
    private Integer sortOrder;
}
