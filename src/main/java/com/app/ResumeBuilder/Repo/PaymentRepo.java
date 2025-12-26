package com.app.ResumeBuilder.Repo;

import com.app.ResumeBuilder.DTO.Payment;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends MongoRepository<Payment,String> {
   Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId );
    List<Payment> findByUserIdOrderByCreatedAtDesc(String userid);
    List<Payment> findByStatus(String status);
}
