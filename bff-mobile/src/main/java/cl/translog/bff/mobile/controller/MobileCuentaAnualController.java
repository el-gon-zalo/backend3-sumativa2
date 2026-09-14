package cl.translog.bff.mobile.controller;

import cl.translog.bff.mobile.dto.CuentaAnualMobileDto;
import cl.translog.bff.mobile.service.MobileCuentaAnualService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mobile/cuentas-anuales")
public class MobileCuentaAnualController {

    private final MobileCuentaAnualService service;

    public MobileCuentaAnualController(MobileCuentaAnualService service) {
        this.service = service;
    }

    @GetMapping("/todas")
    public List<CuentaAnualMobileDto> getAll() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public CuentaAnualMobileDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<CuentaAnualMobileDto> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuenta(cuentaId);
    }
}
