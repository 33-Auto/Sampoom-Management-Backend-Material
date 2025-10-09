package com.sampoom.material.api.material.service;

import com.sampoom.material.api.material.dto.MaterialCategoryResponseDto;
import com.sampoom.material.api.material.dto.MaterialRequestDto;
import com.sampoom.material.api.material.dto.MaterialResponseDto;
import com.sampoom.material.api.material.dto.PageResponseDto;
import com.sampoom.material.api.material.entity.Material;
import com.sampoom.material.api.material.entity.MaterialCategory;
import com.sampoom.material.api.material.repository.MaterialCategoryRepository;
import com.sampoom.material.api.material.repository.MaterialRepository;
import com.sampoom.material.common.exception.NotFoundException;
import com.sampoom.material.common.response.ApiResponse;
import com.sampoom.material.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialCategoryRepository categoryRepository;

    @Transactional
    public PageResponseDto<MaterialResponseDto> getAllMaterials(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Material> materialsPage = materialRepository.findAll(pageRequest);

        List<MaterialResponseDto> materialDtos = materialsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PageResponseDto.<MaterialResponseDto>builder()
                .content(materialDtos)
                .totalElements(materialsPage.getTotalElements())
                .totalPages(materialsPage.getTotalPages())
                .build();
    }

    @Transactional
    public MaterialResponseDto getMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MATERIAL_NOT_FOUND));
        return convertToDto(material);
    }

    @Transactional
    public MaterialResponseDto createMaterial(MaterialRequestDto requestDto) {
        // 카테고리 조회
        MaterialCategory category = categoryRepository.findById(requestDto.getMaterialCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 자재 코드 생성 (카테고리 코드 + 일련번호)
        String materialCode = generateMaterialCode(category);

        // 자재 생성
        Material material = Material.builder()
                .name(requestDto.getName())
                .materialCode(materialCode)
                .materialCategory(category)
                .build();

        material = materialRepository.save(material);
        return convertToDto(material);
    }


    @Transactional
    public MaterialResponseDto updateMaterial(Long id, MaterialRequestDto requestDto) {
        // 자재 조회
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MATERIAL_NOT_FOUND));

        // 새 카테고리 조회
        MaterialCategory newCategory = categoryRepository.findById(requestDto.getMaterialCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.CATEGORY_NOT_FOUND));

        String materialCode = material.getMaterialCode();

        // 카테고리가 변경된 경우 코드도 변경
        if (!material.getMaterialCategory().getId().equals(requestDto.getMaterialCategoryId())) {
            // 기존에 구현된 generateMaterialCode 함수 활용
            materialCode = generateMaterialCode(newCategory);
        }

        // 자재 업데이트
        Material updatedMaterial = Material.builder()
                .id(material.getId())
                .name(requestDto.getName())
                .materialCode(materialCode)
                .materialCategory(newCategory)
                .build();

        materialRepository.save(updatedMaterial);

        return convertToDto(updatedMaterial);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MATERIAL_NOT_FOUND));
        materialRepository.delete(material);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<MaterialResponseDto> getMaterialsByCategory(Long categoryId, int page, int size) {
        // 카테고리 존재 여부 확인
        MaterialCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.CATEGORY_NOT_FOUND));

        // 페이지네이션 설정
        PageRequest pageRequest = PageRequest.of(page, size);

        // 해당 카테고리의 자재 조회
        Page<Material> materialsPage = materialRepository.findByMaterialCategoryId(categoryId, pageRequest);

        List<MaterialResponseDto> materialDtos = materialsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PageResponseDto.<MaterialResponseDto>builder()
                .content(materialDtos)
                .totalElements(materialsPage.getTotalElements())
                .totalPages(materialsPage.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponseDto<MaterialResponseDto> searchMaterials(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Material> materialsPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            materialsPage = materialRepository.findAll(pageRequest);
        } else {
            materialsPage = materialRepository.findByNameContainingIgnoreCaseOrMaterialCodeContainingIgnoreCase(
                    keyword, keyword, pageRequest);
        }

        List<MaterialResponseDto> materialDtos = materialsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return PageResponseDto.<MaterialResponseDto>builder()
                .content(materialDtos)
                .totalElements(materialsPage.getTotalElements())
                .totalPages(materialsPage.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MaterialCategoryResponseDto> getAllCategories() {
        List<MaterialCategory> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MaterialCategoryResponseDto convertToDto(MaterialCategory category) {
        return MaterialCategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .build();
    }

    private MaterialResponseDto convertToDto(Material material) {
        return MaterialResponseDto.builder()
                .id(material.getId())
                .name(material.getName())
                .materialCode(material.getMaterialCode())
                .materialCategoryId(material.getMaterialCategory().getId())
                .materialCategoryName(material.getMaterialCategory().getName())
                .build();
    }

    // 자재 코드 생성 메서드
    private String generateMaterialCode(MaterialCategory category) {
        // 카테고리 코드 가져오기 (예: "METAL")
        String categoryCode = category.getCode();

        // 해당 카테고리의 최대 일련번호 조회
        String latestCode = materialRepository.findTopByCategoryOrderByCodeDesc(category.getId());

        int sequence = 1; // 기본값
        if (latestCode != null && latestCode.contains("-")) {
            // 예: "METAL-0001"에서 "0001" 부분 추출
            String numberPart = latestCode.split("-")[1];
            try {
                sequence = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                // 숫자 파싱 실패 시 기본값 1 사용
            }
        }

        // 포맷: "METAL-0001"
        return String.format("%s-%04d", categoryCode, sequence);
    }
}