package com.incentives.piggyback.invoice.util.constants;

import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public enum PaymentStatus {
    PENDING,
    COMPLETE,
    PROCESSING;

    public static ArrayList<String> getAllPaymentStatus() {
        PaymentStatus[] paymentStatus = PaymentStatus.values();
        ArrayList<String> stringRole = new ArrayList<>();
        for (PaymentStatus status : paymentStatus) {
            stringRole.add(status.toString());
        }
        return stringRole;
    }
}
