package com.prash.razorPay.merchant.security;

import com.prash.razorPay.common.util.HashUtil;
import com.prash.razorPay.merchant.entity.ApiKey;
import com.prash.razorPay.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    public static final String BASIC_PREFIX = "BASIC ";
    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;
   // private final AuthenticatedMerchant authenticatedMerchant;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Basic ")){
            filterChain.doFilter(request,response);
            return ;
        }

        //we get the Authorization header : BASIC apiKey:23rwskndfnskdlsdfn:secret:sldkjf;lsjfekowejfio23 in this format
        // we get the encoded header from the server, it is important to send the encoded things over the internet
        String[] credentials = decode(header);

        if(credentials==null){
            throw  new BadRequestException("Malformed API Key");
        }

        String keyId = credentials[0];
        String rawSecretKey = credentials[1];

        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
                .orElseThrow(() -> new BadCredentialsException("Invalid or missing API Key"));

        if(!apiKey.isEnabled() || !secretMatches(rawSecretKey,apiKey)){
            throw new BadRequestException("Invalid or missing API KEy");
        }

        AuthenticatedMerchant authenticatedMerchant = AuthenticatedMerchant.builder().merchantId(apiKey.getMerchant().getId()).apiKey(apiKey.getKeyId()).build();

        var auth = new UsernamePasswordAuthenticationToken(authenticatedMerchant,null , List.of(new SimpleGrantedAuthority("API_KEY_ROLE")));

        SecurityContextHolder.getContext().setAuthentication(auth);
        AuthenticatedMerchant.builder().merchantId(apiKey.getMerchant().getId()).apiKey(apiKey.getKeyId()).build();

        filterChain.doFilter(request,response);



    }

    public String[] decode(String header){

        String encoded = header.substring(BASIC_PREFIX.length());
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

        int colon = decoded.indexOf(":");
        if(colon<1 ) return null;

        return new String[]{decoded.substring(0,colon), decoded.substring(colon+1)};


    }

    private boolean secretMatches(String rawSecret, ApiKey apiKey ){

        String incomingHash = HashUtil.toSha256Hex(rawSecret);

        if (MessageDigest.isEqual(
                incomingHash.getBytes(StandardCharsets.UTF_8),
                apiKey.getKeySecretHash().getBytes(StandardCharsets.UTF_8))) {
            return true;
        }
        boolean isInGracePeriod = apiKey.getGracePeriodExpiresAt()!=null &&
                LocalDateTime.now().isBefore(apiKey.getGracePeriodExpiresAt());

        return isInGracePeriod
                &&apiKey.getPreviousKeySecretHash() != null
                && passwordEncoder.matches(rawSecret,apiKey.getPreviousKeySecretHash());
    }
}
