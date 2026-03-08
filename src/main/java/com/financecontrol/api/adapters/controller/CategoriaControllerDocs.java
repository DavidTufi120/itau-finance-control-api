package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.request.CategoriaRequest;
import com.financecontrol.api.adapters.controller.response.ApiErrorResponse;
import com.financecontrol.api.adapters.controller.response.CategoriaResponse;
import com.financecontrol.api.adapters.controller.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Categorias", description = "Gerenciamento de categorias financeiras")
public interface CategoriaControllerDocs {

    @Operation(summary = "Listar categorias", description = "Retorna uma lista paginada de categorias. Filtrável por nome.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<PageResponse<CategoriaResponse>> listar(
            @Parameter(description = "Filtro por nome da categoria") @RequestParam(required = false) String nome,
            @Parameter(description = "Número da página (inicia em 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "20") @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Buscar categoria por ID", description = "Retorna os dados de uma categoria pelo seu ID.")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada",
            content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<CategoriaResponse> buscarPorId(
            @Parameter(description = "ID da categoria", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Criar categoria", description = "Cria uma nova categoria. O nome deve ser único no sistema e ter pelo menos 3 caracteres.")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
            headers = @Header(name = "Location", description = "URL da categoria criada"),
            content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (nome em branco ou muito longo)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Já existe uma categoria com o mesmo nome",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<CategoriaResponse> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da categoria a ser criada", required = true,
                    content = @Content(schema = @Schema(implementation = CategoriaRequest.class)))
            @RequestBody @Valid CategoriaRequest request);

    @Operation(summary = "Atualizar categoria", description = "Atualiza o nome de uma categoria existente. O novo nome deve ser único e ter pelo menos 3 caracteres.")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = CategoriaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (nome em branco ou muito longo)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Já existe uma categoria com o mesmo nome",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<CategoriaResponse> atualizar(
            @Parameter(description = "ID da categoria a ser atualizada", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados da categoria", required = true,
                    content = @Content(schema = @Schema(implementation = CategoriaRequest.class)))
            @RequestBody @Valid CategoriaRequest request);

    @Operation(summary = "Deletar categoria", description = "Remove uma categoria pelo ID.")
    @ApiResponse(responseCode = "204", description = "Categoria removida com sucesso")
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<Void> deletar(
            @Parameter(description = "ID da categoria a ser removida", required = true, example = "1") @PathVariable Long id);
}
