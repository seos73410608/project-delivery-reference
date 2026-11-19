
package com.seos.pmis.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SessionSelectionRequest {

    @NotBlank
    private String loginAttemptId;

    @NotEmpty
    private List<@NotNull UUID> sessionIds;
}
