package com.emansy.employeeservice.web.controller;

import com.emansy.employeeservice.business.mappers.CountryMapperImpl;
import com.emansy.employeeservice.business.mappers.EmployeeMapperImpl;
import com.emansy.employeeservice.business.mappers.JobTitleMapperImpl;
import com.emansy.employeeservice.business.mappers.OfficeMapperImpl;
import com.emansy.employeeservice.business.mappers.UserMapperImpl;
import com.emansy.employeeservice.business.repository.EmployeeRepository;
import com.emansy.employeeservice.business.repository.EventIdRepository;
import com.emansy.employeeservice.business.repository.UserRepository;
import com.emansy.employeeservice.business.repository.model.UserEntity;
import com.emansy.employeeservice.business.service.TokenService;
import com.emansy.employeeservice.business.service.impl.EmployeeServiceImpl;
import com.emansy.employeeservice.business.service.impl.UserServiceImpl;
import com.emansy.employeeservice.config.PublicHolidayRestTemplate;
import com.emansy.employeeservice.config.SecurityConfig;
import com.emansy.employeeservice.kafka.KafkaProducer;
import com.emansy.employeeservice.model.LoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({SecurityConfig.class,
        TokenService.class,
        UserServiceImpl.class,
        UserMapperImpl.class,
        EmployeeServiceImpl.class,
        EmployeeMapperImpl.class,
        JobTitleMapperImpl.class,
        OfficeMapperImpl.class,
        CountryMapperImpl.class})
public class AuthControllerTest {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String URL = "/api/v1/token";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EventIdRepository eventIdRepository;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private KafkaProducer kafkaProducer;

    @MockBean
    private PublicHolidayRestTemplate publicHolidayRestTemplate;

    @Test
    void getTokenTestPositive() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity());
        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(new LoginDto("email@email.com", "Password")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
    }

    @Test
    void getTokenTestNegativeBadRequest() throws Exception {
        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(new LoginDto("bad email", "Password")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, times(0)).findByEmail(anyString());
    }

    @Test
    void getTokenTestNegativeBadCredentials() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(new LoginDto("email@email.com", "Password")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    private Optional<UserEntity> createUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("email@email.com");
        userEntity.setPassword(passwordEncoder.encode("Password"));
        userEntity.setRole("ADMIN");
        return Optional.of(userEntity);
    }
}
