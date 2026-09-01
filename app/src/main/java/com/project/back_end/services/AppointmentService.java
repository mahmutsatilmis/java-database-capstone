    package com.project.back_end.services;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import com.project.back_end.models.Appointment;
    import com.project.back_end.models.Doctor;
    import com.project.back_end.repo.AppointmentRepository;
    import com.project.back_end.repo.DoctorRepository;
    import com.project.back_end.repo.PatientRepository;

    @Service
    public class AppointmentService {

        private final AppointmentRepository appointmentRepository;
        private final PatientRepository patientRepository;
        private final DoctorRepository doctorRepository;
        private final TokenService tokenService;

        @Autowired
        public AppointmentService(AppointmentRepository appointmentRepository,
                                PatientRepository patientRepository,
                                DoctorRepository doctorRepository,
                                TokenService tokenService) {
            this.appointmentRepository = appointmentRepository;
            this.patientRepository = patientRepository;
            this.doctorRepository = doctorRepository;
            this.tokenService = tokenService;
        }

        public boolean validateAppointment(Appointment appointment) {
            if (appointment == null) {
                return false;
            }
            if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null
                    || !doctorRepository.existsById(appointment.getDoctor().getId())) {
                return false;
            }
            if (appointment.getPatient() == null || appointment.getPatient().getId() == null
                    || !patientRepository.existsById(appointment.getPatient().getId())) {
                return false;
            }
            if (appointment.getAppointmentTime() == null) {
                return false;
            }
            return true;
        }

        public int bookAppointment(Appointment appointment) {
            try {
                if (appointment == null || !validateAppointment(appointment)) {
                    return 0;
                }
                appointmentRepository.save(appointment);
                return 1;
            } catch (Exception e) {
                return 0;
            }
        }

        @Transactional
        public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
            Map<String, String> response = new HashMap<>();

            try {
                if (appointment == null || appointment.getId() == null || !validateAppointment(appointment)) {
                    response.put("message", "Invalid appointment data.");
                    return ResponseEntity.badRequest().body(response);
                }

                Optional<Appointment> existingAppointment = appointmentRepository.findById(appointment.getId());
                if (existingAppointment.isEmpty()) {
                    response.put("message", "Appointment not found.");
                    return ResponseEntity.badRequest().body(response);
                }

                appointmentRepository.save(appointment);
                response.put("message", "Appointment updated successfully.");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put("message", "Error updating appointment.");
                return ResponseEntity.internalServerError().body(response);
            }
        }

        @Transactional
        public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
            Map<String, String> response = new HashMap<>();

            if (token == null || token.isBlank()) {
                response.put("message", "Unauthorized access.");
                return ResponseEntity.status(401).body(response);
            }

            try {
                Optional<Appointment> appointment = appointmentRepository.findById(id);
                if (appointment.isEmpty()) {
                    response.put("message", "Appointment not found.");
                    return ResponseEntity.badRequest().body(response);
                }

                String identifier = tokenService.extractIdentifier(token);
                if (identifier == null || identifier.isBlank()) {
                    response.put("message", "Invalid token.");
                    return ResponseEntity.status(401).body(response);
                }

                var patientByEmail = patientRepository.findByEmail(identifier);
                Long patientIdFromToken = patientByEmail != null ? patientByEmail.getId() : null;
                if (patientIdFromToken == null || appointment.get().getPatient() == null
                        || appointment.get().getPatient().getId() == null
                        || !appointment.get().getPatient().getId().equals(patientIdFromToken)) {
                    response.put("message", "Token does not match the appointment owner.");
                    return ResponseEntity.status(403).body(response);
                }

                appointmentRepository.delete(appointment.get());
                response.put("message", "Appointment cancelled successfully.");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.put("message", "Error cancelling appointment.");
                return ResponseEntity.internalServerError().body(response);
            }
        }

        public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
            Map<String, Object> response = new HashMap<>();

            if (token == null || token.isBlank()) {
                response.put("appointments", List.of());
                return response;
            }

            try {
                if (date == null) {
                    response.put("appointments", List.of());
                    return response;
                }

                String identifier = tokenService.extractIdentifier(token);
                if (identifier == null || identifier.isBlank()) {
                    response.put("appointments", List.of());
                    return response;
                }

                Doctor doctor = doctorRepository.findByEmail(identifier);
                if (doctor == null || doctor.getId() == null) {
                    response.put("appointments", List.of());
                    return response;
                }

                Long doctorId = doctor.getId();
                LocalDateTime start = date.atStartOfDay();
                LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);
                List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

                if (pname != null && !pname.isBlank()) {
                    appointments = appointments.stream()
                            .filter(a -> a.getPatient() != null
                                    && a.getPatient().getName() != null
                                    && a.getPatient().getName().toLowerCase().contains(pname.toLowerCase()))
                            .toList();
                }

                response.put("appointments", appointments);
                return response;
            } catch (Exception e) {
                response.put("appointments", List.of());
                return response;
            }
        }


    }
