package com.financecontrol.api.adapters.out.persistence.lancamentos;

import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaEntity;
import com.financecontrol.api.adapters.out.persistence.lancamento.LancamentoEntity;
import com.financecontrol.api.adapters.out.persistence.lancamento.LancamentoJpaRepository;
import com.financecontrol.api.adapters.out.persistence.lancamento.LancamentoMapper;
import com.financecontrol.api.adapters.out.persistence.lancamento.LancamentoRepositoryAdapter;
import com.financecontrol.api.adapters.out.persistence.subcategoria.SubcategoriaEntity;
import com.financecontrol.api.adapters.out.persistence.subcategoria.SubcategoriaJpaRepository;
import com.financecontrol.api.domain.lancamento.Lancamento;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LancamentosRepositoryAdapterTest {

    @Mock
    private LancamentoJpaRepository jpaRepository;

    @Mock
    private SubcategoriaJpaRepository subcategoriaJpaRepository;

    @Mock
    private LancamentoMapper mapper;

    @InjectMocks
    private LancamentoRepositoryAdapter adapter;

    private SubcategoriaEntity subcategoriaEntity;
    private LancamentoEntity lancamentoEntity;
    private Lancamento lancamentoDominio;
    private ParametrosPaginacao paginacaoPadrao;

    @BeforeEach
    void setUp() {
        CategoriaEntity categoriaEntity = new CategoriaEntity(1L, "Transporte");
        subcategoriaEntity = new SubcategoriaEntity(1L, "Combustivel", categoriaEntity);
        lancamentoEntity = new LancamentoEntity(1L, new BigDecimal("200.00"), LocalDate.of(2021, 1, 1), subcategoriaEntity, "Gastos com gasolina");
        lancamentoDominio = new Lancamento(1L, new BigDecimal("200.00"), LocalDate.of(2021, 1, 1), 1L, "Gastos com gasolina");
        paginacaoPadrao = new ParametrosPaginacao(0, 20);
    }

    @Test
    void deveBuscarLancamentoPorIdComSucesso() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(lancamentoEntity));
        when(mapper.toDomain(lancamentoEntity)).thenReturn(lancamentoDominio);

        Optional<Lancamento> resultado = adapter.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getValor()).isEqualByComparingTo("200.00");
    }

    @Test
    void deveRetornarVazioQuandoLancamentoNaoEncontradoPorId() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = adapter.findById(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveListarTodosOsLancamentos() {
        Page<LancamentoEntity> pagina = new PageImpl<>(
                List.of(lancamentoEntity), PageRequest.of(0, 20), 1
        );
        when(jpaRepository.findAll(any(PageRequest.class))).thenReturn(pagina);
        when(mapper.toDomain(lancamentoEntity)).thenReturn(lancamentoDominio);

        PaginaResultado<Lancamento> resultado = adapter.findAll(paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.totalElementos()).isEqualTo(1);
        assertThat(resultado.conteudo().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void deveListarLancamentosComFiltros() {
        Page<LancamentoEntity> pagina = new PageImpl<>(
                List.of(lancamentoEntity), PageRequest.of(0, 20), 1
        );
        LocalDate dataInicio = LocalDate.of(2021, 1, 1);
        LocalDate dataFim = LocalDate.of(2021, 12, 31);

        when(jpaRepository.findByFiltros(eq(1L), eq(dataInicio), eq(dataFim), any(PageRequest.class))).thenReturn(pagina);
        when(mapper.toDomain(lancamentoEntity)).thenReturn(lancamentoDominio);

        PaginaResultado<Lancamento> resultado = adapter.findByFiltros(1L, dataInicio, dataFim, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.conteudo().get(0).getIdSubcategoria()).isEqualTo(1L);
    }

    @Test
    void deveListarLancamentosComFiltrosSemSubcategoria() {
        Page<LancamentoEntity> pagina = new PageImpl<>(
                List.of(lancamentoEntity), PageRequest.of(0, 20), 1
        );
        LocalDate dataInicio = LocalDate.of(2021, 1, 1);
        LocalDate dataFim = LocalDate.of(2021, 12, 31);

        when(jpaRepository.findByFiltros(eq(null), eq(dataInicio), eq(dataFim), any(PageRequest.class))).thenReturn(pagina);
        when(mapper.toDomain(lancamentoEntity)).thenReturn(lancamentoDominio);

        PaginaResultado<Lancamento> resultado = adapter.findByFiltros(null, dataInicio, dataFim, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
    }

    @Test
    void deveVerificarExistenciaPorIdSubcategoria() {
        when(jpaRepository.existsBySubcategoriaId(1L)).thenReturn(true);

        boolean existe = adapter.existsByIdSubcategoria(1L);

        assertThat(existe).isTrue();
        verify(jpaRepository).existsBySubcategoriaId(1L);
    }

    @Test
    void deveRetornarFalsoQuandoNaoExisteLancamentoPorSubcategoria() {
        when(jpaRepository.existsBySubcategoriaId(99L)).thenReturn(false);

        boolean existe = adapter.existsByIdSubcategoria(99L);

        assertThat(existe).isFalse();
    }

    @Test
    void deveVerificarExistenciaPorIdCategoria() {
        when(jpaRepository.existsBySubcategoriaCategoriaId(1L)).thenReturn(true);

        boolean existe = adapter.existsByIdCategoria(1L);

        assertThat(existe).isTrue();
        verify(jpaRepository).existsBySubcategoriaCategoriaId(1L);
    }

    @Test
    void deveRetornarFalsoQuandoNaoExisteLancamentoPorCategoria() {
        when(jpaRepository.existsBySubcategoriaCategoriaId(99L)).thenReturn(false);

        boolean existe = adapter.existsByIdCategoria(99L);

        assertThat(existe).isFalse();
    }

    @Test
    void deveSalvarNovoLancamento() {
        Lancamento novoLancamento = new Lancamento(null, new BigDecimal("100.00"), LocalDate.now(), 1L, null);
        LancamentoEntity novaEntity = new LancamentoEntity(null, new BigDecimal("100.00"), LocalDate.now(), subcategoriaEntity, null);
        LancamentoEntity savedEntity = new LancamentoEntity(2L, new BigDecimal("100.00"), LocalDate.now(), subcategoriaEntity, null);
        Lancamento savedDominio = new Lancamento(2L, new BigDecimal("100.00"), LocalDate.now(), 1L, null);

        when(subcategoriaJpaRepository.findById(1L)).thenReturn(Optional.of(subcategoriaEntity));
        when(mapper.toEntity(novoLancamento, subcategoriaEntity)).thenReturn(novaEntity);
        when(jpaRepository.save(novaEntity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedDominio);

        Lancamento resultado = adapter.save(novoLancamento);

        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getValor()).isEqualByComparingTo("100.00");
    }

    @Test
    void deveLancarExcecaoAoSalvarLancamentoComSubcategoriaNaoEncontrada() {
        Lancamento novoLancamento = new Lancamento(null, new BigDecimal("100.00"), LocalDate.now(), 99L, null);

        when(subcategoriaJpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.save(novoLancamento))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveAtualizarLancamentoExistente() {
        Lancamento lancamentoAtualizado = new Lancamento(1L, new BigDecimal("500.00"), LocalDate.of(2022, 6, 15), 1L, "Atualizado");
        LancamentoEntity savedEntity = new LancamentoEntity(1L, new BigDecimal("500.00"), LocalDate.of(2022, 6, 15), subcategoriaEntity, "Atualizado");
        Lancamento resultadoDominio = new Lancamento(1L, new BigDecimal("500.00"), LocalDate.of(2022, 6, 15), 1L, "Atualizado");

        when(subcategoriaJpaRepository.findById(1L)).thenReturn(Optional.of(subcategoriaEntity));
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(lancamentoEntity));
        when(jpaRepository.save(lancamentoEntity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(resultadoDominio);

        Lancamento resultado = adapter.save(lancamentoAtualizado);

        assertThat(resultado.getValor()).isEqualByComparingTo("500.00");
        assertThat(resultado.getComentario()).isEqualTo("Atualizado");
    }

    @Test
    void deveLancarExcecaoAoAtualizarLancamentoNaoEncontrado() {
        Lancamento lancamentoAtualizado = new Lancamento(99L, new BigDecimal("500.00"), LocalDate.now(), 1L, null);

        when(subcategoriaJpaRepository.findById(1L)).thenReturn(Optional.of(subcategoriaEntity));
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.save(lancamentoAtualizado))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveDeletarLancamentoPorId() {
        adapter.deleteById(1L);

        verify(jpaRepository).deleteById(1L);
    }

    @Test
    void deveRetornarPaginaVaziaQuandoNaoHaLancamentos() {
        Page<LancamentoEntity> paginaVazia = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(jpaRepository.findAll(any(PageRequest.class))).thenReturn(paginaVazia);

        PaginaResultado<Lancamento> resultado = adapter.findAll(paginacaoPadrao);

        assertThat(resultado.conteudo()).isEmpty();
        assertThat(resultado.totalElementos()).isZero();
    }
}
