package cl.translog.bff.cajero.dto;

public record TransaccionDto(

    Integer id,
    String fecha,
    Double monto,
    String tipo,
    String estado
){} 