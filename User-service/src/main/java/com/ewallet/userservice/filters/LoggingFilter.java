package com.ewallet.userservice.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;

@Slf4j
public class LoggingFilter extends HttpFilter {

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        MDC.put("X-Request-Id",request.getHeader("X-Request-Id"));
        log.info("Request registered with X-Request-Id: " + MDC.get("X-Request-Id"));
        chain.doFilter(request, response);
        log.info("Request fulfilled successfully");
        MDC.clear();
    }
}
