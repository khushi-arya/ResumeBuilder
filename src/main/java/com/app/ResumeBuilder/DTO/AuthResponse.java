package com.app.ResumeBuilder.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String id;
    private String name;
    private String email;
    private String subcriptionPlan;
    private boolean emailverified;
    private String token ;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
// hello world

}
