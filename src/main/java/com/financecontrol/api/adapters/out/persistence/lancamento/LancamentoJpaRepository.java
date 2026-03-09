package com.financecontrol.api.adapters.out.persistence.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface LancamentoJpaRepository extends JpaRepository<LancamentoEntity, Long> {

    boolean existsBySubcategoriaId(Long idSubcategoria);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LancamentoEntity l WHERE l.subcategoria.categoria.id = :idCategoria")
    boolean existsBySubcategoriaCategoriaId(@Param("idCategoria") Long idCategoria);

    @Query("SELECT l FROM LancamentoEntity l WHERE " +
            "(:idSubcategoria IS NULL OR l.subcategoria.id = :idSubcategoria) AND " +
            "(:dataInicio IS NULL OR l.data >= :dataInicio) AND " +
            "(:dataFim IS NULL OR l.data <= :dataFim)")
    Page<LancamentoEntity> findByFiltros(
            @Param("idSubcategoria") Long idSubcategoria,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable);
}

