package com.financecontrol.api.domain.shared;

import java.util.List;

public record PaginaResultado<T>(List<T> conteudo, int paginaAtual, int tamanhoPagina, long totalElementos,
                                 int totalPaginas) {
}

