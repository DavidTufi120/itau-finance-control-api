package com.financecontrol.api.adapters.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoResponse(
        Long id_lancamento,
        BigDecimal valor,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate data,
        Long id_subcategoria,
        String comentario
) {
}

