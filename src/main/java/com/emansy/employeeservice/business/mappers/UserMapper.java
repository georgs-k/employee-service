package com.emansy.employeeservice.business.mappers;

import com.emansy.employeeservice.business.repository.model.UserEntity;
import com.emansy.employeeservice.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", qualifiedByName = "encode")
    UserEntity dtoToEntity(UserDto userDto);

    @Mapping(target = "password", qualifiedByName = "hide")
    UserDto entityToDto(UserEntity userEntity);

    @Named("encode")
    default String encode(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @Named("hide")
    default String hide(String ignoredPassword) {
        return "********";
    }
}
