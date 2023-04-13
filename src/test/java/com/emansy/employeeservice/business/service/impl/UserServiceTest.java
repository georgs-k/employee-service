package com.emansy.employeeservice.business.service.impl;

import com.emansy.employeeservice.business.mappers.UserMapper;
import com.emansy.employeeservice.business.repository.UserRepository;
import com.emansy.employeeservice.business.repository.model.UserEntity;
import com.emansy.employeeservice.model.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl service;

    private UserDto userDto;

    private UserEntity userEntity;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void init() {
        userDto = new UserDto(1L, "email@email.com", "********", "USER");
        userEntity = new UserEntity(1L, "email@email.com", passwordEncoder.encode("Password"), "USER");
    }

    @Test
    void loadUserByUsernameTestPositive() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        UserDetails userDetails = service.loadUserByUsername("email@email.com");
        assertEquals("email@email.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
        verify(repository, times(1)).findByEmail("email@email.com");
    }

    @Test
    void loadUserByUsernameTestNegative() {
        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("email@email.com"));
        verify(repository, times(1)).findByEmail("email@email.com");
    }

    @Test
    void findAllTestPositive() {
        when(repository.findAll()).thenReturn(Arrays.asList(userEntity, userEntity));
        when(mapper.entityToDto(userEntity)).thenReturn(userDto);
        List<UserDto> resultList = service.findAll();
        assertEquals(2, resultList.size());
        assertEquals(1L, resultList.get(0).getId());
        assertEquals("email@email.com", resultList.get(0).getEmail());
        assertEquals("********", resultList.get(0).getPassword());
        assertEquals("USER", resultList.get(0).getRole());
        verify(repository, times(1)).findAll();
        verify(mapper, times(2)).entityToDto(userEntity);
    }

    @Test
    void findAllTestNegative() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(service.findAll().isEmpty());
        verify(repository, times(1)).findAll();
        verify(mapper, times(0)).entityToDto(userEntity);
    }

    @Test
    void findByIdTestPositive() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(mapper.entityToDto(userEntity)).thenReturn(userDto);
        assertEquals(userDto, service.findById(userDto.getId()).get());
        verify(repository, times(1)).findById(userDto.getId());
        verify(mapper, times(1)).entityToDto(userEntity);
    }

    @Test
    void findByIdTestNegative() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertFalse(service.findById(userDto.getId()).isPresent());
        verify(repository, times(1)).findById(userDto.getId());
        verify(mapper, times(0)).entityToDto(userEntity);
    }

    @Test
    void createTestPositive() {
        when(repository.existsByIdOrEmail(anyLong(), anyString())).thenReturn(false);
        when(mapper.dtoToEntity(userDto)).thenReturn(userEntity);
        service.create(userDto);
        verify(repository, times(1)).existsByIdOrEmail(userDto.getId(), userDto.getEmail());
        verify(mapper, times(1)).dtoToEntity(userDto);
        verify(repository, times(1)).save(userEntity);
    }

    @Test
    void createTestNegative() {
        when(repository.existsByIdOrEmail(anyLong(), anyString())).thenReturn(true);
        assertThrows(HttpClientErrorException.class, () -> service.create(userDto));
        verify(repository, times(1)).existsByIdOrEmail(userDto.getId(), userDto.getEmail());
        verify(mapper, times(0)).dtoToEntity(userDto);
        verify(repository, times(0)).save(userEntity);
    }

    @Test
    void deleteTestPositive() {
        when(repository.findByIdAndEmail(anyLong(), anyString())).thenReturn(Optional.of(userEntity));
        service.delete(userDto);
        verify(repository, times(1)).findByIdAndEmail(userDto.getId(), userDto.getEmail());
        verify(repository, times(1)).delete(userEntity);
    }

    @Test
    void deleteTestNegative() {
        when(repository.findByIdAndEmail(anyLong(), anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.delete(userDto));
        verify(repository, times(1)).findByIdAndEmail(userDto.getId(), userDto.getEmail());
        verify(repository, times(0)).delete(userEntity);
    }

    @Test
    void changeRoleTestPositive() {
        when(repository.findByIdAndEmail(anyLong(), anyString())).thenReturn(Optional.of(userEntity));
        userDto.setRole("ADMIN");
        assertNotEquals("ADMIN", userEntity.getRole());
        service.changeRole(userDto);
        assertEquals("ADMIN", userEntity.getRole());
        verify(repository, times(1)).findByIdAndEmail(userDto.getId(), userDto.getEmail());
        verify(repository, times(1)).save(userEntity);
    }

    @Test
    void changeRoleTestNegative() {
        when(repository.findByIdAndEmail(anyLong(), anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.changeRole(userDto));
        verify(repository, times(1)).findByIdAndEmail(userDto.getId(), userDto.getEmail());
        verify(repository, times(0)).save(userEntity);
    }

    @Test
    void changePasswordTestPositive() {
        when(repository.findByIdAndEmail(anyLong(), anyString())).thenReturn(Optional.of(userEntity));
        userDto.setPassword("NewPassword");
        assertFalse(passwordEncoder.matches("NewPassword", userEntity.getPassword()));
        service.changePassword(userDto);
        assertTrue(passwordEncoder.matches("NewPassword", userEntity.getPassword()));
        verify(repository, times(1)).findByIdAndEmail(userDto.getId(), userDto.getEmail());
        verify(repository, times(1)).save(userEntity);
    }

    @Test
    void changePasswordTestNegative() {
        when(repository.findByIdAndEmail(anyLong(), anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.changePassword(userDto));
        verify(repository, times(1)).findByIdAndEmail(userDto.getId(), userDto.getEmail());
        verify(repository, times(0)).save(userEntity);
    }
}
