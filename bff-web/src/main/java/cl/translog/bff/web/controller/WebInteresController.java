package cl.translog.bff.web.controller;

import cl.translog.bff.web.dto.InteresWebDto;
import cl.translog.bff.web.service.WebInteresService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/intereses")
public class WebInteresController {

    private final WebInteresService service;

    public WebInteresController(WebInteresService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<InteresWebDto> getAll() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public InteresWebDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<InteresWebDto> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuenta(cuentaId);
    }
}
