package com.prash.razorPay.payment.simulator;

import com.prash.razorPay.common.enums.ChaosMode;
import com.prash.razorPay.common.enums.PaymentStatus;
import com.prash.razorPay.common.util.RandomizerUtil;
import com.prash.razorPay.payment.entity.Payment;
import com.prash.razorPay.payment.repository.PaymentRepository;
import com.prash.razorPay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankCallbackSimulator {

    private final PaymentRepository paymentRepository;
    private final SimulatorConfig simulatorConfig;
    private final PaymentService paymentService;

    //@Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallbacks() {

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);
        List<Payment> candidates =
                paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow);

        for(Payment payment: candidates){
            simulateCallback(payment);
        }

    }

    private void simulateCallback(Payment payment) {
        SimulatorConfig.MethodSimulatorConfig methodConfig = simulatorConfig.configFor(payment.getMethod());

        LocalDateTime dueAt = dueAt(payment, methodConfig);

        if(LocalDateTime.now().isBefore(dueAt)){
            return;
        }

        ChaosMode chaosMode= simulatorConfig.getChaosMode() ;

        switch (chaosMode){
            case SUCCESS -> resolve(payment,true);
            case FAILURE -> resolve(payment, false);
            case TIMEOUT -> {
                log.debug("BankCallBack simulator: Payment TimedOut");
            }
            case NORMAL -> resolve(payment,shouldApprove(payment,methodConfig));
        }

    }

    private void resolve(Payment payment, boolean approve){
        if(approve) {
            String bankRef = "SIM_BANK_REF" + RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(), true, bankRef, null,null);

        }else {
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated Bank DECLINED");
        }
    }

    private boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodSimulatorConfig.getSuccessRate();
    }

    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig){

        int range = methodConfig.getMaxDelaySeconds() - methodConfig.getMinDelaySeconds();
        int delaySeconds = methodConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range+1);

        if(simulatorConfig.getChaosMode() == ChaosMode.SLOW){
            delaySeconds *=2;
        }

        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }
}
