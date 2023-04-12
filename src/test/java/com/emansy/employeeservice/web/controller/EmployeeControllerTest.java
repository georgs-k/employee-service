package com.emansy.employeeservice.web.controller;

import com.emansy.employeeservice.business.mappers.CountryMapperImpl;
import com.emansy.employeeservice.business.mappers.EmployeeMapperImpl;
import com.emansy.employeeservice.business.mappers.JobTitleMapperImpl;
import com.emansy.employeeservice.business.mappers.OfficeMapperImpl;
import com.emansy.employeeservice.business.mappers.UserMapperImpl;
import com.emansy.employeeservice.business.repository.EmployeeRepository;
import com.emansy.employeeservice.business.repository.EventIdRepository;
import com.emansy.employeeservice.business.repository.UserRepository;
import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import com.emansy.employeeservice.business.repository.model.EventIdEntity;
import com.emansy.employeeservice.business.repository.model.JobTitleEntity;
import com.emansy.employeeservice.business.repository.model.OfficeEntity;
import com.emansy.employeeservice.business.repository.model.UserEntity;
import com.emansy.employeeservice.business.service.TokenService;
import com.emansy.employeeservice.business.service.impl.EmployeeServiceImpl;
import com.emansy.employeeservice.business.service.impl.UserServiceImpl;
import com.emansy.employeeservice.config.PublicHolidayRestTemplate;
import com.emansy.employeeservice.config.SecurityConfig;
import com.emansy.employeeservice.kafka.KafkaProducer;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.JobTitleDto;
import com.emansy.employeeservice.model.LoginDto;
import com.emansy.employeeservice.model.OfficeDto;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({EmployeeController.class, AuthController.class})
@Import({SecurityConfig.class,
        TokenService.class,
        UserServiceImpl.class,
        UserMapperImpl.class,
        EmployeeServiceImpl.class,
        EmployeeMapperImpl.class,
        JobTitleMapperImpl.class,
        OfficeMapperImpl.class,
        CountryMapperImpl.class})
public class EmployeeControllerTest {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String URL = "/api/v1/employees";

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
    void findAllEmployeesTestPositive() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("ADMIN"));
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(createEmployeeEntity(), createEmployeeEntity()));
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + createToken()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("First name"))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void findAllEmployeesTestNegativeNoEmployees() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("ADMIN"));
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + createToken()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void findAllEmployeesTestNegativeForbidden() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).findAll();
    }

    @Test
    void findAllEmployeesTestNegativeUnauthorized() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(employeeRepository, times(0)).findAll();
    }

    @Test
    void findEmployeeByIdTestPositive() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(createEmployeeEntity()));
        mockMvc.perform(get(URL + "/1").header("Authorization", "Bearer " + createToken()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("First name"))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).findById(anyLong());
    }

    @Test
    void findEmployeeByIdTestNegativeNotFound() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(get(URL + "/1").header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isNotFound());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).findById(anyLong());
    }

    @Test
    void findEmployeeByIdTestNegativeForbidden() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        mockMvc.perform(get(URL + "/2").header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).findById(anyLong());
    }

    @Test
    void findEmployeeByIdTestNegativeUnauthorized() throws Exception {
        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(employeeRepository, times(0)).findById(anyLong());
    }

    @Test
    void saveEmployeeTestPositive() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("ADMIN"));
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(createEmployeeEntity());
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("First name"))
                .andExpect(status().isCreated());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).save(any(EmployeeEntity.class));
    }

    @Test
    void saveEmployeeTestNegativeBadRequest() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        employeeDto.setFirstName("");
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("ADMIN"));
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).save(any(EmployeeEntity.class));
    }

    @Test
    void saveEmployeeTestNegativeForbidden() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).save(any(EmployeeEntity.class));
    }

    @Test
    void saveEmployeeTestNegativeUnauthorized() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        mockMvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(employeeRepository, times(0)).save(any(EmployeeEntity.class));
    }

    @Test
    void updateEmployeeTestPositive() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("ADMIN"));
        when(employeeRepository.existsById(anyLong())).thenReturn(true);
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(createEmployeeEntity());
        mockMvc.perform(put(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("First name"))
                .andExpect(status().isCreated());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).existsById(anyLong());
        verify(employeeRepository, times(1)).save(any(EmployeeEntity.class));
    }

    @Test
    void updateEmployeeTestNegativeBadRequest() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        employeeDto.setFirstName("");
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("ADMIN"));
        mockMvc.perform(put(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).existsById(anyLong());
        verify(employeeRepository, times(0)).save(any(EmployeeEntity.class));
    }

    @Test
    void updateEmployeeTestNegativeNotFound() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("ADMIN"));
        when(employeeRepository.existsById(anyLong())).thenReturn(false);
        mockMvc.perform(put(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).existsById(anyLong());
        verify(employeeRepository, times(0)).save(any(EmployeeEntity.class));
    }

    @Test
    void updateEmployeeTestNegativeForbidden() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        mockMvc.perform(put(URL).header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).save(any(EmployeeEntity.class));
    }

    @Test
    void updateEmployeeTestNegativeUnauthorized() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        mockMvc.perform(put(URL)
                        .content(objectMapper.writeValueAsString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(employeeRepository, times(0)).save(any(EmployeeEntity.class));
    }

    @Test
    void deleteEmployeeByIdTestPositive() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("ADMIN"));
        when(employeeRepository.existsById(anyLong())).thenReturn(true);
        mockMvc.perform(delete(URL + "/1").header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isNoContent());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).existsById(anyLong());
        verify(employeeRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteEmployeeByIdTestNegativeNotFound() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("ADMIN"));
        when(employeeRepository.existsById(anyLong())).thenReturn(false);
        mockMvc.perform(delete(URL + "/1").header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isNotFound());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).existsById(anyLong());
        verify(employeeRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void deleteEmployeeByIdTestNegativeForbidden() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        mockMvc.perform(delete(URL + "/1").header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).existsById(anyLong());
        verify(employeeRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void deleteEmployeeByIdTestNegativeUnauthorized() throws Exception {
        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(employeeRepository, times(0)).existsById(anyLong());
        verify(employeeRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void findAttendedEventsBetweenTestPositive() throws Exception {
        Set<EventDto> eventDtos = new HashSet<>();
        eventDtos.add(createEventDto());
        eventDtos.add(createAnotherEventDto());
        Set<EmployeeEntity> employeeEntities = new HashSet<>();
        employeeEntities.add(createEmployeeEntity());
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        when(employeeRepository.existsById(anyLong())).thenReturn(true);
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        when(kafkaProducer.requestAndReceiveEvents(anySet(), anyString(), anyString())).thenReturn(eventDtos);
        mockMvc.perform(get(URL + "/1/2023-01-01/2024-01-01").header("Authorization", "Bearer " + createToken()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Title"))
                .andExpect(status().isOk());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).existsById(anyLong());
        verify(employeeRepository, times(1)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(1)).requestAndReceiveEvents(anySet(), anyString(), anyString());
    }

    @Test
    void findAttendedEventsBetweenTestNegativeNotFound() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        when(employeeRepository.existsById(anyLong())).thenReturn(false);
        mockMvc.perform(get(URL + "/1/2023-01-01/2024-01-01").header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isNotFound());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).existsById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).requestAndReceiveEvents(anySet(), anyString(), anyString());
    }

    @Test
    void findAttendedEventsBetweenTestNegativeForbidden() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        mockMvc.perform(get(URL + "/2/2023-01-01/2024-01-01").header("Authorization", "Bearer " + createToken()))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).existsById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).requestAndReceiveEvents(anySet(), anyString(), anyString());
    }

    @Test
    void findAttendedEventsBetweenTestNegativeUnauthorized() throws Exception {
        mockMvc.perform(get(URL + "/1/2023-01-01/2024-01-01"))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(employeeRepository, times(0)).existsById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).requestAndReceiveEvents(anySet(), anyString(), anyString());
    }

    @Test
    void unattendTestPositive() throws Exception {
        EventDto eventDto = createEventDto();
        Set<EmployeeEntity> employeeEntities = new HashSet<>();
        employeeEntities.add(createEmployeeEntity());
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        when(employeeRepository.existsById(anyLong())).thenReturn(true);
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.of(createEventIdEntity()));
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        mockMvc.perform(patch(URL + "/1").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(eventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).existsById(anyLong());
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(employeeRepository, times(1)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(1)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void unattendTestNegativeBadRequest() throws Exception {
        EventDto eventDto = createEventDto();
        eventDto.setTitle("");
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        mockMvc.perform(patch(URL + "/1").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(eventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).existsById(anyLong());
        verify(eventIdRepository, times(0)).findById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void unattendTestNegativeNotFound() throws Exception {
        EventDto eventDto = createEventDto();
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        when(employeeRepository.existsById(anyLong())).thenReturn(false);
        mockMvc.perform(patch(URL + "/1").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(eventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(1)).existsById(anyLong());
        verify(eventIdRepository, times(0)).findById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void unattendTestNegativeForbidden() throws Exception {
        EventDto eventDto = createEventDto();
        when(userRepository.findByEmail(anyString())).thenReturn(createUserEntity("USER"));
        mockMvc.perform(patch(URL + "/2").header("Authorization", "Bearer " + createToken())
                        .content(objectMapper.writeValueAsString(eventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(userRepository, times(2)).findByEmail(anyString());
        verify(employeeRepository, times(0)).existsById(anyLong());
        verify(eventIdRepository, times(0)).findById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void unattendTestNegativeUnauthorized() throws Exception {
        EventDto eventDto = createEventDto();
        mockMvc.perform(patch(URL + "/1")
                        .content(objectMapper.writeValueAsString(eventDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(employeeRepository, times(0)).existsById(anyLong());
        verify(eventIdRepository, times(0)).findById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
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

    private Optional<UserEntity> createUserEntity(String role) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("email@email.com");
        userEntity.setPassword(passwordEncoder.encode("Password"));
        userEntity.setRole(role);
        return Optional.of(userEntity);
    }

    private EmployeeEntity createEmployeeEntity() {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setId(1L);
        employeeEntity.setFirstName("First name");
        employeeEntity.setLastName("Last name");
        employeeEntity.setEmail("email@email.com");
        employeeEntity.setPhone("+37100000000");
        employeeEntity.setJobTitleEntity(new JobTitleEntity());
        employeeEntity.setOfficeEntity(new OfficeEntity());
        employeeEntity.setWorkingStartTime(LocalTime.parse("09:00:00"));
        employeeEntity.setWorkingEndTime(LocalTime.parse("18:00:00"));
        Set<EventIdEntity> eventIdEntities = new HashSet<>();
        eventIdEntities.add(new EventIdEntity(1L, Collections.emptySet()));
        eventIdEntities.add(new EventIdEntity(2L, Collections.emptySet()));
        employeeEntity.setEventIdEntities(eventIdEntities);
        return employeeEntity;
    }

    private EventIdEntity createEventIdEntity() {
        EventIdEntity eventIdEntity = new EventIdEntity();
        eventIdEntity.setId(1L);
        Set<EmployeeEntity> employeeEntities = new HashSet<>();
        employeeEntities.add(createEmployeeEntity());
        eventIdEntity.setEmployeeEntities(employeeEntities);
        return eventIdEntity;
    }

    private EmployeeDto createEmployeeDto() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(1L);
        employeeDto.setFirstName("First name");
        employeeDto.setLastName("Last name");
        employeeDto.setEmail("email@email.com");
        employeeDto.setPhone("+37100000000");
        employeeDto.setJobTitleDto(new JobTitleDto());
        employeeDto.setOfficeDto(new OfficeDto());
        employeeDto.setWorkingStartTime("09:00:00");
        employeeDto.setWorkingEndTime("18:00:00");
        employeeDto.setEventIds(new HashSet<>());
        return employeeDto;
    }

    private EventDto createEventDto() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Title");
        eventDto.setDetails("Details");
        eventDto.setDate("2023-16-03");
        eventDto.setStartTime("12:00:00");
        eventDto.setEndTime("13:00:00");
        return eventDto;
    }

    private EventDto createAnotherEventDto() {
        EventDto eventDto = createEventDto();
        eventDto.setId(2L);
        return eventDto;
    }
}
