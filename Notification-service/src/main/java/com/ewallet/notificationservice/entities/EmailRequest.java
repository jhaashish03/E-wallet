package com.ewallet.notificationservice.entities;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    @NotNull
    private String to;
    @NotNull
    private String subject;
    @NotNull
    private String body;
}
