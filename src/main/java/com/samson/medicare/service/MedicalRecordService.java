package com.samson.medicare.service;

import com.samson.medicare.dto.MedicalRecordRequest;
import com.samson.medicare.dto.MedicalRecordResponse;
import com.samson.medicare.entity.Appointment;
import com.samson.medicare.entity.Doctor;
import com.samson.medicare.entity.MedicalRecord;
import com.samson.medicare.entity.Patient;
import com.samson.medicare.exception.ResourceNotFoundException;
import com.samson.medicare.repository.AppointmentRepository;
import com.samson.medicare.repository.DoctorRepository;
import com.samson.medicare.repository.MedicalRecordRepository;
import com.samson.medicare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public MedicalRecordResponse createRecord(MedicalRecordRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + request.getAppointmentId()));
        }

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setAppointment(appointment);
        record.setDiagnosis(request.getDiagnosis());
        record.setPrescription(request.getPrescription());
        record.setTreatment(request.getTreatment());
        record.setNotes(request.getNotes());
        record.setFollowUpRequired(request.getFollowUpRequired());

        MedicalRecord saved = medicalRecordRepository.save(record);
        return mapToResponse(saved);
    }

    public List<MedicalRecordResponse> getRecordsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return medicalRecordRepository.findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<MedicalRecordResponse> getRecordsByDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }
        return medicalRecordRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public MedicalRecordResponse getRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + id));
        return mapToResponse(record);
    }

    @Transactional
    public void deleteRecord(Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medical record not found with id: " + id);
        }
        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordResponse mapToResponse(MedicalRecord record) {
        return new MedicalRecordResponse(
                record.getId(),
                record.getPatient().getId(),
                record.getPatient().getFirstName() + " " + record.getPatient().getLastName(),
                record.getDoctor().getId(),
                record.getDoctor().getFirstName() + " " + record.getDoctor().getLastName(),
                record.getAppointment() != null ? record.getAppointment().getId() : null,
                record.getDiagnosis(),
                record.getPrescription(),
                record.getTreatment(),
                record.getNotes(),
                record.getFollowUpRequired(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}