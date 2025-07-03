package org.velz.storagefiles.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.velz.storagefiles.dto.UserCreateEditDto;
import org.velz.storagefiles.dto.UserReadDto;
import org.velz.storagefiles.entity.User;
import org.velz.storagefiles.exception.UserAlreadyExistsException;
import org.velz.storagefiles.mapper.UserMapper;
import org.velz.storagefiles.repository.UserRepository;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional
    public UserReadDto create(UserCreateEditDto userCreateEditDto) {
        if (userRepository.existsByUsername(userCreateEditDto.getUsername())) {
            throw new UserAlreadyExistsException("login " + userCreateEditDto.getUsername() + " already exists");
        }
        User user = userMapper.mapToUser(userCreateEditDto);
        return userMapper.mapToUserReadDto(userRepository.save(user));


    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("failed to retrieve user: " + username));

    }
}
