package com.financecontrol.api.domain.categoria;

import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;

public interface CategoriaService {

    Categoria buscarPorId(Long id);

    PaginaResultado<Categoria> listar(String nome, ParametrosPaginacao paginacao);

    Categoria criar(Categoria categoria);

    Categoria atualizar(Long id, Categoria categoria);

    void deletar(Long id);
}
