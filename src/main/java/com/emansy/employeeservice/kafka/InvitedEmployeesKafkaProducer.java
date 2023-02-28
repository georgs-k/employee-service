package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.model.EmployeeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class InvitedEmployeesKafkaProducer {

    private final KafkaTemplate<String, List<EmployeeDto>> kafkaTemplate;

    public void send(List<EmployeeDto> employees) {
        Message<List<EmployeeDto>> message = MessageBuilder
                .withPayload(employees)
                .setHeader(KafkaHeaders.TOPIC, "invited_employees_send")
                .build();
        kafkaTemplate.send(message);
    }
}
