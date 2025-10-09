package com.sampoom.material.api.material.repository;

import com.sampoom.material.api.material.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialRepository extends JpaRepository<Material,Long> {
    Page<Material> findAll(Pageable pageable);

    Page<Material> findByMaterialCategoryId(Long categoryId, Pageable pageable);

    Page<Material> findByNameContainingIgnoreCaseOrMaterialCodeContainingIgnoreCase(
            String name, String materialCode, Pageable pageable);

    @Query("SELECT m.materialCode FROM Material m WHERE m.materialCategory.id = :categoryId ORDER BY m.id DESC LIMIT 1")
    String findTopByCategoryOrderByCodeDesc(@Param("categoryId") Long categoryId);
}
