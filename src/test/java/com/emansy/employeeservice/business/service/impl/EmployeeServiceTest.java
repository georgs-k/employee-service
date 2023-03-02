package com.emansy.employeeservice.business.service.impl;

import com.emansy.employeeservice.business.mappers.EmployeeMapper;
import com.emansy.employeeservice.business.repository.EmployeeRepository;
import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import com.emansy.employeeservice.business.repository.model.JobTitleEntity;
import com.emansy.employeeservice.business.repository.model.OfficeEntity;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.JobTitleDto;
import com.emansy.employeeservice.model.OfficeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EmployeeServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EmployeeRepository repository;

    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeServiceImpl service;

    private EmployeeDto employeeDto;

    private EmployeeEntity employeeEntity;

    private List<EmployeeEntity> employeeEntities;

    @BeforeEach
    public void init() {
        employeeDto = createEmployeeDto(1L, "First name", "Last name", "email@email.com",
                "+37100000000", "09:00:00", "18:00:00");
        employeeEntity = createEmployeeEntity(1L, "First name", "Last name", "email@email.com",
                "+37100000000", "09:00:00", "18:00:00");
        employeeEntities = Arrays.asList(employeeEntity, employeeEntity);
    }

    @Test
    void findAllTestPositive() {
        when(repository.findAllByOrderByLastName()).thenReturn(employeeEntities);
        when(mapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        assertEquals(2, service.findAllEmployees().size());
        verify(repository, times(1)).findAllByOrderByLastName();
        verify(mapper, times(2)).entityToDto(employeeEntity);
    }

    @Test
    void findAllTestNegative() {
        when(repository.findAllByOrderByLastName()).thenReturn(Collections.emptyList());
        assertTrue(service.findAllEmployees().isEmpty());
        verify(repository, times(1)).findAllByOrderByLastName();
    }

    @Test
    void findByIdTestPositive() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(employeeEntity));
        when(mapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        assertEquals(employeeDto, service.findById(employeeDto.getId()).get());
        verify(repository, times(1)).findById(employeeDto.getId());
        verify(mapper, times(1)).entityToDto(employeeEntity);
    }

    @Test
    void findByIdTestNegative() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertFalse(service.findById(employeeDto.getId()).isPresent());
        verify(repository, times(1)).findById(employeeDto.getId());
    }

    @Test
    void saveTest() {
        when(mapper.dtoToEntity(employeeDto)).thenReturn(employeeEntity);
        when(repository.save(employeeEntity)).thenReturn(employeeEntity);
        when(mapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        assertEquals(employeeDto, service.save(employeeDto));
        verify(mapper, times(1)).dtoToEntity(employeeDto);
        verify(repository, times(1)).save(employeeEntity);
        verify(mapper, times(1)).entityToDto(employeeEntity);
    }

    @Test
    void updateTestPositive() {
        when(mapper.dtoToEntity(employeeDto)).thenReturn(employeeEntity);
        when(repository.save(employeeEntity)).thenReturn(employeeEntity);
        when(mapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        assertEquals(employeeDto, service.update(employeeDto));
        verify(mapper, times(1)).dtoToEntity(employeeDto);
        verify(repository, times(1)).save(employeeEntity);
        verify(mapper, times(1)).entityToDto(employeeEntity);
    }

    @Test
    void updateTestNegative() {
        EmployeeEntity employeeEntitySaved = createEmployeeEntity(2L, "First name", "Last name",
                "email@email.com", "+37100000000", "09:00:00", "18:00:00");
        EmployeeDto employeeDtoSaved = createEmployeeDto(2L, "First name", "Last name",
                "email@email.com", "+37100000000", "09:00:00", "18:00:00");
        when(mapper.dtoToEntity(employeeDto)).thenReturn(employeeEntity);
        when(repository.save(employeeEntity)).thenReturn(employeeEntitySaved);
        when(mapper.entityToDto(employeeEntitySaved)).thenReturn(employeeDtoSaved);
        assertNotEquals(employeeDto.getId(), service.update(employeeDto).getId());
        verify(mapper, times(1)).dtoToEntity(employeeDto);
        verify(repository, times(1)).save(employeeEntity);
        verify(mapper, times(1)).entityToDto(employeeEntitySaved);
    }

    @Test
    void deleteByIdTest() {
        service.deleteById(anyLong());
        verify(repository, times(1)).deleteById(anyLong());
    }

    @Test
    void existsByIdTestPositive() {
        when(repository.existsById(anyLong())).thenReturn(true);
        assertTrue(service.existsById(anyLong()));
        verify(repository, times(1)).existsById(anyLong());
    }

    @Test
    void existsByIdTestNegative() {
        when(repository.existsById(anyLong())).thenReturn(false);
        assertFalse(service.existsById(anyLong()));
        verify(repository, times(1)).existsById(anyLong());
    }

    private EmployeeDto createEmployeeDto(Long id, String firstName, String lastName, String email,
                                          String phone, String workingStartTime, String workingEndTime) {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(id);
        employeeDto.setFirstName(firstName);
        employeeDto.setLastName(lastName);
        employeeDto.setEmail(email);
        employeeDto.setPhone(phone);
        employeeDto.setJobTitleDto(new JobTitleDto());
        employeeDto.setOfficeDto(new OfficeDto());
        employeeDto.setWorkingStartTime(workingStartTime);
        employeeDto.setWorkingEndTime(workingEndTime);
        return employeeDto;
    }

    private EmployeeEntity createEmployeeEntity(Long id, String firstName, String lastName, String email,
                                                String phone, String workingStartTime, String workingEndTime) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setId(id);
        employeeEntity.setFirstName(firstName);
        employeeEntity.setLastName(lastName);
        employeeEntity.setEmail(email);
        employeeEntity.setPhone(phone);
        employeeEntity.setJobTitleEntity((new JobTitleEntity()));
        employeeEntity.setOfficeEntity(new OfficeEntity());
        employeeEntity.setWorkingStartTime(LocalTime.parse(workingStartTime));
        employeeEntity.setWorkingEndTime(LocalTime.parse(workingEndTime));
        return employeeEntity;
    }
}
