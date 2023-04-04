package com.emansy.employeeservice.business.service.impl;

import com.emansy.employeeservice.business.mappers.EmployeeMapper;
import com.emansy.employeeservice.business.repository.EmployeeRepository;
import com.emansy.employeeservice.business.repository.EventIdRepository;
import com.emansy.employeeservice.business.repository.model.CountryEntity;
import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import com.emansy.employeeservice.business.repository.model.EventIdEntity;
import com.emansy.employeeservice.business.repository.model.JobTitleEntity;
import com.emansy.employeeservice.business.repository.model.OfficeEntity;
import com.emansy.employeeservice.config.PublicHolidayRestTemplate;
import com.emansy.employeeservice.kafka.KafkaProducer;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import com.emansy.employeeservice.model.JobTitleDto;
import com.emansy.employeeservice.model.OfficeDto;
import com.emansy.employeeservice.model.PublicHolidayDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EventIdRepository eventIdRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private PublicHolidayRestTemplate publicHolidayRestTemplate;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeDto employeeDto;

    private EmployeeDto anotherEmployeeDto;

    private EmployeeEntity employeeEntity;

    private EmployeeEntity anotherEmployeeEntity;

    private Set<EmployeeEntity> employeeEntities;

    private EventIdEntity eventIdEntity;

    private EventIdEntity anotherEventIdEntity;

    private Set<EventIdEntity> eventIdEntities;

    private Set<Long> employeeIds;

    private EventDto eventDto;

    private EventDto anotherEventDto;

    private Set<EventDto> eventDtos;

    private final PublicHolidayDto[] publicHolidayDtos = {
            new PublicHolidayDto("2023-12-31"),
            new PublicHolidayDto("2024-01-01")
    };

    @BeforeEach
    public void init() {
        employeeDto = createEmployeeDto(1L, "First name", "Last name", "email@email.com",
                "+37100000000", "09:00:00", "17:00:00");
        anotherEmployeeDto = createEmployeeDto(2L, "First name", "Last name", "email@email.com",
                "+37100000000", "10:00:00", "18:00:00");
        employeeEntity = createEmployeeEntity(1L, "First name", "Last name", "email@email.com",
                "+37100000000", "09:00:00", "17:00:00");
        anotherEmployeeEntity = createEmployeeEntity(2L, "First name", "Last name", "email@email.com",
                "+37100000000", "10:00:00", "18:00:00");
        employeeEntities = createEmployeeEntities(employeeEntity, anotherEmployeeEntity);
        eventIdEntity = createEventIdEntity(1L, employeeEntities);
        anotherEventIdEntity = createEventIdEntity(2L, employeeEntities);
        eventIdEntities = createEventIdEntities(eventIdEntity, anotherEventIdEntity);
        employeeEntity.setEventIdEntities(eventIdEntities);
        anotherEmployeeEntity.setEventIdEntities(eventIdEntities);
        employeeIds = createEmployeeIds(1L, 2L);
        eventDto = createEventDto(
                1L, "Title", "Details", "2023-12-29", "12:00:00", "13:00:00");
        anotherEventDto = createEventDto(
                2L, "Title", "Details", "2023-12-29", "15:00:00", "16:00:00");
        eventDtos = createEventDtos(eventDto, anotherEventDto);
    }

    @Test
    void findAllTestPositive() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employeeEntity, anotherEmployeeEntity));
        when(employeeMapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        when(employeeMapper.entityToDto(anotherEmployeeEntity)).thenReturn(anotherEmployeeDto);
        List<EmployeeDto> resultList = employeeService.findAll();
        assertEquals(2, resultList.size());
        assertEquals(1L, resultList.get(0).getId());
        assertEquals(2L, resultList.get(1).getId());
        verify(employeeRepository, times(1)).findAll();
        verify(employeeMapper, times(1)).entityToDto(employeeEntity);
        verify(employeeMapper, times(1)).entityToDto(anotherEmployeeEntity);
    }

    @Test
    void findAllTestNegative() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(employeeService.findAll().isEmpty());
        verify(employeeRepository, times(1)).findAll();
        verify(employeeMapper, times(0)).entityToDto(employeeEntity);
        verify(employeeMapper, times(0)).entityToDto(anotherEmployeeEntity);
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
        verify(employeeMapper, times(0)).entityToDto(employeeEntity);
    }

    @Test
    void saveTest() {
        when(employeeMapper.dtoToEntity(employeeDto)).thenReturn(employeeEntity);
        when(employeeRepository.save(employeeEntity)).thenReturn(employeeEntity);
        when(employeeMapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        assertEquals(employeeDto, employeeService.save(employeeDto));
        verify(employeeMapper, times(1)).dtoToEntity(employeeDto);
        verify(employeeRepository, times(1)).save(employeeEntity);
        verify(entityManager, times(1)).refresh(any(EmployeeEntity.class));
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
        verify(entityManager, times(1)).refresh(any(EmployeeEntity.class));
        verify(employeeMapper, times(1)).entityToDto(employeeEntity);
    }

    @Test
    void updateTestNegative() {
        when(employeeMapper.dtoToEntity(employeeDto)).thenReturn(employeeEntity);
        when(employeeRepository.save(employeeEntity)).thenReturn(anotherEmployeeEntity);
        when(employeeMapper.entityToDto(anotherEmployeeEntity)).thenReturn(anotherEmployeeDto);
        assertNotEquals(employeeDto.getId(), employeeService.update(employeeDto).getId());
        verify(employeeMapper, times(1)).dtoToEntity(employeeDto);
        verify(employeeRepository, times(1)).save(employeeEntity);
        verify(entityManager, times(1)).refresh(any(EmployeeEntity.class));
        verify(employeeMapper, times(1)).entityToDto(anotherEmployeeEntity);
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

    @Test
    void findAttendingEmployeesTestPositive() {
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.of(eventIdEntity));
        when(employeeMapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        when(employeeMapper.entityToDto(anotherEmployeeEntity)).thenReturn(anotherEmployeeDto);
        assertEquals(2, employeeService.findAttendingEmployees(1L).size());
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(employeeMapper, times(1)).entityToDto(employeeEntity);
        verify(employeeMapper, times(1)).entityToDto(anotherEmployeeEntity);
    }

    @Test
    void findAttendingEmployeesTestNegative() {
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertTrue(employeeService.findAttendingEmployees(1L).isEmpty());
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(employeeMapper, times(0)).entityToDto(employeeEntity);
        verify(employeeMapper, times(0)).entityToDto(anotherEmployeeEntity);
    }

    @Test
    void findNonAttendingEmployeesTestPositive() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employeeEntity, anotherEmployeeEntity));
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(employeeMapper.entityToDto(employeeEntity)).thenReturn(employeeDto);
        when(employeeMapper.entityToDto(anotherEmployeeEntity)).thenReturn(anotherEmployeeDto);
        assertEquals(2, employeeService.findNonAttendingEmployees(1L).size());
        verify(employeeRepository, times(1)).findAll();
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(employeeMapper, times(1)).entityToDto(employeeEntity);
        verify(employeeMapper, times(1)).entityToDto(anotherEmployeeEntity);
    }

    @Test
    void findNonAttendingEmployeesTestNegative() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employeeEntity, anotherEmployeeEntity));
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.of(eventIdEntity));
        assertTrue(employeeService.findNonAttendingEmployees(1L).isEmpty());
        verify(employeeRepository, times(1)).findAll();
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(employeeMapper, times(0)).entityToDto(employeeEntity);
        verify(employeeMapper, times(0)).entityToDto(anotherEmployeeEntity);
    }

    @Test
    void findAttendedEventsBetweenTestPositive() throws ExecutionException, InterruptedException {
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        when(kafkaProducer.requestAndReceiveEvents(anySet(), anyString(), anyString())).thenReturn(eventDtos);
        assertEquals(2, employeeService.findAttendedEventsBetween(employeeIds, "2023-03-21", "").size());
        assertEquals(2, employeeService.findAttendedEventsBetween(employeeIds, "2023-03-21", "2030-01-01").size());
        verify(employeeRepository, times(2)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(2)).requestAndReceiveEvents(anySet(), anyString(), anyString());
    }

    @Test
    void findAttendedEventsBetweenTestNegative() throws ExecutionException, InterruptedException {
        employeeEntity.setEventIdEntities(Collections.emptySet());
        anotherEmployeeEntity.setEventIdEntities(Collections.emptySet());
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        assertTrue(employeeService.findAttendedEventsBetween(employeeIds, "2023-03-21", "").isEmpty());
        assertTrue(employeeService.findAttendedEventsBetween(employeeIds, "2023-03-21", "2030-01-01").isEmpty());
        verify(employeeRepository, times(2)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).requestAndReceiveEvents(anySet(), anyString(), anyString());
    }

    @Test
    void attendEventTestPositive() throws ExecutionException, InterruptedException {
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(eventIdRepository.save(any(EventIdEntity.class))).thenReturn(createEventIdEntity(1L, new HashSet<>()));
        when(kafkaProducer.requestAndReceiveEvents(anySet(), anyString(), anyString())).thenReturn(eventDtos);
        when(publicHolidayRestTemplate.getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class)).thenReturn(publicHolidayDtos);
        EventDto eventDtoRequested = createEventDto(
                3L, "Title", "Details", "2023-12-29", "13:00:00", "15:00:00");
        EventDto eventDtoScheduled = employeeService.attendEvent(employeeIds, eventDtoRequested);
        assertEquals(eventDtoRequested.getDate(), eventDtoScheduled.getDate());
        assertEquals(eventDtoRequested.getStartTime(), eventDtoScheduled.getStartTime());
        assertEquals(eventDtoRequested.getEndTime(), eventDtoScheduled.getEndTime());
        verify(employeeRepository, times(2)).findAllByIdIn(anySet());
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(eventIdRepository, times(1)).save(any(EventIdEntity.class));
        verify(kafkaProducer, times(1)).requestAndReceiveEvents(anySet(), anyString(), anyString());
        verify(publicHolidayRestTemplate, times(1)).getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class);
        verify(kafkaProducer, times(1)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void attendEventTestNegativeNoEmployeesFound() throws ExecutionException, InterruptedException {
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(Collections.emptySet());
        EventDto eventDtoRequested = createEventDto(
                3L, "Title", "Details", "2023-12-29", "13:00:00", "15:00:00");
        EventDto eventDtoScheduled = employeeService.attendEvent(employeeIds, eventDtoRequested);
        assertEquals(eventDtoRequested.getDate(), eventDtoScheduled.getDate());
        assertEquals(eventDtoRequested.getStartTime(), eventDtoScheduled.getStartTime());
        assertEquals(eventDtoRequested.getEndTime(), eventDtoScheduled.getEndTime());
        verify(employeeRepository, times(1)).findAllByIdIn(anySet());
        verify(eventIdRepository, times(0)).findById(anyLong());
        verify(eventIdRepository, times(0)).save(any(EventIdEntity.class));
        verify(kafkaProducer, times(0)).requestAndReceiveEvents(anySet(), anyString(), anyString());
        verify(publicHolidayRestTemplate, times(0)).getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class);
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void attendEventTestNegativeAllRequestedEmployeesAlreadyAttending() throws ExecutionException, InterruptedException {
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.of(eventIdEntity));
        EventDto eventDtoRequested = createEventDto(
                3L, "Title", "Details", "2023-12-29", "13:00:00", "15:00:00");
        EventDto eventDtoScheduled = employeeService.attendEvent(employeeIds, eventDtoRequested);
        assertEquals(eventDtoRequested.getDate(), eventDtoScheduled.getDate());
        assertEquals(eventDtoRequested.getStartTime(), eventDtoScheduled.getStartTime());
        assertEquals(eventDtoRequested.getEndTime(), eventDtoScheduled.getEndTime());
        verify(employeeRepository, times(1)).findAllByIdIn(anySet());
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(eventIdRepository, times(0)).save(any(EventIdEntity.class));
        verify(kafkaProducer, times(0)).requestAndReceiveEvents(anySet(), anyString(), anyString());
        verify(publicHolidayRestTemplate, times(0)).getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class);
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void attendEventTestShortenEvent() throws ExecutionException, InterruptedException {
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(eventIdRepository.save(any(EventIdEntity.class))).thenReturn(new EventIdEntity(1L, new HashSet<>()));
        when(kafkaProducer.requestAndReceiveEvents(anySet(), anyString(), anyString())).thenReturn(eventDtos);
        when(publicHolidayRestTemplate.getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class)).thenReturn(publicHolidayDtos);
        EventDto eventDtoRequested = createEventDto(
                3L, "Title", "Details", "2023-12-28", "10:00:00", "18:00:00");
        EventDto eventDtoScheduled = employeeService.attendEvent(employeeIds, eventDtoRequested);
        assertEquals(eventDtoRequested.getDate(), eventDtoScheduled.getDate());
        assertEquals(eventDtoRequested.getStartTime(), eventDtoScheduled.getStartTime());
        assertEquals("17:00:00", eventDtoScheduled.getEndTime());
        verify(employeeRepository, times(2)).findAllByIdIn(anySet());
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(eventIdRepository, times(1)).save(any(EventIdEntity.class));
        verify(kafkaProducer, times(1)).requestAndReceiveEvents(anySet(), anyString(), anyString());
        verify(publicHolidayRestTemplate, times(1)).getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class);
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void attendEventTestRescheduleToSameDayLater() throws ExecutionException, InterruptedException {
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(eventIdRepository.save(any(EventIdEntity.class))).thenReturn(new EventIdEntity(1L, new HashSet<>()));
        when(kafkaProducer.requestAndReceiveEvents(anySet(), anyString(), anyString())).thenReturn(eventDtos);
        when(publicHolidayRestTemplate.getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class)).thenReturn(publicHolidayDtos);
        EventDto eventDtoRequested = createEventDto(
                3L, "Title", "Details", "2023-12-29", "11:00:00", "13:00:00");
        EventDto eventDtoScheduled = employeeService.attendEvent(employeeIds, eventDtoRequested);
        assertEquals(eventDtoRequested.getDate(), eventDtoScheduled.getDate());
        assertEquals("13:00:00", eventDtoScheduled.getStartTime());
        assertEquals("15:00:00", eventDtoScheduled.getEndTime());
        verify(employeeRepository, times(2)).findAllByIdIn(anySet());
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(eventIdRepository, times(1)).save(any(EventIdEntity.class));
        verify(kafkaProducer, times(1)).requestAndReceiveEvents(anySet(), anyString(), anyString());
        verify(publicHolidayRestTemplate, times(1)).getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class);
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void attendEventTestRescheduleToSameDayLaterThenToNextMorning() throws ExecutionException, InterruptedException {
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(eventIdRepository.save(any(EventIdEntity.class))).thenReturn(new EventIdEntity(1L, new HashSet<>()));
        when(kafkaProducer.requestAndReceiveEvents(anySet(), anyString(), anyString())).thenReturn(eventDtos);
        when(publicHolidayRestTemplate.getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class)).thenReturn(publicHolidayDtos);
        EventDto eventDtoRequested = createEventDto(
                3L, "Title", "Details", "2023-12-29", "15:00:00", "17:00:00");
        EventDto eventDtoScheduled = employeeService.attendEvent(employeeIds, eventDtoRequested);
        assertEquals("2024-01-02", eventDtoScheduled.getDate());
        assertEquals("10:00:00", eventDtoScheduled.getStartTime());
        assertEquals("12:00:00", eventDtoScheduled.getEndTime());
        verify(employeeRepository, times(2)).findAllByIdIn(anySet());
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(eventIdRepository, times(1)).save(any(EventIdEntity.class));
        verify(kafkaProducer, times(1)).requestAndReceiveEvents(anySet(), anyString(), anyString());
        verify(publicHolidayRestTemplate, times(1)).getForObject(
                "https://date.nager.at/api/v3/NextPublicHolidays/LV", PublicHolidayDto[].class);
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void unattendEventTestPositive() {
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.of(eventIdEntity));
        when(employeeRepository.findAllByIdIn(anySet())).thenReturn(employeeEntities);
        assertEquals(eventDto, employeeService.unattendEvent(employeeIds, eventDto));
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(employeeRepository, times(1)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(1)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void unattendEventTestNegativeNoEventId() {
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertEquals(eventDto, employeeService.unattendEvent(employeeIds, eventDto));
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void unattendEventTestNegativeNoAttendingEmployees() {
        eventIdEntity.setEmployeeEntities(Collections.emptySet());
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.of(eventIdEntity));
        assertEquals(eventDto, employeeService.unattendEvent(employeeIds, eventDto));
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void unattendEventTestNegativeWrongAttendingEmployees() {
        employeeIds = createEmployeeIds(3L, 4L);
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.of(eventIdEntity));
        assertEquals(eventDto, employeeService.unattendEvent(employeeIds, eventDto));
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(employeeRepository, times(0)).findAllByIdIn(anySet());
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
    }

    @Test
    void unattendAndDeleteEventTestPositive() {
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.of(eventIdEntity));
        assertEquals(eventDto, employeeService.unattendAndDeleteEvent(eventDto));
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(kafkaProducer, times(1)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
        verify(eventIdRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void unattendAndDeleteEventTestNegativeNoEventId() {
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertEquals(eventDto, employeeService.unattendAndDeleteEvent(eventDto));
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
        verify(eventIdRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void unattendAndDeleteEventTestNegativeNoAttendingEmployees() {
        eventIdEntity.setEmployeeEntities(Collections.emptySet());
        when(eventIdRepository.findById(anyLong())).thenReturn(Optional.of(eventIdEntity));
        assertEquals(eventDto, employeeService.unattendAndDeleteEvent(eventDto));
        verify(eventIdRepository, times(1)).findById(anyLong());
        verify(eventIdRepository, times(1)).deleteById(anyLong());
        verify(kafkaProducer, times(0)).sendAttendanceNotification(anyBoolean(), anySet(), any(EventDto.class));
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
        OfficeEntity officeEntity = new OfficeEntity();
        CountryEntity countryEntity = new CountryEntity();
        employeeEntity.setId(id);
        employeeEntity.setFirstName(firstName);
        employeeEntity.setLastName(lastName);
        employeeEntity.setEmail(email);
        employeeEntity.setPhone(phone);
        employeeEntity.setJobTitleEntity(new JobTitleEntity());
        employeeEntity.setOfficeEntity(officeEntity);
        officeEntity.setCountryEntity(countryEntity);
        countryEntity.setCode("LV");
        employeeEntity.setWorkingStartTime(LocalTime.parse(workingStartTime));
        employeeEntity.setWorkingEndTime(LocalTime.parse(workingEndTime));
        return employeeEntity;
    }

    private Set<EmployeeEntity> createEmployeeEntities(EmployeeEntity employeeEntity, EmployeeEntity anotherEmployeeEntity) {
        Set<EmployeeEntity> employeeEntities = new HashSet<>();
        employeeEntities.add(employeeEntity);
        employeeEntities.add(anotherEmployeeEntity);
        return employeeEntities;
    }

    private EventIdEntity createEventIdEntity(Long id, Set<EmployeeEntity> employeeEntities) {
        EventIdEntity eventIdEntity = new EventIdEntity();
        eventIdEntity.setId(id);
        eventIdEntity.setEmployeeEntities(employeeEntities);
        return eventIdEntity;
    }

    private Set<EventIdEntity> createEventIdEntities(EventIdEntity eventIdEntity, EventIdEntity anotherEventIdEntity) {
        Set<EventIdEntity> eventIdEntities = new HashSet<>();
        eventIdEntities.add(eventIdEntity);
        eventIdEntities.add(anotherEventIdEntity);
        return eventIdEntities;
    }

    private Set<Long> createEmployeeIds(Long id1, Long id2) {
        Set<Long> employeeIds = new HashSet<>();
        employeeIds.add(id1);
        employeeIds.add(id2);
        return employeeIds;
    }

    private EventDto createEventDto(Long id, String title, String details, String date, String startTime, String endTime) {
        EventDto eventDto = new EventDto();
        eventDto.setId(id);
        eventDto.setTitle(title);
        eventDto.setDetails(details);
        eventDto.setDate(date);
        eventDto.setStartTime(startTime);
        eventDto.setEndTime(endTime);
        return eventDto;
    }

    private Set<EventDto> createEventDtos(EventDto eventDto, EventDto anotherEventDto) {
        Set<EventDto> eventDtos = new HashSet<>();
        eventDtos.add(eventDto);
        eventDtos.add(anotherEventDto);
        return eventDtos;
    }
}
