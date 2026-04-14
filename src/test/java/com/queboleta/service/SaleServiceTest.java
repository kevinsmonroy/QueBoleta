package com.queboleta.service;

import com.queboleta.entity.*;
import com.queboleta.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SaleService saleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMaxTenTicketsRule() {
        User user = new User(1010L, "Kevin", "k@mail.com", "555");
        String result = saleService.processReservation(user, null, "VIP", 11);

        assertEquals("ERROR: No more than 10 tickets allowed per customer.", result);
    }

    @Test
    void testSuccessfulReservation() {
        User user = new User(1010L, "Kevin", "k@mail.com", "555");
        EventZone zone = new EventZone(1L, "VIP", 100.0, 100, 100, null);
        Event event = new Event(1L, "Rock Show", null, null, "Arena", List.of(zone));

        String result = saleService.processReservation(user, event, "VIP", 2);

        assertTrue(result.contains("SUCCESS"));
        assertEquals(98, zone.getAvailableTickets());
        verify(saleRepository, times(1)).save(any(Sale.class));
    }
}