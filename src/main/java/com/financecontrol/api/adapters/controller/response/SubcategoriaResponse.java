package com.financecontrol.api.adapters.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubcategoriaResponse(
        @JsonProperty("id_subcategoria")
        Long idSubcategoria,
        String nome,
        @JsonProperty("id_categoria")
        Long idCategoria
) {}
