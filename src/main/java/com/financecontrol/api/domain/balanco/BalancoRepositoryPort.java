package com.financecontrol.api.domain.balanco;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface BalancoRepositoryPort {

    BigDecimal somarReceitas(LocalDate dataInicio, LocalDate dataFim, Long idCategoria);

    BigDecimal somarDespesas(LocalDate dataInicio, LocalDate dataFim, Long idCategoria);
}

