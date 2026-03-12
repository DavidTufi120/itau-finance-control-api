package com.financecontrol.api.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
    }

    @Test
    void deveCriarBeanWebMvcConfigurer() {
        WebMvcConfigurer configurer = corsConfig.corsConfigurer();

        assertThat(configurer).isNotNull();
    }

    @Test
    void deveConfigurarCorsComOrigensMetodosEHeadersEsperados() {
        WebMvcConfigurer configurer = corsConfig.corsConfigurer();
        CorsRegistry registry = new CorsRegistry();

        configurer.addCorsMappings(registry);

        @SuppressWarnings("unchecked")
        List<CorsRegistration> registrations = (List<CorsRegistration>) ReflectionTestUtils.getField(registry, "registrations");
        assertThat(registrations).isNotNull().hasSize(1);

        CorsRegistration registration = registrations.get(0);
        assertThat(ReflectionTestUtils.getField(registration, "pathPattern")).isEqualTo("/**");

        CorsConfiguration configuration = (CorsConfiguration) ReflectionTestUtils.getField(registration, "config");
        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins())
                .containsExactly(
                        "http://localhost:4200",
                        "https://finance-control-frontend-production.up.railway.app"
                );
        assertThat(configuration.getAllowedMethods())
                .containsExactly("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(configuration.getAllowedHeaders()).containsExactly("*");
    }
}

