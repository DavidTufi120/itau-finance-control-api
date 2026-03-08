package com.financecontrol.api.adapters.controller.request;

import com.financecontrol.api.domain.shared.MensagensErro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = MensagensErro.CAMPO_NOME_OBRIGATORIO)
        @Size(max = 100, message = MensagensErro.CAMPO_NOME_TAMANHO_MAXIMO)
        String nome
) {}

