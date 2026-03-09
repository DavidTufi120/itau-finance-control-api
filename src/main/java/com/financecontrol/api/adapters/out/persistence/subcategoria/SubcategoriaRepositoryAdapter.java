package com.financecontrol.api.adapters.out.persistence.subcategoria;

import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaEntity;
import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaJpaRepository;
import com.financecontrol.api.domain.subcategoria.Subcategoria;
import com.financecontrol.api.domain.subcategoria.SubcategoriaRepositoryPort;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SubcategoriaRepositoryAdapter implements SubcategoriaRepositoryPort {
    private final SubcategoriaJpaRepository jpaRepository;
    private final CategoriaJpaRepository categoriaJpaRepository;
    private final SubcategoriaMapper mapper;

    public SubcategoriaRepositoryAdapter(SubcategoriaJpaRepository jpaRepository,
                                         CategoriaJpaRepository categoriaJpaRepository,
                                         SubcategoriaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.categoriaJpaRepository = categoriaJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Subcategoria> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PaginaResultado<Subcategoria> findAll(ParametrosPaginacao paginacao) {
        Page<SubcategoriaEntity> pagina = jpaRepository.findAll(toPageRequest(paginacao));
        return toPaginaResultado(pagina);
    }

    @Override
    public PaginaResultado<Subcategoria> findByNomeContendo(String nome, ParametrosPaginacao paginacao) {
        Page<SubcategoriaEntity> pagina = jpaRepository.findByNomeContainingIgnoreCase(nome, toPageRequest(paginacao));
        return toPaginaResultado(pagina);
    }

    @Override
    public PaginaResultado<Subcategoria> findByIdSubcategoria(Long idSubcategoria, ParametrosPaginacao paginacao) {
        Page<SubcategoriaEntity> pagina = jpaRepository.findByCategoriaId(idSubcategoria, toPageRequest(paginacao));
        return toPaginaResultado(pagina);
    }

    @Override
    public PaginaResultado<Subcategoria> findByNomeContendoEIdSubcategoria(String nome, Long idSubcategoria, ParametrosPaginacao paginacao) {
        Page<SubcategoriaEntity> pagina = jpaRepository.findByNomeContainingIgnoreCaseAndCategoriaId(nome, idSubcategoria, toPageRequest(paginacao));
        return toPaginaResultado(pagina);
    }

    @Override
    public boolean existsByNomeAndIdCategoria(String nome, Long idCategoria) {
        return jpaRepository.existsByNomeAndCategoriaId(nome, idCategoria);
    }

    @Override
    public boolean existsByNomeAndIdCategoriaAndIdDiferente(String nome, Long idCategoria, Long id) {
        return jpaRepository.existsByNomeAndCategoriaIdAndIdNot(nome, idCategoria, id);
    }

    @Override
    public Subcategoria save(Subcategoria subcategoria) {
        CategoriaEntity categoriaEntity = categoriaJpaRepository.findById(subcategoria.getIdCategoria())
                .orElseThrow(() -> new IllegalStateException("Categoria nao encontrada: " + subcategoria.getIdCategoria()));

        SubcategoriaEntity entity;
        if (subcategoria.getId() != null) {
            entity = jpaRepository.findById(subcategoria.getId())
                    .orElseThrow(() -> new IllegalStateException("Subcategoria nao encontrada: " + subcategoria.getId()));
            entity.setNome(subcategoria.getNome());
            entity.setCategoria(categoriaEntity);
        } else {
            entity = mapper.toEntity(subcategoria, categoriaEntity);
        }

        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private PageRequest toPageRequest(ParametrosPaginacao paginacao) {
        return PageRequest.of(paginacao.page(), paginacao.size());
    }

    private PaginaResultado<Subcategoria> toPaginaResultado(Page<SubcategoriaEntity> pagina) {
        List<Subcategoria> conteudo = pagina.getContent().stream()
                .map(mapper::toDomain)
                .toList();
        return new PaginaResultado<>(
                conteudo,
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
        );
    }
}
