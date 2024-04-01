package com.gidas.capneebe.global.utils.logs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Value("${filter.logging.enabled:false}")
    private boolean isFilterEnabled;

    // Lista de URI que deseas omitir del filtro
    private static final List<String> EXCLUDED_PATTERNS = Arrays.asList("/swagger", "/v3/api-docs", "/actuator/health", "/actuator", "/actuator/info");

    private boolean isUriExcluded(String requestUri) {
        return EXCLUDED_PATTERNS.stream().anyMatch(requestUri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obten la URI actual
        String requestUri = request.getRequestURI();
        if (!isFilterEnabled || isUriExcluded(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        final long startTime = System.currentTimeMillis();
        filterChain.doFilter(requestWrapper, responseWrapper);
        final long timeTaken = System.currentTimeMillis() - startTime;

        String requestBody = getContentAsString(requestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
        String responseBody = getContentAsString(responseWrapper.getContentAsByteArray(), response.getCharacterEncoding());

        // Convertir el responseBody a un formato legible
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJson = mapper.readTree(responseBody);
        String responseBodyFormatted = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseJson);

        // Loguea la información
        logRequestResponseInfo(request, response, requestBody, responseBodyFormatted, timeTaken);

        responseWrapper.copyBodyToResponse();
    }

    private String getContentAsString(byte[] contentAsByteArray, String characterEncoding) {
        if (contentAsByteArray == null || contentAsByteArray.length == 0) {
            return "";
        }
        try {
            return new String(contentAsByteArray, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            log.error("Error al convertir el contenido a cadena: {}", e.getMessage());
        }
        return "";
    }

    private void logRequestResponseInfo(HttpServletRequest request, HttpServletResponse response, String requestBody, String responseBody, long timeTaken) {
        // Agregar los parámetros recibidos en la solicitud
        Enumeration<String> parameterNames = request.getParameterNames();
        StringBuilder parameters = new StringBuilder();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.append(Color.YELLOW).append("    |--> ").append(paramName).append(": ").append(Color.CYAN).append(paramValue).append("\n");
        }

        log.warn("\n----------------------------------------------------------------------------\n" +
                Color.PURPLE + "***************************\n" +
                Color.PURPLE + "*** REQUEST CLIENT DATA ***\n" +
                Color.PURPLE + "***************************\n" +

                Color.YELLOW + "RemoteAddress: " + Color.CYAN + request.getRemoteAddr() + "\n" +
                Color.YELLOW + "sec-ch-ua: " + Color.CYAN + request.getHeader("sec-ch-ua") + "\n" +
                Color.YELLOW + "User-Agent: " + Color.CYAN + request.getHeader("User-Agent") + "\n" +
                Color.YELLOW + "sec-ch-ua-platform: " + Color.CYAN + request.getHeader("sec-ch-ua-platform") + "\n" +
                Color.YELLOW + "sec-ch-ua-mobile: " + Color.CYAN + request.getHeader("sec-ch-ua-mobile") + "\n" +
                Color.YELLOW + "Host: " + Color.CYAN + request.getHeader("Host") + "\n" +
                Color.YELLOW + "Authorization: " + Color.CYAN + request.getHeader("Authorization") + "\n" +
                Color.YELLOW + "Method & RequestURI: "  + Color.GREEN + request.getMethod() + " " +  Color.CYAN + request.getRequestURI() + "\n" +
                Color.YELLOW + "==> RequestParameters: \n" + parameters.toString() +
                Color.YELLOW + "==> RequestPayload: \n" + Color.CYAN + requestBody + "\n" +

                Color.PURPLE + "****************************\n" +
                Color.PURPLE + "*** RESPONSE FROM SERVER ***\n" +
                Color.PURPLE + "****************************--->"+ Color.YELLOW + "TimeTaken: " + Color.GREEN + timeTaken +" ms \n" +

                Color.YELLOW + "ResponseCode: " + Color.GREEN + response.getStatus() + "\n" +
                Color.YELLOW + "HeadersNames: " + Color.GREEN + response.getHeaderNames() + "\n" +
                Color.YELLOW + "Mensaj del Servidor: " + Color.GREEN + response.getHeaders("Mensaje-del-servidor") + "\n" +
                Color.YELLOW + "Content-Type: " + Color.GREEN + response.getHeaders("Content-Type") + "\n" +
                Color.YELLOW + "ResponseBody: " + Color.GREEN + responseBody + "\n" + Color.RESET);
    }

}
