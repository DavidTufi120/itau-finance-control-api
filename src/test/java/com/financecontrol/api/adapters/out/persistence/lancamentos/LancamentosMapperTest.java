package com.financecontrol.api.adapters.out.persistence.lancamentos;

import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaEntity;
import com.financecontrol.api.adapters.out.persistence.lancamento.LancamentoEntity;
import com.financecontrol.api.adapters.out.persistence.lancamento.LancamentoMapper;
import com.financecontrol.api.adapters.out.persistence.subcategoria.SubcategoriaEntity;
import com.financecontrol.api.domain.lancamento.Lancamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class LancamentosMapperTest {

    private LancamentoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LancamentoMapper();
    }

    private SubcategoriaEntity subcategoriaEntity() {
        CategoriaEntity categoria = new CategoriaEntity(1L, "Transporte");
        return new SubcategoriaEntity(1L, "Combustivel", categoria);
    }

    @Test
    void deveConverterLancamentoEntityParaDominio() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(
                1L,
                new BigDecimal("200.00"),
                LocalDate.of(2021, 1, 1),
                subcategoria,
                "Gastos com gasolina"
        );

        Lancamento resultado = mapper.toDomain(entity);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getValor()).isEqualByComparingTo("200.00");
        assertThat(resultado.getData()).isEqualTo(LocalDate.of(2021, 1, 1));
        assertThat(resultado.getIdSubcategoria()).isEqualTo(1L);
        assertThat(resultado.getComentario()).isEqualTo("Gastos com gasolina");
    }

    @Test
    void deveConverterLancamentoEntitySemComentarioParaDominio() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(
                2L,
                new BigDecimal("50.00"),
                LocalDate.of(2021, 6, 15),
                subcategoria,
                null
        );

        Lancamento resultado = mapper.toDomain(entity);

        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getComentario()).isNull();
        assertThat(resultado.getIdSubcategoria()).isEqualTo(1L);
    }

    @Test
    void deveConverterDominioParaLancamentoEntity() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        Lancamento lancamento = new Lancamento(null, new BigDecimal("300.00"), LocalDate.of(2021, 3, 10), 1L, "Observacao");

        LancamentoEntity resultado = mapper.toEntity(lancamento, subcategoria);

        assertThat(resultado.getId()).isNull();
        assertThat(resultado.getValor()).isEqualByComparingTo("300.00");
        assertThat(resultado.getData()).isEqualTo(LocalDate.of(2021, 3, 10));
        assertThat(resultado.getSubcategoria()).isEqualTo(subcategoria);
        assertThat(resultado.getComentario()).isEqualTo("Observacao");
    }

    @Test
    void deveConverterDominioParaLancamentoEntityComValorNegativo() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        Lancamento lancamento = new Lancamento(null, new BigDecimal("-200.00"), LocalDate.now(), 1L, null);

        LancamentoEntity resultado = mapper.toEntity(lancamento, subcategoria);

        assertThat(resultado.getValor()).isEqualByComparingTo("-200.00");
        assertThat(resultado.getComentario()).isNull();
    }

    @Test
    void deveConverterLancamentoEntityComIdParaDominio() {
        SubcategoriaEntity subcategoria = subcategoriaEntity();
        LancamentoEntity entity = new LancamentoEntity(
                10L,
                new BigDecimal("999.99"),
                LocalDate.of(2023, 12, 31),
                subcategoria,
                null
        );

        Lancamento resultado = mapper.toDomain(entity);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getValor()).isEqualByComparingTo("999.99");
    }
}
