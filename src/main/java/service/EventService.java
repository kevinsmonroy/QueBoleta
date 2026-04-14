package service;

import entity.Event;
import entity.EventZone;
import repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public String registerEvent(Event newEvent) {
        int totalTickets = 0;

        if (newEvent.getZones() != null) {
            for (EventZone zone : newEvent.getZones()) {
                totalTickets += zone.getTotalCapacity();
            }
        }

        // RF-01: Max 10,000 tickets
        if (totalTickets > 10000) {
            return "Error: La capacidad total no puede exceder 10,000 tickets.";
        }

        eventRepository.save(newEvent);
        return "Evento '" + newEvent.getName() + "' registrado exitosamente.";
    }
}