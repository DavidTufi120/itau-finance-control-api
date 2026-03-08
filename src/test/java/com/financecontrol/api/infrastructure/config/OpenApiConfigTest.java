package com.financecontrol.api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {
    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void deveCriarBeanOpenAPIComInformacoesCorretas() {
        OpenAPI openAPI = openApiConfig.openAPI();
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Finance Control API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getInfo().getDescription()).isNotBlank();
    }

    @Test
    void deveRegistrarRequisitoDeSegurancaApiKey() {
        OpenAPI openAPI = openApiConfig.openAPI();
        assertThat(openAPI.getSecurity()).isNotNull().hasSize(1);
        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertThat(securityRequirement).containsKey("api-key");
    }

    @Test
    void deveRegistrarSchemeDeSegurancaNoHeader() {
        OpenAPI openAPI = openApiConfig.openAPI();
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("api-key");
        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("api-key");
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.APIKEY);
        assertThat(scheme.getIn()).isEqualTo(SecurityScheme.In.HEADER);
        assertThat(scheme.getName()).isEqualTo("api-key");
    }
}