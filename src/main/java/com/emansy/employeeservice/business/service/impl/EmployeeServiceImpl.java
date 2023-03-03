package com.emansy.employeeservice.business.service.impl;

import com.emansy.employeeservice.business.mappers.EmployeeMapper;
import com.emansy.employeeservice.business.repository.EmployeeRepository;
import com.emansy.employeeservice.business.repository.EventIdRepository;
import com.emansy.employeeservice.business.repository.model.EmployeeEntity;
import com.emansy.employeeservice.business.repository.model.EventIdEntity;
import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.model.EmployeeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private final KafkaTemplate<String, List<Long>> kafkaTemplate;

    @Override
    public List<EmployeeDto> findAllEmployees() {
        List<EmployeeEntity> employeeEntities = employeeRepository.findAllByOrderByLastName();
        log.info("Number of all employees is {}", employeeEntities.size());
        return employeeEntities.stream().map(employeeMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<EmployeeDto> findById(Long id) {
        Optional<EmployeeDto> employeeById = employeeRepository.findById(id)
                .flatMap(employeeEntity -> Optional.ofNullable(employeeMapper.entityToDto(employeeEntity)));
        log.info("Employee with id {} is {}", id, employeeById);

        // temporary, for manually testing kafka

        List<Long> list = Arrays.asList(1L, 2L);
        Message<List<Long>> message = MessageBuilder
                .withPayload(list)
                .setHeader(KafkaHeaders.TOPIC, "invited_employees_request")
                .build();
        kafkaTemplate.send(message);

        // temporary, end

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
    public List<EmployeeDto> findAttendingEmployees(List<Long> eventIds) {
        Set<EmployeeEntity> employeeEntities = employeeRepository.findAllByEventIdEntitiesIdIn(eventIds);
        log.info("Found {} employees attending events with ids {}", employeeEntities.size(), eventIds);
        return employeeEntities.stream().map(employeeMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> findNonAttendingEmployees(List<Long> eventIds) {
        Set<EmployeeEntity> employeeEntities = new HashSet<>(employeeRepository.findAllByOrderByLastName());
        employeeEntities.removeAll(employeeRepository.findAllByEventIdEntitiesIdIn(eventIds));
        log.info("Found {} employees not attending events with ids {}", employeeEntities.size(), eventIds);
        return employeeEntities.stream().map(employeeMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public void unattend(List<Long> attendeeIds, Long eventId) {
        Optional<EventIdEntity> eventIdEntity = eventIdRepository.findById(eventId);
        if (!eventIdEntity.isPresent()) {
            log.warn("Event with id {} is not found", eventId);
            return;
        }
        Set<EmployeeEntity> employeesAttending = eventIdEntity.get().getEmployeeEntities();
        Set<EmployeeEntity> employeesUnattending;
        if (attendeeIds.isEmpty()) {
            eventIdRepository.deleteById(eventId);
            employeesUnattending = employeesAttending;
        } else {
            employeesUnattending = employeeRepository.findAllByIdIn(attendeeIds);
            employeesAttending.removeAll(employeesUnattending);
        }
        log.info("{} employees' attendance of the event with id {} is cancelled",
                employeesUnattending.size(), eventId);

//        TO DO: PRODUCE MESSAGE TO NOTIFICATION SERVICE

    }
}
