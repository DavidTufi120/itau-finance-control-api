package com.financecontrol.api.adapters.out.persistence.categoria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Long> {

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Long id);

    Page<CategoriaEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}

