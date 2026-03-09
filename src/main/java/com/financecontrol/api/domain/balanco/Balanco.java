package com.financecontrol.api.domain.balanco;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;

public record Balanco(@Nullable Long idCategoria, @Nullable String nomeCategoria, BigDecimal receita,
                      BigDecimal despesa, BigDecimal saldo) {

}

