package com.financecontrol.api.adapters.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.financecontrol.api.domain.shared.MensagensErro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SubcategoriaRequest(
        @NotBlank(message = MensagensErro.CAMPO_NOME_OBRIGATORIO)
        @Size(max = 100, message = MensagensErro.CAMPO_NOME_TAMANHO_MAXIMO)
        String nome,

        @JsonProperty("id_categoria")
        @NotNull(message = "O campo 'id_categoria' e obrigatorio")
        @Positive(message = "O campo 'id_categoria' deve ser um numero positivo")
        Long idCategoria
) {
}
