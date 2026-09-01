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
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.path}appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable String date,
                                                              @PathVariable String patientName,
                                                              @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "doctor");
        if (validation.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        LocalDate appointmentDate = null;
        if (date != null && !date.isBlank() && !date.equalsIgnoreCase("null") && !date.equalsIgnoreCase("all")) {
            try {
                appointmentDate = LocalDate.parse(date);
            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Invalid date format.");
                return ResponseEntity.badRequest().body(error);
            }
        }

        String searchName = (patientName != null && !patientName.isBlank() && !patientName.equalsIgnoreCase("null") && !patientName.equalsIgnoreCase("all"))
                ? patientName.trim()
                : null;

        return ResponseEntity.ok(appointmentService.getAppointment(searchName, appointmentDate, token));
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@Valid @RequestBody Appointment appointment,
                                                              @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "patient");
        if (validation.containsKey("error")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", String.valueOf(validation.get("error")));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        int validationResult = service.validateAppointment(appointment);
        if (validationResult != 1) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid appointment data.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        int result = appointmentService.bookAppointment(appointment);
        Map<String, String> response = new HashMap<>();
        if (result == 1) {
            response.put("message", "Appointment booked successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put("message", "Failed to book appointment.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@Valid @RequestBody Appointment appointment,
                                                                @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "patient");
        if (validation.containsKey("error")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", String.valueOf(validation.get("error")));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable long id,
                                                                @PathVariable String token) {
        Map<String, Object> validation = service.validateToken(token, "patient");
        if (validation.containsKey("error")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", String.valueOf(validation.get("error")));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        return appointmentService.cancelAppointment(id, token);
    }
}