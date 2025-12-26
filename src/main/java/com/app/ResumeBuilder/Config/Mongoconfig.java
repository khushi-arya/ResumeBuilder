package com.app.ResumeBuilder.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class Mongoconfig {
}
//automatically fill fields like:
//@CreatedDate
//private LocalDateTime createdAt;
//
//@LastModifiedDate
//private LocalDateTime updatedAt;
