package com.emansy.employeeservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@ApiModel(value = "Model of data for changing the password")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ChangePasswordDto {

    @ApiModelProperty(value = "User's email that serves as a login name")
    @Email(message = "A valid email address is required")
    @NotBlank(message = "Required")
    private String email;

    @ApiModelProperty(value = "User's old password")
    @Pattern(regexp = "^[^\\s]*$", message = "A password is required, no whitespace allowed")
    @NotBlank(message = "Required")
    private String oldPassword;

    @ApiModelProperty(value = "User's new password")
    @Pattern(regexp = "^[^\\s]*$", message = "A password is required, no whitespace allowed")
    @NotBlank(message = "Required")
    private String newPassword;

    @ApiModelProperty(value = "User's new password confirmed")
    @Pattern(regexp = "^[^\\s]*$", message = "A password is required, no whitespace allowed")
    @NotBlank(message = "Required")
    private String newPasswordConfirmed;
}
