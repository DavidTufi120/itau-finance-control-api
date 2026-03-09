package com.financecontrol.api.adapters.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BalancoResponse(
        @Nullable CategoriaResponse categoria,
        String receita,
        String despesa,
        String saldo
) {
}