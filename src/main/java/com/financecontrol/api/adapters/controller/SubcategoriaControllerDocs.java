package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.request.SubcategoriaRequest;
import com.financecontrol.api.adapters.controller.response.ApiErrorResponse;
import com.financecontrol.api.adapters.controller.response.PageResponse;
import com.financecontrol.api.adapters.controller.response.SubcategoriaResponse;
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

@Tag(name = "Subcategorias", description = "Gerenciamento de subcategorias financeiras")
public interface SubcategoriaControllerDocs {

    @Operation(summary = "Listar subcategorias", description = "Retorna uma lista paginada de subcategorias. Filtrável por nome e/ou id da subcategoria.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = PageResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<PageResponse<SubcategoriaResponse>> listar(
            @Parameter(description = "Filtro por nome da subcategoria") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtro por ID da subcategoria") @RequestParam(name = "id_subcategoria", required = false) Long idSubcategoria,
            @Parameter(description = "Número da página (inicia em 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "20") @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Buscar subcategoria por ID", description = "Retorna os dados de uma subcategoria pelo seu ID.")
    @ApiResponse(responseCode = "200", description = "Subcategoria encontrada",
            content = @Content(schema = @Schema(implementation = SubcategoriaResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Subcategoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<SubcategoriaResponse> buscarPorId(
            @Parameter(description = "ID da subcategoria", required = true, example = "1") @PathVariable Long id);

    @Operation(summary = "Criar subcategoria", description = "Cria uma nova subcategoria vinculada a uma categoria existente. O nome deve ser único dentro da mesma categoria e ter pelo menos 3 caracteres.")
    @ApiResponse(responseCode = "201", description = "Subcategoria criada com sucesso",
            headers = @Header(name = "Location", description = "URL da subcategoria criada"),
            content = @Content(schema = @Schema(implementation = SubcategoriaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (nome em branco, muito curto ou id_categoria ausente)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Já existe uma subcategoria com o mesmo nome nesta categoria",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<SubcategoriaResponse> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da subcategoria a ser criada", required = true,
                    content = @Content(schema = @Schema(implementation = SubcategoriaRequest.class)))
            @RequestBody @Valid SubcategoriaRequest request);

    @Operation(summary = "Atualizar subcategoria", description = "Atualiza o nome e/ou categoria de uma subcategoria existente. O novo nome deve ser único dentro da categoria e ter pelo menos 3 caracteres.")
    @ApiResponse(responseCode = "200", description = "Subcategoria atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = SubcategoriaResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos (nome em branco, muito curto ou id_categoria ausente)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Subcategoria ou categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Já existe uma subcategoria com o mesmo nome nesta categoria",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<SubcategoriaResponse> atualizar(
            @Parameter(description = "ID da subcategoria a ser atualizada", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados da subcategoria", required = true,
                    content = @Content(schema = @Schema(implementation = SubcategoriaRequest.class)))
            @RequestBody @Valid SubcategoriaRequest request);

    @Operation(summary = "Deletar subcategoria", description = "Remove uma subcategoria pelo ID. Não é permitido remover uma subcategoria que possua lançamentos atrelados.")
    @ApiResponse(responseCode = "204", description = "Subcategoria removida com sucesso")
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Subcategoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "422", description = "Subcategoria possui lançamentos atrelados e não pode ser removida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<Void> deletar(
            @Parameter(description = "ID da subcategoria a ser removida", required = true, example = "1") @PathVariable Long id);
}

