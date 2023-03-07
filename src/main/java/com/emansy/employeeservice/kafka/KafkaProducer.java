package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.model.AttendeesDto;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Log4j2
@RequiredArgsConstructor
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, AttendeesDto> attendeesKafkaTemplate;

//    private final ReplyingKafkaTemplate<> replyingKafkaTemplate;

    public void sendUnattendNotificationRequest(Set<EmployeeDto> employeeDtos, EventDto eventDto) {
        Message<AttendeesDto> message = MessageBuilder
                .withPayload(new AttendeesDto(employeeDtos, eventDto))
                .setHeader(KafkaHeaders.TOPIC, "unattend_notification_request")
                .build();
        attendeesKafkaTemplate.send(message);
        log.info("Unattend notification request is sent to kafka topic: {}",
                message.getHeaders().get(KafkaHeaders.TOPIC));
    }
}
