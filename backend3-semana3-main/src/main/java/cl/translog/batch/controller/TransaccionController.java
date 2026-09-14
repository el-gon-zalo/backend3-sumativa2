package cl.translog.batch.controller;

import cl.translog.batch.domain.Transaccion;
import cl.translog.batch.service.TransaccionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private final TransaccionService service;

    public TransaccionController(TransaccionService service) {
        this.service = service;
    }

    
    @GetMapping("/{id}")
    public Transaccion getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    
    @GetMapping("/todas")
    public List<Transaccion> getAll() {
        return service.obtenerTodos();
    }

}

