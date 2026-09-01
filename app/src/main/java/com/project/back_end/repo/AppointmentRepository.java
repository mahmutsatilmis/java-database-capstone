package com.project.back_end.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.models.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctor_Id(Long doctorId);

    List<Appointment> findByDoctor_IdOrderByAppointmentTimeAsc(Long doctorId);

    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :patientName, '%'))")
    List<Appointment> findByDoctorIdAndPatientName(@Param("doctorId") Long doctorId, @Param("patientName") String patientName);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :patientName, '%'))")
    List<Appointment> findByDoctorIdAndDateAndPatientName(@Param("doctorId") Long doctorId,
                                                         @Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end,
                                                         @Param("patientName") String patientName);

    List<Appointment> findByPatient_Id(Long patientId);

    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> filterByDoctorNameAndPatientId(@Param("doctorName") String doctorName, @Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.status = :status AND LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(@Param("doctorName") String doctorName,
                                                             @Param("patientId") Long patientId,
                                                             @Param("status") int status);

    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    void deleteAllByDoctorId(@Param("doctorId") Long doctorId);
}