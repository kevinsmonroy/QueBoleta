package service;

import entity.SaleStatus;
import entity.Sale;
import repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpirationService {
    private final SaleRepository saleRepository;

    public void processExpirations() {
        LocalDateTime limit = LocalDateTime.now().minusHours(24);
        List<Sale> activeSales = saleRepository.findAll();

        for (Sale sale : activeSales) {
            if (sale.getStatus() == SaleStatus.RESERVED && sale.getReservationDate().isBefore(limit)) {
                sale.setStatus(SaleStatus.EXPIRED);
                saleRepository.save(sale);
            }
        }
    }
}