package com.financecontrol.api.adapters.out.persistence.categoria;

import com.financecontrol.api.domain.categoria.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaMapperTest {
    private CategoriaMapper categoriaMapper;

    @BeforeEach
    void setUp() {
        categoriaMapper = new CategoriaMapper();
    }

    @Test
    void deveConverterEntityParaDomain() {
        CategoriaEntity entity = new CategoriaEntity(1L, "Transporte");
        Categoria domain = categoriaMapper.toDomain(entity);
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getNome()).isEqualTo("Transporte");
    }

    @Test
    void deveConverterDomainParaEntity() {
        Categoria domain = new Categoria(1L, "Transporte");
        CategoriaEntity entity = categoriaMapper.toEntity(domain);
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNome()).isEqualTo("Transporte");
    }

    @Test
    void deveConverterEntitySemIdParaDomain() {
        CategoriaEntity entity = new CategoriaEntity(null, "Saude");
        Categoria domain = categoriaMapper.toDomain(entity);
        assertThat(domain.getId()).isNull();
        assertThat(domain.getNome()).isEqualTo("Saude");
    }

    @Test
    void deveConverterDomainSemIdParaEntity() {
        Categoria domain = Categoria.criar("Saude");
        CategoriaEntity entity = categoriaMapper.toEntity(domain);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getNome()).isEqualTo("Saude");
    }
}