package cl.translog.bff.mobile.service;

import cl.translog.bff.mobile.client.CoreInteresClient;
import cl.translog.bff.mobile.dto.InteresMobileDto;
import cl.translog.bff.mobile.dto.InteresDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobileInteresService {

    private final CoreInteresClient coreClient;

    public MobileInteresService(CoreInteresClient coreClient) {
        this.coreClient = coreClient;
    }

    public InteresMobileDto obtenerPorId(Long id) {
        InteresDto dto = coreClient.interes(id);
        return toMobileDto(dto);
    }

    public List<InteresMobileDto> obtenerPorCuenta(Integer cuentaId) {
        return coreClient.porCuenta(cuentaId).stream()
                .map(this::toMobileDto)
                .toList();
    }

    public List<InteresMobileDto> obtenerTodos() {
        return coreClient.todos().stream()
                .map(this::toMobileDto)
                .toList();
    }


    private InteresMobileDto toMobileDto(InteresDto dto) {
        return new InteresMobileDto(
                dto.id(),
                dto.cuentaId(),
                dto.nombre(),
                dto.saldoOriginal(),
                dto.tipo()
        );
    }
}
