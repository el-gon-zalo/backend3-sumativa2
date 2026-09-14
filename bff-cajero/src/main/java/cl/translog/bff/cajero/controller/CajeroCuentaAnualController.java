package cl.translog.bff.cajero.controller;

import cl.translog.bff.cajero.dto.CuentaAnualCajeroDto;
import cl.translog.bff.cajero.service.CajeroCuentaAnualService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cajero/cuentas-anuales")
public class CajeroCuentaAnualController {

    private final CajeroCuentaAnualService service;

    public CajeroCuentaAnualController(CajeroCuentaAnualService service) {
        this.service = service;
    }

    @GetMapping("/todas")
    public List<CuentaAnualCajeroDto> getAll() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public CuentaAnualCajeroDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<CuentaAnualCajeroDto> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuenta(cuentaId);
    }
}
