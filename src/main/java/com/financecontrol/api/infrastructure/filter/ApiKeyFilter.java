package com.financecontrol.api.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financecontrol.api.adapters.controller.response.ApiErrorResponse;
import com.financecontrol.api.infrastructure.config.SecurityProperties;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiKeyFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final SecurityProperties securityProperties;

    public ApiKeyFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (!isPublicPath(httpRequest) && !isValidApiKey(httpRequest)) {
            logger.warn("Requisicao bloqueada: api-key invalida ou ausente. IP: {}, URI: {}",
                    httpRequest.getRemoteAddr(), httpRequest.getRequestURI());
            writeUnauthorizedResponse(httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith(ApiKeyConstants.ACTUATOR_PATH)
                || uri.startsWith(ApiKeyConstants.SWAGGER_UI_PATH)
                || uri.startsWith(ApiKeyConstants.OPENAPI_DOCS_PATH);
    }

    private boolean isValidApiKey(HttpServletRequest request) {
        String receivedApiKey = request.getHeader(ApiKeyConstants.API_KEY_HEADER);
        return securityProperties.apiKey().equals(receivedApiKey);
    }

    private void writeUnauthorizedResponse(HttpServletResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpResponse.getWriter().write(
                objectMapper.writeValueAsString(
                        new ApiErrorResponse(
                                ApiKeyConstants.ERROR_CODE_UNAUTHORIZED,
                                ApiKeyConstants.ERROR_MESSAGE_UNAUTHORIZED
                        )
                )
        );
    }
}
