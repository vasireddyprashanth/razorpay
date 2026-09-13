package com.prash.razorPay.merchant.security;

import com.prash.razorPay.merchant.security.util.AuthenticatedMerch;
import com.prash.razorPay.merchant.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AuthenticatedMerch authenticatedMerch;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Incoming Request is: {}", request.getRequestURI());

        try {
            String authorizationHeader = request.getHeader("Authorization");

            if(authorizationHeader==null || !authorizationHeader.startsWith("Bearer ")){
                log.info("The auth header is : {}", authorizationHeader);
                filterChain.doFilter(request,response);
                return ;
            }

            String jwtToken = authorizationHeader.substring(7);

            jwtUtil.verifyAccessToken(jwtToken);
            Claims claims = jwtUtil.extractAllClaims(jwtToken);
            log.info("the claims are: {}", claims);

            //GrantedAuthority simpleGrantedAuth=  new SimpleGrantedAuthority(jwtUtil.extractRole(claims));
            String role = jwtUtil.extractRole(claims);
            AuthenticatedMerchant authenticatedMerchant = AuthenticatedMerchant.builder().merchantId(jwtUtil.getMerchantId(claims)).email(claims.getSubject()).build();
            log.info("I came till here");
            if(claims!=null && SecurityContextHolder.getContext().getAuthentication() == null){

                var auth = new UsernamePasswordAuthenticationToken(authenticatedMerchant, null,
                        List.of(new SimpleGrantedAuthority(role))
                );



                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request,response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request,response,null,e);

        }



    }
}
