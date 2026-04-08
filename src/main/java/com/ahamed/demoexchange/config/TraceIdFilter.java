package com.ahamed.demoexchange.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter implements Filter {
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 1. Generate or extract Trace ID
        String traceId = UUID.randomUUID().toString();

        try {
            // 2. Add to MDC so it appears in all log statements for this thread
            MDC.put("traceId", traceId);

            // 3. Add to Response Header
            if (servletResponse instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader(TRACE_ID_HEADER, traceId);
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 4. Clear MDC after request is done to prevent memory leaks
            MDC.remove("traceId");
        }
    }
}
