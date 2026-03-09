package com.financecontrol.api.adapters.out.persistence.subcategoria;

import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaEntity;
import com.financecontrol.api.domain.subcategoria.Subcategoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubcategoriaMapperTest {

    private SubcategoriaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SubcategoriaMapper();
    }

    @Test
    void deveConverterSubcategoriaEntityParaDominio() {
        CategoriaEntity categoriaEntity = new CategoriaEntity(1L, "Transporte");
        SubcategoriaEntity entity = new SubcategoriaEntity(1L, "Combustivel", categoriaEntity);

        Subcategoria resultado = mapper.toDomain(entity);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Combustivel");
        assertThat(resultado.getIdCategoria()).isEqualTo(1L);
    }

    @Test
    void deveConverterDominioParaSubcategoriaEntity() {
        Subcategoria subcategoria = new Subcategoria(null, "Combustivel", 1L);
        CategoriaEntity categoriaEntity = new CategoriaEntity(1L, "Transporte");

        SubcategoriaEntity resultado = mapper.toEntity(subcategoria, categoriaEntity);

        assertThat(resultado.getNome()).isEqualTo("Combustivel");
        assertThat(resultado.getCategoria()).isEqualTo(categoriaEntity);
        assertThat(resultado.getCategoria().getId()).isEqualTo(1L);
    }

    @Test
    void deveConverterSubcategoriaEntityComIdParaDominio() {
        CategoriaEntity categoriaEntity = new CategoriaEntity(2L, "Saude");
        SubcategoriaEntity entity = new SubcategoriaEntity(5L, "Farmacia", categoriaEntity);

        Subcategoria resultado = mapper.toDomain(entity);

        assertThat(resultado.getId()).isEqualTo(5L);
        assertThat(resultado.getNome()).isEqualTo("Farmacia");
        assertThat(resultado.getIdCategoria()).isEqualTo(2L);
    }
}

