package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.request.LancamentoRequest;
import com.financecontrol.api.adapters.controller.response.ApiErrorResponse;
import com.financecontrol.api.adapters.controller.response.LancamentoResponse;
import com.financecontrol.api.adapters.controller.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Lançamentos", description = "Gerenciamento de lançamentos financeiros (créditos e débitos)")
public interface LancamentoControllerDocs {

    @Operation(summary = "Listar lançamentos", description = "Retorna uma lista paginada de lançamentos. Filtrável por id_subcategoria, data_inicio e data_fim.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<PageResponse<LancamentoResponse>> listar(
            @Parameter(description = "Filtro por ID da subcategoria") @RequestParam(name = "id_subcategoria", required = false) Long idSubcategoria,
            @Parameter(description = "Data inicial do filtro (dd/MM/yyyy)", example = "01/01/2024") @RequestParam(name = "data_inicio", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @Parameter(description = "Data final do filtro (dd/MM/yyyy)", example = "31/01/2024") @RequestParam(name = "data_fim", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @Parameter(description = "Número da página (inicia em 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "20") @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Buscar lançamento por ID", description = "Retorna os dados de um lançamento pelo seu ID.")
    @ApiResponse(responseCode = "200", description = "Lançamento encontrado",
            content = @Content(schema = @Schema(implementation = LancamentoResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Lançamento não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<LancamentoResponse> buscarPorId(
            @Parameter(description = "ID do lançamento", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Criar lançamento", description = "Registra um novo lançamento financeiro. O valor pode ser positivo (crédito) ou negativo (débito) e não pode ser zero. A data é opcional — se não informada, utiliza a data atual.")
    @ApiResponse(responseCode = "201", description = "Lançamento criado com sucesso",
            headers = @Header(name = "Location", description = "URL do lançamento criado"),
            content = @Content(schema = @Schema(implementation = LancamentoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (valor zero, subcategoria ausente ou data inválida)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Subcategoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<LancamentoResponse> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do lançamento a ser criado", required = true,
                    content = @Content(schema = @Schema(implementation = LancamentoRequest.class)))
            @RequestBody @Valid LancamentoRequest request);

    @Operation(summary = "Atualizar lançamento", description = "Atualiza os dados de um lançamento existente.")
    @ApiResponse(responseCode = "200", description = "Lançamento atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = LancamentoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (valor zero, subcategoria ausente ou data inválida)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Lançamento ou subcategoria não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<LancamentoResponse> atualizar(
            @Parameter(description = "ID do lançamento a ser atualizado", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados do lançamento", required = true,
                    content = @Content(schema = @Schema(implementation = LancamentoRequest.class)))
            @RequestBody @Valid LancamentoRequest request);

    @Operation(summary = "Deletar lançamento", description = "Remove um lançamento pelo ID.")
    @ApiResponse(responseCode = "204", description = "Lançamento removido com sucesso")
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Lançamento não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<Void> deletar(
            @Parameter(description = "ID do lançamento a ser removido", required = true, example = "1") @PathVariable Long id);
}

