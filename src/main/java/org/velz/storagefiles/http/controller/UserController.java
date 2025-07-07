package org.velz.storagefiles.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.velz.storagefiles.dto.UserCreateEditDto;
import org.velz.storagefiles.dto.UserReadDto;
import org.velz.storagefiles.entity.User;
import org.velz.storagefiles.exception.UserNotAuthorizedException;
import org.velz.storagefiles.service.MinioStorageService;
import org.velz.storagefiles.service.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final MinioStorageService minioStorageService;

    @PostMapping("/auth/sign-up")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserReadDto register(@RequestBody @Validated UserCreateEditDto dto, /*BindingResult bindingResult,*/
                                 HttpSession session) {

//        if (bindingResult.hasErrors()) {
//            ResponseEntity.status(400);
//            throw new RuntimeException();
//        }
        UserReadDto userReadDto = userService.create(dto);
        User user = userService.loadUserByUsername(userReadDto.getUsername());

        authenticateUser(dto, session);

        minioStorageService.createUserRootDirectory(user.getId());

        return userReadDto;
    }

    @PostMapping("/auth/sign-in")
    public UserReadDto login(@RequestBody @Validated UserCreateEditDto dto,
                             HttpSession session) {

        authenticateUser(dto, session);

        return new UserReadDto(dto.getUsername());
    }

//    @GetMapping("/user/me")
////    public UserReadDto checkCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
////
////        return new UserReadDto(userDetails.getUsername());
////    }
    @GetMapping("/user/me")
    public UserReadDto checkCurrentUser(@AuthenticationPrincipal User user) {

        return new UserReadDto(user.getUsername());
    }

    @PostMapping("auth/sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        new SecurityContextLogoutHandler().logout(request, response, authentication);


    }


    public void authenticateUser(UserCreateEditDto dto, HttpSession session) {
        UserDetails userDetails = userService.loadUserByUsername(dto.getUsername());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, dto.getPassword());
        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    @GetMapping("/test")
    public String test() {
        throw new RuntimeException("asdasd");
//        return "rabotaet";
    }
}
