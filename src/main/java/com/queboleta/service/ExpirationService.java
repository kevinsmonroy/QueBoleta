package com.queboleta.service;

import com.queboleta.entity.Sale;
import com.queboleta.entity.SaleStatus;
import com.queboleta.repository.SaleRepository;
import com.queboleta.util.Configuracion;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExpirationService {

    private final SaleRepository saleRepository;
    private final Configuracion configuracion;

    public ExpirationService(SaleRepository saleRepository, Configuracion configuracion) {
        this.saleRepository = saleRepository;
        this.configuracion = configuracion;
    }

    public void processExpirations() {
        int horas = configuracion.getTiempoReservaHoras();
        LocalDateTime limit = LocalDateTime.now().minusHours(horas);
        List<Sale> all = saleRepository.findAll();
        for (Sale sale : all) {
            if (sale.getStatus() == SaleStatus.RESERVED
                    && sale.getReservationDate().isBefore(limit)) {
                sale.setStatus(SaleStatus.EXPIRED);
                saleRepository.save(sale);
            }
        }
    }
}