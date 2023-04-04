package com.emansy.employeeservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@ApiModel(value = "Model of login data ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class LoginDto {

    @ApiModelProperty(value = "Email of an employee as the login name for obtaining a JWT")
    @Email(message = "A valid email address is required")
    @NotBlank(message = "Required")
    private String email;

    @ApiModelProperty(value = "Password of an employee")
    @NotBlank(message = "Required")
    private String password;
}
