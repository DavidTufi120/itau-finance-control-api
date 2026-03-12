package com.financecontrol.api.adapters.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoResponse(
        @JsonProperty("id_lancamento")
        Long idLancamento,
        BigDecimal valor,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate data,
        @JsonProperty("id_subcategoria")
        Long idSubcategoria,
        String comentario
) {
}

