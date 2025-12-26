package com.app.ResumeBuilder.Service;

import com.app.ResumeBuilder.DTO.AuthResponse;
import com.app.ResumeBuilder.DTO.LoginRequest;
import com.app.ResumeBuilder.DTO.RegisterRequest;
import com.app.ResumeBuilder.Exception.InvalidCredentialsException;
import com.app.ResumeBuilder.Exception.ResourceExistException;
import com.app.ResumeBuilder.Model.User;
import com.app.ResumeBuilder.Repo.UserRepo;
import com.app.ResumeBuilder.Util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.base.url:http://localhost:8080}")
    private String appBaseURl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User ReturnUser(RegisterRequest registerRequest){
        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setName(registerRequest.getName());
        newUser.setProfileimgURL(registerRequest.getProfileURL());
        newUser.setSubcriptionplan("Basic");
        newUser.setEmailVerified(false);
        newUser.setVerificationToken(UUID.randomUUID().toString());
        newUser.setVerificationExpires(LocalDateTime.now().plusHours(24));
        return  newUser;
    }
    private AuthResponse ReturnAuthResponse(User newUser) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setId(newUser.getId());
        authResponse.setName(newUser.getName());
        authResponse.setEmail(newUser.getEmail());
        authResponse.setSubcriptionPlan(newUser.getSubcriptionplan());
        authResponse.setCreatedDate(LocalDateTime.now());
        authResponse.setUpdatedDate(LocalDateTime.now().plusHours(24));
        authResponse.setEmailverified(newUser.isEmailVerified());
        authResponse.setToken(null);
        return authResponse;
    }
    private void sendVerificationMail(User user){
      try{
          String link = appBaseURl + "/api/auth/verify-email?token=" + user.getVerificationToken();

          String html =
                  "<div style='font-family: Arial; font-size: 14px;'>" +
                          "<h2>Verify Email</h2>" +
                          "<p>Hi " + user.getName() + ", please confirm your email verification.</p>" +
                          "<a href='" + link + "' " +
                          "style='display:inline-block; margin:20px 0; " +
                          "padding:12px 20px; background:#6366f1; color:#ffffff; " +
                          "text-decoration:none; border-radius:5px;'>" +
                          "Verify Email</a>" +
                          "</div>";
          emailService.sendHTMLMail(user.getEmail(),"Verify Email",html);


      }catch(Exception e){
          throw new RuntimeException("Failed to send verification mail: "+e.getMessage());
      }

    }

    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Registering user with email {}", registerRequest);

        if(userRepo.existsByEmail(registerRequest.getEmail())) {
                log.warn("User with email {} already exists", registerRequest.getEmail());
                throw new ResourceExistException("User with email already exists");
        }
        User newUser = ReturnUser(registerRequest);
        sendVerificationMail(newUser);

        userRepo.save(newUser);
        return ReturnAuthResponse(newUser);
     }

    public void verifyEmail(String token) {
      User user = userRepo.findByVerificationToken(token).orElseThrow(()-> new RuntimeException("Verification token not found"));
      if(user.getVerificationExpires() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
          throw new RuntimeException(" Token has expired.Please Request new one");

      }
      user.setEmailVerified(true);
      user.setVerificationToken(null);
      user.setVerificationExpires(null);
      userRepo.save(user);
    }


    public AuthResponse login(LoginRequest loginRequest) {
        User emailRequest = userRepo.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new RuntimeException("Email not found"));



        if(!passwordEncoder.matches(loginRequest.getPassword(), emailRequest.getPassword())){
            throw new InvalidCredentialsException("Password is incorrect");
        }

        if(!emailRequest.isEmailVerified()){
            throw new InvalidCredentialsException("Email is not verified");
        }


        String token = jwtUtil.generateToken(emailRequest.getId());
          AuthResponse response = ReturnAuthResponse(emailRequest);
          response.setToken(token);
          return response;
    }


    public void ResendEmail(String email) {
      User userverify = userRepo.findByEmail(email).orElseThrow(()-> new RuntimeException("Email not found"));

      if(userverify.isEmailVerified()){
          throw new InvalidCredentialsException("Email is already verified");
      }
      userverify.setVerificationToken(UUID.randomUUID().toString());
      userverify.setVerificationExpires(LocalDateTime.now().plusHours(24));
      userRepo.save(userverify);
      sendVerificationMail(userverify);
    }

    public AuthResponse getProfile(Object principalObject) {
        User existUser =  (User) principalObject;
        return ReturnAuthResponse(existUser);
    }
}
