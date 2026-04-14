package service;

import entity.Sale;
import entity.SaleStatus;
import entity.User;
import entity.Event;
import entity.EventZone;
import repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;

    public String processReservation(User customer, Event event, String zoneName, int quantity) {
        if (quantity > 10) {
            return "ERROR: No more than 10 tickets allowed per customer.";
        }

        if (event == null || event.getZones() == null) {
            return "ERROR: Invalid event.";
        }

        EventZone zone = null;
        for (EventZone z : event.getZones()) {
            if (z.getZoneName().equals(zoneName)) {
                zone = z;
                break;
            }
        }

        if (zone == null) {
            return "ERROR: Zone not found.";
        }

        if (zone.getAvailableTickets() < quantity) {
            return "ERROR: Not enough tickets available.";
        }

        zone.setAvailableTickets(zone.getAvailableTickets() - quantity);

        Sale sale = new Sale(
                UUID.randomUUID().toString(),
                customer,
                SaleStatus.RESERVED,
                LocalDateTime.now(),
                quantity,
                zoneName
        );

        saleRepository.save(sale);
        return "SUCCESS: Reservation created - " + sale.getId();
    }
}