package com.financecontrol.api.adapters.out.persistence.categoria;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaEntityTest {
    @Test
    void deveCriarEntityComIdENome() {
        CategoriaEntity entity = new CategoriaEntity(1L, "Transporte");
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNome()).isEqualTo("Transporte");
    }

    @Test
    void deveCriarEntityComIdNulo() {
        CategoriaEntity entity = new CategoriaEntity(null, "Saude");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getNome()).isEqualTo("Saude");
    }

    @Test
    void deveAtualizarNomeComSucesso() {
        CategoriaEntity entity = new CategoriaEntity(1L, "Antigo");
        entity.setNome("Novo");
        assertThat(entity.getNome()).isEqualTo("Novo");
    }

    @Test
    void deveCriarEntityComConstrutorProtegido() {
        CategoriaEntity entity = new CategoriaEntity(1L, "Transporte");
        assertThat(entity).isNotNull();
    }
}