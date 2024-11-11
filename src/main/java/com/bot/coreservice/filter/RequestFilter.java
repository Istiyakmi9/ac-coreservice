package com.bot.coreservice.filter;

import com.bot.coreservice.config.RouteValidator;
import com.bot.coreservice.entity.User;
import com.bot.coreservice.model.JwtTokenModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bot.coreservice.model.CurrentSession;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
@Order(1)
public class RequestFilter implements Filter {
    @Autowired
    CurrentSession currentSession;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private RouteValidator routeValidator;
    private final static Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            HttpServletRequest request = ((HttpServletRequest)servletRequest);
            LOGGER.info("[CORE SERVICE REQUEST]: " + request.getRequestURI());

            if (request.getRequestURI().startsWith("/resources/")) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                Object headerUserDetail = request.getHeaders("userDetail").nextElement();
                if (headerUserDetail == null || headerUserDetail.toString().isEmpty()) {
                    throw new Exception("Invalid token found. Please contact to admin.");
                }

                var jwtTokenModel = objectMapper.readValue(headerUserDetail.toString(), JwtTokenModel.class);
                if (jwtTokenModel == null)
                    throw new Exception("Invalid token found. Please contact to admin.");

                //currentSession.setUser(objectMapper.readValue(jwtTokenModel.getUserDetail(), User.class));
            }


        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unauthorized access. Please try with valid token.");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
