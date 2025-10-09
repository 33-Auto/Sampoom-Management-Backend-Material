package com.sampoom.material.api.material.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "material_category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_category_id")
    private Long id;

    private String name;
    private String code;
}
