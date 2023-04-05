package com.emansy.employeeservice.business.service;

import com.emansy.employeeservice.model.ChangePasswordDto;
import com.emansy.employeeservice.model.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    List<UserDto> findAll();

    Optional<UserDto> findById(Long id);

    UserDto save(UserDto userDto);

    UserDto update(UserDto userDto);

    void deleteById(Long id);

    boolean existsById(Long id);

    void changePassword(ChangePasswordDto changePasswordDto);
}
