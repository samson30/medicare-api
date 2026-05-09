package com.samson.medicare.dto;

import com.samson.medicare.entity.Specialization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Specialization specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private Integer totalPatients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}