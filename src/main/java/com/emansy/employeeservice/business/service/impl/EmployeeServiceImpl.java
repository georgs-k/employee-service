package com.emansy.employeeservice.business.service.impl;

import com.emansy.employeeservice.business.mappers.EmployeeMapper;
import com.emansy.employeeservice.business.repository.EmployeeRepository;
import com.emansy.employeeservice.business.repository.EventIdRepository;
import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import com.emansy.employeeservice.business.repository.model.EventIdEntity;
import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.kafka.KafkaProducer;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EntityManager entityManager;

    private final EmployeeMapper employeeMapper;

    private final EmployeeRepository employeeRepository;

    private final EventIdRepository eventIdRepository;

    private final KafkaProducer kafkaProducer;

    @Override
    public List<EmployeeDto> findAll() {
        List<EmployeeEntity> employeeEntities = employeeRepository.findAll();
        log.info("Number of all employees is {}", employeeEntities.size());
        return employeeEntities.stream().map(employeeMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<EmployeeDto> findById(Long id) {
        Optional<EmployeeDto> employeeById = employeeRepository.findById(id)
                .flatMap(employeeEntity -> Optional.ofNullable(employeeMapper.entityToDto(employeeEntity)));
        log.info("Employee with id {} is {}", id, employeeById);
        return employeeById;
    }

    @Override
    public EmployeeDto save(EmployeeDto employeeDto) {
        employeeDto.setId(null);
        EmployeeEntity employeeEntitySaved = employeeRepository.save(employeeMapper.dtoToEntity(employeeDto));
        entityManager.refresh(employeeEntitySaved);
        log.info("New employee is saved: {}", employeeEntitySaved);
        return employeeMapper.entityToDto(employeeEntitySaved);
    }

    @Override
    public EmployeeDto update(EmployeeDto employeeDto) {
        EmployeeEntity employeeEntityUpdated = employeeRepository.save(employeeMapper.dtoToEntity(employeeDto));
        entityManager.refresh(employeeEntityUpdated);
        log.info("Employee is updated: {}", employeeEntityUpdated);
        return employeeMapper.entityToDto(employeeEntityUpdated);
    }

    @Override
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
        log.info("Employee with id {} is deleted", id);
    }

    @Override
    public boolean existsById(Long id) {
        return employeeRepository.existsById(id);
    }

    @Override
    public Set<EmployeeDto> findAttendingEmployees(Long eventId) {
        Set<EmployeeEntity> employeeEntities = new HashSet<>();
        Optional<EventIdEntity> eventIdEntity = eventIdRepository.findById(eventId);
        if (eventIdEntity.isPresent()) employeeEntities = eventIdEntity.get().getEmployeeEntities();
        log.info("Found {} employees attending event with id {}", employeeEntities.size(), eventId);
        return employeeEntities.stream().map(employeeMapper::entityToDto).collect(Collectors.toSet());
    }

    @Override
    public Set<EmployeeDto> findNonAttendingEmployees(Long eventId) {
        Set<EmployeeEntity> employeeEntities = new HashSet<>(employeeRepository.findAll());
        Optional<EventIdEntity> eventIdEntity = eventIdRepository.findById(eventId);
        eventIdEntity.ifPresent(idEntity -> employeeEntities.removeAll(idEntity.getEmployeeEntities()));
        log.info("Found {} employees not attending event with id {}", employeeEntities.size(), eventId);
        return employeeEntities.stream().map(employeeMapper::entityToDto).collect(Collectors.toSet());
    }

    @Override
    public Set<EventDto> findAttendedEventsBetween(Set<Long> employeeIds, String fromDate, String thruDate)
            throws ExecutionException, InterruptedException {
        Set<Long> eventIds = new HashSet<>();
        employeeRepository.findAllByIdIn(employeeIds)
                .forEach(employeeEntity -> eventIds
                        .addAll(employeeEntity.getEventIdEntities()
                                .stream().map(EventIdEntity::getId).collect(Collectors.toSet())));
        if (eventIds.isEmpty()) {
            log.info("No attended events found for the employees with ids {}", employeeIds);
            return Collections.emptySet();
        }
        Set<EventDto> eventDtos = kafkaProducer.requestAndReceiveEvents(eventIds, fromDate, thruDate);
        log.info("Found {} events, scheduled between {} and {}, for the employees with ids {}",
                eventDtos.size(), fromDate, thruDate, employeeIds);
        return eventDtos;
    }

    @Override
    public EventDto attendEvent(Set<Long> employeeIds, EventDto eventDto) throws ExecutionException, InterruptedException {
        Set<EventDto> attendedEventDtos = findAttendedEventsBetween(employeeIds, String.valueOf(LocalDate.now()), "");
        return eventDto;
    }

    @Override
    public EventDto unattendEvent(Set<Long> employeeIds, EventDto eventDto) {
        Optional<EventIdEntity> eventIdEntity = eventIdRepository.findById(eventDto.getId());
        if (!eventIdEntity.isPresent()) {
            log.warn("Employees' attendance of the event with id {} is not found", eventDto.getId());
            return eventDto;
        }
        Set<EmployeeEntity> attendingEmployeeEntities = eventIdEntity.get().getEmployeeEntities();
        if (attendingEmployeeEntities.isEmpty()) {
            log.warn("Employees' attendance of the event with id {} is not found", eventDto.getId());
            return eventDto;
        }
        Set<Long> attendingEmployeeIds = attendingEmployeeEntities.stream().map(EmployeeEntity::getId).collect(Collectors.toSet());
        employeeIds.retainAll(attendingEmployeeIds);
        if (employeeIds.isEmpty()) {
            log.warn("Requested employees' attendance of the event with id {} is not found", eventDto.getId());
            return eventDto;
        }
        Set<EmployeeEntity> employeeEntities = employeeRepository.findAllByIdIn(employeeIds);
        kafkaProducer.sendAttendanceNotification(
                false,
                employeeEntities.stream().map(employeeMapper::entityToDto).collect(Collectors.toSet()),
                eventDto);
        log.info("{} employees' attendance of the event with id {} is cancelled",
                employeeEntities.size(), eventDto.getId());
        attendingEmployeeEntities.removeAll(employeeEntities);
        return eventDto;
    }

    @Override
    public EventDto unattendAndDeleteEvent(EventDto eventDto) {
        Optional<EventIdEntity> eventIdEntity = eventIdRepository.findById(eventDto.getId());
        if (!eventIdEntity.isPresent()) {
            log.warn("Employees' attendance of the event with id {} is not found", eventDto.getId());
            return eventDto;
        }
        Set<EmployeeEntity> employeeEntities = eventIdEntity.get().getEmployeeEntities();
        if (employeeEntities.isEmpty()) {
            log.warn("Employees' attendance of the event with id {} is not found", eventDto.getId());
            eventIdRepository.deleteById(eventDto.getId());
            return eventDto;
        }
        kafkaProducer.sendAttendanceNotification(
                false,
                employeeEntities.stream().map(employeeMapper::entityToDto).collect(Collectors.toSet()),
                eventDto);
        log.info("{} employees' attendance of the event with id {} is cancelled",
                employeeEntities.size(), eventDto.getId());
        eventIdRepository.deleteById(eventDto.getId());
        return eventDto;
    }
}
