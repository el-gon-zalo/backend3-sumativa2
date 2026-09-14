package cl.translog.bff.web.dto;

public record InteresWebDto(

    Long id,
    Integer cuentaId,
    String nombre,
    Double saldoOriginal,
    Integer edad,
    String tipo,
    Double interesAplicado,
    Double saldoFinal
){} 