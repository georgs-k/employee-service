package com.emansy.employeeservice.business.service.impl;

import com.emansy.employeeservice.model.EventDto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
class TimeSlot {

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    public TimeSlot(EventDto eventDto) {
        date = LocalDate.parse(eventDto.getDate());
        startTime = LocalTime.parse(eventDto.getStartTime());
        endTime = LocalTime.parse(eventDto.getEndTime());
    }
}
