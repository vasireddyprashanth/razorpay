//package com.prash.razorPay.payment.simulator;
//
//import com.prash.razorPay.common.enums.ChaosMode;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@ConfigurationProperties
//public record test(
//        Integer pollIntervalMs,
//        ChaosMode chaosMode,
//        Map<String,MethodSimulatorConfig>methods
//) {
//    public record MethodSimulatorConfig(
//            Integer minDelaySeconds,
//            Integer maxDelaySeconds,
//            Integer successRate
//    ) {
//    }
//}
//
//
//
