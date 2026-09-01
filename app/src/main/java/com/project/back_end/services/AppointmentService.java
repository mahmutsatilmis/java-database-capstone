package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String patientName, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();

        if (token == null || token.isBlank()) {
            response.put("error", "Unauthorized access");
            return response;
        }

        String email = tokenService.extractEmail(token);
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null) {
            response.put("error", "Doctor not found for the provided token");
            return response;
        }

        Long doctorId = doctor.getId();
        String cleanName = (patientName != null && !patientName.isBlank() && !patientName.equalsIgnoreCase("null") && !patientName.equalsIgnoreCase("all"))
                ? patientName.trim()
                : null;

        List<Appointment> appointments;

        if (date != null && cleanName != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);
            appointments = appointmentRepository.findByDoctorIdAndDateAndPatientName(doctorId, start, end, cleanName);
        } else if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else if (cleanName != null) {
            appointments = appointmentRepository.findByDoctorIdAndPatientName(doctorId, cleanName);
        } else {
            appointments = appointmentRepository.findByDoctor_IdOrderByAppointmentTimeAsc(doctorId);
        }

        List<AppointmentDTO> dtoList = appointments.stream()
                .map(this::convertToAppointmentDTO)
                .toList();

        response.put("appointments", dtoList);
        return response;
    }

    public int bookAppointment(Appointment appointment) {
        try {
            if (appointment == null) return 0;
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        if (appointment == null || appointment.getId() == null) {
            response.put("message", "Invalid appointment data.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            if (!appointmentRepository.existsById(appointment.getId())) {
                response.put("message", "Appointment not found.");
                return ResponseEntity.status(404).body(response);
            }
            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error updating appointment.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!appointmentRepository.existsById(id)) {
                response.put("message", "Appointment not found.");
                return ResponseEntity.status(404).body(response);
            }
            appointmentRepository.deleteById(id);
            response.put("message", "Appointment cancelled successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error cancelling appointment.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        if (appointment == null) return null;

        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor() != null ? appointment.getDoctor().getId() : null,
                appointment.getDoctor() != null ? appointment.getDoctor().getName() : null,
                appointment.getPatient() != null ? appointment.getPatient().getId() : null,
                appointment.getPatient() != null ? appointment.getPatient().getName() : null,
                appointment.getPatient() != null ? appointment.getPatient().getEmail() : null,
                appointment.getPatient() != null ? appointment.getPatient().getPhone() : null,
                appointment.getPatient() != null ? appointment.getPatient().getAddress() : null,
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}