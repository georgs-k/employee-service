package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class EmployeesKafkaConsumer {

    private final EmployeeService employeeService;

    private final EmployeesKafkaProducer employeesKafkaProducer;

    @KafkaListener(topics = "invited_employees_request", groupId = "employee_group")
    public void handleInvitedEmployeesRequest(List<Long> eventIds) {
        log.info("Request for employees invited to events with ids {} is received", eventIds);
        employeesKafkaProducer.sendInvitedEmployees(employeeService.findInvitedEmployees(eventIds));
    }

    @KafkaListener(topics = "uninvited_employees_request", groupId = "employee_group")
    public void handleUninvitedEmployeesRequest(List<Long> eventIds) {
        log.info("Request for employees not invited to events with ids {} is received", eventIds);
        employeesKafkaProducer.sendUninvitedEmployees(employeeService.findUninvitedEmployees(eventIds));
    }
}
