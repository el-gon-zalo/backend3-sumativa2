package cl.translog.bff.mobile.dto;

public record CuentaAnualDto(

    Long id,
    Integer cuentaId,
    String fecha,
    String transaccion,
    Double monto,
    String descripcion
){} 