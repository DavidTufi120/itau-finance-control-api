package com.financecontrol.api.domain.subcategoria;

import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;

import java.util.Optional;

public interface SubcategoriaRepositoryPort {
    Optional<Subcategoria> findById(Long id);

    PaginaResultado<Subcategoria> findAll(ParametrosPaginacao paginacao);

    PaginaResultado<Subcategoria> findByNomeContendo(String nome, ParametrosPaginacao paginacao);

    PaginaResultado<Subcategoria> findByIdSubcategoria(Long idSubcategoria, ParametrosPaginacao paginacao);

    PaginaResultado<Subcategoria> findByNomeContendoEIdSubcategoria(String nome, Long idSubcategoria, ParametrosPaginacao paginacao);

    boolean existsByNomeAndIdCategoria(String nome, Long idCategoria);

    boolean existsByNomeAndIdCategoriaAndIdDiferente(String nome, Long idCategoria, Long id);


    Subcategoria save(Subcategoria subcategoria);

    void deleteById(Long id);
}
