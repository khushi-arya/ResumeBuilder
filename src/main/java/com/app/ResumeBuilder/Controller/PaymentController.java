package com.app.ResumeBuilder.Controller;

import com.app.ResumeBuilder.DTO.Payment;
import com.app.ResumeBuilder.Service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.app.ResumeBuilder.Util.AppConstant.Premium;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/payment")
@Slf4j
public class PaymentController {
    @Autowired
    private final PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String,String> request , Authentication authentication) throws RazorpayException {
        String planType = request.get("planType");
        if(!Premium.equalsIgnoreCase(planType)){
            return ResponseEntity.badRequest().build();
        }
       Payment payment=  paymentService.createPayment(authentication.getPrincipal(),planType);
        Map<String,Object> response = Map.of(
                "orderId",payment.getRazorpayOrderId(),
                "amount", payment.getAmount(),
                "courrency", payment.getCurrency(),
                "recipient",payment.getRecipient()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verfiyPayment(@RequestBody Map<String,String> request ) throws RazorpayException {
        String razorpayOrderId = request.get("razorpayOrderId");
        String paymentId = request.get("paymentId");
        String razorpaySignature = request.get("razorpaySignature");

        if (Objects.isNull(razorpayOrderId) ||
                Objects.isNull(razorpaySignature) ||
                Objects.isNull(paymentId)) {
            return ResponseEntity.badRequest().build();

        }

      boolean value =   paymentService.verfiyPayment(razorpaySignature,paymentId,razorpayOrderId);
      if(!value){
          return ResponseEntity.badRequest().build();
      }else {
          return ResponseEntity.ok().build();
      }


    }

    @GetMapping("/history")
    public ResponseEntity<?> getPaymentHistory(Authentication authentication) {
        List< Payment> paymentList=     paymentService.getUserPayment(authentication.getPrincipal());
        return ResponseEntity.ok(paymentList);
    }

    @GetMapping("/order/{orderid}")
    public ResponseEntity<?> getOrderDetail(@PathVariable String orderid) {
     Payment paymentDetails =  paymentService.getOrderDetail(orderid);
     return ResponseEntity.ok(paymentDetails);
    }

}
