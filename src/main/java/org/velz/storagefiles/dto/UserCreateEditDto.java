package org.velz.storagefiles.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@AllArgsConstructor
@Builder
public class UserCreateEditDto {

    @Size(min = 5, max = 15)
    @Pattern(regexp = "^[a-zA-Z0-9]+[a-zA-Z_0-9]*[a-zA-Z0-9]+$")
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>\\[\\]/`~+=\\-_';]*$")
    @Size(min = 5, max = 15)
    private String password;


}
