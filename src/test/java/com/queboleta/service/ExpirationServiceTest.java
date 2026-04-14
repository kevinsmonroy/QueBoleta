package com.queboleta.service;

import com.queboleta.entity.Sale;
import com.queboleta.entity.SaleStatus;
import com.queboleta.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ExpirationServiceTest {

    @Mock
    private SaleRepository saleRepository;

    private ExpirationService expirationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        expirationService = new ExpirationService(saleRepository);
    }

    @Test
    void testProcessSuccessfulExpiration() {
        Sale oldReservation = new Sale();
        oldReservation.setId("EXP-001");
        oldReservation.setStatus(SaleStatus.RESERVED);
        oldReservation.setReservationDate(LocalDateTime.now().minusHours(25));

        when(saleRepository.findAll()).thenReturn(Arrays.asList(oldReservation));

        expirationService.processExpirations();

        assertEquals(SaleStatus.EXPIRED, oldReservation.getStatus(),
                "Reservation should have changed to EXPIRED due to expired time");
        verify(saleRepository, times(1)).save(oldReservation);
    }

    @Test
    void testDoNotExpireRecentReservations() {
        Sale recentReservation = new Sale();
        recentReservation.setId("NEW-001");
        recentReservation.setStatus(SaleStatus.RESERVED);
        recentReservation.setReservationDate(LocalDateTime.now().minusMinutes(10));

        when(saleRepository.findAll()).thenReturn(Arrays.asList(recentReservation));

        expirationService.processExpirations();

        assertEquals(SaleStatus.RESERVED, recentReservation.getStatus(),
                "Recent reservation should NOT have expired");
        verify(saleRepository, never()).save(recentReservation);
    }
}