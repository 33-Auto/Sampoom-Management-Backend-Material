package com.sampoom.material.api.material.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialResponseDto {
    private Long id;
    private String name;
    private String materialCode;
    private Long materialCategoryId;
    private String materialCategoryName;
}