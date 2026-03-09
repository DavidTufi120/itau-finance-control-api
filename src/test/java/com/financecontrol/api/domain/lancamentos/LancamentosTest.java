package com.financecontrol.api.domain.lancamentos;

import com.financecontrol.api.domain.lancamento.Lancamento;
import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LancamentosTest {

    @Test
    void deveCriarLancamentoComTodosOsCampos() {
        LocalDate data = LocalDate.of(2024, 1, 10);
        Lancamento lancamento = new Lancamento(1L, new BigDecimal("200.00"), data, 1L, "Gasolina do mês");

        assertThat(lancamento.getId()).isEqualTo(1L);
        assertThat(lancamento.getValor()).isEqualByComparingTo("200.00");
        assertThat(lancamento.getData()).isEqualTo(data);
        assertThat(lancamento.getIdSubcategoria()).isEqualTo(1L);
        assertThat(lancamento.getComentario()).isEqualTo("Gasolina do mês");
    }

    @Test
    void deveCriarLancamentoSemComentario() {
        Lancamento lancamento = new Lancamento(1L, new BigDecimal("100.00"), LocalDate.now(), 1L, null);

        assertThat(lancamento.getComentario()).isNull();
    }

    @Test
    void deveCriarLancamentoComValorNegativo() {
        Lancamento lancamento = new Lancamento(1L, new BigDecimal("-150.50"), LocalDate.now(), 2L, null);

        assertThat(lancamento.getValor()).isEqualByComparingTo("-150.50");
    }

    @Test
    void deveCriarLancamentoViaMetodoEstaticoCriar() {
        LocalDate data = LocalDate.of(2024, 3, 5);
        Lancamento lancamento = Lancamento.criar(new BigDecimal("300.00"), data, 1L, "Farmácia");

        assertThat(lancamento.getId()).isNull();
        assertThat(lancamento.getValor()).isEqualByComparingTo("300.00");
        assertThat(lancamento.getData()).isEqualTo(data);
        assertThat(lancamento.getIdSubcategoria()).isEqualTo(1L);
        assertThat(lancamento.getComentario()).isEqualTo("Farmácia");
    }

    @Test
    void deveCriarLancamentoUsandoDataAtualQuandoDataNaoInformada() {
        Lancamento lancamento = Lancamento.criar(new BigDecimal("50.00"), null, 1L, null);

        assertThat(lancamento.getData()).isEqualTo(LocalDate.now());
    }

    @Test
    void deveValidarValorComSucesso() {
        Lancamento lancamento = new Lancamento(1L, new BigDecimal("100.00"), LocalDate.now(), 1L, null);

        assertThatNoException().isThrownBy(lancamento::validarValor);
    }

    @Test
    void deveLancarExcecaoAoValidarValorZero() {
        Lancamento lancamento = new Lancamento(1L, BigDecimal.ZERO, LocalDate.now(), 1L, null);

        assertThatThrownBy(lancamento::validarValor)
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining(MensagensErro.LANCAMENTO_VALOR_ZERO)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_VALOR_INVALIDO);
    }

    @Test
    void deveLancarExcecaoAoValidarValorNulo() {
        Lancamento lancamento = new Lancamento(1L, null, LocalDate.now(), 1L, null);

        assertThatThrownBy(lancamento::validarValor)
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_VALOR_INVALIDO);
    }

    @Test
    void devePermitirValorNegativoNaValidacao() {
        Lancamento lancamento = new Lancamento(1L, new BigDecimal("-200.00"), LocalDate.now(), 1L, null);

        assertThatNoException().isThrownBy(lancamento::validarValor);
    }
}
