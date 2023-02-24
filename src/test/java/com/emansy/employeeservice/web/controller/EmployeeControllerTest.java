package com.emansy.employeeservice.web.controller;

import com.emansy.employeeservice.business.service.EmployeeService;
import com.emansy.employeeservice.model.EmployeeDto;
import com.emansy.employeeservice.model.JobTitleDto;
import com.emansy.employeeservice.model.OfficeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class EmployeeControllerTest {

    public static final String URL = "/api/v1/employees";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Test
    void findAllEmployeesTestPositive() throws Exception {
        when(service.findAll()).thenReturn(Arrays.asList(createEmployeeDto(), createEmployeeDto()));
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("First name"))
                .andExpect(status().isOk());
        verify(service, times(1)).findAll();
    }

    @Test
    void findAllEmployeesTestNegative() throws Exception {
        when(service.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());
        verify(service, times(1)).findAll();
    }

    @Test
    void findEmployeeByIdTestPositive() throws Exception {
        when(service.findById(anyLong())).thenReturn(Optional.of(createEmployeeDto()));
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("First name"))
                .andExpect(status().isOk());
        verify(service, times(1)).findById(anyLong());
    }

    @Test
    void findEmployeeByIdTestNegative() throws Exception {
        when(service.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/1"))
                .andExpect(status().isNotFound());
        verify(service, times(1)).findById(anyLong());
    }

    @Test
    void saveEmployeeTestPositive() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        when(service.save(employeeDto)).thenReturn(employeeDto);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("First name"))
                .andExpect(status().isCreated());
        verify(service, times(1)).save(employeeDto);
    }

    @Test
    void saveEmployeeTestNegative() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        employeeDto.setFirstName("");
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(asJsonString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, times(0)).save(employeeDto);
    }

    @Test
    void updateEmployeeTestPositive() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        when(service.existsById(anyLong())).thenReturn(true);
        when(service.update(employeeDto)).thenReturn(employeeDto);
        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(asJsonString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("First name"))
                .andExpect(status().isCreated());
        verify(service, times(1)).existsById(anyLong());
        verify(service, times(1)).update(employeeDto);
    }

    @Test
    void updateEmployeeTestNegativeBadRequest() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        employeeDto.setFirstName("");
        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(asJsonString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, times(0)).existsById(anyLong());
        verify(service, times(0)).update(employeeDto);
    }

    @Test
    void updateEmployeeTestNegativeNotFound() throws Exception {
        EmployeeDto employeeDto = createEmployeeDto();
        when(service.existsById(anyLong())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL)
                        .content(asJsonString(employeeDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(service, times(1)).existsById(anyLong());
        verify(service, times(0)).update(employeeDto);
    }

    @Test
    void deleteEmployeeByIdTestPositive() throws Exception {
        when(service.existsById(anyLong())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/1"))
                .andExpect(status().isNoContent());
        verify(service, times(1)).existsById(anyLong());
        verify(service, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteEmployeeByIdTestNegative() throws Exception {
        when(service.existsById(anyLong())).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/1"))
                .andExpect(status().isNotFound());
        verify(service, times(1)).existsById(anyLong());
        verify(service, times(0)).deleteById(anyLong());
    }

    private EmployeeDto createEmployeeDto() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(1L);
        employeeDto.setFirstName("First name");
        employeeDto.setLastName("Last name");
        employeeDto.setEmail("email@email.com");
        employeeDto.setPhone("+37100000000");
        employeeDto.setJobTitleDto(new JobTitleDto());
        employeeDto.setOfficeDto(new OfficeDto());
        employeeDto.setWorkingStartTime("09:00:00");
        employeeDto.setWorkingEndTime("18:00:00");
        employeeDto.setEventIds(new ArrayList<>());
        return employeeDto;
    }

    public static String asJsonString(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
