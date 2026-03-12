package com.financecontrol.api.adapters.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoriaResponse(
        @JsonProperty("id_categoria")
        Long idCategoria,
        String nome
) {}

