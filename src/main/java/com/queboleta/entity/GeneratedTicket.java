package com.queboleta.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "generated_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uniqueCode;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;
}