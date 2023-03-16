package com.emansy.employeeservice.business.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
class TimeSlot {

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}
