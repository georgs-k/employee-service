package com.emansy.employeeservice.kafka;

import com.emansy.employeeservice.model.AttendeesDto;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Log4j2
@RequiredArgsConstructor
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, AttendeesDto> attendanceKafkaTemplate;

    public void sendAttendanceNotification(Boolean whetherToAttendOrToUnattend, Set<EmployeeDto> employeeDtos, EventDto eventDto) {
        attendanceKafkaTemplate.send("attendance-notification", new AttendeesDto(whetherToAttendOrToUnattend, employeeDtos, eventDto));
        log.info("Attendance notification is sent to kafka topic: attendance-notification");
    }
}
