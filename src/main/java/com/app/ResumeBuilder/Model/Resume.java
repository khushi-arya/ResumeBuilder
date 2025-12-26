package com.app.ResumeBuilder.Model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Slf4j
@Document(collection = "RESUME")
public class Resume {
    @Id
    @JsonProperty("_id")
    private String id;
    @Field("userid")
    private String userid;
    private String title;
    private String thumbnailLink;
    private Template template;
    private ProfileInfo profileInfo;
    private ContactInfo contactInfo;
    private List<WorkExperience> workExperience;
    private List<Education> education;
    private List<Skill> skill;
    private List<Project> project;
    private List<Certification> certification;
    private List<Languages> languages;
    private List<String> interests;
    @CreatedDate
    private LocalDateTime  createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Template{
        private String theme;
        private List<String> colourPalette;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfileInfo{
        private String profilePreviewUrl;
        private String fullName;
        private String designation;
        private String summary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContactInfo{
        private String email;
        private String phone;
        private String location;
        private String linkedin;
        private String github;
        private String website;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkExperience{
        private String company;
        private String role;
        private String startDate;
        private String endDate;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Education{
        private String degree;
        private String institution;
        private String startDate;
        private String endDate;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Skill{
        private String skillName;
        private Integer progress;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Project{
        private String projectName;
        private String description;
        private String github;
        private String liveDemo;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Certification{
        private String title;
        private String issuer;
        private String year;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Languages{
        private String name;
        private Integer progress;
    }

}
