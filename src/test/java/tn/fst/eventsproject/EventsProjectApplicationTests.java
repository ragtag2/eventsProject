package tn.fst.eventsproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.fst.eventsproject.entities.Event;
import tn.fst.eventsproject.entities.Logistics;
import tn.fst.eventsproject.entities.Participant;
import tn.fst.eventsproject.entities.Tache;
import tn.fst.eventsproject.repositories.EventRepository;
import tn.fst.eventsproject.repositories.LogisticsRepository;
import tn.fst.eventsproject.repositories.ParticipantRepository;
import tn.fst.eventsproject.services.EventServicesImpl;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServicesImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    private Participant participant;
    private Event event;
    private Logistics logistics;

    @BeforeEach
    void setUp() {
        participant = new Participant();
        participant.setIdPart(1);
        participant.setEvents(new HashSet<>());

        event = new Event();
        event.setDescription("Test Event");
        event.setParticipants(new HashSet<>());
        event.setLogistics(new HashSet<>());

        logistics = new Logistics();
        logistics.setReserve(true);
        logistics.setPrixUnit(10);
        logistics.setQuantite(2);
    }

    // ------------------------------------------------------------
    @Test
    void testAddParticipant() {
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant saved = eventServices.addParticipant(participant);

        assertEquals(participant, saved);
        verify(participantRepository).save(participant);
    }

    // ------------------------------------------------------------
    @Test
    void testAddAffectEvenParticipant_ById() {
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventServices.addAffectEvenParticipant(event, 1);

        assertTrue(participant.getEvents().contains(event));
        verify(eventRepository).save(event);
    }

    // -----------------------------------s-------------------------
    @Test
    void testAddAffectEvenParticipant_FromEventObject() {
        Participant p2 = new Participant();
        p2.setIdPart(2);
        event.getParticipants().add(p2);

        when(participantRepository.findById(2)).thenReturn(Optional.of(p2));
        when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventServices.addAffectEvenParticipant(event);

        assertTrue(p2.getEvents().contains(event));
        verify(eventRepository).save(event);
    }

    // ------------------------------------------------------------
    @Test
    void testAddAffectLog() {
        Event existingEvent = new Event();
        existingEvent.setDescription("Test Event");
        existingEvent.setLogistics(new HashSet<>());

        when(eventRepository.findByDescription("Test Event")).thenReturn(existingEvent);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics saved = eventServices.addAffectLog(logistics, "Test Event");

        assertTrue(existingEvent.getLogistics().contains(logistics));
        verify(logisticsRepository).save(logistics);
    }

    // ------------------------------------------------------------
    @Test
    void testGetLogisticsDates() {
        event.getLogistics().add(logistics);

        when(eventRepository.findByDateDebutBetween(any(), any()))
                .thenReturn(List.of(event));

        List<Logistics> result = eventServices.getLogisticsDates(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        assertEquals(1, result.size());
        assertTrue(result.contains(logistics));
    }

    // ------------------------------------------------------------
    @Test
    void testCalculCout() {
        event.getLogistics().add(logistics); // 10 * 2 = 20

        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache(
                "Tounsi", "Ahmed", Tache.ORGANISATEUR
        )).thenReturn(List.of(event));

        eventServices.calculCout();
S
        assertEquals(20, event.getCout());
        verify(eventRepository).save(event);
    }
}
