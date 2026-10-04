package com.utown.utownbackend.service;

import com.utown.utownbackend.dto.PaymentResult;
import com.utown.utownbackend.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class MockPaymentGatewayImpl implements PaymentGateway {

    @Override
    public PaymentResult charge(Order order, BigDecimal amount) {
        log.info("MockPaymentGateway: processing charge of {} for order {}", amount, order.getId());
        
        // Simulate an occasional failure for large orders in tests or based on some condition
        // Here, we'll simply assume success for the mock unless we want to simulate failure (e.g., if totalAmount is exactly 9999.99)
        if (amount.compareTo(new BigDecimal("9999.99")) == 0) {
            log.warn("MockPaymentGateway: Charge failed for order {}", order.getId());
            return new PaymentResult(false, null, "Insufficient funds");
        }
        
        String transactionId = "txn_mock_" + UUID.randomUUID().toString();
        log.info("MockPaymentGateway: Charge successful for order {}. Transaction ID: {}", order.getId(), transactionId);
        return new PaymentResult(true, transactionId, "Success");
    }

    @Override
    public PaymentResult refund(Order order, String transactionId) {
        log.info("MockPaymentGateway: processing refund for transaction {} of order {}", transactionId, order.getId());
        
        if (transactionId == null || transactionId.isEmpty()) {
            return new PaymentResult(false, null, "No transaction ID provided");
        }
        
        String refundTxnId = "ref_mock_" + UUID.randomUUID().toString();
        log.info("MockPaymentGateway: Refund successful for order {}. Refund Transaction ID: {}", order.getId(), refundTxnId);
        return new PaymentResult(true, refundTxnId, "Refund Success");
    }
}
