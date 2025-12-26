package com.app.ResumeBuilder.Controller;

import com.app.ResumeBuilder.DTO.CreateResumeRequest;
import com.app.ResumeBuilder.Model.Resume;
import com.app.ResumeBuilder.Service.FileService;
import com.app.ResumeBuilder.Service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.app.ResumeBuilder.Util.AppConstant.*;

@RestController
@RequestMapping(RESUME)
@RequiredArgsConstructor
@Slf4j
public class ResumeController {
    @Autowired
    private final ResumeService resumeService;
    @Autowired
    private final FileService fileService;
    @PostMapping
    public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest createResumeRequest, Authentication authentication) {
      Resume newresume = resumeService.createResume(createResumeRequest, authentication.getPrincipal());
      return ResponseEntity.status(HttpStatus.CREATED).body(newresume);
    }

    @GetMapping
    public ResponseEntity<?> getUserResume(Authentication authentication) {
      List<Resume> resumes =  resumeService.getUserResume(authentication.getPrincipal());
      return ResponseEntity.status(HttpStatus.OK).body(resumes);
    }

    @GetMapping(ID)
    public ResponseEntity<?> getResumeById(@PathVariable String id ,Authentication authentication) {
    Resume existResume = resumeService.getResumeById(id , authentication.getPrincipal());
     return ResponseEntity.status(HttpStatus.OK).body(existResume);
    }

    @PutMapping(ID)
    public ResponseEntity<?> updateResume(@PathVariable String id,@RequestBody Resume updateData,Authentication authentication) {
       Resume updateResume =  resumeService.updateResume(id , updateData , authentication.getPrincipal());
       return ResponseEntity.ok().body(updateResume);

    }

    @PutMapping(UPLOAD_image)
    public ResponseEntity<?> createResume(@PathVariable String id ,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "data") CreateResumeRequest request , Authentication authentication
    ) throws IOException {

        Map<String,String> response = fileService.uploadResumeImage(id,authentication.getPrincipal() ,thumbnail ,profileImage);

        // method body
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(ID)
    public ResponseEntity<?> deleteResume(@PathVariable String id,Authentication authentication) {
      resumeService.deleteResume(id,authentication.getPrincipal());
      return ResponseEntity.status(HttpStatus.OK).body("Resume has been deleted");
    }
 }

