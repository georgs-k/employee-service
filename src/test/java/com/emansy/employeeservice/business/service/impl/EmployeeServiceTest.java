package com.emansy.employeeservice.business.service.impl;

import com.emansy.employeeservice.business.mappers.EmployeeMapper;
import com.emansy.employeeservice.business.repository.EmployeeRepository;
import com.emansy.employeeservice.business.repository.EventIdRepository;
import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import com.emansy.employeeservice.business.repository.model.EventIdEntity;
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
    private EventIdRepository eventIdRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeDto employeeDto;

    private EmployeeEntity employeeEntity;

    private List<EmployeeEntity> employeeEntities;

    private EventIdEntity eventIdEntity;

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
        when(employeeRepository.findAll()).thenReturn(employeeEntities);
        when(employeeMapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        assertEquals(2, employeeService.findAll().size());
        verify(employeeRepository, times(1)).findAll();
        verify(employeeMapper, times(2)).entityToDto(employeeEntity);
    }

    @Test
    void findAllTestNegative() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(employeeService.findAll().isEmpty());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void findByIdTestPositive() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employeeEntity));
        when(employeeMapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        assertEquals(employeeDto, employeeService.findById(employeeDto.getId()).get());
        verify(employeeRepository, times(1)).findById(employeeDto.getId());
        verify(employeeMapper, times(1)).entityToDto(employeeEntity);
    }

    @Test
    void findByIdTestNegative() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertFalse(employeeService.findById(employeeDto.getId()).isPresent());
        verify(employeeRepository, times(1)).findById(employeeDto.getId());
    }

    @Test
    void saveTest() {
        when(employeeMapper.dtoToEntity(employeeDto)).thenReturn(employeeEntity);
        when(employeeRepository.save(employeeEntity)).thenReturn(employeeEntity);
        when(employeeMapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        assertEquals(employeeDto, employeeService.save(employeeDto));
        verify(employeeMapper, times(1)).dtoToEntity(employeeDto);
        verify(employeeRepository, times(1)).save(employeeEntity);
        verify(employeeMapper, times(1)).entityToDto(employeeEntity);
    }

    @Test
    void updateTestPositive() {
        when(employeeMapper.dtoToEntity(employeeDto)).thenReturn(employeeEntity);
        when(employeeRepository.save(employeeEntity)).thenReturn(employeeEntity);
        when(employeeMapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        assertEquals(employeeDto, employeeService.update(employeeDto));
        verify(employeeMapper, times(1)).dtoToEntity(employeeDto);
        verify(employeeRepository, times(1)).save(employeeEntity);
        verify(employeeMapper, times(1)).entityToDto(employeeEntity);
    }

    @Test
    void updateTestNegative() {
        EmployeeEntity employeeEntitySaved = createEmployeeEntity(2L, "First name", "Last name",
                "email@email.com", "+37100000000", "09:00:00", "18:00:00");
        EmployeeDto employeeDtoSaved = createEmployeeDto(2L, "First name", "Last name",
                "email@email.com", "+37100000000", "09:00:00", "18:00:00");
        when(employeeMapper.dtoToEntity(employeeDto)).thenReturn(employeeEntity);
        when(employeeRepository.save(employeeEntity)).thenReturn(employeeEntitySaved);
        when(employeeMapper.entityToDto(employeeEntitySaved)).thenReturn(employeeDtoSaved);
        assertNotEquals(employeeDto.getId(), employeeService.update(employeeDto).getId());
        verify(employeeMapper, times(1)).dtoToEntity(employeeDto);
        verify(employeeRepository, times(1)).save(employeeEntity);
        verify(employeeMapper, times(1)).entityToDto(employeeEntitySaved);
    }

    @Test
    void deleteByIdTest() {
        employeeService.deleteById(anyLong());
        verify(employeeRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void existsByIdTestPositive() {
        when(employeeRepository.existsById(anyLong())).thenReturn(true);
        assertTrue(employeeService.existsById(anyLong()));
        verify(employeeRepository, times(1)).existsById(anyLong());
    }

    @Test
    void existsByIdTestNegative() {
        when(employeeRepository.existsById(anyLong())).thenReturn(false);
        assertFalse(employeeService.existsById(anyLong()));
        verify(employeeRepository, times(1)).existsById(anyLong());
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
