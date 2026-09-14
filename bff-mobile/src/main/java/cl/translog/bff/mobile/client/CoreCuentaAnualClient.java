package cl.translog.bff.mobile.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import cl.translog.bff.mobile.dto.CuentaAnualDto;

@Component
public class CoreCuentaAnualClient {

    private final RestClient restClient;

    public CoreCuentaAnualClient(RestClient cuentaAnualRestClient) {
        this.restClient = cuentaAnualRestClient;
    }

    public CuentaAnualDto cuentaAnual(Long id) {
        return restClient.get()
                .uri("/api/cuentas-anuales/{id}", id)
                .retrieve()
                .body(CuentaAnualDto.class);
    }

    public List<CuentaAnualDto> porCuenta(Integer cuentaId) {
        return restClient.get()
                .uri("/api/cuentas-anuales/cuenta/{cuentaId}", cuentaId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<CuentaAnualDto> todas() {
        return restClient.get()
                .uri("/api/cuentas-anuales/todas")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}