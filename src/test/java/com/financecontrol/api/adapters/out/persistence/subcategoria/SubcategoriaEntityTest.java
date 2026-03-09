package com.financecontrol.api.adapters.out.persistence.subcategoria;

import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubcategoriaEntityTest {

    @Test
    void deveCriarSubcategoriaEntityComTodosOsCampos() {
        CategoriaEntity categoriaEntity = new CategoriaEntity(1L, "Transporte");
        SubcategoriaEntity entity = new SubcategoriaEntity(1L, "Combustivel", categoriaEntity);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNome()).isEqualTo("Combustivel");
        assertThat(entity.getCategoria()).isEqualTo(categoriaEntity);
        assertThat(entity.getCategoria().getId()).isEqualTo(1L);
    }

    @Test
    void deveAtualizarNomeDaSubcategoriaEntity() {
        CategoriaEntity categoriaEntity = new CategoriaEntity(1L, "Transporte");
        SubcategoriaEntity entity = new SubcategoriaEntity(1L, "Combustivel", categoriaEntity);

        entity.setNome("Gasolina");

        assertThat(entity.getNome()).isEqualTo("Gasolina");
    }

    @Test
    void deveAtualizarCategoriaDaSubcategoriaEntity() {
        CategoriaEntity categoriaAntiga = new CategoriaEntity(1L, "Transporte");
        CategoriaEntity categoriaNova = new CategoriaEntity(2L, "Saude");
        SubcategoriaEntity entity = new SubcategoriaEntity(1L, "Combustivel", categoriaAntiga);

        entity.setCategoria(categoriaNova);

        assertThat(entity.getCategoria().getId()).isEqualTo(2L);
        assertThat(entity.getCategoria().getNome()).isEqualTo("Saude");
    }

    @Test
    void deveCriarSubcategoriaEntitySemIdParaNovaEntidade() {
        CategoriaEntity categoriaEntity = new CategoriaEntity(1L, "Transporte");
        SubcategoriaEntity entity = new SubcategoriaEntity(null, "Combustivel", categoriaEntity);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getNome()).isEqualTo("Combustivel");
    }

    @Test
    void deveCriarSubcategoriaEntityComCategoriaNula() {
        SubcategoriaEntity entity = new SubcategoriaEntity(1L, "Combustivel", null);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNome()).isEqualTo("Combustivel");
        assertThat(entity.getCategoria()).isNull();
    }

    @Test
    void deveCriarFuncaoSubcategoriaEntity(){
        SubcategoriaEntity entity = new SubcategoriaEntity();

        assertThat(entity).isNotNull();
    }


}

