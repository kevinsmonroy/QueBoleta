package service;

import entity.Payment;
import entity.Sale;
import entity.SaleStatus;
import repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final SaleRepository saleRepository;

    @Transactional
    public String processPayment(Sale sale, Payment payment) {
        if (!payment.isApproved()) {
            return "Transaccion declinada.";
        }

        // Actualizamos el estado de la venta
        sale.setStatus(SaleStatus.PAID);
        saleRepository.save(sale);

        return "Pago exitoso, " + sale.getId() + " esta activa.";
    }
}