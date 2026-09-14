package cl.translog.bff.mobile.service;

import cl.translog.bff.mobile.client.CoreCuentaAnualClient;
import cl.translog.bff.mobile.dto.CuentaAnualDto;
import cl.translog.bff.mobile.dto.CuentaAnualMobileDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobileCuentaAnualService {

    private final CoreCuentaAnualClient coreClient;

    public MobileCuentaAnualService(CoreCuentaAnualClient coreClient) {
        this.coreClient = coreClient;
    }

    public CuentaAnualMobileDto obtenerPorId(Long id) {
        CuentaAnualDto dto = coreClient.cuentaAnual(id);
        return toMobileDto(dto);
    }

    public List<CuentaAnualMobileDto> obtenerPorCuenta(Integer cuentaId) {
        return coreClient.porCuenta(cuentaId).stream()
                .map(this::toMobileDto)
                .toList();
    }

    public List<CuentaAnualMobileDto> obtenerTodas() {
        return coreClient.todas().stream()
                .map(this::toMobileDto)
                .toList();
    }

    private CuentaAnualMobileDto toMobileDto(CuentaAnualDto dto) {
        return new CuentaAnualMobileDto(
                dto.id(),
                dto.cuentaId(),
                dto.fecha(),
                dto.transaccion()
        );
    }
}
