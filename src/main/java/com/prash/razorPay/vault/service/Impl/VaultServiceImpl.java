package com.prash.razorPay.vault.service.Impl;

import com.prash.razorPay.common.entity.Money;
import com.prash.razorPay.common.enums.CardBrand;
import com.prash.razorPay.common.exception.ResourceNotFoundException;
import com.prash.razorPay.common.util.RandomizerUtil;
import com.prash.razorPay.payment.processor.PaymentProcessorRouter;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorRequest;
import com.prash.razorPay.payment.processor.dto.PaymentProcessorResponse;
import com.prash.razorPay.vault.config.VaultEncryptionConfig;
import com.prash.razorPay.vault.dto.request.TokenizeRequest;
import com.prash.razorPay.vault.dto.response.TokenizeResponse;
import com.prash.razorPay.vault.entity.CardToken;
import com.prash.razorPay.vault.entity.VaultCard;
import com.prash.razorPay.vault.repository.CardTokenRepository;
import com.prash.razorPay.vault.repository.VaultCardRepository;
import com.prash.razorPay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {

    private final CardTokenRepository cardTokenRepository;
    private final VaultCardRepository vaultCardRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    @Transactional
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {

        String lastFour=request.pan().substring(request.pan().length()-4);
        String bin=request.pan().substring(0,4);
        CardBrand cardBrand=detectBrand(request.pan());

        byte[] dek= KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncryptor(dek)
                .encrypt(request.pan().getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDek = dekEncryptor.encrypt(dek);

        VaultCard savedVaultCard = vaultCardRepository.save(VaultCard.builder()
                .lastFour(lastFour)
                .bin(bin).
                encryptedPan(encryptedPan)
                .encryptedDek(encryptedDek)
                .brand(cardBrand)
                .expiryMonth(request.expiryMonth().toString())
                .expiryYear(request.expiryYear().toString())
                .cardHolderName(request.cardHolderName())
                .build());

        String token = RandomizerUtil.randomBase64(32);

        CardToken cardToken = cardTokenRepository.save(CardToken.builder()
                .token(token)
                .vaultCard(savedVaultCard)
                .customer(request.customerId())
                .merchant(merchantId)
                .build());

        return new TokenizeResponse(token,lastFour,cardBrand, request.expiryMonth(), request.expiryYear());
    }

    @Override
    public PaymentProcessorResponse charge(String token, UUID paymentId, Money amount, Map<String, Object> methodDetails) {
        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("CardToken", token));

        byte[] panBytes=null;

        try {

            VaultCard vaultCard = cardToken.getVaultCard();
            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());

            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(paymentId, pan, expiry, amount, methodDetails);
            PaymentProcessorResponse response = paymentProcessorRouter.charge(paymentProcessorRequest);

            log.info("Vault charge registered, token={}****", token.substring(0,4));
            return response;
        }catch (Exception e){
            log.warn("Vault charge failed, token={}****", token.substring(0,6));
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED",e.getMessage());
        }finally {
            if(token!=null) Arrays.fill(panBytes,(byte)0);
        }
    }

    private CardBrand detectBrand(String pan) {
        if(pan.startsWith("4")) return CardBrand.VISA;
        if (pan.startsWith("5")) return CardBrand.MASTERCARD;
        return CardBrand.RUPAY;

    }
}
