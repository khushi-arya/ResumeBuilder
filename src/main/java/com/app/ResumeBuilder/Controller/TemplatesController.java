package com.app.ResumeBuilder.Controller;


import com.app.ResumeBuilder.Service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/template")
@Slf4j
public class TemplatesController {
    @Autowired
    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<?> getTemplate(Authentication authentication) {
    Map<String,Object> response = templateService.getTemplate(authentication.getPrincipal());
   return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
