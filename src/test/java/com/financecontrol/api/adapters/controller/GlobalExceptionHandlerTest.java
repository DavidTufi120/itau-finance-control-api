package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void shouldReturnBadRequestWhenValidationFails() {
        FieldError fieldError = new FieldError("objeto", "nome", "é obrigatório");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleValidationException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("erro_validacao");
        assertThat(response.getBody().mensagem()).isEqualTo("O campo 'nome' é obrigatório");
    }

    @Test
    void shouldReturnBadRequestWhenValidationFailsWithNoFieldError() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of());

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleValidationException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("erro_validacao");
        assertThat(response.getBody().mensagem()).isEqualTo("Requisição inválida");
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsNotReadable() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleNotReadableException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("corpo_invalido");
    }

    @Test
    void shouldReturnBadRequestWhenRequiredParameterIsMissing() {
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException("data_inicio", "String");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleMissingParameterException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("parametro_ausente");
        assertThat(response.getBody().mensagem()).isEqualTo("O parâmetro 'data_inicio' é obrigatório");
    }

    @Test
    void shouldReturnBadRequestWhenParameterTypeIsInvalid() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("id");
        when(exception.getValue()).thenReturn("abc");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleTypeMismatchException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("tipo_invalido");
        assertThat(response.getBody().mensagem()).isEqualTo("O parâmetro 'id' possui um valor inválido: 'abc'");
    }

    @Test
    void shouldReturnNotFoundWhenRouteDoesNotExist() {
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleNoResourceFoundException(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("rota_nao_encontrada");
    }

    @Test
    void shouldReturnInternalServerErrorWhenUnexpectedExceptionOccurs() {
        Exception exception = new RuntimeException("erro inesperado");

        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleGenericException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().codigo()).isEqualTo("erro_interno");
    }
}

