package cl.translog.bff.cajero.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** Configura el cliente HTTP que apunta al CORE. */
@Configuration
public class RestClientConfig {
    @Bean
    RestClient coreStreamingRestClient(
            @Value("${core-streaming.base-url}") String baseUrl
    ) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
