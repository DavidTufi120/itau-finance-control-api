package com.financecontrol.api.adapters.out.persistence.balanco;

import com.financecontrol.api.adapters.out.persistence.lancamento.LancamentoJpaRepository;
import com.financecontrol.api.domain.balanco.BalancoRepositoryPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class BalancoRepositoryAdapter implements BalancoRepositoryPort {
    private final LancamentoJpaRepository jpaRepository;

    public BalancoRepositoryAdapter(LancamentoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BigDecimal somarReceitas(LocalDate dataInicio, LocalDate dataFim, Long idCategoria) {
        return jpaRepository.somarReceitas(dataInicio, dataFim, idCategoria);
    }

    @Override
    public BigDecimal somarDespesas(LocalDate dataInicio, LocalDate dataFim, Long idCategoria) {
        return jpaRepository.somarDespesas(dataInicio, dataFim, idCategoria);
    }
}