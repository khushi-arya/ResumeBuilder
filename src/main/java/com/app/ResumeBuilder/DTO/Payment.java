package com.app.ResumeBuilder.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "Payment")
public class Payment {
    private String id;
    private String userId;
    private String RazorpayOrderId;
    private String RazorpayPaymentId;
    private String RazorpaySignature;
    private Integer Amount;
    private String planType;
    private String Currency;
    @Builder.Default
    private String Status = "Created";
    private String recipient;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
