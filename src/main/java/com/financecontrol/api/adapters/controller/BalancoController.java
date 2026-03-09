package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.response.BalancoResponse;
import com.financecontrol.api.adapters.controller.response.CategoriaResponse;
import com.financecontrol.api.domain.balanco.Balanco;
import com.financecontrol.api.domain.balanco.BalancoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/balanco")
public class BalancoController implements BalancoControllerDocs {

    private final BalancoService balancoService;

    public BalancoController(BalancoService balancoService) {
        this.balancoService = balancoService;
    }

    @GetMapping
    public ResponseEntity<BalancoResponse> consultar(
            @RequestParam(name = "data_inicio") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam(name = "data_fim") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @RequestParam(name = "id_categoria", required = false) Long idCategoria) {

        Balanco balanco = balancoService.calcular(dataInicio, dataFim, idCategoria);
        return ResponseEntity.ok(toResponse(balanco));
    }

    private BalancoResponse toResponse(Balanco balanco) {
        CategoriaResponse categoriaResponse = null;
        if (balanco.idCategoria() != null) {
            categoriaResponse = new CategoriaResponse(balanco.idCategoria(), balanco.nomeCategoria());
        }
        return new BalancoResponse(
                categoriaResponse,
                balanco.receita().toPlainString(),
                balanco.despesa().toPlainString(),
                balanco.saldo().toPlainString()
        );
    }
}

