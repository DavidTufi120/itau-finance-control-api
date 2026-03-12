package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.request.LancamentoRequest;
import com.financecontrol.api.adapters.controller.response.LancamentoResponse;
import com.financecontrol.api.adapters.controller.response.PageResponse;
import com.financecontrol.api.domain.lancamento.Lancamento;
import com.financecontrol.api.domain.lancamento.LancamentoService;
import com.financecontrol.api.domain.shared.PaginaResultado;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LancamentosControllerTest {

    @Mock
    private LancamentoService lancamentoService;

    @InjectMocks
    private LancamentoController lancamentoController;

    private Lancamento lancamentoGasolina;

    @BeforeEach
    void setUp() {
        lancamentoGasolina = new Lancamento(1L, new BigDecimal("200.00"), LocalDate.of(2024, 1, 1), 1L, "Gastos com gasolina");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/v1/lancamentos");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @Test
    void deveRetornarListaPaginadaDeLancamentos() {
        PaginaResultado<Lancamento> paginaResultado = new PaginaResultado<>(
                List.of(lancamentoGasolina), 0, 20, 1, 1
        );
        when(lancamentoService.listar(null, null, null, new ParametrosPaginacao(0, 20))).thenReturn(paginaResultado);

        ResponseEntity<PageResponse<LancamentoResponse>> resposta = lancamentoController.listar(null, null, null, 0, 20);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().content()).hasSize(1);
        assertThat(resposta.getBody().totalElements()).isEqualTo(1);
    }

    @Test
    void deveRetornarListaFiltradaPorSubcategoriaEPeriodo() {
        LocalDate dataInicio = LocalDate.of(2024, 1, 1);
        LocalDate dataFim = LocalDate.of(2024, 1, 31);
        PaginaResultado<Lancamento> paginaResultado = new PaginaResultado<>(
                List.of(lancamentoGasolina), 0, 20, 1, 1
        );
        when(lancamentoService.listar(eq(1L), eq(dataInicio), eq(dataFim), any(ParametrosPaginacao.class)))
                .thenReturn(paginaResultado);

        ResponseEntity<PageResponse<LancamentoResponse>> resposta = lancamentoController.listar(1L, dataInicio, dataFim, 0, 20);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().content().get(0).idSubcategoria()).isEqualTo(1L);
    }

    @Test
    void deveRetornarLancamentoPorId() {
        when(lancamentoService.buscarPorId(1L)).thenReturn(lancamentoGasolina);

        ResponseEntity<LancamentoResponse> resposta = lancamentoController.buscarPorId(1L);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().idLancamento()).isEqualTo(1L);
        assertThat(resposta.getBody().valor()).isEqualByComparingTo("200.00");
    }

    @Test
    void deveCriarLancamentoERetornar201ComLocation() {
        LancamentoRequest request = new LancamentoRequest(
                new BigDecimal("200.00"),
                LocalDate.of(2024, 1, 1),
                1L,
                "Gastos com gasolina"
        );
        when(lancamentoService.criar(any(Lancamento.class))).thenReturn(lancamentoGasolina);

        ResponseEntity<LancamentoResponse> resposta = lancamentoController.criar(request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().idLancamento()).isEqualTo(1L);
        assertThat(resposta.getBody().valor()).isEqualByComparingTo("200.00");
        assertThat(resposta.getHeaders().getLocation()).isNotNull();
        assertThat(resposta.getHeaders().getLocation().toString()).contains("1");
    }

    @Test
    void deveCriarLancamentoSemDataEUsarDataAtual() {
        Lancamento lancamentoSemData = new Lancamento(2L, new BigDecimal("50.00"), LocalDate.now(), 1L, null);
        LancamentoRequest request = new LancamentoRequest(
                new BigDecimal("50.00"),
                null,
                1L,
                null
        );
        when(lancamentoService.criar(any(Lancamento.class))).thenReturn(lancamentoSemData);

        ResponseEntity<LancamentoResponse> resposta = lancamentoController.criar(request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().data()).isEqualTo(LocalDate.now());
    }

    @Test
    void deveAtualizarLancamentoERetornar200() {
        LancamentoRequest request = new LancamentoRequest(
                new BigDecimal("350.00"),
                LocalDate.of(2024, 2, 1),
                1L,
                "Gasolina atualizada"
        );
        Lancamento lancamentoAtualizado = new Lancamento(1L, new BigDecimal("350.00"), LocalDate.of(2024, 2, 1), 1L, "Gasolina atualizada");
        when(lancamentoService.atualizar(eq(1L), any(Lancamento.class))).thenReturn(lancamentoAtualizado);

        ResponseEntity<LancamentoResponse> resposta = lancamentoController.atualizar(1L, request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().valor()).isEqualByComparingTo("350.00");
        assertThat(resposta.getBody().comentario()).isEqualTo("Gasolina atualizada");
        verify(lancamentoService).atualizar(eq(1L), any(Lancamento.class));
    }

    @Test
    void deveDeletarLancamentoERetornar204() {
        ResponseEntity<Void> resposta = lancamentoController.deletar(1L);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(resposta.getBody()).isNull();
        verify(lancamentoService).deletar(1L);
    }
}
