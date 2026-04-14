package com.queboleta.service;

import com.queboleta.entity.*;
import com.queboleta.repository.SaleRepository;
import com.queboleta.repository.UserRepository; // 1. Importa el repo de usuarios
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 2. Para asegurar la integridad

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository; // 3. Agrégalo aquí

    public SaleService(SaleRepository saleRepository, UserRepository userRepository) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository; // 4. Inicialízalo
    }

    /** RQ-02 */
    @Transactional // Recomendado para que si algo falla, no se guarde el usuario a medias
    public String processReservation(User customer, Event event, String zoneName, int quantity) {
        if (quantity > 10)
            return "ERROR: No se permiten más de 10 boletas por compra.";
        if (event == null || event.getZones() == null)
            return "ERROR: Evento inválido.";

        // --- EL CAMBIO CLAVE ESTÁ AQUÍ ---
        // Guardamos o actualizamos el cliente para que Hibernate no saque el error del ID 123
        if (customer != null) {
            userRepository.save(customer);
        }
        // ---------------------------------

        EventZone zone = event.getZones().stream()
                .filter(z -> z.getZoneName().equalsIgnoreCase(zoneName))
                .findFirst().orElse(null);

        if (zone == null)
            return "ERROR: Zona no encontrada.";
        if (zone.getAvailableTickets() < quantity)
            return "ERROR: Solo quedan " + zone.getAvailableTickets() + " boletas en " + zoneName + ".";

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
        return "SUCCESS: Reserva creada — ID: " + sale.getId();
    }

    /** RQ-03: marcar venta como pagada */
    public String reportPayment(Sale sale, double amountPaid, String voucher) {
        sale.setStatus(SaleStatus.PAID);
        saleRepository.save(sale);
        return "Pago registrado. Comprobante: " + voucher;
    }

    /** RQ-04 */
    public List<Sale> getSalesByUser(long identificationNumber) {
        return saleRepository.findAll().stream()
                .filter(s -> s.getCustomer() != null
                        && s.getCustomer().getIdentificationNumber() == identificationNumber)
                .sorted((a, b) -> b.getReservationDate().compareTo(a.getReservationDate()))
                .collect(Collectors.toList());
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
}