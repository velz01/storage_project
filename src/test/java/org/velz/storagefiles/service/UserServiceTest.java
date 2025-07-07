package org.velz.storagefiles.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.velz.storagefiles.IntegrationTestBase;
import org.velz.storagefiles.dto.UserCreateEditDto;
import org.velz.storagefiles.exception.UserAlreadyExistsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor
public class UserServiceTest extends IntegrationTestBase {
    public static final String USERNAME = "test";
    public static final String PASSWORD = "test";

    @Autowired
    private UserService userService;

    @Test
    void findUserAfterRegistrationTest() {
        UserCreateEditDto user = UserCreateEditDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        userService.create(user);

        assertThat(userService.loadUserByUsername(USERNAME).getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void createUserWithNonUniqueUsernameThrowsExceptionTest() {
        UserCreateEditDto user = UserCreateEditDto.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
        userService.create(user);
        assertThrows(UserAlreadyExistsException.class, () -> userService.create(user));
    }
}
