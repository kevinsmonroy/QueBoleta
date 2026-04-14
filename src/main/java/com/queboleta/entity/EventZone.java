package com.queboleta.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String zoneName;
    private double price;
    private int totalCapacity;

    private int availableTickets;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}