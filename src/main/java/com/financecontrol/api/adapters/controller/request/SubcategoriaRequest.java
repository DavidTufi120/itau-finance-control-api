package com.financecontrol.api.adapters.controller.request;

import com.financecontrol.api.domain.shared.MensagensErro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SubcategoriaRequest(
        @NotBlank(message = MensagensErro.CAMPO_NOME_OBRIGATORIO)
        @Size(max = 100, message = MensagensErro.CAMPO_NOME_TAMANHO_MAXIMO)
        String nome,

        @NotNull(message = "O campo 'id_categoria' e obrigatorio")
        @Positive(message = "O campo 'id_categoria' deve ser um numero positivo")
        Long id_categoria
) {
}
