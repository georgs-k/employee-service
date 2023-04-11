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
import com.emansy.employeeservice.business.repository.model.JobTitleEntity;
import com.emansy.employeeservice.business.repository.model.OfficeEntity;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
//
//    @Test
//    void findAllEmployeesTestNegative() throws Exception {
//        when(service.findAll()).thenReturn(Collections.emptyList());
//        mockMvc.perform(get(URL))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(0)))
//                .andExpect(status().isOk());
//        verify(service, times(1)).findAll();
//    }
//
//    @Test
//    void findEmployeeByIdTestPositive() throws Exception {
//        when(service.findById(anyLong())).thenReturn(Optional.of(createEmployeeDto()));
//        mockMvc.perform(get(URL + "/1"))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.firstName").value("First name"))
//                .andExpect(status().isOk());
//        verify(service, times(1)).findById(anyLong());
//    }
//
//    @Test
//    void findEmployeeByIdTestNegative() throws Exception {
//        when(service.findById(anyLong())).thenReturn(Optional.empty());
//        mockMvc.perform(get(URL + "/1"))
//                .andExpect(status().isNotFound());
//        verify(service, times(1)).findById(anyLong());
//    }
//
//    @Test
//    void saveEmployeeTestPositive() throws Exception {
//        EmployeeDto employeeDto = createEmployeeDto();
//        when(service.save(employeeDto)).thenReturn(employeeDto);
//        mockMvc.perform(post(URL)
//                        .content(objectMapper.writeValueAsString(employeeDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.firstName").value("First name"))
//                .andExpect(status().isCreated());
//        verify(service, times(1)).save(employeeDto);
//    }
//
//    @Test
//    void saveEmployeeTestNegative() throws Exception {
//        EmployeeDto employeeDto = createEmployeeDto();
//        employeeDto.setFirstName("");
//        mockMvc.perform(post(URL)
//                        .content(objectMapper.writeValueAsString(employeeDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//        verify(service, times(0)).save(employeeDto);
//    }
//
//    @Test
//    void updateEmployeeTestPositive() throws Exception {
//        EmployeeDto employeeDto = createEmployeeDto();
//        when(service.existsById(anyLong())).thenReturn(true);
//        when(service.update(employeeDto)).thenReturn(employeeDto);
//        mockMvc.perform(put(URL)
//                        .content(objectMapper.writeValueAsString(employeeDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.firstName").value("First name"))
//                .andExpect(status().isCreated());
//        verify(service, times(1)).existsById(anyLong());
//        verify(service, times(1)).update(employeeDto);
//    }
//
//    @Test
//    void updateEmployeeTestNegativeBadRequest() throws Exception {
//        EmployeeDto employeeDto = createEmployeeDto();
//        employeeDto.setFirstName("");
//        mockMvc.perform(put(URL)
//                        .content(objectMapper.writeValueAsString(employeeDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//        verify(service, times(0)).existsById(anyLong());
//        verify(service, times(0)).update(employeeDto);
//    }
//
//    @Test
//    void updateEmployeeTestNegativeNotFound() throws Exception {
//        EmployeeDto employeeDto = createEmployeeDto();
//        when(service.existsById(anyLong())).thenReturn(false);
//        mockMvc.perform(put(URL)
//                        .content(objectMapper.writeValueAsString(employeeDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//        verify(service, times(1)).existsById(anyLong());
//        verify(service, times(0)).update(employeeDto);
//    }
//
//    @Test
//    void deleteEmployeeByIdTestPositive() throws Exception {
//        when(service.existsById(anyLong())).thenReturn(true);
//        mockMvc.perform(delete(URL + "/1"))
//                .andExpect(status().isNoContent());
//        verify(service, times(1)).existsById(anyLong());
//        verify(service, times(1)).deleteById(anyLong());
//    }
//
//    @Test
//    void deleteEmployeeByIdTestNegative() throws Exception {
//        when(service.existsById(anyLong())).thenReturn(false);
//        mockMvc.perform(delete(URL + "/1"))
//                .andExpect(status().isNotFound());
//        verify(service, times(1)).existsById(anyLong());
//        verify(service, times(0)).deleteById(anyLong());
//    }
//
//    @Test
//    void findAttendedEventsBetweenTestPositive() throws Exception {
//        Set<EventDto> eventDtos = new HashSet<>();
//        eventDtos.add(createEventDto());
//        eventDtos.add(createAnotherEventDto());
//        when(service.existsById(anyLong())).thenReturn(true);
//        when(service.findAttendedEventsBetween(anySet(), anyString(), anyString())).thenReturn(eventDtos);
//        mockMvc.perform(get(URL + "/1/2023-01-01/2024-01-01"))
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].title").value("Title"))
//                .andExpect(status().isOk());
//        verify(service, times(1)).existsById(anyLong());
//        verify(service, times(1)).findAttendedEventsBetween(anySet(), anyString(), anyString());
//    }
//
//    @Test
//    void findAttendedEventsBetweenTestNegative() throws Exception {
//        when(service.existsById(anyLong())).thenReturn(false);
//        mockMvc.perform(get(URL + "/1/2023-01-01/2024-01-01"))
//                .andExpect(status().isNotFound());
//        verify(service, times(1)).existsById(anyLong());
//        verify(service, times(0)).findAttendedEventsBetween(anySet(), anyString(), anyString());
//    }
//
//    @Test
//    void unattendTestPositive() throws Exception {
//        EventDto eventDto = createEventDto();
//        when(service.existsById(anyLong())).thenReturn(true);
//        when(service.unattendEvent(anySet(), any())).thenReturn(eventDto);
//        mockMvc.perform(patch(URL + "/1")
//                        .content(objectMapper.writeValueAsString(eventDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//        verify(service, times(1)).existsById(anyLong());
//        verify(service, times(1)).unattendEvent(anySet(), any());
//    }
//
//    @Test
//    void unattendTestNegativeBadRequest() throws Exception {
//        EventDto eventDto = createEventDto();
//        eventDto.setTitle("");
//        mockMvc.perform(patch(URL + "/1")
//                        .content(objectMapper.writeValueAsString(eventDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//        verify(service, times(0)).existsById(anyLong());
//        verify(service, times(0)).unattendEvent(anySet(), any());
//    }
//
//    @Test
//    void unattendTestNegativeNotFound() throws Exception {
//        EventDto eventDto = createEventDto();
//        when(service.existsById(anyLong())).thenReturn(false);
//        mockMvc.perform(patch(URL + "/1")
//                        .content(objectMapper.writeValueAsString(eventDto))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//        verify(service, times(1)).existsById(anyLong());
//        verify(service, times(0)).unattendEvent(anySet(), any());
//    }

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
        employeeEntity.setEventIdEntities(new HashSet<>());
        return employeeEntity;
    }
//
//    private EventDto createEventDto() {
//        EventDto eventDto = new EventDto();
//        eventDto.setId(1L);
//        eventDto.setTitle("Title");
//        eventDto.setDetails("Details");
//        eventDto.setDate("2023-16-03");
//        eventDto.setStartTime("12:00:00");
//        eventDto.setEndTime("13:00:00");
//        return eventDto;
//    }
//
//    private EventDto createAnotherEventDto() {
//        EventDto eventDto = createEventDto();
//        eventDto.setId(2L);
//        return eventDto;
//    }
}
