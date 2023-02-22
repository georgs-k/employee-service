package com.emansy.employeeservice.business.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorModel {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
