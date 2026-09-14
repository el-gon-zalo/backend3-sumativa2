package cl.translog.bff.mobile.controller;

import cl.translog.bff.mobile.dto.TransaccionMobileDto;
import cl.translog.bff.mobile.service.MobileTransaccionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mobile/transacciones")
public class MobileTransaccionController {

    private final MobileTransaccionService service;

    public MobileTransaccionController(MobileTransaccionService service) {
        this.service = service;
    }

    @GetMapping("/todas")
    public List<TransaccionMobileDto> getAll() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public TransaccionMobileDto getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

}
