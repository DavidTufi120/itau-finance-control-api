package com.financecontrol.api.domain.lancamento;

import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;

import java.time.LocalDate;

public interface LancamentoService {

    PaginaResultado<Lancamento> listar(Long idSubcategoria, LocalDate dataInicio, LocalDate dataFim, ParametrosPaginacao paginacao);

    Lancamento buscarPorId(Long id);

    Lancamento criar(Lancamento lancamento);

    Lancamento atualizar(Long id, Lancamento lancamento);

    void deletar(Long id);
}

