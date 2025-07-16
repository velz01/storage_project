package org.velz.storagefiles.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.velz.storagefiles.entity.Role;

@Getter
@AllArgsConstructor
public class UserReadDto {
    private String username;

}
