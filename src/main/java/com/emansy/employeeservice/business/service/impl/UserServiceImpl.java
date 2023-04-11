package com.emansy.employeeservice.business.service.impl;

import com.emansy.employeeservice.business.mappers.UserMapper;
import com.emansy.employeeservice.business.repository.UserRepository;
import com.emansy.employeeservice.business.repository.model.UserEntity;
import com.emansy.employeeservice.business.service.UserService;
import com.emansy.employeeservice.model.UserDto;
import com.emansy.employeeservice.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Override
    public List<UserDto> findAll() {
        List<UserEntity> userEntities = userRepository.findAll();
        log.info("Number of all users is {}", userEntities.size());
        return userEntities.stream().map(userMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        Optional<UserDto> userById = userRepository.findById(id)
                .flatMap(userEntity -> Optional.ofNullable(userMapper.entityToDto(userEntity)));
        log.info("User with id {} is {}", id, userById);
        return userById;
    }

    @Override
    public void create(UserDto userDto) {
        Long id = userDto.getId();
        String email = userDto.getEmail();
        if (userRepository.existsByIdOrEmail(id, email)) {
            log.error("Exception {} is thrown. User is not created: user with id {} or email {} already exists",
                    HttpStatus.CONFLICT, id, email);
            throw new HttpClientErrorException(HttpStatus.CONFLICT, "User with id " + id + " or email " + email + " already exists");
        }
        UserEntity userEntitySaved = userRepository.save(userMapper.dtoToEntity(userDto));
        log.info("New user created and saved: {}", userEntitySaved);
    }

    @Override
    public void delete(UserDto userDto) {
        Long id = userDto.getId();
        String email = userDto.getEmail();
        Optional<UserEntity> userEntityToDeleteOptional = userRepository.findByIdAndEmail(id, email);
        if (!userEntityToDeleteOptional.isPresent()) {
            log.error("Exception {} is thrown. User is not deleted: user with id {} and email {} is not found",
                    HttpStatus.NOT_FOUND, id, email);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User with id " + id + " and email " + email + " is not found");
        }
        UserEntity userEntityToDelete = userEntityToDeleteOptional.get();
        userRepository.delete(userEntityToDelete);
        log.info("User is deleted: {}", userEntityToDelete);
    }

    @Override
    public void changeRole(UserDto userDto) {
        Long id = userDto.getId();
        String email = userDto.getEmail();
        Optional<UserEntity> userEntityToUpdateOptional = userRepository.findByIdAndEmail(id, email);
        if (!userEntityToUpdateOptional.isPresent()) {
            log.error("Exception {} is thrown. User role is not changed: user with id {} and email {} is not found",
                    HttpStatus.NOT_FOUND, id, email);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User with id " + id + " and email " + email + " is not found");
        }
        UserEntity userEntityToUpdate = userEntityToUpdateOptional.get();
        userEntityToUpdate.setRole(userDto.getRole());
        userRepository.save(userEntityToUpdate);
        log.info("User role is changed: {}", userEntityToUpdate);
    }

    @Override
    public void changePassword(UserDto userDto) {
        Long id = userDto.getId();
        String email = userDto.getEmail();
        Optional<UserEntity> userEntityToUpdateOptional = userRepository.findByIdAndEmail(id, email);
        if (!userEntityToUpdateOptional.isPresent()) {
            log.error("Exception {} is thrown. Password is not changed: user with id {} and email {} is not found",
                    HttpStatus.NOT_FOUND, id, email);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "User with id " + id + " and email " + email + " is not found");
        }
        UserEntity userEntityToUpdate = userEntityToUpdateOptional.get();
        userEntityToUpdate.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(userEntityToUpdate);
        log.info("Password is changed: {}", userEntityToUpdate);
    }
}
