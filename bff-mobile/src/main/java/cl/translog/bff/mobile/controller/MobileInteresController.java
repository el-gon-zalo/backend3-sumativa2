package cl.translog.bff.mobile.controller;

import cl.translog.bff.mobile.dto.InteresMobileDto;
import cl.translog.bff.mobile.service.MobileInteresService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mobile/intereses")
public class MobileInteresController {

    private final MobileInteresService service;

    public MobileInteresController(MobileInteresService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<InteresMobileDto> getAll() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public InteresMobileDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<InteresMobileDto> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuenta(cuentaId);
    }
}
