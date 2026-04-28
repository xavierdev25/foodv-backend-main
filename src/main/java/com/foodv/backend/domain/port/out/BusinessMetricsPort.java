package com.foodv.backend.domain.port.out;

public interface BusinessMetricsPort {

    void recordOrderCreated();

    void recordOrderCompleted();

    void recordOrderCancelled();

    void recordPaymentCompleted();

    void recordPaymentFailed();

    void recordUserRegistered();

    void recordStoreCreated();
}
