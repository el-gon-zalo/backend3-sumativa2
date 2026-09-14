package cl.translog.bff.mobile.dto;

public record TransaccionDto(

    Integer id,
    String fecha,
    Double monto,
    String tipo,
    String estado
){} 