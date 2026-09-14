package cl.translog.bff.mobile.dto;

public record TransaccionMobileDto(

    Integer id,
    String fecha,
    Double monto,
    String tipo
){} 