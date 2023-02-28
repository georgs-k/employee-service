package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InvitedEmployeesKafkaConsumer {

    private final EmployeeService employeeService;

    private final InvitedEmployeesKafkaProducer invitedEmployeesKafkaProducer;

    @KafkaListener(topics = "invited_employees_request", groupId = "employee_group")
    public void handleRequest(List<Long> eventIds) {
        invitedEmployeesKafkaProducer.send(employeeService.findInvitedEmployees(eventIds));
    }
}
