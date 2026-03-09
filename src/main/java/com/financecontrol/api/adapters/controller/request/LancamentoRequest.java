package com.financecontrol.api.adapters.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequest(
        @NotNull(message = "O campo 'valor' é obrigatório")
        BigDecimal valor,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate data,

        @JsonProperty("id_subcategoria")
        @NotNull(message = "O campo 'id_subcategoria' é obrigatório")
        @Positive(message = "O campo 'id_subcategoria' deve ser um número positivo")
        Long idSubcategoria,

        @Nullable
        String comentario
) {
}

