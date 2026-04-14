package entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User customer;

    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    private LocalDateTime reservationDate;

    private int quantity;

    private String zoneName;
}