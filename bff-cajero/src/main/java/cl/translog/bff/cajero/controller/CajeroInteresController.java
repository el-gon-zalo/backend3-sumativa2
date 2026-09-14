package cl.translog.bff.cajero.controller;

import cl.translog.bff.cajero.dto.InteresCajeroDto;
import cl.translog.bff.cajero.service.CajeroInteresService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cajero/intereses")
public class CajeroInteresController {

    private final CajeroInteresService service;

    public CajeroInteresController(CajeroInteresService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<InteresCajeroDto> getAll() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public InteresCajeroDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/cuenta/{cuentaId}")
    public List<InteresCajeroDto> getByCuentaId(@PathVariable Integer cuentaId) {
        return service.obtenerPorCuenta(cuentaId);
    }
}
