package com.sampoom.material.api.material.controller;

import com.sampoom.material.api.material.dto.MaterialCategoryResponseDto;
import com.sampoom.material.api.material.dto.MaterialRequestDto;
import com.sampoom.material.api.material.dto.MaterialResponseDto;
import com.sampoom.material.api.material.dto.PageResponseDto;
import com.sampoom.material.api.material.service.MaterialService;
import com.sampoom.material.common.response.ApiResponse;
import com.sampoom.material.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Material", description = "Material 관련 API 입니다.")
@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "자재 목록 조회", description = "모든 자재 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<MaterialResponseDto>>> getAllMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(SuccessStatus.OK, materialService.getAllMaterials(page, size));
    }

    @Operation(summary = "자재 상세 조회", description = "자재 ID로 특정 자재 정보를 조회합니다.")
    @GetMapping("/{materialId}")
    public ResponseEntity<ApiResponse<MaterialResponseDto>> getMaterialById(@PathVariable("materialId") Long id) {
        return ApiResponse.success(SuccessStatus.OK, materialService.getMaterialById(id));
    }

    @Operation(summary = "자재 등록", description = "새로운 자재를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MaterialResponseDto>> createMaterial(@RequestBody MaterialRequestDto materialDto) {

        return ApiResponse.success(SuccessStatus.CREATED,materialService.createMaterial(materialDto));
    }

    @Operation(summary = "자재 수정", description = "기존 자재 정보를 수정합니다.")
    @PutMapping("/{materialId}")
    public ResponseEntity<ApiResponse<MaterialResponseDto>> updateMaterial(@PathVariable("materialId") Long id, @RequestBody MaterialRequestDto requestDto) {

        return ApiResponse.success(SuccessStatus.OK,materialService.updateMaterial(id, requestDto));
    }

    @Operation(summary = "자재 삭제", description = "자재를 삭제합니다.")
    @DeleteMapping("/{materialId}")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(@PathVariable("materialId") Long id) {
        materialService.deleteMaterial(id);
        return ApiResponse.success_only(SuccessStatus.OK);
    }

    @Operation(summary = "카테고리별 자재 조회", description = "특정 카테고리에 속한 자재를 조회합니다.")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponseDto<MaterialResponseDto>>> getMaterialsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(SuccessStatus.OK, materialService.getMaterialsByCategory(categoryId, page, size));
    }

    @Operation(summary = "카테고리 목록 조회", description = "모든 자재 카테고리를 조회합니다.")
    @GetMapping("/category")
    public ResponseEntity<ApiResponse<List<MaterialCategoryResponseDto>>> getAllCategories() {
        return ApiResponse.success(SuccessStatus.OK, materialService.getAllCategories());
    }

    @Operation(summary = "자재 검색", description = "자재명 또는 자재 코드로 자재를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponseDto<MaterialResponseDto>>> searchMaterials(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(SuccessStatus.OK, materialService.searchMaterials(keyword, page, size));
    }



}
