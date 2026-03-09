package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.response.ApiErrorResponse;
import com.financecontrol.api.adapters.controller.response.BalancoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "Balanço", description = "Consulta do balanço geral de lançamentos em um período")
public interface BalancoControllerDocs {

    @Operation(
            summary = "Consultar balanço",
            description = "Calcula o balanço geral (receita, despesa e saldo) dos lançamentos no período informado. " +
                    "Opcionalmente pode ser filtrado por categoria. " +
                    "O campo 'categoria' só é retornado quando o filtro id_categoria for informado."
    )
    @ApiResponse(responseCode = "200", description = "Balanço calculado com sucesso",
            content = @Content(schema = @Schema(implementation = BalancoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos (datas ausentes ou inválidas)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "api-key ausente ou inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    ResponseEntity<BalancoResponse> consultar(
            @Parameter(description = "Data inicial do período (dd/MM/yyyy)", required = true, example = "01/01/2021")
            @RequestParam(name = "data_inicio") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,

            @Parameter(description = "Data final do período (dd/MM/yyyy)", required = true, example = "31/01/2021")
            @RequestParam(name = "data_fim") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,

            @Parameter(description = "ID da categoria para filtrar o balanço (opcional)")
            @RequestParam(name = "id_categoria", required = false) Long idCategoria
    );
}

