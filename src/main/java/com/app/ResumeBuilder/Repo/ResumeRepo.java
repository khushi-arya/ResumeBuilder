package com.app.ResumeBuilder.Repo;

import com.app.ResumeBuilder.Model.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepo extends MongoRepository<Resume,String> {

    List<Resume> findByUseridOrderByUpdatedAtDesc(String userid);
    Optional<Resume> findByUseridAndId(String userid, String id);
}
