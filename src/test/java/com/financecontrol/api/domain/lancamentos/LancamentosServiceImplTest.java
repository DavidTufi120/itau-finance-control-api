package com.financecontrol.api.domain.lancamentos;

import com.financecontrol.api.domain.lancamento.Lancamento;
import com.financecontrol.api.domain.lancamento.LancamentoRepositoryPort;
import com.financecontrol.api.domain.lancamento.LancamentoServiceImpl;
import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import com.financecontrol.api.domain.shared.PaginaResultado;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.subcategoria.Subcategoria;
import com.financecontrol.api.domain.subcategoria.SubcategoriaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LancamentosServiceImplTest {

    @Mock
    private LancamentoRepositoryPort lancamentoRepositoryPort;

    @Mock
    private SubcategoriaRepositoryPort subcategoriaRepositoryPort;

    @InjectMocks
    private LancamentoServiceImpl lancamentoService;

    private Lancamento lancamentoGasolina;
    private Subcategoria subcategoriaExistente;
    private ParametrosPaginacao paginacaoPadrao;

    @BeforeEach
    void setUp() {
        lancamentoGasolina = new Lancamento(1L, new BigDecimal("200.00"), LocalDate.of(2024, 1, 1), 1L, "Gasolina do mês");
        subcategoriaExistente = new Subcategoria(1L, "Combustível", 1L);
        paginacaoPadrao = new ParametrosPaginacao(0, 20);
    }

    @Test
    void deveBuscarLancamentoPorIdComSucesso() {
        when(lancamentoRepositoryPort.findById(1L)).thenReturn(Optional.of(lancamentoGasolina));

        Lancamento resultado = lancamentoService.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getValor()).isEqualByComparingTo("200.00");
        assertThat(resultado.getIdSubcategoria()).isEqualTo(1L);
        verify(lancamentoRepositoryPort).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoLancamentoNaoEncontradoPorId() {
        when(lancamentoRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lancamentoService.buscarPorId(99L))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);
    }

    @Test
    void deveListarTodosOsLancamentosSemFiltros() {
        PaginaResultado<Lancamento> paginaEsperada = new PaginaResultado<>(
                List.of(lancamentoGasolina), 0, 20, 1, 1
        );
        when(lancamentoRepositoryPort.findAll(paginacaoPadrao)).thenReturn(paginaEsperada);

        PaginaResultado<Lancamento> resultado = lancamentoService.listar(null, null, null, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.totalElementos()).isEqualTo(1);
        verify(lancamentoRepositoryPort).findAll(paginacaoPadrao);
        verify(lancamentoRepositoryPort, never()).findByFiltros(any(), any(), any(), any());
    }

    @Test
    void deveListarLancamentosComFiltroDeSubcategoria() {
        PaginaResultado<Lancamento> paginaEsperada = new PaginaResultado<>(
                List.of(lancamentoGasolina), 0, 20, 1, 1
        );
        when(lancamentoRepositoryPort.findByFiltros(1L, null, null, paginacaoPadrao)).thenReturn(paginaEsperada);

        PaginaResultado<Lancamento> resultado = lancamentoService.listar(1L, null, null, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        verify(lancamentoRepositoryPort).findByFiltros(1L, null, null, paginacaoPadrao);
        verify(lancamentoRepositoryPort, never()).findAll(any());
    }

    @Test
    void deveListarLancamentosComFiltroDePeriodo() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 1, 31);
        PaginaResultado<Lancamento> paginaEsperada = new PaginaResultado<>(
                List.of(lancamentoGasolina), 0, 20, 1, 1
        );
        when(lancamentoRepositoryPort.findByFiltros(null, dataInicio, dataFim, paginacaoPadrao)).thenReturn(paginaEsperada);

        PaginaResultado<Lancamento> resultado = lancamentoService.listar(null, dataInicio, dataFim, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        verify(lancamentoRepositoryPort).findByFiltros(null, dataInicio, dataFim, paginacaoPadrao);
        verify(lancamentoRepositoryPort, never()).findAll(any());
    }

    @Test
    void deveListarLancamentosComApenasDataFim() {
        LocalDate dataFim = LocalDate.of(2024, 1, 31);
        PaginaResultado<Lancamento> paginaEsperada = new PaginaResultado<>(
                List.of(lancamentoGasolina), 0, 20, 1, 1
        );
        when(lancamentoRepositoryPort.findByFiltros(null, null, dataFim, paginacaoPadrao)).thenReturn(paginaEsperada);

        PaginaResultado<Lancamento> resultado = lancamentoService.listar(null, null, dataFim, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        verify(lancamentoRepositoryPort).findByFiltros(null, null, dataFim, paginacaoPadrao);
        verify(lancamentoRepositoryPort, never()).findAll(any());
    }

    @Test
    void deveCriarLancamentoComSucesso() {
        Lancamento novoLancamento = Lancamento.criar(new BigDecimal("200.00"), LocalDate.of(2024, 1, 1), 1L, "Gasolina do mês");
        when(subcategoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(subcategoriaExistente));
        when(lancamentoRepositoryPort.save(novoLancamento)).thenReturn(lancamentoGasolina);

        Lancamento resultado = lancamentoService.criar(novoLancamento);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getValor()).isEqualByComparingTo("200.00");
        verify(subcategoriaRepositoryPort).findById(1L);
        verify(lancamentoRepositoryPort).save(novoLancamento);
    }

    @Test
    void deveLancarExcecaoAoCriarLancamentoComValorZero() {
        Lancamento lancamentoInvalido = Lancamento.criar(BigDecimal.ZERO, LocalDate.now(), 1L, null);

        assertThatThrownBy(() -> lancamentoService.criar(lancamentoInvalido))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining(MensagensErro.LANCAMENTO_VALOR_ZERO)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_VALOR_INVALIDO);

        verify(lancamentoRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCriarLancamentoComSubcategoriaInexistente() {
        Lancamento novoLancamento = Lancamento.criar(new BigDecimal("100.00"), LocalDate.now(), 99L, null);
        when(subcategoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lancamentoService.criar(novoLancamento))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);

        verify(lancamentoRepositoryPort, never()).save(any());
    }

    @Test
    void deveAtualizarLancamentoComSucesso() {
        Lancamento dadosAtualizados = Lancamento.criar(new BigDecimal("350.00"), LocalDate.of(2024, 2, 1), 1L, "Revisão do mês");
        Lancamento lancamentoAtualizado = new Lancamento(1L, new BigDecimal("350.00"), LocalDate.of(2024, 2, 1), 1L, "Revisão do mês");

        when(lancamentoRepositoryPort.findById(1L)).thenReturn(Optional.of(lancamentoGasolina));
        when(subcategoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(subcategoriaExistente));
        when(lancamentoRepositoryPort.save(any(Lancamento.class))).thenReturn(lancamentoAtualizado);

        Lancamento resultado = lancamentoService.atualizar(1L, dadosAtualizados);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getValor()).isEqualByComparingTo("350.00");
        assertThat(resultado.getComentario()).isEqualTo("Revisão do mês");
        verify(lancamentoRepositoryPort).save(any(Lancamento.class));
    }

    @Test
    void deveAtualizarLancamentoComDataNulaUsandoDataAtual() {
        Lancamento dadosAtualizados = new Lancamento(null, new BigDecimal("350.00"), null, 1L, null);
        Lancamento lancamentoAtualizado = new Lancamento(1L, new BigDecimal("350.00"), LocalDate.now(), 1L, null);

        when(lancamentoRepositoryPort.findById(1L)).thenReturn(Optional.of(lancamentoGasolina));
        when(subcategoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(subcategoriaExistente));
        when(lancamentoRepositoryPort.save(any(Lancamento.class))).thenReturn(lancamentoAtualizado);

        Lancamento resultado = lancamentoService.atualizar(1L, dadosAtualizados);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getData()).isEqualTo(LocalDate.now());
        verify(lancamentoRepositoryPort).save(any(Lancamento.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarLancamentoInexistente() {
        Lancamento dadosAtualizados = Lancamento.criar(new BigDecimal("200.00"), LocalDate.now(), 1L, null);
        when(lancamentoRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lancamentoService.atualizar(99L, dadosAtualizados))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);

        verify(lancamentoRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarLancamentoComValorZero() {
        Lancamento dadosInvalidos = Lancamento.criar(BigDecimal.ZERO, LocalDate.now(), 1L, null);

        assertThatThrownBy(() -> lancamentoService.atualizar(1L, dadosInvalidos))
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_VALOR_INVALIDO);

        verify(lancamentoRepositoryPort, never()).findById(any());
        verify(lancamentoRepositoryPort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComSubcategoriaInexistente() {
        Lancamento dadosAtualizados = Lancamento.criar(new BigDecimal("200.00"), LocalDate.now(), 99L, null);
        when(lancamentoRepositoryPort.findById(1L)).thenReturn(Optional.of(lancamentoGasolina));
        when(subcategoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lancamentoService.atualizar(1L, dadosAtualizados))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);

        verify(lancamentoRepositoryPort, never()).save(any());
    }

    @Test
    void deveDeletarLancamentoComSucesso() {
        when(lancamentoRepositoryPort.findById(1L)).thenReturn(Optional.of(lancamentoGasolina));

        lancamentoService.deletar(1L);

        verify(lancamentoRepositoryPort).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarLancamentoInexistente() {
        when(lancamentoRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lancamentoService.deletar(99L))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("99")
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);

        verify(lancamentoRepositoryPort, never()).deleteById(any());
    }
}

