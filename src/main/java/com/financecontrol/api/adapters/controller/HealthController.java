package com.financecontrol.api.adapters.controller;

import com.financecontrol.api.adapters.controller.response.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "Verificação de saúde da aplicação")
@RestController
@RequestMapping("/v1/health")
public class HealthController {

    @Operation(summary = "Health check", description = "Retorna o status atual da aplicação.")
    @ApiResponse(responseCode = "200", description = "Aplicação operacional")
    @GetMapping
    public ResponseEntity<HealthResponse> getHealth() {
        return ResponseEntity.ok(new HealthResponse("UP"));
    }
}
