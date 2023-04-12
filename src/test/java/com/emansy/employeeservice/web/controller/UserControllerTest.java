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
import com.emansy.employeeservice.model.UserDto;
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
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
public class UserControllerTest {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String URL = "/api/v1/users";

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
    void findAllUsersTestPositive() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("ADMIN")));
        when(userRepository.findAll()).thenReturn(Arrays.asList(createUserEntity("USER"), createUserEntity("ADMIN")));
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + createToken()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("email@email.com"))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAllUsersTestNegativeForbidden() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).findAll();
    }

    @Test
    void findAllUsersTestNegativeUnauthorized() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(userRepository, times(0)).findAll();
    }

    @Test
    void findUserByIdTestPositive() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("ADMIN")));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(get(URL + "/1").header("Authorization", "Bearer " + createToken()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("email@email.com"))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void findUserByIdTestNegativeNotFound() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("ADMIN")));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get(URL + "/1").header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isNotFound());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void findUserByIdTestNegativeForbidden() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(get(URL + "/2").header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).findById(anyLong());
    }

    @Test
    void findUserByIdTestNegativeUnauthorized() throws Exception {
        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(userRepository, times(0)).findById(anyLong());
    }

    @Test
    void createUserTestPositive() throws Exception {
        UserDto userDto = createUserDto();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("ADMIN")));
        when(userRepository.existsByIdOrEmail(anyLong(), anyString())).thenReturn(false);
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(1)).existsByIdOrEmail(anyLong(), anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUserTestNegativeBadRequest() throws Exception {
        UserDto userDto = createUserDto();
        userDto.setEmail("bad email");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("ADMIN")));
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).existsByIdOrEmail(anyLong(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    void createUserTestNegativeForbidden() throws Exception {
        UserDto userDto = createUserDto();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).existsByIdOrEmail(anyLong(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    void createUserTestNegativeUnauthorized() throws Exception {
        UserDto userDto = createUserDto();
        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(userRepository, times(0)).existsByIdOrEmail(anyLong(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    void deleteUserTestPositive() throws Exception {
        UserDto userDto = createUserDto();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("ADMIN")));
        when(userRepository.findByIdAndEmail(anyLong(), anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(1)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(1)).delete(any(UserEntity.class));
    }

    @Test
    void deleteUserTestNegativeBadRequest() throws Exception {
        UserDto userDto = createUserDto();
        userDto.setEmail("bad email");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("ADMIN")));
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(0)).delete(any(UserEntity.class));
    }

    @Test
    void deleteUserTestNegativeForbidden() throws Exception {
        UserDto userDto = createUserDto();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(0)).delete(any(UserEntity.class));
    }

    @Test
    void deleteUserTestNegativeUnauthorized() throws Exception {
        UserDto userDto = createUserDto();
        mockMvc.perform(patch(URL)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(userRepository, times(0)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(0)).delete(any(UserEntity.class));
    }

    @Test
    void changeRoleTestPositive() throws Exception {
        UserDto userDto = createUserDto();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("ADMIN")));
        when(userRepository.findByIdAndEmail(anyLong(), anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(put(URL + "/role").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(1)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void changeRoleTestNegativeBadRequest() throws Exception {
        UserDto userDto = createUserDto();
        userDto.setEmail("bad email");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("ADMIN")));
        mockMvc.perform(put(URL + "/role").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    void changeRoleTestNegativeForbidden() throws Exception {
        UserDto userDto = createUserDto();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(put(URL + "/role").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    void changeRoleTestNegativeUnauthorized() throws Exception {
        UserDto userDto = createUserDto();
        mockMvc.perform(put(URL + "/role")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(userRepository, times(0)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    void changePasswordTestPositive() throws Exception {
        UserDto userDto = createUserDto();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        when(userRepository.findByIdAndEmail(anyLong(), anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(put(URL + "/password").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(1)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void changePasswordTestNegativeBadRequest() throws Exception {
        UserDto userDto = createUserDto();
        userDto.setEmail("bad email");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(put(URL + "/password").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    void changePasswordTestNegativeForbidden() throws Exception {
        UserDto userDto = createUserDto();
        userDto.setId(2L);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserEntity("USER")));
        mockMvc.perform(put(URL + "/password").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(userRepository, times(0)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    @Test
    void changePasswordTestNegativeUnauthorized() throws Exception {
        UserDto userDto = createUserDto();
        mockMvc.perform(put(URL + "/password")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(userRepository, times(0)).findByIdAndEmail(anyLong(), anyString());
        verify(userRepository, times(0)).save(any(UserEntity.class));
    }

    private String createToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/token")
                        .content(objectMapper.writeValueAsString(new LoginDto("email@email.com", "Password")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    private UserEntity createUserEntity(String role) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("email@email.com");
        userEntity.setPassword(passwordEncoder.encode("Password"));
        userEntity.setRole(role);
        return userEntity;
    }

    private UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("email@email.com");
        userDto.setPassword("Password");
        userDto.setRole("USER");
        return userDto;
    }
}
