package com.trilabs94.ecm_payment.controller;

import com.trilabs94.ecm_payment.dto.PaymentResponse;
import com.trilabs94.ecm_payment.dto.RetryPaymentRequest;
import com.trilabs94.ecm_payment.service.impl.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Schema(
        name = "Payment Controller",
        description = "APIs for managing payments"
)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Get Payment by Reference",
            description = "Retrieve payment details using the payment reference."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @GetMapping("/{reference}")
    public ResponseEntity<PaymentResponse> getPaymentByReference(
            @PathVariable String reference
    ) {
        PaymentResponse response = paymentService.getPaymentByReference(reference);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get Payments by Order ID",
            description = "Retrieve all payments associated with a specific order ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No payments found for the given order ID"
            )
    })
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(
            @PathVariable Long orderId
    ) {
        List<PaymentResponse> responses = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Retry Payment",
            description = "Retry a failed payment using its reference."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment retried successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or payment cannot be retried"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @PostMapping("/{reference}/retry")
    public ResponseEntity<PaymentResponse> retryPayment(
            @PathVariable String reference,
            @RequestBody(required = false) RetryPaymentRequest request
    ) {
        PaymentResponse response = paymentService.retryPayment(reference, request.getReason());
        return ResponseEntity.ok(response);
    }
}
