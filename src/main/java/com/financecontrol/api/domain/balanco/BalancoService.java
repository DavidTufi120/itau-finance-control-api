package com.financecontrol.api.domain.balanco;

import java.time.LocalDate;

public interface BalancoService {

    Balanco calcular(LocalDate dataInicio, LocalDate dataFim, Long idCategoria);
}

