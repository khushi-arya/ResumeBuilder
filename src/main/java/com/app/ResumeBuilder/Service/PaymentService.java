package com.app.ResumeBuilder.Service;

import com.app.ResumeBuilder.DTO.AuthResponse;
import com.app.ResumeBuilder.DTO.Payment;
import com.app.ResumeBuilder.Model.User;
import com.app.ResumeBuilder.Repo.PaymentRepo;
import com.app.ResumeBuilder.Repo.UserRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.app.ResumeBuilder.Util.AppConstant.Premium;

@Service
public class PaymentService {
    @Autowired
  private PaymentRepo paymentRepo;
    @Autowired
    private AuthService authService;
    private RazorpayClient razorpayClient;
  @Value("${rozorpay.key.id}")
  private String razorpayKeyId;
    @Value("${rozorpay.key.secrete}")
  private String razorpaySecret;
    @Autowired
    private UserRepo userRepo;


    public Payment createPayment(Object principal, String planType) throws RazorpayException {
        AuthResponse response= authService.getProfile(principal);

        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId,razorpaySecret);
        int amount = 999;
        String currencyCode = "INR";
        String receipt =Premium + "_"+ UUID.randomUUID().toString().substring(0, 8);

        JSONObject razorpayPayment = new JSONObject();

        razorpayPayment.put("amount", amount);
        razorpayPayment.put("currency", currencyCode);
        razorpayPayment.put("receipt", receipt);

        Order razorpayOrder = razorpayClient.orders.create(razorpayPayment);


        // Step 4: Save the order details into database
        Payment payment = Payment.builder()
                .userId(response.getId())
                .RazorpayOrderId(razorpayOrder.get("id"))
                .Amount(amount)
                .Currency(currencyCode)
                .planType(planType)
                .Status("created")
                .recipient(receipt)
                .build();

        return paymentRepo.save(payment);
    }

    public boolean verfiyPayment(String razorpaySignature, String paymentId, String razorpayOrderId) throws RazorpayException {

        try{
            JSONObject attribute = new JSONObject();
            attribute.put("razorpay_order_id", razorpayOrderId);
            attribute.put("razorpay_payment_id", paymentId);
            attribute.put("razorpay_signature", razorpaySignature);


            boolean isValid = Utils.verifyPaymentSignature(attribute,razorpaySecret);
            if(isValid){
                Payment payment= paymentRepo.findByRazorpayOrderId(razorpayOrderId).orElseThrow(()->new RazorpayException("Payment not found"));
                payment.setRazorpayPaymentId(paymentId);
                payment.setRazorpaySignature(razorpaySignature);
                payment.setStatus("paid");
                paymentRepo.save(payment);


                upgradeUserSubcription(payment.getUserId() , payment.getPlanType());
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    private void upgradeUserSubcription(String userId, String planType) {
        User user = userRepo.findById(userId).orElseThrow(()->new UsernameNotFoundException("Not found"));
        user.setSubcriptionplan(planType);
        userRepo.save(user);
    }

    public List<Payment> getUserPayment(Object principal) {
       AuthResponse response =  authService.getProfile(principal);
       paymentRepo.findByUserIdOrderByCreatedAtDesc(response.getId());
       return paymentRepo.findByUserIdOrderByCreatedAtDesc(response.getId());
    }

    public Payment getOrderDetail(String orderid) {
        Payment result = paymentRepo.findByRazorpayPaymentId(orderid).orElseThrow(()->new RuntimeException("Payment not found"));
        return result;
    }
}
