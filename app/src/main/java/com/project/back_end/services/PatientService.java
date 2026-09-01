package com.project.back_end.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            if (patient == null) {
                return 0;
            }
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();

        if (id == null || token == null || token.isBlank()) {
            response.put("message", "Unauthorized access.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            Patient patientFromToken = resolvePatientFromToken(token);
            if (patientFromToken == null || !id.equals(patientFromToken.getId())) {
                response.put("message", "Token does not match the patient.");
                return ResponseEntity.status(401).body(response);
            }

            List<Appointment> appointments = appointmentRepository.findByPatient_Id(id);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::convertToAppointmentDTO)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving appointments.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();

        if (id == null) {
            response.put("message", "Patient id is required.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition.");
                return ResponseEntity.badRequest().body(response);
            }

            List<Appointment> appointments = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::convertToAppointmentDTO)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering appointments.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        if (name == null || name.isBlank() || patientId == null) {
            response.put("message", "Doctor name and patient id are required.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::convertToAppointmentDTO)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering appointments by doctor.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();

        if (name == null || name.isBlank()) {
            response.put("message", "Doctor name is required.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("message", "Invalid condition.");
                return ResponseEntity.badRequest().body(response);
            }

            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::convertToAppointmentDTO)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering appointments by doctor and condition.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        if (token == null || token.isBlank()) {
            response.put("message", "Unauthorized access.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            Patient patient = resolvePatientFromToken(token);
            if (patient == null) {
                response.put("message", "Patient not found for the provided token.");
                return ResponseEntity.status(401).body(response);
            }

            response.put("patient", patient);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving patient details.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        Patient patient = appointment.getPatient();
        com.project.back_end.models.Doctor doctor = appointment.getDoctor();

        return new AppointmentDTO(
                appointment.getId(),
                doctor != null ? doctor.getId() : null,
                doctor != null ? doctor.getName() : null,
                patient != null ? patient.getId() : null,
                patient != null ? patient.getName() : null,
                patient != null ? patient.getEmail() : null,
                patient != null ? patient.getPhone() : null,
                patient != null ? patient.getAddress() : null,
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }

    private Patient resolvePatientFromToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String emailFromToken = tokenService.extractEmail(token);
        if (emailFromToken == null || emailFromToken.isBlank()) {
            return null;
        }

        return patientRepository.findByEmail(emailFromToken);
    }
}
