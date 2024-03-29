package com.ewallet.transactionservice.dtos;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link com.ewallet.transactionservice.entities.Transaction}
 */
@Builder
public record TransactionDto(@NotNull @Email @NotEmpty @NotBlank String sender,
                             @NotNull @Email @NotEmpty @NotBlank String receiver,
                             @Size(min = 0, max = 100) String message,
                             @NotNull @Min(1) @Positive Double amount) implements Serializable {
}