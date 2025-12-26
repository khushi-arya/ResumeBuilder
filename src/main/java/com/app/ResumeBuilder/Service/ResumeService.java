package com.app.ResumeBuilder.Service;

import com.app.ResumeBuilder.DTO.AuthResponse;
import com.app.ResumeBuilder.DTO.CreateResumeRequest;
import com.app.ResumeBuilder.Model.Resume;
import com.app.ResumeBuilder.Repo.ResumeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {
    @Autowired
    private ResumeRepo resumeRepo;
    @Autowired
    private AuthService authService;

    public Resume createResume(CreateResumeRequest createResumeRequest,  Object authentication) {
        Resume newresume = new Resume();

        //get current profile
        AuthResponse response = authService.getProfile(authentication);

        newresume.setUserid(response.getId());
        newresume.setTitle(createResumeRequest.getTitle());
        setDefaultResume(newresume);
        return  resumeRepo.save(newresume);
    }

    private void setDefaultResume(Resume newresume) {
        newresume.setProfileInfo(new Resume.ProfileInfo());
        newresume.setContactInfo(new Resume.ContactInfo());
        newresume.setWorkExperience(new ArrayList<>());
        newresume.setEducation(new ArrayList<>());
        newresume.setSkill(new ArrayList<>());
        newresume.setProject(new ArrayList<>());
        newresume.setCertification(new ArrayList<>());
        newresume.setLanguages(new ArrayList<>());
        newresume.setInterests(new ArrayList<>());
    }

    public List<Resume> getUserResume(Object authentication) {
        AuthResponse response = authService.getProfile(authentication);
        System.out.println("Service - Raw authentication: " + authentication.getClass().getSimpleName());
        System.out.println("Service - Profile ID: '" + (response != null ? response.getId() : "NULL") + "'");

        if (response == null || response.getId() == null) {
            System.out.println("No valid profile found");
            return new ArrayList<>();
        }

        String userId = response.getId();
        System.out.println("Querying repo with userid: '" + userId + "'");

        List<Resume> resumes = resumeRepo.findByUseridOrderByUpdatedAtDesc(userId);
        System.out.println("Repo returned: " + resumes.size() + " resumes");

        return resumes;
    }

    public Resume getResumeById(String resumeid, Object principal) {
      AuthResponse response=  authService.getProfile(principal);
      Resume exitResume =  resumeRepo.findByUseridAndId(response.getId(),resumeid).orElseThrow(()->new RuntimeException("Resume not found"));

      return exitResume;
    }

    public Resume updateResume(String id, Resume updateData, Object principal) {
        AuthResponse response=  authService.getProfile(principal);
       Resume existResume=   resumeRepo.findByUseridAndId(response.getId(),id).orElseThrow(()->new RuntimeException("Resume not found"));

       existResume.setThumbnailLink(updateData.getThumbnailLink());
       existResume.setTitle(updateData.getTitle());
       existResume.setProfileInfo(updateData.getProfileInfo());
       existResume.setTemplate(updateData.getTemplate());
       existResume.setEducation(updateData.getEducation());
       existResume.setSkill(updateData.getSkill());
       existResume.setProject(updateData.getProject());
       existResume.setCertification(updateData.getCertification());
       existResume.setLanguages(updateData.getLanguages());
       existResume.setInterests(updateData.getInterests());
       existResume.setWorkExperience(updateData.getWorkExperience());
       existResume.setLanguages(updateData.getLanguages());
       existResume.setContactInfo(updateData.getContactInfo());
       existResume.setInterests(updateData.getInterests());

       resumeRepo.save(existResume);

       return existResume;


    }

    public void deleteResume(String id, Object principal) {
        AuthResponse response=  authService.getProfile(principal);
        Resume existResume=   resumeRepo.findByUseridAndId(response.getId(),id).orElseThrow(()->new RuntimeException("Resume not found"));
        resumeRepo.delete(existResume);
    }
}
