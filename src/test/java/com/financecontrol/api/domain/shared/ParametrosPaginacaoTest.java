package com.financecontrol.api.domain.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParametrosPaginacaoTest {

    @Test
    void deveCriarParametrosDePaginacaoQuandoValoresForemValidos() {
        ParametrosPaginacao parametros = new ParametrosPaginacao(0, 20);

        assertThat(parametros.page()).isEqualTo(0);
        assertThat(parametros.size()).isEqualTo(20);
    }

    @Test
    void deveLancarExcecaoQuandoPaginaForMenorQueZero() {
        assertThatThrownBy(() -> new ParametrosPaginacao(-1, 20))
                .isInstanceOf(NegocioException.class)
                .hasMessage(MensagensErro.PAGINA_INVALIDA)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_ERRO_VALIDACAO);
    }

    @Test
    void deveLancarExcecaoQuandoTamanhoDaPaginaForZero() {
        assertThatThrownBy(() -> new ParametrosPaginacao(0, 0))
                .isInstanceOf(NegocioException.class)
                .hasMessage(MensagensErro.TAMANHO_PAGINA_INVALIDO)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_ERRO_VALIDACAO);
    }

    @Test
    void deveLancarExcecaoQuandoTamanhoDaPaginaForNegativo() {
        assertThatThrownBy(() -> new ParametrosPaginacao(0, -5))
                .isInstanceOf(NegocioException.class)
                .hasMessage(MensagensErro.TAMANHO_PAGINA_INVALIDO)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_ERRO_VALIDACAO);
    }
}

