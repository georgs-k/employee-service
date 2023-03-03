package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.model.EmployeeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class EmployeeKafkaProducer {
//
//    private final KafkaTemplate<String, List<EmployeeDto>> kafkaTemplate;
//
//    public void sendInvitedEmployees(List<EmployeeDto> employees) {
//        Message<List<EmployeeDto>> message = MessageBuilder
//                .withPayload(employees)
//                .setHeader(KafkaHeaders.TOPIC, "invited_employees_response")
//                .build();
//        kafkaTemplate.send(message);
//        log.info("List of {} invited employees is sent to kafka topic: {}",
//                employees.size(), message.getHeaders().get(KafkaHeaders.TOPIC));
//    }
//
//    public void sendUninvitedEmployees(List<EmployeeDto> employees) {
//        Message<List<EmployeeDto>> message = MessageBuilder
//                .withPayload(employees)
//                .setHeader(KafkaHeaders.TOPIC, "uninvited_employees_response")
//                .build();
//        kafkaTemplate.send(message);
//        log.info("List of {} uninvited employees is sent to kafka topic: {}",
//                employees.size(), message.getHeaders().get(KafkaHeaders.TOPIC));
//    }

//
//    private final ReplyingKafkaTemplate<> replyingKafkaTemplate;
}
