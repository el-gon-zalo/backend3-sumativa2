package cl.translog.bff.web.service;

import cl.translog.bff.web.client.CoreInteresClient;
import cl.translog.bff.web.dto.InteresWebDto;
import cl.translog.bff.web.dto.InteresDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebInteresService {

    private final CoreInteresClient coreClient;

    public WebInteresService(CoreInteresClient coreClient) {
        this.coreClient = coreClient;
    }

    public InteresWebDto obtenerPorId(Long id) {
        InteresDto dto = coreClient.interes(id);
        return toWebDto(dto);
    }

    public List<InteresWebDto> obtenerPorCuenta(Integer cuentaId) {
        return coreClient.porCuenta(cuentaId).stream()
                .map(this::toWebDto)
                .toList();
    }

    public List<InteresWebDto> obtenerTodos() {
        return coreClient.todos().stream()
                .map(this::toWebDto)
                .toList();
    }


    private InteresWebDto toWebDto(InteresDto dto) {
        return new InteresWebDto(
                dto.id(),
                dto.cuentaId(),
                dto.nombre(),
                dto.saldoOriginal(),
                dto.edad(),
                dto.tipo(),
                dto.interesAplicado(),
                dto.saldoFinal()
        );
    }
}
