package com.financecontrol.api.adapters.out.persistence.subcategoria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface SubcategoriaJpaRepository extends JpaRepository<SubcategoriaEntity, Long> {

    boolean existsByNomeAndCategoriaId(String nome, Long idCategoria);

    boolean existsByNomeAndCategoriaIdAndIdNot(String nome, Long idCategoria, Long id);

    Page<SubcategoriaEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<SubcategoriaEntity> findById(Long id, Pageable pageable);

    Page<SubcategoriaEntity> findByNomeContainingIgnoreCaseAndId(String nome, Long id, Pageable pageable);
}
