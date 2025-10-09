package com.sampoom.material.api.material.repository;

import com.sampoom.material.api.material.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory,Long> {
}
