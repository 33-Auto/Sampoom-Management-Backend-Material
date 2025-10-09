package com.sampoom.material.api.material.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialCategoryResponseDto {
    private Long id;
    private String name;
    private String code;
}