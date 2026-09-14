package cl.translog.bff.mobile.service;

import cl.translog.bff.mobile.client.CoreTransaccionClient;
import cl.translog.bff.mobile.dto.TransaccionMobileDto;
import cl.translog.bff.mobile.dto.TransaccionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobileTransaccionService {

    private final CoreTransaccionClient coreClient;

    public MobileTransaccionService(CoreTransaccionClient coreClient) {
        this.coreClient = coreClient;
    }

    public TransaccionMobileDto obtenerPorId(Long id) {
        TransaccionDto dto = coreClient.transaccion(id);
        return toMobileDto(dto);
    }

    public List<TransaccionMobileDto> obtenerTodas() {
        return coreClient.todas().stream()
                .map(this::toMobileDto)
                .toList();
    }


    private TransaccionMobileDto toMobileDto(TransaccionDto dto) {
        return new TransaccionMobileDto(
                dto.id(),
                dto.fecha(),
                dto.monto(),
                dto.tipo()
        );
    }
}
