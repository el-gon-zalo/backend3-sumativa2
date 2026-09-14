package cl.translog.bff.mobile.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import cl.translog.bff.mobile.dto.InteresDto;

@Component
public class CoreInteresClient {

    private final RestClient restClient;

    public CoreInteresClient(RestClient interesRestClient) {
        this.restClient = interesRestClient;
    }

    public InteresDto interes(Long id) {
        return restClient.get()
                .uri("/api/intereses/{id}", id)
                .retrieve()
                .body(InteresDto.class);
    }

    public List<InteresDto> porCuenta(Integer cuentaId) {
        return restClient.get()
                .uri("/api/intereses/cuenta/{cuentaId}", cuentaId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<InteresDto> todos() {
        return restClient.get()
                .uri("/api/intereses/todos")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}