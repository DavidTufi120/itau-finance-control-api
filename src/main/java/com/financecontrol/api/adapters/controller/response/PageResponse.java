package com.financecontrol.api.adapters.controller.response;

import com.financecontrol.api.domain.shared.PaginaResultado;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <S, T> PageResponse<T> de(PaginaResultado<S> resultado, Function<S, T> mapper) {
        return new PageResponse<>(
                resultado.conteudo().stream().map(mapper).toList(),
                resultado.paginaAtual(),
                resultado.tamanhoPagina(),
                resultado.totalElementos(),
                resultado.totalPaginas()
        );
    }
}
