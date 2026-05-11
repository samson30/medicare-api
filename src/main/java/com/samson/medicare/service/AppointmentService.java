package com.samson.medicare.service;

import com.samson.medicare.dto.AppointmentRequest;
import com.samson.medicare.dto.AppointmentResponse;
import com.samson.medicare.entity.Appointment;
import com.samson.medicare.entity.AppointmentStatus;
import com.samson.medicare.entity.Doctor;
import com.samson.medicare.entity.Patient;
import com.samson.medicare.exception.BookingConflictException;
import com.samson.medicare.exception.ResourceNotFoundException;
import com.samson.medicare.repository.AppointmentRepository;
import com.samson.medicare.repository.DoctorRepository;
import com.samson.medicare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        LocalDateTime start = request.getAppointmentTime();
        LocalDateTime end = start.plusMinutes(request.getDurationMinutes());

        List<Appointment> doctorAppointments = appointmentRepository.findScheduledByDoctorId(doctor.getId());

        boolean hasConflict = doctorAppointments.stream().anyMatch(existing -> {
            LocalDateTime existingStart = existing.getAppointmentTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getDurationMinutes());
            return existingStart.isBefore(end) && existingEnd.isAfter(start);
        });

        if (hasConflict) {
            throw new BookingConflictException(
                    "Doctor already has an appointment at this time slot");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setDurationMinutes(request.getDurationMinutes());
        appointment.setReason(request.getReason());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);
        return mapToResponse(saved);
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return mapToResponse(appointment);
    }

    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BookingConflictException(
                    "Only scheduled appointments can be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment updated = appointmentRepository.save(appointment);
        return mapToResponse(updated);
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long id, String notes) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BookingConflictException(
                    "Only scheduled appointments can be completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setNotes(notes);
        Appointment updated = appointmentRepository.save(appointment);
        return mapToResponse(updated);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        return new AppointmentResponse(
                appointment.getId(),
                patient.getId(),
                patient.getFirstName() + " " + patient.getLastName(),
                doctor.getId(),
                doctor.getFirstName() + " " + doctor.getLastName(),
                appointment.getAppointmentTime(),
                appointment.getDurationMinutes(),
                appointment.getStatus(),
                appointment.getReason(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}