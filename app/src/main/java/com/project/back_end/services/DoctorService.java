package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService,
                         PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        if (doctorId == null || date == null) {
            return List.of();
        }

        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);
        if (doctorOptional.isEmpty()) {
            return List.of();
        }

        Doctor doctor = doctorOptional.get();
        List<String> availableTimes = doctor.getAvailableTimes() == null ? new ArrayList<>() : doctor.getAvailableTimes();
        if (availableTimes.isEmpty()) {
            return availableTimes;
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

        Set<LocalTime> bookedTimes = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end)
                .stream()
                .map(Appointment::getAppointmentTime)
                .filter(Objects::nonNull)
                .map(LocalDateTime::toLocalTime)
                .collect(Collectors.toSet());

        List<String> filteredSlots = new ArrayList<>();
        for (String timeSlot : availableTimes) {
            if (timeSlot == null || timeSlot.isBlank()) {
                continue;
            }

            LocalTime startTime = parseSlotStartTime(timeSlot);
            if (startTime != null && !bookedTimes.contains(startTime)) {
                filteredSlots.add(timeSlot);
            }
        }

        return filteredSlots;
    }

    public int saveDoctor(Doctor doctor) {
        if (doctor == null) {
            return 0;
        }

        try {
            if (doctor.getEmail() == null || doctor.getEmail().isBlank()) {
                return 0;
            }

            Doctor existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
            if (existingDoctor != null) {
                return -1;
            }

            doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        if (doctor == null || doctor.getId() == null) {
            return 0;
        }

        try {
            Optional<Doctor> existingOptional = doctorRepository.findById(doctor.getId());
            if (existingOptional.isEmpty()) {
                return -1;
            }

            Doctor existing = existingOptional.get();
            if (doctor.getPassword() != null && !doctor.getPassword().isBlank()) {
                doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            } else {
                doctor.setPassword(existing.getPassword());
            }

            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) {
                return -1;
            }

            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();

        if (login == null || login.getIdentifier() == null || login.getIdentifier().isBlank()
                || login.getPassword() == null || login.getPassword().isBlank()) {
            response.put("message", "Invalid login credentials.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
            boolean matches = doctor != null && (
                doctor.getPassword().equals(login.getPassword()) ||
                passwordEncoder.matches(login.getPassword(), doctor.getPassword())
            );

            if (!matches) {
                response.put("message", "Invalid email or password.");
                return ResponseEntity.status(401).body(response);
            }

            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            response.put("message", "Doctor login successful.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error validating doctor.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = (name == null || name.isBlank() || name.equalsIgnoreCase("null"))
                ? doctorRepository.findAll()
                : doctorRepository.findByNameLike(name.trim());
        response.put("doctors", doctors);
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();

        String cleanName = (name == null || name.isBlank() || name.equalsIgnoreCase("null")) ? null : name.trim();
        String cleanSpecialty = (specialty == null || specialty.isBlank() || specialty.equalsIgnoreCase("null")) ? null : specialty.trim();
        String cleanTime = (amOrPm == null || amOrPm.isBlank() || amOrPm.equalsIgnoreCase("null")) ? null : amOrPm.trim().toUpperCase(Locale.ROOT);

        List<Doctor> doctors;

        if (cleanName != null && cleanSpecialty != null) {
            doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(cleanName, cleanSpecialty);
        } else if (cleanName != null) {
            doctors = doctorRepository.findByNameLike(cleanName);
        } else if (cleanSpecialty != null) {
            doctors = doctorRepository.findBySpecialtyIgnoreCase(cleanSpecialty);
        } else {
            doctors = doctorRepository.findAll();
        }

        if (cleanTime != null && (cleanTime.equals("AM") || cleanTime.equals("PM"))) {
            doctors = filterDoctorByTime(doctors, cleanTime);
        }

        response.put("doctors", doctors);
        return response;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (doctors == null || doctors.isEmpty() || amOrPm == null || amOrPm.isBlank()) {
            return doctors == null ? List.of() : doctors;
        }

        return doctors.stream()
                .filter(doctor -> doctor != null && doctor.getAvailableTimes() != null)
                .filter(doctor -> doctor.getAvailableTimes().stream().anyMatch(slot -> matchesAmOrPm(slot, amOrPm)))
                .collect(Collectors.toList());
    }

    private boolean matchesAmOrPm(String slot, String amOrPm) {
        if (slot == null || slot.isBlank()) {
            return false;
        }

        String upperSlot = slot.toUpperCase(Locale.ROOT);
        if (upperSlot.contains("AM") || upperSlot.contains("PM")) {
            return upperSlot.contains(amOrPm);
        }

        LocalTime startTime = parseSlotStartTime(slot);
        if (startTime == null) {
            return false;
        }

        int hour = startTime.getHour();
        if ("AM".equals(amOrPm)) {
            return hour < 12;
        }
        if ("PM".equals(amOrPm)) {
            return hour >= 12;
        }
        return false;
    }

    private LocalTime parseSlotStartTime(String slot) {
        if (slot == null || slot.isBlank()) {
            return null;
        }

        String trimmed = slot.trim();
        String firstPart = trimmed.contains("-") ? trimmed.split("-")[0].trim() : trimmed;

        try {
            return LocalTime.parse(firstPart);
        } catch (Exception e) {
            try {
                String cleaned = firstPart.replace("AM", "").replace("PM", "").trim();
                String[] parts = cleaned.split(":");
                if (parts.length == 2) {
                    int hour = Integer.parseInt(parts[0].trim());
                    int minute = Integer.parseInt(parts[1].trim());
                    return LocalTime.of(hour, minute);
                }
            } catch (Exception ignored) {
                return null;
            }
            return null;
        }
    }
}