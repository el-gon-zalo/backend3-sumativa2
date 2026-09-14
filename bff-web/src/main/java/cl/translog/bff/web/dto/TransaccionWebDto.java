package cl.translog.bff.web.dto;

public record TransaccionWebDto(

    Integer id,
    String fecha,
    Double monto,
    String tipo,
    String estado
){} 