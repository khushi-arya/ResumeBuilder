package com.app.ResumeBuilder.Service;

import com.app.ResumeBuilder.DTO.AuthResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.ResumeBuilder.Util.AppConstant.Premium;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {
    @Autowired
    private final AuthService authService;

    public Map<String,Object> getTemplate(Object authentication) {
     AuthResponse response=  authService.getProfile(authentication);
        List<String> availableTemplates;

        Boolean isPremium = Premium.equalsIgnoreCase(response.getSubcriptionPlan());

        if(isPremium){
          availableTemplates = List.of("01","02","03");
        }else {
            availableTemplates= List.of("01");
        }

        Map<String,Object> restriction = new HashMap<>();
        restriction.put("availableTemplates",availableTemplates);
        restriction.put("allTemplate",List.of("01","02","03"));
        restriction.put("SubcriptionPlan",response.getSubcriptionPlan());
        restriction.put("isPremium",isPremium);

        return restriction;



    }

}
