package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.response.BalancoResponse;
import com.financecontrol.api.domain.balanco.Balanco;
import com.financecontrol.api.domain.balanco.BalancoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalancoControllerTest {

    @Mock
    private BalancoService balancoService;

    @InjectMocks
    private BalancoController balancoController;

    private final LocalDate dataInicio = LocalDate.of(2021, 1, 1);
    private final LocalDate dataFim = LocalDate.of(2021, 1, 31);

    @Test
    void deveRetornarBalancoSemCategoriaQuandoIdCategoriaEhNulo() {
        Balanco balanco = new Balanco(null, null, new BigDecimal("2320.00"), new BigDecimal("1000.00"), new BigDecimal("1320.00"));
        when(balancoService.calcular(dataInicio, dataFim, null)).thenReturn(balanco);

        ResponseEntity<BalancoResponse> resposta = balancoController.consultar(dataInicio, dataFim, null);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().categoria()).isNull();
        assertThat(resposta.getBody().receita()).isEqualTo("2320.00");
        assertThat(resposta.getBody().despesa()).isEqualTo("1000.00");
        assertThat(resposta.getBody().saldo()).isEqualTo("1320.00");
        verify(balancoService).calcular(dataInicio, dataFim, null);
    }

    @Test
    void deveRetornarBalancoComCategoriaQuandoIdCategoriaInformado() {
        Balanco balanco = new Balanco(1L, "Transporte", new BigDecimal("2320.00"), new BigDecimal("1000.00"), new BigDecimal("1320.00"));
        when(balancoService.calcular(dataInicio, dataFim, 1L)).thenReturn(balanco);

        ResponseEntity<BalancoResponse> resposta = balancoController.consultar(dataInicio, dataFim, 1L);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().categoria()).isNotNull();
        assertThat(resposta.getBody().categoria().id_categoria()).isEqualTo(1L);
        assertThat(resposta.getBody().categoria().nome()).isEqualTo("Transporte");
        assertThat(resposta.getBody().receita()).isEqualTo("2320.00");
        assertThat(resposta.getBody().despesa()).isEqualTo("1000.00");
        assertThat(resposta.getBody().saldo()).isEqualTo("1320.00");
        verify(balancoService).calcular(dataInicio, dataFim, 1L);
    }

    @Test
    void deveRetornarSaldoNegativoQuandoDespesaMaiorQueReceita() {
        Balanco balanco = new Balanco(null, null, new BigDecimal("200.00"), new BigDecimal("500.00"), new BigDecimal("-300.00"));
        when(balancoService.calcular(dataInicio, dataFim, null)).thenReturn(balanco);

        ResponseEntity<BalancoResponse> resposta = balancoController.consultar(dataInicio, dataFim, null);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().saldo()).isEqualTo("-300.00");
    }
}
