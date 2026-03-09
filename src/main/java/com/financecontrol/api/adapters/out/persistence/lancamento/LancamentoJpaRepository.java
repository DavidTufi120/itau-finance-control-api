package com.financecontrol.api.adapters.out.persistence.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface LancamentoJpaRepository extends JpaRepository<LancamentoEntity, Long> {

    boolean existsBySubcategoriaId(Long idSubcategoria);

    boolean existsBySubcategoriaCategoriaId(Long categoriaId);

    @Query("SELECT COALESCE(SUM(l.valor), 0) FROM LancamentoEntity l WHERE " +
            "l.valor > 0 AND " +
            "l.data >= :dataInicio AND l.data <= :dataFim AND " +
            "(:idCategoria IS NULL OR l.subcategoria.categoria.id = :idCategoria)")
    BigDecimal somarReceitas(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("idCategoria") Long idCategoria);

    @Query("SELECT COALESCE(SUM(ABS(l.valor)), 0) FROM LancamentoEntity l WHERE " +
            "l.valor < 0 AND " +
            "l.data >= :dataInicio AND l.data <= :dataFim AND " +
            "(:idCategoria IS NULL OR l.subcategoria.categoria.id = :idCategoria)")
    BigDecimal somarDespesas(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("idCategoria") Long idCategoria);

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

