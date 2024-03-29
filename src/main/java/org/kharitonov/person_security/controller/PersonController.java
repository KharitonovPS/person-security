package org.kharitonov.person_security.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.kharitonov.person_security.models.UserDto;
import org.kharitonov.person_security.services.AdminService;
import org.kharitonov.person_security.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Kharitonov Pavel on 30.01.2024.
 * registration of new User at /registration endpoint
 * check is principal was created at /greetings endpoint
 * check admin role at /admin endpoint
 * change role of user with @PreAuthorize (in adminService) at /make-admin endpoint
 */
@Slf4j
@RestController()
@RequestMapping("/api/v1")
public class PersonController {

    private final RegistrationService registrationService;
    private final AdminService adminService;


    public PersonController(
            RegistrationService registrationService,
            AdminService adminService
    ) {
        this.registrationService = registrationService;
        this.adminService = adminService;
    }

    @GetMapping("/greetings")
    public ResponseEntity<Map<String, String>> getGreetings(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("Request fas taken from principal -> {}", userDetails.getUsername());
        log.info("User roles: {}", userDetails.getAuthorities());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting", "Hello, %s!"
                        .formatted(userDetails.getUsername()
                        )));
    }

    //registration of new user with custom validation
    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> addUser(
            @RequestBody @Valid UserDto dto
    ) {
        registrationService.addPerson(dto);
        log.info("Registration of new user {}", dto.username());
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> adminPage(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("Request fas taken from admin -> {}",
                userDetails.getUsername());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("greeting", "Hello, from admin!"));
    }

    @GetMapping("/make-admin/{username}")
    public ResponseEntity<HttpStatus> makeAdmin(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails details) {
        log.info("Admin {}, try to change ROLE_ADMIN to user {}.",
                details.getUsername(), username);
        return adminService.makeAdmin(username);
    }
}


