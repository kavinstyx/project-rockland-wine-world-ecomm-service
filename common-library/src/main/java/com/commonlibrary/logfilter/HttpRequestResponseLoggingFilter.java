package com.commonlibrary.logfilter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class HttpRequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("msg-data-service");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //ensure request id is present or generate new one
        String requestId = request.getHeader("X-Request-Id");

        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString(); //generate unique id
        }


        //wrap the request in wrapper
        CustomHttpServletRequestWrapper wrappedRequest = new CustomHttpServletRequestWrapper(request);
        wrappedRequest.setHeader("X-Request-Id", requestId);

        //wrap the response in wrapper
        CustomHttpServletResponseWrapper wrappedResponse = new CustomHttpServletResponseWrapper(response);

        //log the request before processing
        logRequest(wrappedRequest);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            wrappedResponse.setHeader("X-Request-Id", requestId);   //
            logResponse(wrappedRequest, wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(CustomHttpServletRequestWrapper request) throws IOException {
        Map<String, Object> requestLog = new HashMap<>();
        requestLog.put("requestId", request.getHeader("X-Request-Id"));
        requestLog.put("method", request.getMethod());
        requestLog.put("url", request.getRequestURL().toString());

        // Query parameters
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestLog.put("query", queryString);
        }

        //log headers
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        requestLog.put("headers", headers);

        // Log body if content type is acceptable
        String body = request.getBody();
        if (body != null && !body.isEmpty()) {
            try {
                if (isValidJson(body)) {
                    requestLog.put("body", objectMapper.readValue(body, Map.class)); // Parse JSON and log as structured map
                } else {
                    requestLog.put("body", body); // Log as a plain string
                }
            } catch (JsonProcessingException e) {
                log.error("Error processing request body as JSON", e);
                requestLog.put("body", body); // Log as plain text if JSON processing fails
            }
        }


        log.info("Request: {}", toJsonString(requestLog));

    }

    private void logResponse(CustomHttpServletRequestWrapper request, CustomHttpServletResponseWrapper response)
    {
        Map<String, Object> responseLog = new HashMap<>();

        responseLog.put("requestId", request.getHeader("X-Request-Id"));
        responseLog.put("status", response.getStatus());

        //log response headers
        Map<String, String> responseHeaders = new HashMap<>();
        response.getHeaderNames().forEach(headerName -> {
            response.getHeaders(headerName).forEach(headerValue -> {
                responseHeaders.put(headerName, headerValue);
            });
        });
        responseLog.put("headers", responseHeaders);

        //log response body
        String responseBody = response.getCaptureAsString();
        if (responseBody != null && !responseBody.isEmpty()) {
            try {
                if (isValidJson(responseBody)) {
                    responseLog.put("body", objectMapper.readValue(responseBody, Map.class)); // Parse JSON and log as structured map
                } else {
                    responseLog.put("body", responseBody); // Log as a plain string
                }
            } catch (JsonProcessingException e) {
                log.error("Error processing response body as JSON", e);
                responseLog.put("body", responseBody); // Log as plain text if JSON processing fails
            }
        }

        log.info("Response: {}", toJsonString(responseLog));
    }

    private String toJsonString(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert map to JSON string", e);
            return "{}"; // Return empty JSON object on error
        }
    }

    private boolean isValidJson(String body) {
        try {
            objectMapper.readTree(body); // Check if it's valid JSON
            return true;
        } catch (IOException e) {
            return false; // It's not JSON
        }
    }
}
