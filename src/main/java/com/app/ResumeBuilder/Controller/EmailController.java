package com.app.ResumeBuilder.Controller;

import com.app.ResumeBuilder.Service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
@Slf4j
public class EmailController {
    @Autowired
    private final EmailService emailService;

    @PostMapping(value = "/send-resume" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestPart("recipientEmail") String recipientEmail, @RequestPart("subject") String subject, @RequestPart("message") String message,
    @RequestPart("pdfFile")MultipartFile pdfFile , Authentication authentication) throws IOException, MessagingException {
        Map<String, Object> response = new HashMap<>();
        if(Objects.isNull(recipientEmail)|| Objects.isNull(pdfFile)) {
            response.put("message", "Please fill all the fields");
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }
        byte[] pdfbyte = pdfFile.getBytes();
        String originalFilename = pdfFile.getOriginalFilename();
         String filename = Objects.nonNull(originalFilename)?originalFilename :"Resume.pdf";
       String emailsubject =   Objects.nonNull(subject)? subject:"Resume Application";
       String emailBody = Objects.nonNull(message)?message:"Please fill all the fields";
       emailService.sendEmailwithAttachement(recipientEmail , emailsubject, emailBody, pdfbyte, filename);
       return ResponseEntity.ok(response);
    }
}
