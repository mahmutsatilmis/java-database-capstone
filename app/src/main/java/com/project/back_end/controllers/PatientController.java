package com.project.back_end.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    @Autowired
    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "patient");
        if (validation.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@Valid @RequestBody Patient patient) {
        if (!service.validatePatient(patient)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Patient with email id or phone no already exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        int result = patientService.createPatient(patient);
        Map<String, String> response = new HashMap<>();
        if (result == 1) {
            response.put("message", "Signup successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(@PathVariable Long id,
                                                                    @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "patient");
        if (validation.containsKey("error")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", String.valueOf(validation.get("error")));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(@PathVariable String condition,
                                                                      @PathVariable String name,
                                                                      @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "patient");
        if (validation.containsKey("error")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", String.valueOf(validation.get("error")));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String sanitizedCondition = (condition == null || condition.equalsIgnoreCase("null") || condition.equalsIgnoreCase("all") || condition.equalsIgnoreCase("allAppointments")) ? null : condition;
        String sanitizedName = (name == null || name.equalsIgnoreCase("null") || name.equalsIgnoreCase("all")) ? null : name;

        return service.filterPatient(sanitizedCondition, sanitizedName, token);
    }
}