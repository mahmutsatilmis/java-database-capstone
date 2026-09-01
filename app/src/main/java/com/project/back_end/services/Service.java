package com.project.back_end.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

@org.springframework.stereotype.Service
public class Service {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   TokenService tokenService,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public Map<String, Object> validateToken(String token, String role) {
        Map<String, Object> response = new HashMap<>();

        if (token == null || token.isBlank()) {
            response.put("error", "Token is missing.");
            return response;
        }

        if (!tokenService.validateToken(token, role)) {
            response.put("error", "Invalid " + role + " token.");
            return response;
        }

        return response;
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();

        if (receivedAdmin == null || receivedAdmin.getUsername() == null || receivedAdmin.getUsername().isBlank()
                || receivedAdmin.getPassword() == null || receivedAdmin.getPassword().isBlank()) {
            response.put("message", "Invalid admin credentials.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid username or password.");
                return ResponseEntity.status(401).body(response);
            }

            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            response.put("message", "Admin login successful.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error validating admin.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> response = new HashMap<>();

        if ((name == null || name.isBlank())
                && (specialty == null || specialty.isBlank())
                && (time == null || time.isBlank())) {
            response.put("doctors", doctorService.getDoctors());
            return response;
        }

        response.putAll(doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time));
        return response;
    }

    public int validateAppointment(Appointment appointment) {
        if (appointment == null || appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return -1;
        }

        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId()).orElse(null);
        if (doctor == null) {
            return -1;
        }

        if (appointment.getAppointmentTime() == null) {
            return 0;
        }

        List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), appointment.getAppointmentTime().toLocalDate());
        if (availableSlots == null || availableSlots.isEmpty()) {
            return 0;
        }

        String appointmentTime = appointment.getAppointmentTime().toLocalTime().toString();
        for (String slot : availableSlots) {
            if (slot == null || slot.isBlank()) {
                continue;
            }
            String slotStart = slot.contains("-") ? slot.split("-")[0].trim() : slot.trim();
            if (slotStart.equals(appointmentTime)) {
                return 1;
            }
        }

        return 0;
    }

    public boolean validatePatient(Patient patient) {
        if (patient == null) {
            return true;
        }

        String email = patient.getEmail();
        String phone = patient.getPhone();
        if ((email == null || email.isBlank()) && (phone == null || phone.isBlank())) {
            return true;
        }

        Patient existing = patientRepository.findByEmailOrPhone(email, phone);
        return existing == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();

        if (login == null || login.getIdentifier() == null || login.getIdentifier().isBlank()
                || login.getPassword() == null || login.getPassword().isBlank()) {
            response.put("message", "Invalid login credentials.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());
            if (patient == null || !patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid email or password.");
                return ResponseEntity.status(401).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            response.put("message", "Patient login successful.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error validating patient.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();

        if (token == null || token.isBlank()) {
            response.put("message", "Unauthorized access.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            ResponseEntity<Map<String, Object>> patientResponse = patientService.getPatientDetails(token);
            Patient patient = patientResponse.getBody() != null
                    ? (Patient) patientResponse.getBody().get("patient")
                    : null;

            if (patient == null) {
                response.put("message", "Patient not found for the provided token.");
                return ResponseEntity.status(401).body(response);
            }

            Long patientId = patient.getId();
            if (condition == null || condition.isBlank()) {
                if (name == null || name.isBlank()) {
                    return patientService.getPatientAppointment(patientId, token);
                }
                return patientService.filterByDoctor(name, patientId);
            }

            if (name == null || name.isBlank()) {
                return patientService.filterByCondition(condition, patientId);
            }

            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        } catch (Exception e) {
            response.put("message", "Error filtering patient appointments.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
