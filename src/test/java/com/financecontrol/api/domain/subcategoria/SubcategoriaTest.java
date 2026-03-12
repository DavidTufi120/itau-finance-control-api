package com.financecontrol.api.domain.subcategoria;

import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubcategoriaTest {

    @Test
    void deveCriarSubcategoriaComFabricaComSucesso() {
        Subcategoria subcategoria = Subcategoria.criar("Combustivel", 1L);

        assertThat(subcategoria.getId()).isNull();
        assertThat(subcategoria.getNome()).isEqualTo("Combustivel");
        assertThat(subcategoria.getIdCategoria()).isEqualTo(1L);
    }

    @Test
    void deveCriarSubcategoriaComConstrutorCompleto() {
        Subcategoria subcategoria = new Subcategoria(1L, "Combustivel", 1L);

        assertThat(subcategoria.getId()).isEqualTo(1L);
        assertThat(subcategoria.getNome()).isEqualTo("Combustivel");
        assertThat(subcategoria.getIdCategoria()).isEqualTo(1L);
    }

    @Test
    void deveValidarNomeComSucesso() {
        Subcategoria subcategoria = Subcategoria.criar("Combustivel", 1L);

        subcategoria.validarNome();

        assertThat(subcategoria.getNome()).isEqualTo("Combustivel");
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNulo() {
        Subcategoria subcategoria = new Subcategoria(null, null, 1L);

        assertThatThrownBy(subcategoria::validarNome)
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_ERRO_VALIDACAO);
    }

    @Test
    void devePermitirNomeComDoisCaracteres() {
        Subcategoria subcategoria = Subcategoria.criar("AB", 1L);

        subcategoria.validarNome();

        assertThat(subcategoria.getNome()).isEqualTo("AB");
    }

    @Test
    void deveLancarExcecaoQuandoNomeForApenasEspacos() {
        Subcategoria subcategoria = Subcategoria.criar("   ", 1L);

        assertThatThrownBy(subcategoria::validarNome)
                .isInstanceOf(NegocioException.class)
                .extracting("codigo")
                .isEqualTo(MensagensErro.CODIGO_ERRO_VALIDACAO);
    }

    @Test
    void deveAtualizarNomePorSetter() {
        Subcategoria subcategoria = Subcategoria.criar("Combustivel", 1L);

        subcategoria.setNome("Gasolina");

        assertThat(subcategoria.getNome()).isEqualTo("Gasolina");
    }

    @Test
    void deveAtualizarIdCategoriaPorSetter() {
        Subcategoria subcategoria = Subcategoria.criar("Combustivel", 1L);

        subcategoria.setIdCategoria(2L);

        assertThat(subcategoria.getIdCategoria()).isEqualTo(2L);
    }
}

