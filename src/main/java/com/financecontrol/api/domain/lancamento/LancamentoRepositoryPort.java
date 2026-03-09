package com.financecontrol.api.domain.lancamento;

import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;

import java.time.LocalDate;
import java.util.Optional;

public interface LancamentoRepositoryPort {

    Optional<Lancamento> findById(Long id);

    PaginaResultado<Lancamento> findAll(ParametrosPaginacao paginacao);

    PaginaResultado<Lancamento> findByFiltros(Long idSubcategoria, LocalDate dataInicio, LocalDate dataFim, ParametrosPaginacao paginacao);

    boolean existsByIdSubcategoria(Long idSubcategoria);

    boolean existsByIdCategoria(Long idCategoria);

    Lancamento save(Lancamento lancamento);

    void deleteById(Long id);
}

