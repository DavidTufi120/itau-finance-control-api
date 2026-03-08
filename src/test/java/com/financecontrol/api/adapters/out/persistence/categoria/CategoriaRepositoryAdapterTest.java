package com.financecontrol.api.adapters.out.persistence.categoria;

import com.financecontrol.api.domain.categoria.Categoria;
import com.financecontrol.api.domain.shared.PaginaResultado;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaRepositoryAdapterTest {
    @Mock
    private CategoriaJpaRepository jpaRepository;

    @Mock
    private CategoriaMapper mapper;

    @InjectMocks
    private CategoriaRepositoryAdapter repositoryAdapter;
    private CategoriaEntity categoriaEntity;
    private Categoria categoriaDomain;
    private ParametrosPaginacao paginacaoPadrao;

    @BeforeEach
    void setUp() {
        categoriaEntity = new CategoriaEntity(1L, "Transporte");
        categoriaDomain = new Categoria(1L, "Transporte");
        paginacaoPadrao = new ParametrosPaginacao(0, 20);
    }

    @Test
    void deveRetornarCategoriaAoBuscarPorIdExistente() {
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
        when(mapper.toDomain(categoriaEntity)).thenReturn(categoriaDomain);
        Optional<Categoria> resultado = repositoryAdapter.findById(1L);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getNome()).isEqualTo("Transporte");
        verify(jpaRepository).findById(1L);
    }

    @Test
    void deveRetornarVazioAoBuscarPorIdInexistente() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Categoria> resultado = repositoryAdapter.findById(99L);
        assertThat(resultado).isEmpty();
        verify(jpaRepository).findById(99L);
    }

    @Test
    void deveRetornarTodasAsCategoriasPaginadas() {
        Page<CategoriaEntity> page = new PageImpl<>(List.of(categoriaEntity), PageRequest.of(0, 20), 1);
        when(jpaRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(mapper.toDomain(categoriaEntity)).thenReturn(categoriaDomain);
        PaginaResultado<Categoria> resultado = repositoryAdapter.findAll(paginacaoPadrao);
        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.totalElementos()).isEqualTo(1);
        assertThat(resultado.paginaAtual()).isEqualTo(0);
        assertThat(resultado.tamanhoPagina()).isEqualTo(20);
        assertThat(resultado.totalPaginas()).isEqualTo(1);
    }

    @Test
    void deveRetornarCategoriasFiltradasPorNome() {
        Page<CategoriaEntity> page = new PageImpl<>(List.of(categoriaEntity), PageRequest.of(0, 20), 1);
        when(jpaRepository.findByNomeContainingIgnoreCase(eq("Trans"), any(PageRequest.class))).thenReturn(page);
        when(mapper.toDomain(categoriaEntity)).thenReturn(categoriaDomain);
        PaginaResultado<Categoria> resultado = repositoryAdapter.findByNomeContendo("Trans", paginacaoPadrao);
        assertThat(resultado.conteudo()).hasSize(1);
        assertThat(resultado.conteudo().get(0).getNome()).isEqualTo("Transporte");
        verify(jpaRepository).findByNomeContainingIgnoreCase(eq("Trans"), any(PageRequest.class));
    }

    @Test
    void deveRetornarTrueQuandoNomeJaExiste() {
        when(jpaRepository.existsByNome("Transporte")).thenReturn(true);
        boolean existe = repositoryAdapter.existsByNome("Transporte");
        assertThat(existe).isTrue();
        verify(jpaRepository).existsByNome("Transporte");
    }

    @Test
    void deveRetornarFalseQuandoNomeNaoExiste() {
        when(jpaRepository.existsByNome("Inexistente")).thenReturn(false);
        boolean existe = repositoryAdapter.existsByNome("Inexistente");
        assertThat(existe).isFalse();
    }

    @Test
    void deveRetornarTrueQuandoNomeExisteEmIdDiferente() {
        when(jpaRepository.existsByNomeAndIdNot("Transporte", 2L)).thenReturn(true);
        boolean existe = repositoryAdapter.existsByNomeAndIdDiferente("Transporte", 2L);
        assertThat(existe).isTrue();
        verify(jpaRepository).existsByNomeAndIdNot("Transporte", 2L);
    }

    @Test
    void deveRetornarFalseQuandoNomeNaoExisteEmIdDiferente() {
        when(jpaRepository.existsByNomeAndIdNot("Transporte", 1L)).thenReturn(false);
        boolean existe = repositoryAdapter.existsByNomeAndIdDiferente("Transporte", 1L);
        assertThat(existe).isFalse();
    }

    @Test
    void deveSalvarCategoriaComSucesso() {
        when(mapper.toEntity(categoriaDomain)).thenReturn(categoriaEntity);
        when(jpaRepository.save(categoriaEntity)).thenReturn(categoriaEntity);
        when(mapper.toDomain(categoriaEntity)).thenReturn(categoriaDomain);
        Categoria resultado = repositoryAdapter.save(categoriaDomain);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Transporte");
        verify(jpaRepository).save(categoriaEntity);
    }

    @Test
    void deveDeletarCategoriaPorIdComSucesso() {
        repositoryAdapter.deleteById(1L);
        verify(jpaRepository).deleteById(1L);
    }
}