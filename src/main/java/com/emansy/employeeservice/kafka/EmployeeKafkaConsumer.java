package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class EmployeeKafkaConsumer {

    private final EmployeeService employeeService;

    private final EmployeeKafkaProducer employeeKafkaProducer;

    @KafkaListener(topics = "invited_employees_request", groupId = "employee_group")
    public void handleInvitedEmployeesRequest(List<Integer> integerEventIds) {
        List<Long> eventIds = integerEventIds.stream().map(Long::valueOf).collect(Collectors.toList());
        log.info("Request for employees invited to events with ids {} is received", eventIds);
        employeeKafkaProducer.sendInvitedEmployees(employeeService.findInvitedEmployees(eventIds));
    }

    @KafkaListener(topics = "uninvited_employees_request", groupId = "employee_group")
    public void handleUninvitedEmployeesRequest(List<Integer> integerEventIds) {
        List<Long> eventIds = integerEventIds.stream().map(Long::valueOf).collect(Collectors.toList());
        log.info("Request for employees not invited to events with ids {} is received", eventIds);
        employeeKafkaProducer.sendUninvitedEmployees(employeeService.findUninvitedEmployees(eventIds));
    }
}
