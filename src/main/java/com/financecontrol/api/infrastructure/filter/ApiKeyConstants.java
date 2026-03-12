package com.financecontrol.api.infrastructure.filter;

public final class ApiKeyConstants {

    public static final String API_KEY_HEADER = "api-key";
    public static final String ACTUATOR_HEALTH_PATH = "/actuator/health";
    public static final String SWAGGER_UI_PATH = "/swagger-ui";
    public static final String SWAGGER_HTML_PATH = "/swagger-ui.html";
    public static final String OPENAPI_DOCS_PATH = "/v3/api-docs";

    public static final String ERROR_CODE_UNAUTHORIZED = "nao_autorizado";
    public static final String ERROR_MESSAGE_UNAUTHORIZED = "api-key invalida ou nao informada";

    private ApiKeyConstants() {
    }
}

