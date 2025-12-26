package com.app.ResumeBuilder.Controller;

import com.app.ResumeBuilder.DTO.AuthResponse;
import com.app.ResumeBuilder.DTO.LoginRequest;
import com.app.ResumeBuilder.DTO.RegisterRequest;
import com.app.ResumeBuilder.Model.User;
import com.app.ResumeBuilder.Service.AuthService;
import com.app.ResumeBuilder.Service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.app.ResumeBuilder.Util.AppConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private FileService fileService;

    @PostMapping(REGISTER)
    public ResponseEntity<?>  register(@Valid @RequestBody RegisterRequest registerRequest)
    {
          AuthResponse  authResponse = authService.register(registerRequest);
          return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @GetMapping(VERFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token)
    {
     authService.verifyEmail(token);
     return ResponseEntity.ok(
            Map.of("message", "Email is verified")
    );
    }

    @PostMapping(UPLOAD_PROFILE)
    public ResponseEntity<?> uploadimage(@RequestParam("image") MultipartFile file) throws IOException {
        Map<String, String> reponse = fileService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK).body(reponse);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest)
    {
          AuthResponse response = authService.login(loginRequest);
           return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public String testValidation()
    {
        return "Token is valid";
    }

    @PostMapping(resendVerificationEmail)
    public ResponseEntity<?> resendVerificationEmail(@RequestParam String email){
        authService.ResendEmail(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(email);
    }


    @GetMapping(GetProfile)
    public ResponseEntity<?> getProfile(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User is not authenticated");
        }

        User user = (User) authentication.getPrincipal();
        AuthResponse profile = authService.getProfile(user);
        return ResponseEntity.ok(profile);
    }

}
