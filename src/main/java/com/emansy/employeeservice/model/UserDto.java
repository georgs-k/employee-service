package com.emansy.employeeservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@ApiModel(value = "Model of user data ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserDto {

    @ApiModelProperty(value = "User's id")
    @Positive(message = "A positive integer number is required")
    @NotNull(message = "Required")
    private Long id;

    @ApiModelProperty(value = "User's email that serves as a login name")
    @Email(message = "A valid email address is required")
    @NotBlank(message = "Required")
    private String email;

    @ApiModelProperty(value = "User's password")
    @Pattern(regexp = "^[^\\s]*$", message = "A password is required, no whitespace allowed")
    @NotBlank(message = "Required")
    private String password;

    @ApiModelProperty(value = "User's role")
    @Pattern(regexp = "^(USER|ADMIN)$", message = "Accepted values: USER, ADMIN")
    @NotBlank(message = "Required")
    private String role;
}
