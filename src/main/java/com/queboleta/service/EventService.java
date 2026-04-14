package com.queboleta.service;

import com.queboleta.entity.Event;
import com.queboleta.entity.EventZone;
import com.queboleta.repository.EventRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /** RQ-01 */
    public String registerEvent(Event newEvent) {
        int totalTickets = 0;
        if (newEvent.getZones() != null) {
            for (EventZone zone : newEvent.getZones()) {
                totalTickets += zone.getTotalCapacity();
            }
        }
        if (totalTickets > 10000) {
            return "Error: La capacidad total no puede exceder 10,000 boletas.";
        }
        eventRepository.save(newEvent);
        return "Evento '" + newEvent.getName() + "' registrado exitosamente.";
    }

    /** RQ-05 */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}