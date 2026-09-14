package cl.translog.batch.controller;

import cl.translog.batch.domain.CuentaAnual;
import cl.translog.batch.service.CuentaAnualService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas-anuales")
public class CuentaAnualController {

    private final CuentaAnualService service;

    public CuentaAnualController(CuentaAnualService service) {
        this.service = service;
    }

    
    @GetMapping("/{id}")
    public CuentaAnual getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    
    @GetMapping("/todas")
    public List<CuentaAnual> getAll() {
        return service.obtenerTodos();
    }

    
    @GetMapping("/cuenta/{cuentaId}")
    public List<CuentaAnual> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuentaId(cuentaId);
    }
}

