package com.financecontrol.api.adapters.out.persistence.categoria;

import com.financecontrol.api.domain.categoria.Categoria;
import com.financecontrol.api.domain.categoria.CategoriaRepositoryPort;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository jpaRepository;
    private final CategoriaMapper mapper;

    public CategoriaRepositoryAdapter(CategoriaJpaRepository jpaRepository, CategoriaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PaginaResultado<Categoria> findAll(ParametrosPaginacao paginacao) {
        Page<CategoriaEntity> pagina = jpaRepository.findAll(toPageRequest(paginacao));
        return toPaginaResultado(pagina);
    }

    @Override
    public PaginaResultado<Categoria> findByNomeContendo(String nome, ParametrosPaginacao paginacao) {
        Page<CategoriaEntity> pagina = jpaRepository.findByNomeContainingIgnoreCase(nome, toPageRequest(paginacao));
        return toPaginaResultado(pagina);
    }

    @Override
    public boolean existsByNome(String nome) {
        return jpaRepository.existsByNome(nome);
    }

    @Override
    public boolean existsByNomeAndIdDiferente(String nome, Long id) {
        return jpaRepository.existsByNomeAndIdNot(nome, id);
    }

    @Override
    public Categoria save(Categoria categoria) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(categoria)));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private PageRequest toPageRequest(ParametrosPaginacao paginacao) {
        return PageRequest.of(paginacao.page(), paginacao.size());
    }

    private PaginaResultado<Categoria> toPaginaResultado(Page<CategoriaEntity> pagina) {
        List<Categoria> conteudo = pagina.getContent().stream()
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
