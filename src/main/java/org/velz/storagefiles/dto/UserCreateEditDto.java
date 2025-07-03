package org.velz.storagefiles.dto;

import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class UserCreateEditDto {

    @Size(min = 5, max = 15)
    String username;

    @Size(min = 5, max = 15)
    String password;


}
