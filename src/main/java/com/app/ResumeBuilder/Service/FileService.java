package com.app.ResumeBuilder.Service;

import com.app.ResumeBuilder.DTO.AuthResponse;
import com.app.ResumeBuilder.Model.Resume;
import com.app.ResumeBuilder.Repo.ResumeRepo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileService {
    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepo resumeRepo;

    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        Map<String, Object> result =
                cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap("resource_type", "image")
                );

        return Map.of(
                "imageURL", result.get("secure_url").toString()
        );
    }


    public Map<String, String> uploadResumeImage(String id, Object principal, MultipartFile thumbnail, MultipartFile profileImage) throws IOException {
        AuthResponse response =  authService.getProfile(principal);
       Resume existResume =  resumeRepo.findByUseridAndId(response.getId(),id).orElseThrow(()->new RuntimeException("Resume not found "));

        Map<String,String> returnValue = new HashMap<>();
        Map<String, String> result ;

        if(Objects.nonNull(thumbnail)) {
            result = uploadImage(thumbnail);
            existResume.setThumbnailLink(result.get("imageURl"));
            returnValue.put("thumbnailLink", result.get("imageURL"));
        }

        if(Objects.nonNull(profileImage)) {
            result = uploadImage(profileImage);
            if(Objects.isNull(existResume.getProfileInfo())){
                existResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existResume.getProfileInfo().setProfilePreviewUrl(result.get("imageURL"));
            returnValue.put("profilePrevieUrl", result.get("imageURL"));

        }

         resumeRepo.save(existResume);
        returnValue.put("message"," ImageUploaded Successfully");


        return returnValue;
    }
}
