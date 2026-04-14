package service;

import entity.Event;
import entity.EventZone;
import org.junit.jupiter.api.Test;
import repository.EventRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    @Test
    public void testValidateExcessTickets() {
        EventRepository mockRepo = mock(EventRepository.class);
        EventService service = new EventService(mockRepo);

        EventZone largeZone = new EventZone(null, "General", 50.0, 11000, 11000, null);
        Event event = new Event(null, "Metal Fest", null, null, "Bogota", List.of(largeZone));

        String result = service.registerEvent(event);

        assertTrue(result.contains("Error"), "Should fail for exceeding 10,000 tickets");
        verify(mockRepo, times(0)).save(any());
    }
}