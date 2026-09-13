package com.prash.razorPay.merchant.security;

import com.prash.razorPay.common.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    private static final String[] JWT_ROUTES= {"/v1/auth/**", "/v1/merchants/**", "/v1/admins/**", "/actuator/**"};
    private static final String[] API_KEYS_ROUTES = {"/v1/orders/**", "/v1/payments/**", "/v1/vault/**"};

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Bean
    public SecurityFilterChain jwtChain(HttpSecurity httpSecurity){

        return httpSecurity
                .securityMatcher(JWT_ROUTES)
                .csrf(csrf->csrf.disable())
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers("/v1/merchants/**").authenticated()
                        .requestMatchers("/v1/admins/**").authenticated()
                        .requestMatchers("/actuator/**").permitAll())
                .exceptionHandling(e->e.authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public SecurityFilterChain apiKeyFilter(HttpSecurity httpSecurity){

        return httpSecurity
                .securityMatcher(API_KEYS_ROUTES)
                .csrf(csrf->csrf.disable())
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->auth.anyRequest().authenticated())
                .exceptionHandling(e->e.authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }



    @Bean
    public AuthenticationManager authenticationManager(MerchantUserDetailsService userDetailsService,PasswordEncoder passwordEncoder){

        log.info("i have reached here");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);

    }


}
