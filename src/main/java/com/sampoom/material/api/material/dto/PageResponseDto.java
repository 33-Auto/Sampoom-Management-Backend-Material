package com.sampoom.material.api.material.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;         // 실제 데이터
    private long totalElements;      // 총 요소 수
    private int totalPages;          // 총 페이지 수
}