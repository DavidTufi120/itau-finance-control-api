package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.request.CategoriaRequest;
import com.financecontrol.api.adapters.controller.response.CategoriaResponse;
import com.financecontrol.api.adapters.controller.response.PageResponse;
import com.financecontrol.api.domain.categoria.Categoria;
import com.financecontrol.api.domain.categoria.CategoriaService;
import com.financecontrol.api.domain.shared.PaginaResultado;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {
    @Mock
    private CategoriaService categoriaService;
    @InjectMocks
    private CategoriaController categoriaController;
    private Categoria categoriaTransporte;

    @BeforeEach
    void setUp() {
        categoriaTransporte = new Categoria(1L, "Transporte");
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/v1/categorias");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @Test
    void deveRetornarListaPaginadaDeCategorias() {
        PaginaResultado<Categoria> paginaResultado = new PaginaResultado<>(
                List.of(categoriaTransporte), 0, 20, 1, 1
        );
        when(categoriaService.listar(null, new ParametrosPaginacao(0, 20))).thenReturn(paginaResultado);
        ResponseEntity<PageResponse<CategoriaResponse>> resposta = categoriaController.listar(null, 0, 20);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().content()).hasSize(1);
        assertThat(resposta.getBody().totalElements()).isEqualTo(1);
    }

    @Test
    void deveRetornarListaFiltradaPorNome() {
        PaginaResultado<Categoria> paginaResultado = new PaginaResultado<>(
                List.of(categoriaTransporte), 0, 20, 1, 1
        );
        when(categoriaService.listar(eq("Trans"), any(ParametrosPaginacao.class))).thenReturn(paginaResultado);
        ResponseEntity<PageResponse<CategoriaResponse>> resposta = categoriaController.listar("Trans", 0, 20);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().content().get(0).nome()).isEqualTo("Transporte");
    }

    @Test
    void deveRetornarCategoriaPorId() {
        when(categoriaService.buscarPorId(1L)).thenReturn(categoriaTransporte);
        ResponseEntity<CategoriaResponse> resposta = categoriaController.buscarPorId(1L);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().id_categoria()).isEqualTo(1L);
        assertThat(resposta.getBody().nome()).isEqualTo("Transporte");
    }

    @Test
    void deveCriarCategoriaERetornar201ComLocation() {
        CategoriaRequest request = new CategoriaRequest("Transporte");
        when(categoriaService.criar(any(Categoria.class))).thenReturn(categoriaTransporte);
        ResponseEntity<CategoriaResponse> resposta = categoriaController.criar(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().id_categoria()).isEqualTo(1L);
        assertThat(resposta.getBody().nome()).isEqualTo("Transporte");
        assertThat(resposta.getHeaders().getLocation()).isNotNull();
        assertThat(resposta.getHeaders().getLocation().toString()).contains("1");
    }

    @Test
    void deveAtualizarCategoriaERetornar200() {
        CategoriaRequest request = new CategoriaRequest("Alimentacao");
        Categoria categoriaAtualizada = new Categoria(1L, "Alimentacao");
        when(categoriaService.atualizar(eq(1L), any(Categoria.class))).thenReturn(categoriaAtualizada);
        ResponseEntity<CategoriaResponse> resposta = categoriaController.atualizar(1L, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().nome()).isEqualTo("Alimentacao");
        verify(categoriaService).atualizar(eq(1L), any(Categoria.class));
    }

    @Test
    void deveDeletarCategoriaERetornar204() {
        ResponseEntity<Void> resposta = categoriaController.deletar(1L);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(resposta.getBody()).isNull();
        verify(categoriaService).deletar(1L);
    }
}