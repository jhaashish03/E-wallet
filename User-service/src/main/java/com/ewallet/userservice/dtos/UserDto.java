package com.ewallet.userservice.dtos;

import com.ewallet.userservice.entities.User;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */

@Builder
public record UserDto(
        @NotNull(message = "User-Name cannot be null.") @Email(message = "User-Name should be a valid email.") @NotEmpty(message = "User-Name cannot be empty.") @NotBlank(message = "User-Name cannot be blank.") String userName,
        @NotNull(message = "First Name cannot be null.") @NotEmpty(message = "First Name cannot be empty.") @NotBlank(message = "First Name cannot be blank.") String firstName,
        @NotNull(message = "Last Name cannot be null.") @NotEmpty @NotBlank String lastName,
        @NotNull(message = "Kyc Id cannot be null.") @Digits(integer = 64, fraction = 0) @NotEmpty(message = "Kyc Id cannot be empty.") @NotBlank(message = "Kyc Id cannot be blank.") String kycId) implements Serializable {
}