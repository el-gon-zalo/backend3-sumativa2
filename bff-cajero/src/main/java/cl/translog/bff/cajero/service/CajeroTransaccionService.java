package cl.translog.bff.cajero.service;

import cl.translog.bff.cajero.client.CoreTransaccionClient;
import cl.translog.bff.cajero.dto.TransaccionCajeroDto;
import cl.translog.bff.cajero.dto.TransaccionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CajeroTransaccionService {

    private final CoreTransaccionClient coreClient;

    public CajeroTransaccionService(CoreTransaccionClient coreClient) {
        this.coreClient = coreClient;
    }

    public TransaccionCajeroDto obtenerPorId(Long id) {
        TransaccionDto dto = coreClient.transaccion(id);
        return toCajeroDto(dto);
    }

    public List<TransaccionCajeroDto> obtenerTodas() {
        return coreClient.todas().stream()
                .map(this::toCajeroDto)
                .toList();
    }


    private TransaccionCajeroDto toCajeroDto(TransaccionDto dto) {
        return new TransaccionCajeroDto(
                dto.id(),
                dto.fecha(),
                dto.monto()
        );
    }
}
