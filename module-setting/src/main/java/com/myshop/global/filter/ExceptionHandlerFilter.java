package com.myshop.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.global.exception.UsernameFromTokenException;
import com.myshop.global.exception.handler.ErrorDetailResponse;
import com.myshop.global.exception.handler.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        } catch (UsernameFromTokenException ex) {
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

    private void setErrorResponse(HttpStatus status, HttpServletResponse response,Throwable ex){
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorDetailResponse detailResponse = ErrorDetailResponse.builder().msg(ex.getMessage()).build();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(Arrays.asList(detailResponse))
                .build();
        try{
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}