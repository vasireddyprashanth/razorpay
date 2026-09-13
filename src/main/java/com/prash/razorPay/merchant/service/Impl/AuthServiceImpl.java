package com.prash.razorPay.merchant.service.Impl;

import com.prash.razorPay.common.enums.MerchantStatus;
import com.prash.razorPay.common.enums.UserRole;
import com.prash.razorPay.common.exception.DuplicateResourceException;
import com.prash.razorPay.common.exception.ResourceNotFoundException;
import com.prash.razorPay.merchant.dto.request.LoginRequest;
import com.prash.razorPay.merchant.dto.response.LoginResponse;
import com.prash.razorPay.merchant.dto.test.MerchantGetRequest;
import com.prash.razorPay.merchant.dto.request.MerchantSignupRequest;
import com.prash.razorPay.merchant.dto.response.MerchantResponse;
import com.prash.razorPay.merchant.entity.AppUser;
import com.prash.razorPay.merchant.entity.Merchant;
import com.prash.razorPay.merchant.mapper.MerchantMapper;
import com.prash.razorPay.merchant.repository.AppUserRepository;
import com.prash.razorPay.merchant.repository.MerchantRepository;
import com.prash.razorPay.merchant.security.util.JwtUtil;
import com.prash.razorPay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {


        if(merchantRepository.existsByEmail(request.email())){
            throw  new DuplicateResourceException("DUPLICATE EMAIL" ,"The merchant with email id  " + request.email() + "already exist" );
        }

        Merchant merchant=Merchant.builder()
                .name(request.name())
                .email(request.email())
                .businessType(request.businessType())
                .status(MerchantStatus.PENDING_KYC)
                .businessName(request.businessName())
                .build();

       merchant = merchantRepository.save(merchant);

        AppUser appUser=AppUser.builder()
                .email(request.email())
                .merchant(merchant)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);

        return merchantMapper.toGetMerchant(merchant);

    }

    @Override
    public MerchantResponse getMerchant(MerchantGetRequest request) {
        Merchant merchant = merchantRepository.findById(request.merchantId()).orElseThrow(() -> new ResourceNotFoundException("merchant", request.merchantId()));
        return merchantMapper.toGetMerchant(merchant);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(),request.password())
        );

        AppUser user = appUserRepository.findByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("USER", request.email()));

        String accessToken = jwtUtil.generateAccessToken(request.email(), user.getMerchant().getId(), user.getRole().toString());
        return new LoginResponse(accessToken);
    }
}
