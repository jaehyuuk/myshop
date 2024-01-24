package com.myshop.global.filter;

import com.myshop.global.exception.UsernameFromTokenException;
import com.myshop.global.exception.handler.ErrorDetailResponse;
import com.myshop.global.exception.handler.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        } catch (UsernameFromTokenException ex){
            log.info("UsernameFromTokenException handler filter");
            setErrorResponse(HttpStatus.FORBIDDEN,response,ex);
        } catch (RuntimeException ex){
            log.info("RuntimeException exception handler filter");
            setErrorResponse(HttpStatus.FORBIDDEN,response,ex);
        } catch (Exception ex){
            log.info("Exception exception handler filter");
            setErrorResponse(HttpStatus.FORBIDDEN,response,ex);
        }

    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response,Throwable ex){
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(new ErrorDetailResponse(ex.getMessage()));
    }
}