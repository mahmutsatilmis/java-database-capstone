package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(@PathVariable String user,
                                                                     @PathVariable Long doctorId,
                                                                     @PathVariable String date,
                                                                     @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, user);
        if (validation.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        try {
            LocalDate availabilityDate = LocalDate.parse(date);
            Map<String, Object> response = new HashMap<>();
            response.put("availability", doctorService.getDoctorAvailability(doctorId, availabilityDate));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid date format.");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(@Valid @RequestBody Doctor doctor,
                                                         @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "admin");
        if (validation.containsKey("error")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", String.valueOf(validation.get("error")));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        int result = doctorService.saveDoctor(doctor);
        Map<String, String> response = new HashMap<>();
        if (result == 1) {
            response.put("message", "Doctor added to db");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        if (result == -1) {
            response.put("message", "Doctor already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        response.put("message", "Some internal error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@Valid @RequestBody Doctor doctor,
                                                            @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "admin");
        if (validation.containsKey("error")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", String.valueOf(validation.get("error")));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        int result = doctorService.updateDoctor(doctor);
        Map<String, String> response = new HashMap<>();
        if (result == 1) {
            response.put("message", "Doctor updated");
            return ResponseEntity.ok(response);
        }
        if (result == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("message", "Some internal error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id,
                                                            @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "admin");
        if (validation.containsKey("error")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", String.valueOf(validation.get("error")));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        int result = doctorService.deleteDoctor(id);
        Map<String, String> response = new HashMap<>();
        if (result == 1) {
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response);
        }
        if (result == -1) {
            response.put("message", "Doctor not found with id");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("message", "Some internal error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterDoctorsByQuery(@RequestParam(required = false) String name,
                                                                    @RequestParam(required = false) String time,
                                                                    @RequestParam(required = false) String specialty) {
        return ResponseEntity.ok(service.filterDoctor(name, specialty, time));
    }
}