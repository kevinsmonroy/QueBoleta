package com.queboleta.service;

import com.queboleta.entity.Payment;
import com.queboleta.entity.PaymentMethod;
import com.queboleta.entity.Sale;
import com.queboleta.entity.SaleStatus;
import com.queboleta.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeclinedPayment() {
        Sale sale = new Sale("S-001", null, SaleStatus.RESERVED, LocalDateTime.now(), 2, "VIP");
        Payment payment = new Payment(null, PaymentMethod.CREDIT_CARD, 100.0, LocalDateTime.now(), false, sale);

        String result = paymentService.processPayment(sale, payment);

        assertEquals("Transaccion declinada.", result);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void testSuccessfulPayment() {
        Sale sale = new Sale("S-001", null, SaleStatus.RESERVED, LocalDateTime.now(), 2, "VIP");
        Payment payment = new Payment(null, PaymentMethod.CREDIT_CARD, 100.0, LocalDateTime.now(), true, sale);

        String result = paymentService.processPayment(sale, payment);

        assertTrue(result.contains("Pago exitoso"));
        assertEquals(SaleStatus.PAID, sale.getStatus());
        verify(saleRepository, times(1)).save(sale);
    }
}
