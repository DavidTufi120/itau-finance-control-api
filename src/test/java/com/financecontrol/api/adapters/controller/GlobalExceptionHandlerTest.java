package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.response.ApiErrorResponse;
import com.financecontrol.api.domain.shared.MensagensErro;
import com.financecontrol.api.domain.shared.NegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/v1/test");
    }

    @Test
    void deveRetornar400QuandoValidacaoDeCampoFalhar() {
        FieldError fieldError = new FieldError("objeto", "nome", "é obrigatório");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleValidationException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("erro_validacao");
        assertThat(resposta.getBody().mensagem()).isEqualTo("O campo 'nome' é obrigatório");
    }

    @Test
    void deveRetornar400ComMensagemGenericaQuandoNaoHouverCampoDeErro() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of());
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleValidationException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("erro_validacao");
        assertThat(resposta.getBody().mensagem()).isEqualTo("Requisição inválida");
    }

    @Test
    void deveRetornar400QuandoCorpoDaRequisicaoForInvalido() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleNotReadableException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("corpo_invalido");
        assertThat(resposta.getBody().mensagem()).isEqualTo("O corpo da requisição está ausente ou com formato inválido");
    }

    @Test
    void deveRetornar400QuandoParametroObrigatorioEstiverAusente() {
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException("data_inicio", "String");
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleMissingParameterException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("parametro_ausente");
        assertThat(resposta.getBody().mensagem()).isEqualTo("O parâmetro 'data_inicio' é obrigatório");
    }

    @Test
    void deveRetornar400QuandoTipoDoParametroForInvalido() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("id");
        when(exception.getValue()).thenReturn("abc");
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleTypeMismatchException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("tipo_invalido");
        assertThat(resposta.getBody().mensagem()).isEqualTo("O parâmetro 'id' possui um valor inválido: 'abc'");
    }

    @Test
    void deveRetornar404QuandoRotaNaoExistir() {
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleNoResourceFoundException(request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("rota_nao_encontrada");
        assertThat(resposta.getBody().mensagem()).isEqualTo("A rota acessada não existe");
    }

    @Test
    void deveRetornar500QuandoOcorrerErroInesperado() {
        Exception exception = new RuntimeException("erro inesperado");
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleGenericException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("erro_interno");
        assertThat(resposta.getBody().mensagem()).isEqualTo("Ocorreu um erro interno, tente novamente mais tarde");
    }

    @Test
    void deveRetornar404QuandoNegocioExceptionForRecursoNaoEncontrado() {
        NegocioException exception = new NegocioException(
                MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO,
                MensagensErro.CATEGORIA_NAO_ENCONTRADA + "1"
        );
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleNegocioException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo(MensagensErro.CODIGO_RECURSO_NAO_ENCONTRADO);
        assertThat(resposta.getBody().mensagem()).contains("1");
    }

    @Test
    void deveRetornar400QuandoNegocioExceptionForNomeInvalido() {
        NegocioException exception = new NegocioException(
                MensagensErro.CODIGO_NOME_INVALIDO,
                MensagensErro.NOME_MUITO_CURTO
        );
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleNegocioException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo(MensagensErro.CODIGO_NOME_INVALIDO);
        assertThat(resposta.getBody().mensagem()).isEqualTo(MensagensErro.NOME_MUITO_CURTO);
    }

    @Test
    void deveRetornar409QuandoNegocioExceptionForNomeDuplicado() {
        NegocioException exception = new NegocioException(
                MensagensErro.CODIGO_NOME_DUPLICADO,
                MensagensErro.CATEGORIA_NOME_DUPLICADO + "Transporte"
        );
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleNegocioException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo(MensagensErro.CODIGO_NOME_DUPLICADO);
        assertThat(resposta.getBody().mensagem()).contains("Transporte");
    }

    @Test
    void deveRetornar503QuandoBancoDeDadosForInacessivel() {
        DataAccessException exception = new DataAccessResourceFailureException("Banco inacessivel");
        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleDataAccessException(exception, request);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("servico_indisponivel");
        assertThat(resposta.getBody().mensagem()).isEqualTo("O serviço está temporariamente indisponível, tente novamente em instantes");
    }

    @Test
    void deveRetornar400QuandoNegocioExceptionUsarCodigoErroValidacao() {
        NegocioException exception = new NegocioException(
                "erro_validacao",
                "Erro de validação de negócio"
        );

        ResponseEntity<ApiErrorResponse> resposta = globalExceptionHandler.handleNegocioException(exception, request);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().codigo()).isEqualTo("erro_validacao");
    }
}