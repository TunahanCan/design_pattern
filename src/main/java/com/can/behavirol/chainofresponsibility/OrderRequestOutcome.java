package com.can.behavirol.chainofresponsibility;

/**
 * Zincirin boolean dönüş değerine ek olarak isteğin neden durduğunu açıklar.
 */
public enum OrderRequestOutcome {
    PENDING,
    PROCESSED,
    REJECTED,
    DUPLICATE
}
