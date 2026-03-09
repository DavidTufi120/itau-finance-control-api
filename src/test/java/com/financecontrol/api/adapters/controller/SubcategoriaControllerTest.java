package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.request.SubcategoriaRequest;
import com.financecontrol.api.adapters.controller.response.PageResponse;
import com.financecontrol.api.adapters.controller.response.SubcategoriaResponse;
import com.financecontrol.api.domain.subcategoria.Subcategoria;
import com.financecontrol.api.domain.subcategoria.SubcategoriaService;
import com.financecontrol.api.domain.shared.ParametrosPaginacao;
import com.financecontrol.api.domain.shared.PaginaResultado;
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
class SubcategoriaControllerTest {

    @Mock
    private SubcategoriaService subcategoriaService;

    @InjectMocks
    private SubcategoriaController subcategoriaController;

    private Subcategoria subcategoriaCombutivel;

    @BeforeEach
    void setUp() {
        subcategoriaCombutivel = new Subcategoria(1L, "Combustivel", 1L);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/v1/subcategorias");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @Test
    void deveRetornarListaPaginadaDeSubcategorias() {
        PaginaResultado<Subcategoria> paginaResultado = new PaginaResultado<>(
                List.of(subcategoriaCombutivel), 0, 20, 1, 1
        );
        when(subcategoriaService.listar(null, null, new ParametrosPaginacao(0, 20))).thenReturn(paginaResultado);

        ResponseEntity<PageResponse<SubcategoriaResponse>> resposta = subcategoriaController.listar(null, null, 0, 20);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().content()).hasSize(1);
        assertThat(resposta.getBody().totalElements()).isEqualTo(1);
    }

    @Test
    void deveRetornarListaFiltradaPorNome() {
        PaginaResultado<Subcategoria> paginaResultado = new PaginaResultado<>(
                List.of(subcategoriaCombutivel), 0, 20, 1, 1
        );
        when(subcategoriaService.listar(eq("Combu"), eq(null), any(ParametrosPaginacao.class))).thenReturn(paginaResultado);

        ResponseEntity<PageResponse<SubcategoriaResponse>> resposta = subcategoriaController.listar("Combu", null, 0, 20);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().content().get(0).nome()).isEqualTo("Combustivel");
    }

    @Test
    void deveRetornarListaFiltradaPorIdSubcategoria() {
        PaginaResultado<Subcategoria> paginaResultado = new PaginaResultado<>(
                List.of(subcategoriaCombutivel), 0, 20, 1, 1
        );
        when(subcategoriaService.listar(eq(null), eq(1L), any(ParametrosPaginacao.class))).thenReturn(paginaResultado);

        ResponseEntity<PageResponse<SubcategoriaResponse>> resposta = subcategoriaController.listar(null, 1L, 0, 20);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().content().get(0).id_subcategoria()).isEqualTo(1L);
    }

    @Test
    void deveRetornarSubcategoriaPorId() {
        when(subcategoriaService.buscarPorId(1L)).thenReturn(subcategoriaCombutivel);

        ResponseEntity<SubcategoriaResponse> resposta = subcategoriaController.buscarPorId(1L);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().id_subcategoria()).isEqualTo(1L);
        assertThat(resposta.getBody().nome()).isEqualTo("Combustivel");
        assertThat(resposta.getBody().id_categoria()).isEqualTo(1L);
    }

    @Test
    void deveCriarSubcategoriaERetornar201ComLocation() {
        SubcategoriaRequest request = new SubcategoriaRequest("Combustivel", 1L);
        when(subcategoriaService.criar(any(Subcategoria.class))).thenReturn(subcategoriaCombutivel);

        ResponseEntity<SubcategoriaResponse> resposta = subcategoriaController.criar(request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().id_subcategoria()).isEqualTo(1L);
        assertThat(resposta.getBody().nome()).isEqualTo("Combustivel");
        assertThat(resposta.getHeaders().getLocation()).isNotNull();
        assertThat(resposta.getHeaders().getLocation().toString()).contains("1");
    }

    @Test
    void deveAtualizarSubcategoriaERetornar200() {
        SubcategoriaRequest request = new SubcategoriaRequest("Gasolina", 1L);
        Subcategoria subcategoriaAtualizada = new Subcategoria(1L, "Gasolina", 1L);
        when(subcategoriaService.atualizar(eq(1L), any(Subcategoria.class))).thenReturn(subcategoriaAtualizada);

        ResponseEntity<SubcategoriaResponse> resposta = subcategoriaController.atualizar(1L, request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().nome()).isEqualTo("Gasolina");
        verify(subcategoriaService).atualizar(eq(1L), any(Subcategoria.class));
    }

    @Test
    void deveDeletarSubcategoriaERetornar204() {
        ResponseEntity<Void> resposta = subcategoriaController.deletar(1L);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(resposta.getBody()).isNull();
        verify(subcategoriaService).deletar(1L);
    }
}

