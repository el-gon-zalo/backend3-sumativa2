package cl.translog.bff.cajero.controller;

import cl.translog.bff.cajero.dto.TransaccionCajeroDto;
import cl.translog.bff.cajero.service.CajeroTransaccionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cajero/transacciones")
public class CajeroTransaccionController {

    private final CajeroTransaccionService service;

    public CajeroTransaccionController(CajeroTransaccionService service) {
        this.service = service;
    }

    @GetMapping("/todas")
    public List<TransaccionCajeroDto> getAll() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public TransaccionCajeroDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

}
