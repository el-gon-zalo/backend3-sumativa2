package cl.translog.bff.web.controller;

import cl.translog.bff.web.dto.TransaccionWebDto;
import cl.translog.bff.web.service.WebTransaccionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/transacciones")
public class WebTransaccionController {

    private final WebTransaccionService service;

    public WebTransaccionController(WebTransaccionService service) {
        this.service = service;
    }

    @GetMapping("/todas")
    public List<TransaccionWebDto> getAll() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public TransaccionWebDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

}
