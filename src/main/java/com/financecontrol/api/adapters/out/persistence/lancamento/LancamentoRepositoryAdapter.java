package com.financecontrol.api.adapters.out.persistence.lancamento;

import com.financecontrol.api.adapters.out.persistence.subcategoria.SubcategoriaEntity;
import com.financecontrol.api.adapters.out.persistence.subcategoria.SubcategoriaJpaRepository;
import com.financecontrol.api.domain.lancamento.Lancamento;
import com.financecontrol.api.domain.lancamento.LancamentoRepositoryPort;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class LancamentoRepositoryAdapter implements LancamentoRepositoryPort {

    private final LancamentoJpaRepository jpaRepository;
    private final SubcategoriaJpaRepository subcategoriaJpaRepository;
    private final LancamentoMapper mapper;

    public LancamentoRepositoryAdapter(LancamentoJpaRepository jpaRepository,
                                       SubcategoriaJpaRepository subcategoriaJpaRepository,
                                       LancamentoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.subcategoriaJpaRepository = subcategoriaJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Lancamento> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PaginaResultado<Lancamento> findAll(ParametrosPaginacao paginacao) {
        Page<LancamentoEntity> pagina = jpaRepository.findAll(toPageRequest(paginacao));
        return toPaginaResultado(pagina);
    }

    @Override
    public PaginaResultado<Lancamento> findByFiltros(Long idSubcategoria, LocalDate dataInicio, LocalDate dataFim, ParametrosPaginacao paginacao) {
        Page<LancamentoEntity> pagina = jpaRepository.findByFiltros(idSubcategoria, dataInicio, dataFim, toPageRequest(paginacao));
        return toPaginaResultado(pagina);
    }

    @Override
    public boolean existsByIdSubcategoria(Long idSubcategoria) {
        return jpaRepository.existsBySubcategoriaId(idSubcategoria);
    }

    @Override
    public boolean existsByIdCategoria(Long idCategoria) {
        return jpaRepository.existsBySubcategoriaCategoriaId(idCategoria);
    }

    @Override
    public Lancamento save(Lancamento lancamento) {
        SubcategoriaEntity subcategoriaEntity = subcategoriaJpaRepository
                .findById(lancamento.getIdSubcategoria())
                .orElseThrow(() -> new IllegalStateException(
                        "Subcategoria nao encontrada: " + lancamento.getIdSubcategoria()));

        LancamentoEntity entity;
        if (lancamento.getId() != null) {
            entity = jpaRepository.findById(lancamento.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Lancamento nao encontrado: " + lancamento.getId()));
            entity.setValor(lancamento.getValor());
            entity.setData(lancamento.getData());
            entity.setSubcategoria(subcategoriaEntity);
            entity.setComentario(lancamento.getComentario());
        } else {
            entity = mapper.toEntity(lancamento, subcategoriaEntity);
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

    private PaginaResultado<Lancamento> toPaginaResultado(Page<LancamentoEntity> pagina) {
        List<Lancamento> conteudo = pagina.getContent().stream()
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

