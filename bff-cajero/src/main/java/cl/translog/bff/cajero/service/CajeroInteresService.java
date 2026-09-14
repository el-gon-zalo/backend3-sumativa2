package cl.translog.bff.cajero.service;

import cl.translog.bff.cajero.client.CoreInteresClient;
import cl.translog.bff.cajero.dto.InteresCajeroDto;
import cl.translog.bff.cajero.dto.InteresDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CajeroInteresService {

    private final CoreInteresClient coreClient;

    public CajeroInteresService(CoreInteresClient coreClient) {
        this.coreClient = coreClient;
    }

    public InteresCajeroDto obtenerPorId(Long id) {
        InteresDto dto = coreClient.interes(id);
        return toCajeroDto(dto);
    }

    public List<InteresCajeroDto> obtenerPorCuenta(Integer cuentaId) {
        return coreClient.porCuenta(cuentaId).stream()
                .map(this::toCajeroDto)
                .toList();
    }

    public List<InteresCajeroDto> obtenerTodos() {
        return coreClient.todos().stream()
                .map(this::toCajeroDto)
                .toList();
    }


    private InteresCajeroDto toCajeroDto(InteresDto dto) {
        return new InteresCajeroDto(
                dto.id(),
                dto.cuentaId(),
                dto.nombre(),
                dto.saldoOriginal()
        );
    }
}
