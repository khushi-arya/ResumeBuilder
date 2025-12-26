package com.app.ResumeBuilder.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2 , max = 20)
    private String name;
    @NotBlank(message = "Email should be valid")
    @Size(min = 2, max = 40)
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 2, max = 20, message = "Password must include lower letter,speicial char")
    private String password;
    private String profileURL;
}
