package com.financecontrol.api.domain.categoria;

import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;

import java.util.Optional;

public interface CategoriaRepositoryPort {

    Optional<Categoria> findById(Long id);

    PaginaResultado<Categoria> findAll(ParametrosPaginacao paginacao);

    PaginaResultado<Categoria> findByNomeContendo(String nome, ParametrosPaginacao paginacao);

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdDiferente(String nome, Long id);

    Categoria save(Categoria categoria);

    void deleteById(Long id);
}
