package com.financecontrol.api.adapters.out.persistence.lancamentos;

import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaEntity;
import com.financecontrol.api.adapters.out.persistence.lancamento.LancamentoEntity;
import com.financecontrol.api.adapters.out.persistence.subcategoria.SubcategoriaEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class LancamentosEntityTest {

    private SubcategoriaEntity subcategoriaEntity() {
        CategoriaEntity categoria = new CategoriaEntity(1L, "Transporte");
        return new SubcategoriaEntity(1L, "Combustivel", categoria);
    }

    @Test
    void deveCriarLancamentoEntityComTodosOsCampos() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(
                1L,
                new BigDecimal("200.00"),
                LocalDate.of(2021, 1, 1),
                subcategoria,
                "Gastos com gasolina"
        );

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getValor()).isEqualByComparingTo("200.00");
        assertThat(entity.getData()).isEqualTo(LocalDate.of(2021, 1, 1));
        assertThat(entity.getSubcategoria()).isEqualTo(subcategoria);
        assertThat(entity.getComentario()).isEqualTo("Gastos com gasolina");
    }

    @Test
    void deveCriarLancamentoEntitySemComentario() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(
                2L,
                new BigDecimal("50.00"),
                LocalDate.of(2021, 6, 15),
                subcategoria,
                null
        );

        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getComentario()).isNull();
    }

    @Test
    void deveCriarLancamentoEntitySemIdParaNovaEntidade() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(
                null,
                new BigDecimal("100.00"),
                LocalDate.now(),
                subcategoria,
                null
        );

        assertThat(entity.getId()).isNull();
    }

    @Test
    void deveAtualizarValorDoLancamentoEntity() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(1L, new BigDecimal("100.00"), LocalDate.now(), subcategoria, null);

        entity.setValor(new BigDecimal("999.99"));

        assertThat(entity.getValor()).isEqualByComparingTo("999.99");
    }

    @Test
    void deveAtualizarDataDoLancamentoEntity() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(1L, new BigDecimal("100.00"), LocalDate.of(2021, 1, 1), subcategoria, null);

        entity.setData(LocalDate.of(2022, 12, 31));

        assertThat(entity.getData()).isEqualTo(LocalDate.of(2022, 12, 31));
    }

    @Test
    void deveAtualizarSubcategoriaDoLancamentoEntity() {
        SubcategoriaEntity subcategoriaAntiga = subcategoriaEntity();
        CategoriaEntity categoriaNova = new CategoriaEntity(2L, "Saude");
        SubcategoriaEntity subcategoriaNova = new SubcategoriaEntity(2L, "Farmacia", categoriaNova);
        LancamentoEntity entity = new LancamentoEntity(1L, new BigDecimal("100.00"), LocalDate.now(), subcategoriaAntiga, null);

        entity.setSubcategoria(subcategoriaNova);

        assertThat(entity.getSubcategoria().getId()).isEqualTo(2L);
        assertThat(entity.getSubcategoria().getNome()).isEqualTo("Farmacia");
    }

    @Test
    void deveAtualizarComentarioDoLancamentoEntity() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(1L, new BigDecimal("100.00"), LocalDate.now(), subcategoria, "Comentario antigo");

        entity.setComentario("Comentario novo");

        assertThat(entity.getComentario()).isEqualTo("Comentario novo");
    }

    @Test
    void deveCriarLancamentoEntityViaConstrutorProtegido() {
        LancamentoEntity entity = new LancamentoEntity();

        assertThat(entity).isNotNull();
    }

    @Test
    void deveCriarLancamentoEntityComValorNegativo() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(
                1L,
                new BigDecimal("-150.50"),
                LocalDate.now(),
                subcategoria,
                "Debito"
        );

        assertThat(entity.getValor()).isEqualByComparingTo("-150.50");
    }
}
