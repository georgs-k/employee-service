package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.business.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AllEmployeeKafkaConsumer {

    private final EmployeeService employeeService;

    private final AllEmployeeKafkaProducer allEmployeeKafkaProducer;

    @KafkaListener(topics = "all_employee_request", groupId = "employeeGroup")
    public void handleRequest(List<Long> ignoredList) {
        allEmployeeKafkaProducer.send(employeeService.findAll());
    }
}
