package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeesKafkaConsumer {

    private final EmployeeService employeeService;

    private final EmployeesKafkaProducer employeesKafkaProducer;

    @KafkaListener(topics = "invited_employees_request", groupId = "employee_group")
    public void handleInvitedEmployeesRequest(List<Long> eventIds) {
        employeesKafkaProducer.sendInvitedEmployees(employeeService.findInvitedEmployees(eventIds));
    }

    @KafkaListener(topics = "uninvited_employees_request", groupId = "employee_group")
    public void handleUninvitedEmployeesRequest(List<Long> eventIds) {
        employeesKafkaProducer.sendUninvitedEmployees(employeeService.findUninvitedEmployees(eventIds));
    }
}
