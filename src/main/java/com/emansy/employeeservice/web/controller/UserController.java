package com.emansy.employeeservice.web.controller;

import com.emansy.employeeservice.business.service.UserService;
import com.emansy.employeeservice.model.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Api(tags = "User Data Controller")
@Log4j2
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @ApiOperation(value = "Finds all users",
            notes = "Returns an entire list of users",
            response = UserDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<List<UserDto>> findAllUsers(@AuthenticationPrincipal Jwt token) {
        Map<String, Object> claims = token.getClaims();
        if (!claims.get("role").equals("[ADMIN]")) {
            log.warn("Access denied. Requested resource is forbidden");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.info("Retrieve list of all users");
        List<UserDto> users = userService.findAll();
        log.debug("Size of user list is {}", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Finds a user by id",
            notes = "Provide an id to find a specific user",
            response = UserDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<UserDto> findUserById(
    @ApiParam(value = "Id of a user", required = true)
    @PathVariable
    @NotNull @Positive(message = "a positive integer number is required")
    Long id,
    @AuthenticationPrincipal
    Jwt token) {
        Map<String, Object> claims = token.getClaims();
        if (!claims.get("role").equals("[ADMIN]")) {
            log.warn("Access denied. Requested resource is forbidden");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.info("Find a user by id: {}", id);
        Optional<UserDto> user = userService.findById(id);
        if (!user.isPresent()) {
            log.warn("User with id {} is not found.", id);
            return ResponseEntity.notFound().build();
        }
        log.debug("User with id {} is {}", id, user);
        return ResponseEntity.ok(user.get());
    }

    @PostMapping
    @ApiOperation(value = "Saves a new user",
            notes = "Provide user data to save a new user",
            response = UserDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 400, message = "Missed required parameters, parameters are not valid"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<Void> createUser(
            @Valid @RequestBody UserDto userDto, BindingResult bindingResult, @AuthenticationPrincipal Jwt token) {
        Map<String, Object> claims = token.getClaims();
        if (!claims.get("role").equals("[ADMIN]")) {
            log.warn("Access denied. Requested resource is forbidden");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.info("Create and save a new user by passing {}", userDto);
        if (bindingResult.hasErrors()) {
            log.error("New user is not created: error {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }
        userService.create(userDto);
        log.debug("New user is created and saved");
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    @ApiOperation(value = "Deletes a user",
            notes = "Provide user data to delete a user",
            response = UserDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 400, message = "Missed required parameters, parameters are not valid"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<Void> deleteUser(
            @Valid @RequestBody UserDto userDto, BindingResult bindingResult, @AuthenticationPrincipal Jwt token) {
        Map<String, Object> claims = token.getClaims();
        if (!claims.get("role").equals("[ADMIN]")) {
            log.warn("Access denied. Requested resource is forbidden");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.info("Delete a user by passing {}", userDto);
        if (bindingResult.hasErrors()) {
            log.error("User is not deleted: error {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }
        userService.delete(userDto);
        log.debug("User is deleted");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/role")
    @ApiOperation(value = "Changes the role of a user",
            notes = "Provide user data to change user's role",
            response = UserDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 400, message = "Missed required parameters, parameters are not valid"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<Void> changeRole(
            @Valid @RequestBody UserDto userDto, BindingResult bindingResult, @AuthenticationPrincipal Jwt token) {
        Map<String, Object> claims = token.getClaims();
        if (!claims.get("role").equals("[ADMIN]")) {
            log.warn("Access denied. Requested resource is forbidden");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.info("Change the role of a user by passing {}", userDto);
        if (bindingResult.hasErrors()) {
            log.error("User role is not changed: error {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }
        userService.changeRole(userDto);
        log.debug("User's role is changed");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    @ApiOperation(value = "Changes the password of a user",
            notes = "Provide user data to change user's password",
            response = UserDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The request has succeeded"),
            @ApiResponse(code = 400, message = "Missed required parameters, parameters are not valid"),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The server has not found anything matching the Request-URI"),
            @ApiResponse(code = 500, message = "Server error")})
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody UserDto userDto, BindingResult bindingResult, @AuthenticationPrincipal Jwt token) {
        Map<String, Object> claims = token.getClaims();
        if (!claims.get("role").equals("[USER]") || !claims.get("id").equals(userDto.getId())) {
            log.warn("Access denied. Requested resource is forbidden");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        log.info("Change the password of a user by passing {}", userDto);
        if (bindingResult.hasErrors()) {
            log.error("User password is not changed: error {}", bindingResult);
            return ResponseEntity.badRequest().build();
        }
        userService.changeRole(userDto);
        log.debug("User's password is changed");
        return ResponseEntity.ok().build();
    }
}
