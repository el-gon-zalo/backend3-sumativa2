package cl.translog.bff.web.dto;

public record TransaccionDto(

    Integer id,
    String fecha,
    Double monto,
    String tipo,
    String estado
){} 