package com.ewallet.walletservice.dtos;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link com.ewallet.walletservice.entities.WalletCredit}
 */
@Builder
public record WalletCreditDto(@NotNull @Email @NotEmpty @NotBlank String userName,
                              @NotNull @Min(1)  @Positive Double amount, String contactNumber,
                              String name) implements Serializable {
}