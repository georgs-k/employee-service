package com.emansy.employeeservice.web.controller;

import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.EventDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Api(tags = "Employee Controller")
@Log4j2
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @ApiOperation(value = "Finds all employees",
            notes = "Returns an entire list of employees",
            response = EmployeeDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<List<EmployeeDto>> findAllEmployees() {
        log.info("Retrieve list of all employees");
        List<EmployeeDto> employees = employeeService.findAll();
        log.debug("Size of employee list is {}", employees.size());
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Finds an employee by id",
            notes = "Provide an id to find a specific employee",
            response = EmployeeDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<EmployeeDto> findEmployeeById(
            @ApiParam(value = "Id of an employee", required = true)
            @PathVariable @NotNull @Positive(message = "a positive integer number is required")
            Long id) {
        log.info("Find an employee by id: {}", id);
        Optional<EmployeeDto> employee = employeeService.findById(id);
        if (!employee.isPresent()) {
            log.warn("Employee with id {} is not found.", id);
            return ResponseEntity.notFound().build();
        }
        log.debug("Employee with id {} is {}", id, employee);
        return ResponseEntity.ok(employee.get());
    }

    @PostMapping
    @ApiOperation(value = "Saves a new employee",
            notes = "Provide employee data to save a new employee",
            response = EmployeeDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The employee is successfully saved"),
            @ApiResponse(code = 400, message = "Missed required parameters, parameters are not valid"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<EmployeeDto> saveEmployee(
            @Valid @RequestBody EmployeeDto employeeDto, BindingResult bindingResult) {
        log.info("Create and save a new employee by passing {}", employeeDto);
        if (bindingResult.hasErrors()) {
            log.error("New employee is not created: error {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }
        EmployeeDto employeeDtoSaved = employeeService.save(employeeDto);
        log.debug("New employee is created and saved: {}", employeeDtoSaved);
        return new ResponseEntity<>(employeeDtoSaved, HttpStatus.CREATED);
    }

    @PutMapping
    @ApiOperation(value = "Updates an employee",
            notes = "Provide employee data for updating an existing employee",
            response = EmployeeDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The employee is successfully updated"),
            @ApiResponse(code = 400, message = "Missed required parameters, parameters are not valid"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<EmployeeDto> updateEmployee(
            @Valid @RequestBody EmployeeDto employeeDto, BindingResult bindingResult) {
        log.info("Update an existing employee by passing {}", employeeDto);
        if (bindingResult.hasErrors()) {
            log.error("Employee is not updated: error {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }
        Long id = employeeDto.getId();
        if (id == null || !employeeService.existsById(id)) {
            log.warn("Employee for update with id {} is not found", id);
            return ResponseEntity.notFound().build();
        }
        EmployeeDto employeeDtoUpdated = employeeService.update(employeeDto);
        log.debug("Existing employee is updated: {}", employeeDtoUpdated);
        return new ResponseEntity<>(employeeDtoUpdated, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes an employee by id",
            notes = "Provide an id to delete a specific employee")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The employee is successfully deleted"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<Void> deleteEmployeeById(
            @ApiParam(value = "Id of an employee", required = true)
            @PathVariable @NotNull @Positive(message = "a positive integer number is required")
            Long id) {
        log.info("Delete an employee by id: {}", id);
        if (!employeeService.existsById(id)) {
            log.warn("Employee for delete with id {} is not found", id);
            return ResponseEntity.notFound().build();
        }
        employeeService.deleteById(id);
        log.debug("Employee with id {} is deleted", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/{fromDate}/{thruDate}")
    @ApiOperation(value = "Finds attended events by employee's id and a date interval",
            notes = "Provide employee's id, earliest date and latest date for the event",
            response = EventDto.class, responseContainer = "Set")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<Set<EventDto>> findAttendedEventsBetween(
            @ApiParam(value = "Id of an employee", required = true)
            @PathVariable @NotNull @Positive(message = "a positive integer number is required")
            Long id,
            @ApiParam(value = "Earliest date of an event", required = true)
            @PathVariable @NotBlank @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Required date format: yyyy-MM-dd")
            String fromDate,
            @ApiParam(value = "Latest date of an event", required = true)
            @PathVariable @NotBlank @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Required date format: yyyy-MM-dd")
            String thruDate) throws ExecutionException, InterruptedException {
        log.info("Find events attended by the employee with id {}, scheduled between {} and {}", id, fromDate, thruDate);
        Set<EventDto> events = employeeService.findAttendedEventsBetween(id, fromDate, thruDate);
        log.debug("Number of events is {}", events.size());
        return ResponseEntity.ok(events);
    }

    @PatchMapping("/{id}")
    @ApiOperation(value = "Cancels an employee's attendance of an event (if exists)",
            notes = "Provide an employee's id and event data")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "The employee's attendance is cancelled (if existed)"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<Void> unattend(
            @ApiParam(value = "Id of an employee", required = true)
            @PathVariable @NotNull @Positive(message = "a positive integer number is required")
            Long id,
            @Valid @RequestBody EventDto eventDto,
            BindingResult bindingResult) {
        log.info("Cancel employee's with id {} attendance of event with id {} (if exists)", id, eventDto.getId());
        if (bindingResult.hasErrors()) {
            log.error("Attendance not cancelled: error {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }
        Set<Long> idWrappedForService = new HashSet<>();
        idWrappedForService.add(id);
        employeeService.unattendEvent(idWrappedForService, eventDto);
        log.debug("Employee's with id {} attendance of event with id {} is cancelled (if existed)", id, eventDto.getId());
        return ResponseEntity.noContent().build();
    }
}
