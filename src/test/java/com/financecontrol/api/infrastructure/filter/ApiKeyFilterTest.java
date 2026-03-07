package com.financecontrol.api.infrastructure.filter;

import com.financecontrol.api.infrastructure.config.SecurityProperties;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ApiKeyFilterTest {

    private static final String VALID_API_KEY = "aXRhw7o=";

    private ApiKeyFilter apiKeyFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityProperties securityProperties = new SecurityProperties(VALID_API_KEY);
        apiKeyFilter = new ApiKeyFilter(securityProperties);
        filterChain = Mockito.mock(FilterChain.class);
    }

    @Test
    void shouldAllowRequestWithValidApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(ApiKeyConstants.API_KEY_HEADER, VALID_API_KEY);

        apiKeyFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldRejectRequestWithInvalidApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(ApiKeyConstants.API_KEY_HEADER, "chave-errada");

        apiKeyFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldRejectRequestWithoutApiKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiKeyFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}

