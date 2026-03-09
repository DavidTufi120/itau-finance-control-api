package com.financecontrol.api.adapters.out.persistence.subcategoria;

import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaEntity;
import com.financecontrol.api.adapters.out.persistence.categoria.CategoriaJpaRepository;
import com.financecontrol.api.domain.subcategoria.Subcategoria;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubcategoriaRepositoryAdapterTest {

    @Mock
    private SubcategoriaJpaRepository jpaRepository;

    @Mock
    private CategoriaJpaRepository categoriaJpaRepository;

    @Mock
    private SubcategoriaMapper mapper;

    @InjectMocks
    private SubcategoriaRepositoryAdapter adapter;

    private CategoriaEntity categoriaEntity;
    private SubcategoriaEntity subcategoriaEntity;
    private Subcategoria subcategoriaDominio;
    private ParametrosPaginacao paginacaoPadrao;

    @BeforeEach
    void setUp() {
        categoriaEntity = new CategoriaEntity(1L, "Transporte");
        subcategoriaEntity = new SubcategoriaEntity(1L, "Combustivel", categoriaEntity);
        subcategoriaDominio = new Subcategoria(1L, "Combustivel", 1L);
        paginacaoPadrao = new ParametrosPaginacao(0, 20);
    }

    @Test
    void deveBuscarSubcategoriaPorIdComSucesso() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(subcategoriaEntity));
        when(mapper.toDomain(subcategoriaEntity)).thenReturn(subcategoriaDominio);

        Optional<Subcategoria> resultado = adapter.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getNome()).isEqualTo("Combustivel");
    }

    @Test
    void deveRetornarVazioQuandoSubcategoriaNaoEncontradaPorId() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Subcategoria> resultado = adapter.findById(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void deveListarTodasAsSubcategorias() {
        Page<SubcategoriaEntity> pagina = new PageImpl<>(
                List.of(subcategoriaEntity), PageRequest.of(0, 20), 1
        );
        when(jpaRepository.findAll(any(PageRequest.class))).thenReturn(pagina);
        when(mapper.toDomain(subcategoriaEntity)).thenReturn(subcategoriaDominio);

        PaginaResultado<Subcategoria> resultado = adapter.findAll(paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.totalElementos()).isEqualTo(1);
    }

    @Test
    void deveListarSubcategoriasFiltradasPorNome() {
        Page<SubcategoriaEntity> pagina = new PageImpl<>(
                List.of(subcategoriaEntity), PageRequest.of(0, 20), 1
        );
        when(jpaRepository.findByNomeContainingIgnoreCase(eq("Combu"), any(PageRequest.class))).thenReturn(pagina);
        when(mapper.toDomain(subcategoriaEntity)).thenReturn(subcategoriaDominio);

        PaginaResultado<Subcategoria> resultado = adapter.findByNomeContendo("Combu", paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.conteudo().get(0).getNome()).isEqualTo("Combustivel");
    }

    @Test
    void deveListarSubcategoriasFiltradasPorId() {
        Page<SubcategoriaEntity> pagina = new PageImpl<>(
                List.of(subcategoriaEntity), PageRequest.of(0, 20), 1
        );
        when(jpaRepository.findById(eq(1L), any(PageRequest.class))).thenReturn(pagina);
        when(mapper.toDomain(subcategoriaEntity)).thenReturn(subcategoriaDominio);

        PaginaResultado<Subcategoria> resultado = adapter.findByIdPaginado(1L, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
    }

    @Test
    void deveListarSubcategoriasFiltradasPorNomeEId() {
        Page<SubcategoriaEntity> pagina = new PageImpl<>(
                List.of(subcategoriaEntity), PageRequest.of(0, 20), 1
        );
        when(jpaRepository.findByNomeContainingIgnoreCaseAndId(eq("Combu"), eq(1L), any(PageRequest.class))).thenReturn(pagina);
        when(mapper.toDomain(subcategoriaEntity)).thenReturn(subcategoriaDominio);

        PaginaResultado<Subcategoria> resultado = adapter.findByNomeContendoEId("Combu", 1L, paginacaoPadrao);

        assertThat(resultado.conteudo()).hasSize(1);
    }

    @Test
    void deveVerificarExistenciaPorNomeECategoria() {
        when(jpaRepository.existsByNomeAndCategoriaId("Combustivel", 1L)).thenReturn(true);

        boolean existe = adapter.existsByNomeAndIdCategoria("Combustivel", 1L);

        assertThat(existe).isTrue();
        verify(jpaRepository).existsByNomeAndCategoriaId("Combustivel", 1L);
    }

    @Test
    void deveVerificarExistenciaPorNomeCategoriaEIdDiferente() {
        when(jpaRepository.existsByNomeAndCategoriaIdAndIdNot("Gasolina", 1L, 1L)).thenReturn(false);

        boolean existe = adapter.existsByNomeAndIdCategoriaAndIdDiferente("Gasolina", 1L, 1L);

        assertThat(existe).isFalse();
        verify(jpaRepository).existsByNomeAndCategoriaIdAndIdNot("Gasolina", 1L, 1L);
    }

    @Test
    void deveSalvarNovaSubcategoria() {
        Subcategoria novaSubcategoria = new Subcategoria(null, "Combustivel", 1L);
        SubcategoriaEntity entitySemId = new SubcategoriaEntity(null, "Combustivel", categoriaEntity);
        SubcategoriaEntity entitySalva = new SubcategoriaEntity(1L, "Combustivel", categoriaEntity);

        when(categoriaJpaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
        when(mapper.toEntity(novaSubcategoria, categoriaEntity)).thenReturn(entitySemId);
        when(jpaRepository.save(entitySemId)).thenReturn(entitySalva);
        when(mapper.toDomain(entitySalva)).thenReturn(subcategoriaDominio);

        Subcategoria resultado = adapter.save(novaSubcategoria);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Combustivel");
        verify(jpaRepository).save(entitySemId);
    }

    @Test
    void deveLancarExcecaoAoSalvarSubcategoriaComCategoriaNaoEncontrada() {
        Subcategoria novaSubcategoria = new Subcategoria(null, "Combustivel", 99L);
        when(categoriaJpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.save(novaSubcategoria))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveAtualizarSubcategoriaExistente() {
        Subcategoria dadosAtualizados = new Subcategoria(1L, "Gasolina", 1L);
        SubcategoriaEntity entityAtualizada = new SubcategoriaEntity(1L, "Gasolina", categoriaEntity);
        Subcategoria dominioAtualizado = new Subcategoria(1L, "Gasolina", 1L);

        when(categoriaJpaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(subcategoriaEntity));
        when(jpaRepository.save(subcategoriaEntity)).thenReturn(entityAtualizada);
        when(mapper.toDomain(entityAtualizada)).thenReturn(dominioAtualizado);

        Subcategoria resultado = adapter.save(dadosAtualizados);

        assertThat(resultado.getNome()).isEqualTo("Gasolina");
        verify(jpaRepository).save(subcategoriaEntity);
    }

    @Test
    void deveDeletarSubcategoriaPorId() {
        adapter.deleteById(1L);

        verify(jpaRepository).deleteById(1L);
    }
}

