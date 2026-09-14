package cl.translog.bff.web.controller;

import cl.translog.bff.web.dto.CuentaAnualWebDto;
import cl.translog.bff.web.service.WebCuentaAnualService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/cuentas-anuales")
public class WebCuentaAnualController {

    private final WebCuentaAnualService service;

    public WebCuentaAnualController(WebCuentaAnualService service) {
        this.service = service;
    }

    @GetMapping("/todas")
    public List<CuentaAnualWebDto> getAll() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public CuentaAnualWebDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<CuentaAnualWebDto> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuenta(cuentaId);
    }
}
