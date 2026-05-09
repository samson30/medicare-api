package com.samson.medicare.service;

import com.samson.medicare.dto.DoctorRequest;
import com.samson.medicare.dto.DoctorResponse;
import com.samson.medicare.entity.Doctor;
import com.samson.medicare.entity.Specialization;
import com.samson.medicare.exception.DuplicateResourceException;
import com.samson.medicare.exception.ResourceNotFoundException;
import com.samson.medicare.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Doctor with email " + request.getEmail() + " already exists");
        }
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("Doctor with license " + request.getLicenseNumber() + " already exists");
        }

        Doctor doctor = new Doctor();
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setYearsOfExperience(request.getYearsOfExperience());

        Doctor saved = doctorRepository.save(doctor);
        return mapToResponse(saved);
    }

    public List<DoctorResponse> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return mapToResponse(doctor);
    }

    public List<DoctorResponse> getDoctorsBySpecialization(Specialization specialization) {
        return doctorRepository.findBySpecialization(specialization)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setYearsOfExperience(request.getYearsOfExperience());

        Doctor updated = doctorRepository.save(doctor);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        int totalPatients = doctor.getPatients() != null ? doctor.getPatients().size() : 0;
        return new DoctorResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getSpecialization(),
                doctor.getLicenseNumber(),
                doctor.getYearsOfExperience(),
                totalPatients,
                doctor.getCreatedAt(),
                doctor.getUpdatedAt()
        );
    }
}