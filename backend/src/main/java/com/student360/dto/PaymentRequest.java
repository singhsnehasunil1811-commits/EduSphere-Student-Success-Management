package com.student360.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PaymentRequest(
    @Positive(message = "Amount must be positive")
    double amount,
    
    @NotNull(message = "Payment date is required")
    LocalDate paymentDate,
    
    String remarks
) {}
