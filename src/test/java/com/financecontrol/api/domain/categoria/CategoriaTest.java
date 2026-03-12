package com.financecontrol.api.domain.categoria;

import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoriaTest {
    @Test
    void deveCriarCategoriaComNomeValido() {
        Categoria categoria = Categoria.criar("Transporte");
        assertThat(categoria.getNome()).isEqualTo("Transporte");
        assertThat(categoria.getId()).isNull();
    }

    @Test
    void deveCriarCategoriaComIdENome() {
        Categoria categoria = new Categoria(1L, "Saude");
        assertThat(categoria.getId()).isEqualTo(1L);
        assertThat(categoria.getNome()).isEqualTo("Saude");
    }

    @Test
    void deveAtualizarNomeComSucesso() {
        Categoria categoria = new Categoria(1L, "Antigo");
        categoria.setNome("Novo");
        assertThat(categoria.getNome()).isEqualTo("Novo");
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNulo() {
        Categoria categoria = Categoria.criar(null);
        assertThatThrownBy(categoria::validarNome)
                .isInstanceOf(NegocioException.class)
                .hasMessage(MensagensErro.CAMPO_NOME_OBRIGATORIO)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_ERRO_VALIDACAO);
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazio() {
        Categoria categoria = Categoria.criar("");
        assertThatThrownBy(categoria::validarNome)
                .isInstanceOf(NegocioException.class)
                .hasMessage(MensagensErro.CAMPO_NOME_OBRIGATORIO);
    }

    @Test
    void devePermitirNomeComDoisCaracteres() {
        Categoria categoria = Categoria.criar("AB");
        categoria.validarNome();
        assertThat(categoria.getNome()).isEqualTo("AB");
    }

    @Test
    void devePassarValidacaoComNomePreenchido() {
        Categoria categoria = Categoria.criar("ABC");
        categoria.validarNome();
        assertThat(categoria.getNome()).isEqualTo("ABC");
    }

    @Test
    void deveLancarExcecaoQuandoNomeForApenasEspacos() {
        Categoria categoria = Categoria.criar("   ");
        assertThatThrownBy(categoria::validarNome)
                .isInstanceOf(NegocioException.class)
                .hasMessage(MensagensErro.CAMPO_NOME_OBRIGATORIO);
    }
}