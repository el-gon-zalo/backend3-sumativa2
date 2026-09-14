package cl.translog.bff.cajero.service;

import cl.translog.bff.cajero.client.CoreCuentaAnualClient;
import cl.translog.bff.cajero.dto.CuentaAnualDto;
import cl.translog.bff.cajero.dto.CuentaAnualCajeroDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CajeroCuentaAnualService {

    private final CoreCuentaAnualClient coreClient;

    public CajeroCuentaAnualService(CoreCuentaAnualClient coreClient) {
        this.coreClient = coreClient;
    }

    public CuentaAnualCajeroDto obtenerPorId(Long id) {
        CuentaAnualDto dto = coreClient.cuentaAnual(id);
        return toCajeroDto(dto);
    }

    public List<CuentaAnualCajeroDto> obtenerPorCuenta(Integer cuentaId) {
        return coreClient.porCuenta(cuentaId).stream()
                .map(this::toCajeroDto)
                .toList();
    }

    public List<CuentaAnualCajeroDto> obtenerTodas() {
        return coreClient.todas().stream()
                .map(this::toCajeroDto)
                .toList();
    }

    private CuentaAnualCajeroDto toCajeroDto(CuentaAnualDto dto) {
        return new CuentaAnualCajeroDto(
                dto.id(),
                dto.cuentaId(),
                dto.fecha()
        );
    }
}
