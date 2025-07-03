package org.velz.storagefiles.mapper;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.velz.storagefiles.dto.UserCreateEditDto;
import org.velz.storagefiles.dto.UserReadDto;
import org.velz.storagefiles.entity.User;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;



    @Mapping(target = "password", expression = "java(passwordEncoder.encode(dto.getPassword()))")
    public abstract User mapToUser(UserCreateEditDto dto);

    public abstract UserReadDto mapToUserReadDto(User user);
}
