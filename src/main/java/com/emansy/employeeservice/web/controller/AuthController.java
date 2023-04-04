package com.emansy.employeeservice.web.controller;

import com.emansy.employeeservice.business.service.TokenService;
import com.emansy.employeeservice.model.LoginDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "Authentication Controller")
@Log4j2
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/token")
public class AuthController {

    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    @PostMapping
    @ApiOperation(value = "Returns a JWT (Json Web Token)",
            notes = "Provide employee's valid email and password to obtain a JWT",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Missing or invalid email or password"),
            @ApiResponse(code = 401, message = "Authentication failed"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<String> getToken(@Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {
        log.info("Request for a JWT token by passing {}", loginDto);
        if (bindingResult.hasErrors()) {
            log.error("JWT is not generated: error {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        String token = tokenService.generateToken(authentication);
        log.debug("JWT is generated: {}", token);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
