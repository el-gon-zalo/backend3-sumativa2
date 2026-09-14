package cl.translog.bff.cajero.dto;

public record InteresDto(

    Long id,
    Integer cuentaId,
    String nombre,
    Double saldoOriginal,
    Integer edad,
    String tipo,
    Double interesAplicado,
    Double saldoFinal
){} 