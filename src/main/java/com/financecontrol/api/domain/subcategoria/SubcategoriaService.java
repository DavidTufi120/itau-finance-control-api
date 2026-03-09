package com.financecontrol.api.domain.subcategoria;

import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;

public interface SubcategoriaService {
    Subcategoria buscarPorId(Long id);

    PaginaResultado<Subcategoria> listar(String nome, Long idSubcategoria, ParametrosPaginacao paginacao);

    Subcategoria criar(Subcategoria subcategoria);

    Subcategoria atualizar(Long id, Subcategoria subcategoria);

    void deletar(Long id);
}
