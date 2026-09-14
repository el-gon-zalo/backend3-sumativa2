package cl.translog.bff.web.service;

import cl.translog.bff.web.client.CoreTransaccionClient;
import cl.translog.bff.web.dto.TransaccionWebDto;
import cl.translog.bff.web.dto.TransaccionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebTransaccionService {

    private final CoreTransaccionClient coreClient;

    public WebTransaccionService(CoreTransaccionClient coreClient) {
        this.coreClient = coreClient;
    }

    public TransaccionWebDto obtenerPorId(Long id) {
        TransaccionDto dto = coreClient.transaccion(id);
        return toWebDto(dto);
    }

    public List<TransaccionWebDto> obtenerTodas() {
        return coreClient.todas().stream()
                .map(this::toWebDto)
                .toList();
    }


    private TransaccionWebDto toWebDto(TransaccionDto dto) {
        return new TransaccionWebDto(
                dto.id(),
                dto.fecha(),
                dto.monto(),
                dto.tipo(),
                dto.estado()
        );
    }
}
