package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.response.ApiErrorResponse;
import com.financecontrol.api.domain.shared.NegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        FieldError firstError = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);

        String mensagem = firstError != null
                ? String.format("O campo '%s' %s", firstError.getField(), firstError.getDefaultMessage())
                : "Requisição inválida";

        logger.warn("Erro de validação em {}: {}", request.getRequestURI(), mensagem);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("erro_validacao", mensagem));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {

        logger.warn("Corpo da requisição inválido em {}: {}", request.getRequestURI(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("corpo_invalido", "O corpo da requisição está ausente ou com formato inválido"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParameterException(
            MissingServletRequestParameterException exception,
            HttpServletRequest request) {

        String mensagem = String.format("O parâmetro '%s' é obrigatório", exception.getParameterName());

        logger.warn("Parâmetro ausente em {}: {}", request.getRequestURI(), mensagem);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("parametro_ausente", mensagem));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {

        String mensagem = String.format("O parâmetro '%s' possui um valor inválido: '%s'",
                exception.getName(), exception.getValue());

        logger.warn("Tipo inválido em {}: {}", request.getRequestURI(), mensagem);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse("tipo_invalido", mensagem));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(HttpServletRequest request) {

        logger.warn("Rota não encontrada: {}", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("rota_nao_encontrada", "A rota acessada não existe"));
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ApiErrorResponse> handleNegocioException(
            NegocioException exception,
            HttpServletRequest request) {

        HttpStatus status = resolverStatus(exception.getCodigo());

        logger.warn("Erro de negócio em {}: {}", request.getRequestURI(), exception.getMessage());

        return ResponseEntity
                .status(status)
                .body(new ApiErrorResponse(exception.getCodigo(), exception.getMessage()));
    }

    private HttpStatus resolverStatus(String codigo) {
        return switch (codigo) {
            case "recurso_nao_encontrado" -> HttpStatus.NOT_FOUND;
            case "nome_invalido", "erro_validacao" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.CONFLICT;
        };
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccessException(
            DataAccessException exception,
            HttpServletRequest request) {

        logger.error("Falha de acesso ao banco de dados em {}: {}", request.getRequestURI(), exception.getMessage(), exception);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiErrorResponse("servico_indisponivel", "O serviço está temporariamente indisponível, tente novamente em instantes"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request) {

        logger.error("Erro inesperado em {}: {}", request.getRequestURI(), exception.getMessage(), exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse("erro_interno", "Ocorreu um erro interno, tente novamente mais tarde"));
    }
}

