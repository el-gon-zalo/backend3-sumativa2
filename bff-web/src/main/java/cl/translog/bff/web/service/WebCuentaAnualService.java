package cl.translog.bff.web.service;

import cl.translog.bff.web.client.CoreCuentaAnualClient;
import cl.translog.bff.web.dto.CuentaAnualDto;
import cl.translog.bff.web.dto.CuentaAnualWebDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebCuentaAnualService {

    private final CoreCuentaAnualClient coreClient;

    public WebCuentaAnualService(CoreCuentaAnualClient coreClient) {
        this.coreClient = coreClient;
    }

    public CuentaAnualWebDto obtenerPorId(Long id) {
        CuentaAnualDto dto = coreClient.cuentaAnual(id);
        return toWebDto(dto);
    }

    public List<CuentaAnualWebDto> obtenerPorCuenta(Integer cuentaId) {
        return coreClient.porCuenta(cuentaId).stream()
                .map(this::toWebDto)
                .toList();
    }

    public List<CuentaAnualWebDto> obtenerTodas() {
        return coreClient.todas().stream()
                .map(this::toWebDto)
                .toList();
    }

    private CuentaAnualWebDto toWebDto(CuentaAnualDto dto) {
        return new CuentaAnualWebDto(
                dto.id(),
                dto.cuentaId(),
                dto.fecha(),
                dto.transaccion(),
                dto.monto(),
                dto.descripcion()
        );
    }
}
