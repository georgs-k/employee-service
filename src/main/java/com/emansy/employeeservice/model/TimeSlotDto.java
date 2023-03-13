package com.emansy.employeeservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class TimeSlotDto {

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}
