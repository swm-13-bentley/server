package com.schedch.mvp.config.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.webjars.NotFoundException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (InvalidParameterException e) { //token expired, no refresh -> need re log-in
            setErrorResponse(HttpStatus.BAD_REQUEST, response, e);
        } catch (NotFoundException e) { //user not found for this email
            setErrorResponse(HttpStatus.NOT_FOUND, response, e);
        } catch (TokenExpiredException e) {
            setErrorResponse(HttpStatus.FORBIDDEN, response, e);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex){
        response.setStatus(status.value());
        response.setContentType("application/json; charset=utf-8");
        try {
            response.getWriter().write(new Gson().toJson(ex.getMessage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
