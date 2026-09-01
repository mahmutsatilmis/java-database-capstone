package com.project.back_end.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();

        if (prescription == null) {
            response.put("message", "Prescription data is required.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (existing != null && !existing.isEmpty()) {
                response.put("message", "Prescription already exists for this appointment.");
                return ResponseEntity.status(400).body(response);
            }

            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("message", "Error saving prescription.");
            return ResponseEntity.status(500).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();

        if (appointmentId == null) {
            response.put("message", "Appointment ID is required.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescriptions == null || prescriptions.isEmpty()) {
                response.put("message", "No prescription found for this appointment.");
                return ResponseEntity.status(404).body(response);
            }

            response.put("prescription", prescriptions.get(0));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving prescription.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
