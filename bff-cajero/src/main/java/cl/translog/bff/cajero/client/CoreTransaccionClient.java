package cl.translog.bff.cajero.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import cl.translog.bff.cajero.dto.TransaccionDto;

@Component
public class CoreTransaccionClient {

    private final RestClient restClient;

    public CoreTransaccionClient(RestClient transaccionRestClient) {
        this.restClient = transaccionRestClient;
    }

    public TransaccionDto transaccion(Long id) {
        return restClient.get()
                .uri("/api/transacciones/{id}", id)
                .retrieve()
                .body(TransaccionDto.class);
    }

    public List<TransaccionDto> todas() {
        return restClient.get()
                .uri("/api/transacciones/todas")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}